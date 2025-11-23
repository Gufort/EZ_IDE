package SemanticCheckLogic;

import Interpret.Memory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

public class SymbolTable {
    public enum SemanticType{IntType, DoubleType, BoolType, BigIntegerType,
        StringType, ObjectType, BadType, NoType, AnyType}

    // Добавляем ArrayName в KindType
    public enum KindType{VarName, FuncName, ArrayName}

    public static class SymbolInfo{
        public String name;
        public KindType kindType;
        public SemanticType semanticType;
        public SemanticType[] params;
        public int address;
        public int index;

        // Добавляем поля для массивов
        public SemanticType elementType;  // Тип элементов массива
        public int size;                  // Размер массива (-1 если динамический)
        public int length;                // Фактическая длина (для инициализированных массивов)

        // Конструктор для переменных и функций
        public SymbolInfo(String name, KindType kindType, SemanticType semanticType, int address, SemanticType... params) {
            this.name = name;
            this.kindType = kindType;
            this.semanticType = semanticType;
            this.address = address;
            this.params = params;
            this.size = -1;
            this.length = 0;
        }

        // Конструктор для массивов
        public SymbolInfo(String name, KindType kindType, SemanticType elementType, int address, int size, int length) {
            this.name = name;
            this.kindType = kindType;
            this.semanticType = SemanticType.ObjectType; // Массив всегда ObjectType
            this.elementType = elementType;
            this.address = address;
            this.size = size;
            this.length = length;
            this.params = new SemanticType[0];
        }
    }

    public static SemanticType[] NumTypes = new SemanticType[]{SemanticType.IntType, SemanticType.DoubleType, SemanticType.BigIntegerType };
    public static Dictionary<String, SymbolInfo> SymTable = new Hashtable<String, SymbolInfo>(){};

    public static void initStandardFunctionsTable(){
        SymTable.put("sqrt", new SymbolInfo("sqrt", KindType.FuncName, SemanticType.DoubleType, -1, SemanticType.DoubleType));
        SymTable.put("print", new SymbolInfo("print", KindType.FuncName, SemanticType.AnyType, -1, SemanticType.DoubleType));
        SymTable.put("length", new SymbolInfo("length", KindType.FuncName, SemanticType.IntType, -1, SemanticType.ObjectType));
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
                break;
        }
        return address;
    }

    public static int allocateArray(SemanticType elementType, int size, int initialLength) {
        int address = -1;
        int actualSize = size > 0 ? size : initialLength;
        if (actualSize <= 0) {
            actualSize = 10;
        }

        switch(elementType) {
            case IntType:
                address = Memory.allocateIntArray(actualSize);
                break;
            case DoubleType:
                address = Memory.allocateDoubleArray(actualSize);
                break;
            case BoolType:
                address = Memory.allocateBooleanArray(actualSize);
                break;
            case BigIntegerType:
                address = Memory.allocateBigIntegerArray(actualSize);
                break;
            default:
                address = Memory.allocateObjectArray(actualSize);
                break;
        }

        return address;
    }

    public static SymbolInfo getArrayInfo(String name) {
        SymbolInfo info = SymTable.get(name);
        if (info != null && info.kindType == KindType.ArrayName) {
            return info;
        }
        return null;
    }

    public static boolean isArray(String name) {
        SymbolInfo info = SymTable.get(name);
        return info != null && info.kindType == KindType.ArrayName;
    }

    public static SemanticType getArrayElementType(String name) {
        SymbolInfo info = getArrayInfo(name);
        return info != null ? info.elementType : SemanticType.BadType;
    }

    public static int getArraySize(String name) {
        SymbolInfo info = getArrayInfo(name);
        return info != null ? info.size : -1;
    }
}