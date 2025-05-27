import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

public class CodeWriter {
    private int labelCounter = 0;
    private PrintWriter writer;
    private String fileName;

    public CodeWriter(File file) {
        try {
            writer = new PrintWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void writeInit() {
        // SP = 256
        writer.write(
                "@256\n" +
                "D=A\n" +
                "@SP\n" +
                "M=D\n"
        );
        // call Sys.init
        writeCall("Sys.init", 0);
    }

    public void writeArithmetic(String command) {
        switch (command) {
            case "add":
                writer.write(arithmeticTemplate() + "M=D+M\n");
                break;
            case "sub" :
                writer.write(arithmeticTemplate() + "M=M-D\n");
                break;
            case "and":
                writer.write(arithmeticTemplate() + "M=D&M\n");
                break;
            case "or":
                writer.write(arithmeticTemplate() + "M=D|M\n");
                break;
            case "neg":
                writer.write("@SP\nA=M-1\nM=-M\n");
                break;
            case "not":
                writer.write("@SP\nA=M-1\nM=!M\n");
                break;
            case "eq":
                writeComparison("JEQ");
                break;
            case "gt":
                writeComparison("JGT");
                break;
            case "lt":
                writeComparison("JLT");
                break;
            default:
                break;
        }
    }

    private String arithmeticTemplate() {
        return "@SP\nAM=M-1\nD=M\nA=A-1\n";
    }

    public void writePushPop(String command, String segment, int index) {
        if (command.equals("push")) {
            switch (segment) {
                case "constant":
                    writer.write(
                            "@" + index + "\n" +
                            "D=A\n" +
                            pushDToStack()
                    );
                    break;
                case "local":
                case "argument":
                case "this":
                case "that":
                    writer.write(
                            "@" + index + "\n" +
                            "D=A\n" +
                            "@" + getSegmentBase(segment) + "\n" +
                            "A=D+M\n" +
                            "D=M\n" +
                            pushDToStack()
                    );
                    break;
                case "temp":
                    writer.write(
                          "@" + (5 + index) + "\n" +
                          "D=M\n" +
                          pushDToStack()
                    );
                    break;
                case "pointer":
                    writer.write(
                            "@" + (3 + index) + "\n" +
                            "D=M\n" +
                            pushDToStack()
                    );
                    break;
                case "static" :
                    writer.write(
                            "@" + fileName + "." + index + "\n" +
                            "D=M\n" +
                            pushDToStack()
                    );
                    break;
            }
        } else if (command.equals("pop")) {
            switch (segment) {
                case "local":
                case "argument":
                case "this":
                case "that":
                    writer.write(
                            "@" + index + "\n" +
                            "D=A\n" +
                            "@" + getSegmentBase(segment) + "\n" +
                            "D=D+M\n" +     // get the target address
                            "@R13\n" +      // write target address to R13
                            "M=D\n" +
                            popStackToD() + // get value from stack to D
                            "@R13\n" +      // go to target address
                            "A=M\n" +
                            "M=D\n"         // write value into target
                    );
                    break;
                case "temp":
                    writer.write(
                            popStackToD() +
                            "@" + (5 + index) + "\n" +
                            "M=D\n"
                    );
                    break;
                case "pointer":
                    writer.write(
                            popStackToD() +
                            "@" + (3 + index) + "\n" +
                            "M=D\n"
                    );
                    break;
                case "static":
                    writer.write(
                            popStackToD() +
                            "@" + fileName + "." + index + "\n" +
                            "M=D\n"
                    );
                    break;
            }
        }
    }

    private String pushDToStack() {
        return "@SP\n" +
               "A=M\n" +
               "M=D\n" +
               "@SP\n" +
               "M=M+1\n";
    }

    private String popStackToD() {
        return "@SP\n" +
               "AM=M-1\n" +
               "D=M\n";
    }

    private String getSegmentBase(String segment) {
        switch (segment) {
            case "local": return "LCL";
            case "argument": return "ARG";
            case "this": return "THIS";
            case "that": return "THAT";
            default: return "";
        }
    }

    private void writeComparison(String jumpCommand) {
        String labelTrue = "TRUE_" + labelCounter;
        String labelEnd = "END_" + labelCounter;
        labelCounter++;

        writer.write(
                "@SP\n" +
                "AM=M-1\n" +
                "D=M\n" +
                "A=A-1\n" +
                "D=M-D\n" +
                "@" + labelTrue + "\n" +
                "D;" + jumpCommand + "\n" +
                "@SP\n" +
                "A=M-1\n" +
                "M=0\n" +       // false, set to 0
                "@" + labelEnd + "\n" +
                "0;JMP\n" +
                "(" + labelTrue + ")\n" +
                "@SP\n" +
                "A=M-1\n" +
                "M=-1\n" +      // true, set to -1
                "(" + labelEnd + ")\n"
        );
    }

    public void close() {
        writer.close();
    }

    public void writeLabel(String label) {
        writer.write(
                "(" + label + ")\n"
        );
    }

    public void writeGoto(String label) {
        writer.write(
                "@" + label + "\n" +
                "0;JMP\n"
        );
    }

    public void writeIf(String label) {
        writer.write(
                "@SP\n" +
                "AM=M-1\n" +
                "D=M\n" +
                "@" + label + "\n" +
                "D;JNE\n"       // true: -1; false: 0
        );
    }

    public void writeFunction(String functionName, int nVars) {
        // declare the function label
        writer.write("(" + functionName + ")\n");

        // initialize nVars local variables to 0
        for (int i = 0; i < nVars; i++) {
            writer.write(
                    "@0\n" +
                    "D=A\n" +
                    pushDToStack()
            );
        }
    }

    private int callCounter = 0;
    public void writeCall(String functionName, int nVars) {
        String returnLabel = "RET_" + functionName + "_" + callCounter++;
        writer.write(
                // create a return address
                "@" + returnLabel + "\n" +
                "D=A\n" +
                // push return address into stack
                pushDToStack() +

                // push LCL
                "@LCL\n" +
                "D=M\n" +
                pushDToStack() +

                // push ARG
                "@ARG\n" +
                "D=M\n" +
                pushDToStack() +

                // push THIS
                "@THIS\n" +
                "D=M\n" +
                pushDToStack() +

                // push THAT
                "@THAT\n" +
                "D=M\n" +
                pushDToStack() +

                // reposition ARG = SP - 5 - nVars
                "@SP\n" +
                "D=M\n" +
                "@5\n" +
                "D=D-A\n" +
                "@" + nVars + "\n" +
                "D=D-A\n" +
                "@ARG\n" +
                "M=D\n" +

                // reposition LCL = SP
                "@SP\n" +
                "D=M\n" +
                "@LCL\n" +
                "M=D\n" +

                // go to execute callee's code
                "@" + functionName + "\n" +
                "0;JMP\n" +

                // return address
                "(" + returnLabel + ")\n"
        );
    }

    public void writeReturn() {
        writer.write(
                // endFrame = LCL, save to register 13
                "@LCL\n" +
                "D=M\n" +
                "@R13\n" +
                "M=D\n" +

                // retAddr = *(endFrame - 5)
                "@5\n" +
                "A=D-A\n" +
                "D=M\n" +
                "@R14\n" +
                "M=D\n" +

                // *ARG = pop()
                popStackToD() +
                "@ARG\n" +
                "A=M\n" +
                "M=D\n" +

                // SP = ARG + 1
                "@ARG\n" +
                "D=M+1\n" +
                "@SP\n" +
                "M=D\n" +

                // THAT = *(endFrame - 1)
                "@R13\n" +
                "AM=M-1\n" +    // endFrame -= 1
                "D=M\n" +
                "@THAT\n" +
                "M=D\n" +

                // THIS = *(endFrame - 2)
                "@R13\n" +
                "AM=M-1\n" +
                "D=M\n" +
                "@THIS\n" +
                "M=D\n" +

                // ARG = *(endFrame - 3)
                "@R13\n" +
                "AM=M-1\n" +
                "D=M\n" +
                "@ARG\n" +
                "M=D\n" +

                // LCL = *(endFrame - 4)
                "@R13\n" +
                "AM=M-1\n" +
                "D=M\n" +
                "@LCL\n" +
                "M=D\n" +

                // goto retAddr
                "@R14\n" +
                "A=M\n" +
                "0;JMP\n"
        );
    }
}
