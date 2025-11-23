package VirtualMachine;

import java.math.BigInteger;

public class ThreeAddressCode {
    public enum Commands{
        // = const
        ICAAS, // int
        RCAAS, // double
        BCAAS, // bool
        BICAAS,// bigInteger

        // =
        IASS, // int assignment
        RASS, // double assignment
        BASS, // bool assignment
        BIASS, // BigInteger assignment

        // +=
        IASSADD, // += int
        RASSADD, // += double
        BIASSADD, // += BigInteger

        // -=
        IASSSUB, // -= int
        RASSSUB, // -= double
        BIASSSUB, // -= BigInteger

        // *=
        IASSMUL, // *= int
        RASSMUL, // *= double
        BIASSMUL, // *= BigInteger

        // /=
        IASSDIV, // /= int
        RASSDIV, // /= double
        BIASSDIV, // /= BigInteger

        // a + b
        IADD, // int + int
        RADD, // double + double
        BIADD, // BigInteger + BigInteger

        // a - b
        ISUB, // int - int
        RSUB, // double - double
        BISUB, // BigInteger - BigInteger

        // a * b
        IMUL, // int * int
        RMUL, // double * double
        BIMUL, // BigInteger * BigInteger

        // a / b
        IDIV, // int / int
        RDIV, // double / double
        BIDIV, // BigInteger / BigInteger

        // a < b
        ILT, // int less then
        RLT, // double less then
        BILT, // BigInteger less then

        // a > b
        IGT, // int greater then
        RGT, // double greater then
        BIGT, // BigInteger greater then

        // a >= b
        IGEQ, // int >= int
        RGEQ, // double >= double
        BIGEQ, // BigInteger >= BigInteger

        // a <= b
        ILEQ, // int <= int
        RLEQ, // double <= double
        BILEQ, // BigInteger <= BigInteger

        // a == b
        IEQ, // int == int
        REQ, // double == double
        BEQ, // bool == bool
        BIEQ, // BigInteger == BigInteger

        // a != b
        INEQ, // int != int
        RNEQ, // double != double
        BNEQ, // bool != bool
        BINEQ, // BigInteger != BigInteger

        // Array realization
        ARRALLOC, // выделение памяти по массив
        ARRSTORE, // запись элемента в массив
        ARRLOAD,  // чтение элемента из массива
        ARRLEN,   // длина массива

        // Conversions
        CONITR, // convert int to double
        CONITBI, // convert int to BigInteger
        CONBITI, // convert BigInteger to int
        CONBTR, // convert BigInteger to double

        CALL, // func call
        PARAM, // parameter passing

        IIF, // cond jump
        IFN, // if not

        PUSH,
        POP,

        LABEL, // label marker
        GOTO, // uncond jump
        STOP// stop
    }

    public Commands command;
    public int indexInMemory = -1;
    public int indexOfFirstOperand = -1;
    public int indexOfSecondOperand = -1;

    public int IValue;
    public double RValue;
    public boolean BValue;
    public BigInteger BIValue;
    public String label;
    public ValueType value;


    public static ThreeAddressCode create(Commands command) {
        var code = new ThreeAddressCode();
        code.command = command;
        return code;
    }


    public static ThreeAddressCode create(Commands command, int indexInMemory) {
        var code = new ThreeAddressCode();
        code.command = command;
        code.indexInMemory = indexInMemory;
        return code;
    }


    public static ThreeAddressCode createConst(Commands command, int indexInMemory, int IValue) {
        var code = new ThreeAddressCode();
        code.command = command;
        code.indexInMemory = indexInMemory;
        code.IValue = IValue;
        return code;
    }

    public static ThreeAddressCode createConst(Commands command, int indexInMemory, double RValue) {
        var code = new ThreeAddressCode();
        code.command = command;
        code.indexInMemory = indexInMemory;
        code.RValue = RValue;
        return code;
    }

    public static ThreeAddressCode createConst(Commands command, int indexInMemory, String BIValue) {
        var code = new ThreeAddressCode();
        code.command = command;
        code.indexInMemory = indexInMemory;
        code.BIValue = new BigInteger(BIValue);
        return code;
    }


    public static ThreeAddressCode create(Commands command, String label) {
        var code = new ThreeAddressCode();
        code.command = command;
        code.label = label;
        return code;
    }


    public static ThreeAddressCode create(Commands command, int indexInMemory, String label) {
        var code = new ThreeAddressCode();
        code.command = command;
        code.indexInMemory = indexInMemory;
        code.label = label;
        return code;
    }

    public static ThreeAddressCode createAssign(Commands command, int destIndex, int srcIndex) {
        var code = new ThreeAddressCode();
        code.command = command;
        code.indexInMemory = destIndex;
        code.indexOfFirstOperand = srcIndex;
        return code;
    }

    public static ThreeAddressCode createConvert(Commands command, int srcIndex, int destIndex) {
        var code = new ThreeAddressCode();
        code.command = command;
        code.indexOfFirstOperand = srcIndex;
        code.indexInMemory = destIndex;
        return code;
    }

    public static ThreeAddressCode createBinary(Commands command, int indexOfFirstOperand,
                                                int indexOfSecondOperand, int resultIndex) {
        var code = new ThreeAddressCode();
        code.command = command;
        code.indexOfFirstOperand = indexOfFirstOperand;
        code.indexOfSecondOperand = indexOfSecondOperand;
        code.indexInMemory = resultIndex;
        return code;
    }

    public static ThreeAddressCode createAssignOperation(Commands command, int destIndex,
                                                         int indexOfFirstOperand, int indexOfSecondOperand) {
        var code = new ThreeAddressCode();
        code.command = command;
        code.indexInMemory = destIndex;
        code.indexOfFirstOperand = indexOfFirstOperand;
        code.indexOfSecondOperand = indexOfSecondOperand;
        return code;
    }
}