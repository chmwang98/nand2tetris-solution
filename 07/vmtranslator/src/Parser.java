import java.util.List;

public class Parser {
    private List<String> lines;
    private int currentLine;
    private String currentCommand;

    public Parser(List<String> lines) {
        this.lines = lines;
        currentLine = -1;
    }

    public boolean hasMoreLines() {
        return currentLine < lines.size() - 1;
    }

    public void reset() {
        this.currentLine = -1;
    }

    public void advance() {
        currentLine++;
        currentCommand = lines.get(currentLine).trim();
    }

    public String arg1() {
        if (commandType().equals(CommandType.C_ARITHMETIC)) {
            return currentCommand;
        } else {
            return currentCommand.split(" ")[1];
        }
    }

    public String arg2() {
        return currentCommand.split(" ")[2];
    }

    public CommandType commandType() {
        if (currentCommand.startsWith("@")) {
            return CommandType.C_ARITHMETIC;
        } else if (currentCommand.startsWith("(")) {
            return CommandType.C_PUSH;
        } else {
            return CommandType.C_POP;
        }
    }

    public enum CommandType {
        C_ARITHMETIC, C_PUSH, C_POP;
    }
}
