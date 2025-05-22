import java.util.HashMap;
import java.util.Map;

public class Code {
    private Map<String, String> COMP_TABLE;
    private Map<String, String> DEST_TABLE;
    private Map<String, String> JUMP_TABLE;

    public Code() {
        COMP_TABLE = new HashMap<>();
        COMP_TABLE.put("0", "0101010");
        COMP_TABLE.put("1", "0111111");
        COMP_TABLE.put("-1","0111010");
        COMP_TABLE.put("D", "0001100");
        COMP_TABLE.put("A", "0110000");
        COMP_TABLE.put("!D", "0001101");
        COMP_TABLE.put("!A", "0110001");
        COMP_TABLE.put("-D", "0001111");
        COMP_TABLE.put("-A", "0110011");
        COMP_TABLE.put("D+1","0011111");
        COMP_TABLE.put("A+1","0110111");
        COMP_TABLE.put("D+A","0000010");
        COMP_TABLE.put("D-A","0010011");
        COMP_TABLE.put("A-D","0000111");
        COMP_TABLE.put("D&A","0000000");
        COMP_TABLE.put("D|A","0010101");
        COMP_TABLE.put("M","1110000");
        COMP_TABLE.put("!M","1110001");
        COMP_TABLE.put("-M","1110011");
        COMP_TABLE.put("M+1","1110111");
        COMP_TABLE.put("M-1","1110010");
        COMP_TABLE.put("D+M","1000010");
        COMP_TABLE.put("D-M","1010011");
        COMP_TABLE.put("M-D","1000111");
        COMP_TABLE.put("D&M","1000000");
        COMP_TABLE.put("D|M","1010101");
        COMP_TABLE.put("D-1","0001110");
        COMP_TABLE.put("A-1","0110010");

        DEST_TABLE = new HashMap<>();
        DEST_TABLE.put("",    "000");
        DEST_TABLE.put("M",   "001");
        DEST_TABLE.put("D",   "010");
        DEST_TABLE.put("MD",  "011");
        DEST_TABLE.put("A",   "100");
        DEST_TABLE.put("AM",  "101");
        DEST_TABLE.put("AD",  "110");
        DEST_TABLE.put("AMD", "111");

        JUMP_TABLE = new HashMap<>();
        JUMP_TABLE.put("",    "000");
        JUMP_TABLE.put("JGT", "001");
        JUMP_TABLE.put("JEQ", "010");
        JUMP_TABLE.put("JGE", "011");
        JUMP_TABLE.put("JLT", "100");
        JUMP_TABLE.put("JNE", "101");
        JUMP_TABLE.put("JLE", "110");
        JUMP_TABLE.put("JMP", "111");
    }


    public String dest(String s) {
        return DEST_TABLE.get(s);
    }

    public String comp(String s) {
        return COMP_TABLE.get(s);
    }

    public String jump(String s) {
        return JUMP_TABLE.get(s);
    }

    public String toBinary(int value) {
        return String.format("%16s", Integer.toBinaryString(value)).replace(' ', '0');
    }
}
