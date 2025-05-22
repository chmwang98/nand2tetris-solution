import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class HackAssembler {
    private Parser parser;
    private SymbolTable symbolTable;
    private Code code;
    private int romAddress;

    public HackAssembler(List<String> lines) {
        parser = new Parser(lines);
        symbolTable = new SymbolTable();
        code = new Code();
        romAddress = 0;
    }

    public List<String> assemble() {
        firstPass();
        return secondPass();
    }

    private void firstPass() {
        while (parser.hasMoreLines()) {
            parser.advance();
            if (parser.instructionType().equals(Parser.InstructionType.L_INSTRUCTION)) {
                symbolTable.addEntry(parser.symbol(), romAddress);
            } else{
                // if instruction is label, address doesn't increment
                romAddress++;
            }
        }
    }

    private List<String> secondPass() {
        // Start again from the beginning
        parser.reset();
        List<String> machineCode = new ArrayList<>();
        while (parser.hasMoreLines()) {
            parser.advance();
            if (parser.instructionType().equals(Parser.InstructionType.L_INSTRUCTION)) {
                continue;
            }

            String binaryLine;
            if (parser.instructionType().equals(Parser.InstructionType.A_INSTRUCTION)) {
                binaryLine = processAInstruction();
            } else {
                binaryLine = processCInstruction();
            }
            machineCode.add(binaryLine);
        }
        return machineCode;
    }

    private String processAInstruction() {
        String symbol = parser.symbol();
        try {
            int value = Integer.parseInt(symbol);
            return code.toBinary(value);
        } catch (NumberFormatException e) {
            if (!symbolTable.contains(symbol)) {
                symbolTable.addVariable(symbol);
            }
            return code.toBinary(symbolTable.getAddress(symbol));
        }
    }

    private String processCInstruction() {
        return "111" +
                code.comp(parser.comp()) +
                code.dest(parser.dest()) +
                code.jump(parser.jump());
    }

    public static void main(String[] args) {
        List<String> lines = readLines(args[0]);
        HackAssembler assembler = new HackAssembler(lines);
        List<String> machineCode = assembler.assemble();
        writeToFile(args[0], machineCode);
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
        } catch (IOException o) {
            o.printStackTrace();
        }
        return lines;
    }

    private static void writeToFile(String filename, List<String> lines) {
        String newName = filename.replace(".asm", ".hack");
        try (PrintWriter pw = new PrintWriter(newName)) {
            for (String line : lines) {
                pw.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
