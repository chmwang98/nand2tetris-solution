import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

import static java.lang.String.join;

public class Parser {
    private List<String> lines;
    private int currentLine;
    private String currentInstruction;

    public Parser(List<String> lines) {
        this.lines = lines;
        this.currentLine = -1;
    }

    public boolean hasMoreLines() {
        return currentLine < lines.size() - 1;
    }

    public void reset() {
        this.currentLine = -1;
    }

    public void advance() {
        currentLine++;
        currentInstruction = lines.get(currentLine).trim();
    }

    public InstructionType instructionType() {
        if (currentInstruction.startsWith("@")) {
            return InstructionType.A_INSTRUCTION;
        } else if (currentInstruction.startsWith("(")) {
            return InstructionType.L_INSTRUCTION;
        } else {
            return InstructionType.C_INSTRUCTION;
        }
    }

    public String symbol() {
        if (instructionType().equals(InstructionType.A_INSTRUCTION)) {
            return currentInstruction.substring(1);
        } else if (instructionType().equals(InstructionType.L_INSTRUCTION)) {
            return currentInstruction.substring(1, currentInstruction.length() - 1);
        } else {
            return null;
        }
    }

    public String dest() {
        if (currentInstruction.contains("=")) {
            return currentInstruction.split("=")[0];
        }
        return "";
    }

    public String comp() {
        String temp = currentInstruction;
        if (temp.contains("=")) {
            temp = temp.split("=")[1];
        }
        if (temp.contains(";")) {
            temp = temp.split(";")[0];
        }
        return temp;
    }

    public String jump() {
        if (currentInstruction.contains(";")) {
            return currentInstruction.split(";")[1];
        }
        return "";
    }

    public enum InstructionType {
        A_INSTRUCTION, C_INSTRUCTION, L_INSTRUCTION;
    }
}
