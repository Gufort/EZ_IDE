package VirtualMachine;

import java.math.BigInteger;

public class ValueType {
    public enum VarValueType{
        INTEGER, REAL, BOOLEAN, BIGINTEGER
    }

    public ValueType(){}

    public int integer;
    public double real;
    public boolean bool;
    public BigInteger bigInteger =  new BigInteger("0");
    public VarValueType type;

    public ValueType(int integer){
        this.integer = integer;
        this.type = VarValueType.INTEGER;
    }

    public ValueType(double real){
        this.real = real;
        this.type = VarValueType.REAL;
    }

    public ValueType(boolean bool){
        this.bool = bool;
        this.type = VarValueType.BOOLEAN;
    }

    public ValueType(BigInteger bigInteger){
        this.bigInteger = bigInteger;
        this.type = VarValueType.BIGINTEGER;
    }

    @Override public String toString(){
        switch(type){
            case INTEGER: return Integer.toString(this.integer);
            case REAL: return Double.toString(this.real);
            case BOOLEAN: return Boolean.toString(this.bool);
            case BIGINTEGER: return this.bigInteger.toString();
        }
        return null;
    }
}