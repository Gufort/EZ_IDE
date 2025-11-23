package Interpret;

import java.math.BigInteger;

public class Memory {
    private static int MEMORY_SIZE = 100;
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
    public static BigInteger getBigInteger(int address) { return (BigInteger) memory[address]; }

    public static void setInt(int address, int value) {
        memory[address] = value;
    }
    public static void setDouble(int address, double value) {
        memory[address] = value;
    }
    public static void setBoolean(int address, boolean value) {
        memory[address] = value;
    }
    public static void setBigInteger(int address, BigInteger value) { memory[address] = value; }

    public static void reset() {
        memory = new Object[MEMORY_SIZE];
        nextAddress = 0;
    }
}