import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VMTranslator {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java VMTranslator <input.vm | folder>");
            return;
        }

        File input = new File(args[0]);
        File outputFile;
        List<File> vmFiles = new ArrayList<>();

        if (input.isFile() && input.getName().endsWith(".vm")) {
            vmFiles.add(input);
            outputFile = new File(input.getAbsolutePath().replace(".vm", ".asm"));
        } else if (input.isDirectory()) {
            File[] files = input.listFiles((dir, name) -> name.endsWith(".vm"));
            if (files == null || files.length == 0) {
                System.err.println("No .vm files in folder!");
                return;
            }
            for (File f : files) {
                vmFiles.add(f);
            }
            String dirName = input.getName();
            outputFile = new File(input, dirName + ".asm");
        } else {
            System.err.println("Invalid input. Input must be a .vm file or a folder!");
            return;
        }

        CodeWriter codeWriter = new CodeWriter(outputFile);

        // if input is a folder, write bootstrap code
        if (input.isDirectory()) {
            codeWriter.writeInit();
        }

        for (File vmFile : vmFiles) {
            String fileName = vmFile.getName().replace(".vm", "");
            codeWriter.setFileName(fileName);
            List<String> lines = readLines(vmFile);
            Parser parser = new Parser(lines);

            while (parser.hasMoreLines()) {
                parser.advance();
                Parser.CommandType type = parser.commandType();

                switch (type) {
                    case C_ARITHMETIC:
                        codeWriter.writeArithmetic(parser.arg1());
                        break;
                    case C_PUSH:
                        codeWriter.writePushPop("push", parser.arg1(), parser.arg2());
                        break;
                    case C_POP:
                        codeWriter.writePushPop("pop", parser.arg1(), parser.arg2());
                        break;
                    case C_LABEL:
                        codeWriter.writeLabel(parser.arg1());
                        break;
                    case C_GOTO:
                        codeWriter.writeGoto(parser.arg1());
                        break;
                    case C_IF:
                        codeWriter.writeIf(parser.arg1());
                        break;
                    case C_FUNCTION:
                        codeWriter.writeFunction(parser.arg1(), parser.arg2());
                        break;
                    case C_RETURN:
                        codeWriter.writeReturn();
                        break;
                    case C_CALL:
                        codeWriter.writeCall(parser.arg1(), parser.arg2());
                        break;
                    default:
                        break;
                }
            }
        }
        codeWriter.close();
    }

    private static List<String> readLines(File file) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.replaceAll("//.*", "").trim();
                if (!line.isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }
}
