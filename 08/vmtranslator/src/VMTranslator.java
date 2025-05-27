import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VMTranslator {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java VMTranslator <input.vm>");
            return;
        }

        String inputFilePath = args[0];
        if (!inputFilePath.endsWith(".vm")) {
            System.err.println("Input file must be a .vm file!");
            return;
        }

        List<String> lines = readLines(inputFilePath);

        String outputFilePath = inputFilePath.replace(".vm", ".asm");
        File outputFile = new File(outputFilePath);
        CodeWriter codeWriter = new CodeWriter(outputFile);
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
                default:
                    break;
            }
        }
        codeWriter.close();
    }

    private static List<String> readLines(String filename) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
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
