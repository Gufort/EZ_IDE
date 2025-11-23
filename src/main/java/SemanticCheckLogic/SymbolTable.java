package SemanticCheckLogic;

import Interpret.Memory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

public class SymbolTable {
    public enum SemanticType{IntType, DoubleType, BoolType, BigIntegerType,
                            StringType, ObjectType, BadType, NoType, AnyType}
    public enum KindType{VarName, FuncName}

    public static class SymbolInfo{
        public String name;
        public KindType kindType;
        public SemanticType semanticType;
        public SemanticType[] params;
        public int address;
        public int index;

        public SymbolInfo(String name, KindType kindType, SemanticType semanticType, int address, SemanticType... params) {
            this.name = name;
            this.kindType = kindType;
            this.semanticType = semanticType;
            this.address = address;
            this.params = params;
        }
    }
    public static SemanticType[] NumTypes = new SemanticType[]{SemanticType.IntType, SemanticType.DoubleType};
    public static Dictionary<String, SymbolInfo> SymTable = new Hashtable<String, SymbolInfo>(){};

    public static void initStandardFunctionsTable(){
        SymTable.put("sqrt", new SymbolInfo("sqrt", KindType.FuncName, SemanticType.DoubleType, -1, SemanticType.DoubleType));
        SymTable.put("print", new SymbolInfo("print", KindType.FuncName, SemanticType.AnyType, -1, SemanticType.DoubleType));
    }

    static {
        initStandardFunctionsTable();
    }

    public static int allocateVariable(SemanticType type, Object initialValue) {
        int address = -1;
        switch(type) {
            case IntType:
                address = Memory.allocateInt((Integer) initialValue);
                break;
            case DoubleType:
                address = Memory.allocateDouble((Double) initialValue);
                break;
            case BoolType:
                address = Memory.allocateBoolean((Boolean) initialValue);
                break;
            case BigIntegerType:
                address = Memory.allocateBigInteger((BigInteger) initialValue);
        }
        return address;
    }
}
