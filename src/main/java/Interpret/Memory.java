package Interpret;

import java.math.BigInteger;

public class Memory {
    private static int MEMORY_SIZE = 1000;
    private static Object[] memory = new Object[MEMORY_SIZE];
    private static int nextAddress = 0;

    public static int allocateInt(int value) {
        int address = nextAddress++;
        memory[address] = value;
        return address;
    }

    public static int allocateDouble(double value) {
        int address = nextAddress++;
        memory[address] = value;
        return address;
    }

    public static int allocateBoolean(boolean value) {
        int address = nextAddress++;
        memory[address] = value;
        return address;
    }

    public static int allocateBigInteger(BigInteger value) {
        int address = nextAddress++;
        memory[address] = value;
        return address;
    }

    public static int getInt(int address) {
        return (Integer) memory[address];
    }

    public static double getDouble(int address) {
        return (Double) memory[address];
    }

    public static boolean getBoolean(int address) {
        return (Boolean) memory[address];
    }

    public static BigInteger getBigInteger(int address) {
        return (BigInteger) memory[address];
    }

    public static void setInt(int address, int value) {
        memory[address] = value;
    }

    public static void setDouble(int address, double value) {
        memory[address] = value;
    }

    public static void setBoolean(int address, boolean value) {
        memory[address] = value;
    }

    public static void setBigInteger(int address, BigInteger value) {
        memory[address] = value;
    }

    public static int allocateIntArray(int size) {
        int address = nextAddress;
        memory[nextAddress++] = size;
        for (int i = 0; i < size; i++) {
            memory[nextAddress++] = 0;
        }
        return address;
    }

    public static int allocateDoubleArray(int size) {
        int address = nextAddress;
        memory[nextAddress++] = size;
        for (int i = 0; i < size; i++) {
            memory[nextAddress++] = 0.0;
        }
        return address;
    }

    public static int allocateBooleanArray(int size) {
        int address = nextAddress;
        memory[nextAddress++] = size;
        for (int i = 0; i < size; i++) {
            memory[nextAddress++] = false;
        }
        return address;
    }

    public static int allocateBigIntegerArray(int size) {
        int address = nextAddress;
        memory[nextAddress++] = size;
        for (int i = 0; i < size; i++) {
            memory[nextAddress++] = BigInteger.ZERO;
        }
        return address;
    }

    public static int allocateObjectArray(int size) {
        int address = nextAddress;
        memory[nextAddress++] = size;
        for (int i = 0; i < size; i++) {
            memory[nextAddress++] = null;
        }
        return address;
    }

    public static int getArrayElementInt(int arrayAddress, int index) {
        checkArrayBounds(arrayAddress, index);
        return (Integer) memory[arrayAddress + 1 + index];
    }

    public static double getArrayElementDouble(int arrayAddress, int index) {
        checkArrayBounds(arrayAddress, index);
        return (Double) memory[arrayAddress + 1 + index];
    }

    public static boolean getArrayElementBoolean(int arrayAddress, int index) {
        checkArrayBounds(arrayAddress, index);
        return (Boolean) memory[arrayAddress + 1 + index];
    }

    public static BigInteger getArrayElementBigInteger(int arrayAddress, int index) {
        checkArrayBounds(arrayAddress, index);
        return (BigInteger) memory[arrayAddress + 1 + index];
    }

    public static Object getArrayElementObject(int arrayAddress, int index) {
        checkArrayBounds(arrayAddress, index);
        return memory[arrayAddress + 1 + index];
    }

    public static void setArrayElementInt(int arrayAddress, int index, int value) {
        checkArrayBounds(arrayAddress, index);
        memory[arrayAddress + 1 + index] = value;
    }

    public static void setArrayElementDouble(int arrayAddress, int index, double value) {
        checkArrayBounds(arrayAddress, index);
        memory[arrayAddress + 1 + index] = value;
    }

    public static void setArrayElementBoolean(int arrayAddress, int index, boolean value) {
        checkArrayBounds(arrayAddress, index);
        memory[arrayAddress + 1 + index] = value;
    }

    public static void setArrayElementBigInteger(int arrayAddress, int index, BigInteger value) {
        checkArrayBounds(arrayAddress, index);
        memory[arrayAddress + 1 + index] = value;
    }

    public static void setArrayElementObject(int arrayAddress, int index, Object value) {
        checkArrayBounds(arrayAddress, index);
        memory[arrayAddress + 1 + index] = value;
    }

    public static int getArraySize(int arrayAddress) {
        return (Integer) memory[arrayAddress];
    }

    public static boolean isValidArrayIndex(int arrayAddress, int index) {
        int size = getArraySize(arrayAddress);
        return index >= 0 && index < size;
    }

    private static void checkArrayBounds(int arrayAddress, int index) {
        if (!isValidArrayIndex(arrayAddress, index)) {
            throw new RuntimeException("Array index out of bounds: " + index +
                    ", array size: " + getArraySize(arrayAddress));
        }
    }

    public static void initializeArray(int arrayAddress, Object[] initialValues) {
        int size = getArraySize(arrayAddress);
        int copyLength = Math.min(size, initialValues.length);

        for (int i = 0; i < copyLength; i++) {
            memory[arrayAddress + 1 + i] = initialValues[i];
        }
    }

    public static Object[] getArrayContents(int arrayAddress) {
        int size = getArraySize(arrayAddress);
        Object[] contents = new Object[size];
        for (int i = 0; i < size; i++) {
            contents[i] = memory[arrayAddress + 1 + i];
        }
        return contents;
    }

    public static void reset() {
        memory = new Object[MEMORY_SIZE];
        nextAddress = 0;
    }

    public static void printMemoryState() {
        System.out.println("=== Memory State ===");
        System.out.println("Next address: " + nextAddress);
        for (int i = 0; i < nextAddress; i++) {
            System.out.println(i + ": " + memory[i]);
        }
    }
}