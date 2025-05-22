import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private static final int DATA_STARTING_ADDRESS = 16;
    private int dataAddress;
    private Map<String, Integer> symbolTable;
    public SymbolTable() {
        initializeSymbolTable();
        dataAddress = DATA_STARTING_ADDRESS;
    }

    private void initializeSymbolTable() {
        symbolTable = new HashMap<>();

        for (int i = 0; i < 16; i++) {
            symbolTable.put("R" + i, i);
        }
        symbolTable.put("SCREEN", 16384);
        symbolTable.put("KBD", 24576);
        symbolTable.put("SP", 0);
        symbolTable.put("LCL", 1);
        symbolTable.put("ARG", 2);
        symbolTable.put("THIS", 3);
        symbolTable.put("THAT", 4);
    }

    public void addEntry(String symbol, int address) {
        symbolTable.put(symbol, address);
    }

    public void addVariable(String symbol) {
        symbolTable.put(symbol, dataAddress++);
    }

    public boolean contains(String symbol) {
        return symbolTable.containsKey(symbol);
    }

    public int getAddress(String symbol) {
        return symbolTable.get(symbol);
    }
}
