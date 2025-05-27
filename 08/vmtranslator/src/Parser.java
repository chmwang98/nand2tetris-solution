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

    public int arg2() {
        return Integer.parseInt(currentCommand.split(" ")[2]);
    }

    public CommandType commandType() {
        if (currentCommand.startsWith("push")) {
            return CommandType.C_PUSH;
        } else if (currentCommand.startsWith("pop")) {
            return CommandType.C_POP;
        } else if (currentCommand.startsWith("label")) {
            return CommandType.C_LABEL;
        } else if (currentCommand.startsWith("goto")) {
            return CommandType.C_GOTO;
        } else if (currentCommand.startsWith("if-goto")) {
            return CommandType.C_IF;
        } else if (currentCommand.startsWith("function")) {
            return CommandType.C_FUNCTION;
        } else if (currentCommand.startsWith("call")) {
            return CommandType.C_CALL;
        } else if (currentCommand.startsWith("return")) {
            return CommandType.C_RETURN;
        } else {
            return CommandType.C_ARITHMETIC;
        }
    }

    public enum CommandType {
        C_ARITHMETIC, C_PUSH, C_POP, C_LABEL, C_GOTO, C_IF, C_FUNCTION, C_RETURN, C_CALL;
    }
}
