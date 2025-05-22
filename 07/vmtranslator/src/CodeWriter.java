import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class CodeWriter {
    private PrintWriter writer;

    public CodeWriter(File file) {
        try {
            writer = new PrintWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeArithmetic(String command) {
        String asmCode = "";
        switch (command) {
            case "add":
                writer.write("@SP\nAM=M-1\nD=M\nA=A-1\nM=D+M\n");
                break;
            case "sub" :
                writer.write("@SP\nAM=M-1\nD=M\nA=A-1\nM=D-M\n");
                break;
            case "neg":
                writer.write("@SP\nA=M-1\nM=-M\n");
                break;
            case "eq":
                writeComparison("JEQ");
                break;
            case "gt":
                break;
            case "lt":
                break;
            case "and":
                writer.write("@SP\nAM=M-1\nD=M\nA=A-1\nM=D&M\n");
                break;
            case "or":
                writer.write("@SP\nAM=M-1\nD=M\nA=A-1\nM=DM\n");
                break;
            case "not":
                break;
            default:
                break;
        }
    }

    public void writePushPop(String command, String segment, int index) {

    }

    public void close() {

    }

    private void writeComparison(String jumpCommand) throws IOException {
        String labelTrue = "Label"
    }
}
