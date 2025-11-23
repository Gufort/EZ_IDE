package VirtualMachine;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;

public class SimpleVirtualMachine {
    public static ValueType[] memory = new ValueType[1000];
    private static Consumer<String> outputHandler = System.out::println;

    private static Hashtable<String, Integer> labelAddresses = new Hashtable<String, Integer>();
    private static Stack<Integer> callStack = new Stack<Integer>();
    private static Stack<ValueType> paramStack = new Stack<ValueType>();
    private static ThreeAddressCode[] program;
    private static Hashtable<String, Runnable> standardFunctions = new Hashtable<String, Runnable>();

    static {
        standardFunctions.put("Print", () -> executePrintFunction());
        standardFunctions.put("print", () -> executePrintFunction());
    }

    private static int programCounter = 0;
    private static final double TOLERANCE = 0.00000001;

    public static void loadProgram(List<ThreeAddressCode> pr){
        labelAddresses.clear();
        for(int i = 0; i < pr.size(); i++){
            if(pr.get(i).command == ThreeAddressCode.Commands.LABEL
                    && pr.get(i).label != null)
                labelAddresses.put(pr.get(i).label, i);
        }
        program = pr.toArray(new ThreeAddressCode[pr.size()]);
        programCounter = 0;
    }

    public static void run() throws Exception{
        if(program == null) throw new Exception("Program isn't loaded!");
        programCounter = 0;

        while(programCounter < program.length){
            var command = program[programCounter];
            execute(command);
            if(command.command == ThreeAddressCode.Commands.STOP)
                break;
            programCounter++;
        }
    }

    public static void initialize() {
        for (int i = 0; i < memory.length; i++)
            memory[i] = new ValueType();
        callStack.clear();
        paramStack.clear();
    }

    public static void increaseMemorySize(int size) {
        if(size >= memory.length){
            int newSize = Math.max(size + 100, memory.length * 2);
            int oldSize = memory.length;
            ValueType[] newArray = new ValueType[newSize];
            System.arraycopy(memory, 0, newArray, 0, memory.length);
            memory = newArray;
            for(int i = oldSize; i < memory.length; i++) {
                memory[i] = new ValueType();
            }
        }
    }

    public static void resetVM(){
        programCounter = 0;
        callStack.clear();
        paramStack.clear();
        labelAddresses.clear();
        program = null;
        initialize();
    }

    private static void ensureMemoryAccess(int index) {
        if (index < 0) {
            throw new RuntimeException("Invalid memory index: " + index);
        }
        increaseMemorySize(index + 1);
    }

    public static void execute(ThreeAddressCode tar) throws Exception {
        if (tar.indexInMemory >= 0) ensureMemoryAccess(tar.indexInMemory);
        if (tar.indexOfFirstOperand >= 0) ensureMemoryAccess(tar.indexOfFirstOperand);
        if (tar.indexOfSecondOperand >= 0) ensureMemoryAccess(tar.indexOfSecondOperand);

        switch(tar.command){
            case ThreeAddressCode.Commands.ICAAS:
                if(tar.IValue != 0)
                    memory[tar.indexInMemory].integer = tar.IValue;
                else if (tar.indexOfFirstOperand >= 0)
                    memory[tar.indexInMemory].integer = memory[tar.indexOfFirstOperand].integer;
                break;
            case ThreeAddressCode.Commands.RCAAS:
                if(tar.RValue != 0)
                    memory[tar.indexInMemory].real = tar.RValue;
                else if (tar.indexOfFirstOperand >= 0)
                    memory[tar.indexInMemory].real = memory[tar.indexOfFirstOperand].real;
                break;
            case ThreeAddressCode.Commands.BCAAS:
                memory[tar.indexInMemory].bool = tar.BValue;
                break;

            case ThreeAddressCode.Commands.IASS:
                if (tar.indexOfFirstOperand >= 0)
                    memory[tar.indexInMemory].integer = memory[tar.indexOfFirstOperand].integer;
                break;
            case ThreeAddressCode.Commands.RASS:
                if (tar.indexOfFirstOperand >= 0)
                    memory[tar.indexInMemory].real = memory[tar.indexOfFirstOperand].real;
                break;
            case ThreeAddressCode.Commands.BASS:
                if (tar.indexOfFirstOperand >= 0)
                    memory[tar.indexInMemory].bool = memory[tar.indexOfFirstOperand].bool;
                break;

            case ThreeAddressCode.Commands.IASSADD:
                if (tar.indexOfFirstOperand >= 0 && tar.indexOfSecondOperand >= 0)
                    memory[tar.indexInMemory].integer = memory[tar.indexOfFirstOperand].integer + memory[tar.indexOfSecondOperand].integer;
                break;
            case ThreeAddressCode.Commands.RASSADD:
                if (tar.indexOfFirstOperand >= 0 && tar.indexOfSecondOperand >= 0)
                    memory[tar.indexInMemory].real = memory[tar.indexOfFirstOperand].real + memory[tar.indexOfSecondOperand].real;
                break;
            case ThreeAddressCode.Commands.IASSSUB:
                if (tar.indexOfFirstOperand >= 0 && tar.indexOfSecondOperand >= 0)
                    memory[tar.indexInMemory].integer = memory[tar.indexOfFirstOperand].integer - memory[tar.indexOfSecondOperand].integer;
                break;
            case ThreeAddressCode.Commands.RASSSUB:
                if (tar.indexOfFirstOperand >= 0 && tar.indexOfSecondOperand >= 0)
                    memory[tar.indexInMemory].real = memory[tar.indexOfFirstOperand].real - memory[tar.indexOfSecondOperand].real;
                break;
            case ThreeAddressCode.Commands.IASSMUL:
                if (tar.indexOfFirstOperand >= 0 && tar.indexOfSecondOperand >= 0)
                    memory[tar.indexInMemory].integer = memory[tar.indexOfFirstOperand].integer * memory[tar.indexOfSecondOperand].integer;
                break;
            case ThreeAddressCode.Commands.RASSMUL:
                if (tar.indexOfFirstOperand >= 0 && tar.indexOfSecondOperand >= 0)
                    memory[tar.indexInMemory].real = memory[tar.indexOfFirstOperand].real * memory[tar.indexOfSecondOperand].real;
                break;
            case ThreeAddressCode.Commands.IASSDIV:
                if (tar.indexOfFirstOperand >= 0 && tar.indexOfSecondOperand >= 0) {
                    if(memory[tar.indexOfSecondOperand].integer == 0)
                        throw new Exception("Divide by zero!!!");
                    memory[tar.indexInMemory].integer = memory[tar.indexOfFirstOperand].integer / memory[tar.indexOfSecondOperand].integer;
                }
                break;
            case ThreeAddressCode.Commands.RASSDIV:
                if (tar.indexOfFirstOperand >= 0 && tar.indexOfSecondOperand >= 0) {
                    if(Math.abs(memory[tar.indexOfSecondOperand].real) < TOLERANCE)
                        throw new Exception("Divide by zero!!!");
                    memory[tar.indexInMemory].real = memory[tar.indexOfFirstOperand].real / memory[tar.indexOfSecondOperand].real;
                }
                break;

            case ThreeAddressCode.Commands.IADD:
                if (tar.indexOfFirstOperand >= 0 && tar.indexOfSecondOperand >= 0)
                    memory[tar.indexInMemory].integer = memory[tar.indexOfFirstOperand].integer + memory[tar.indexOfSecondOperand].integer;
                break;
            case ThreeAddressCode.Commands.RADD:
                if (tar.indexOfFirstOperand >= 0 && tar.indexOfSecondOperand >= 0)
                    memory[tar.indexInMemory].real = memory[tar.indexOfFirstOperand].real + memory[tar.indexOfSecondOperand].real;
                break;
            case ThreeAddressCode.Commands.ISUB:
                if (tar.indexOfFirstOperand >= 0 && tar.indexOfSecondOperand >= 0)
                    memory[tar.indexInMemory].integer = memory[tar.indexOfFirstOperand].integer - memory[tar.indexOfSecondOperand].integer;
                break;
            case ThreeAddressCode.Commands.RSUB:
                if (tar.indexOfFirstOperand >= 0 && tar.indexOfSecondOperand >= 0)
                    memory[tar.indexInMemory].real = memory[tar.indexOfFirstOperand].real - memory[tar.indexOfSecondOperand].real;
                break;
            case ThreeAddressCode.Commands.IMUL:
                if (tar.indexOfFirstOperand >= 0 && tar.indexOfSecondOperand >= 0)
                    memory[tar.indexInMemory].integer = memory[tar.indexOfFirstOperand].integer * memory[tar.indexOfSecondOperand].integer;
                break;
            case ThreeAddressCode.Commands.RMUL:
                if (tar.indexOfFirstOperand >= 0 && tar.indexOfSecondOperand >= 0)
                    memory[tar.indexInMemory].real = memory[tar.indexOfFirstOperand].real * memory[tar.indexOfSecondOperand].real;
                break;
            case ThreeAddressCode.Commands.IDIV:
                if (tar.indexOfFirstOperand >= 0 && tar.indexOfSecondOperand >= 0) {
                    if(memory[tar.indexOfSecondOperand].integer == 0)
                        throw new Exception("Divide by zero!!!");
                    memory[tar.indexInMemory].integer = memory[tar.indexOfFirstOperand].integer / memory[tar.indexOfSecondOperand].integer;
                }
                break;
            case ThreeAddressCode.Commands.RDIV:
                if (tar.indexOfFirstOperand >= 0 && tar.indexOfSecondOperand >= 0) {
                    if(Math.abs(memory[tar.indexOfSecondOperand].real) < TOLERANCE)
                        throw new Exception("Divide by zero!!!");
                    memory[tar.indexInMemory].real = memory[tar.indexOfFirstOperand].real / memory[tar.indexOfSecondOperand].real;
                }
                break;

            case ThreeAddressCode.Commands.ILT:
                if (tar.indexOfFirstOperand >= 0 && tar.indexOfSecondOperand >= 0)
                    memory[tar.indexInMemory].bool = memory[tar.indexOfFirstOperand].integer < memory[tar.indexOfSecondOperand].integer;
                break;
            case ThreeAddressCode.Commands.RLT:
                if (tar.indexOfFirstOperand >= 0 && tar.indexOfSecondOperand >= 0)
                    memory[tar.indexInMemory].bool = memory[tar.indexOfFirstOperand].real < memory[tar.indexOfSecondOperand].real;
                break;
            case ThreeAddressCode.Commands.IGT:
                if (tar.indexOfFirstOperand >= 0 && tar.indexOfSecondOperand >= 0)
                    memory[tar.indexInMemory].bool = memory[tar.indexOfFirstOperand].integer > memory[tar.indexOfSecondOperand].integer;
                break;
            case ThreeAddressCode.Commands.RGT:
                if (tar.indexOfFirstOperand >= 0 && tar.indexOfSecondOperand >= 0)
                    memory[tar.indexInMemory].bool = memory[tar.indexOfFirstOperand].real > memory[tar.indexOfSecondOperand].real;
                break;

            case ThreeAddressCode.Commands.ILEQ:
                if (tar.indexOfFirstOperand >= 0 && tar.indexOfSecondOperand >= 0)
                    memory[tar.indexInMemory].bool = memory[tar.indexOfFirstOperand].integer <= memory[tar.indexOfSecondOperand].integer;
                break;
            case ThreeAddressCode.Commands.RLEQ:
                if (tar.indexOfFirstOperand >= 0 && tar.indexOfSecondOperand >= 0)
                    memory[tar.indexInMemory].bool = memory[tar.indexOfFirstOperand].real <= memory[tar.indexOfSecondOperand].real;
                break;
            case ThreeAddressCode.Commands.IGEQ:
                if (tar.indexOfFirstOperand >= 0 && tar.indexOfSecondOperand >= 0)
                    memory[tar.indexInMemory].bool = memory[tar.indexOfFirstOperand].integer >= memory[tar.indexOfSecondOperand].integer;
                break;
            case ThreeAddressCode.Commands.RGEQ:
                if (tar.indexOfFirstOperand >= 0 && tar.indexOfSecondOperand >= 0)
                    memory[tar.indexInMemory].bool = memory[tar.indexOfFirstOperand].real >= memory[tar.indexOfSecondOperand].real;
                break;

            case ThreeAddressCode.Commands.IEQ:
                if (tar.indexOfFirstOperand >= 0 && tar.indexOfSecondOperand >= 0) {
                    int val1 = memory[tar.indexOfFirstOperand].integer;
                    int val2 = memory[tar.indexOfSecondOperand].integer;
                    boolean result = val1 == val2;
                    memory[tar.indexInMemory].bool = result;
                }
                break;
            case ThreeAddressCode.Commands.REQ:
                if (tar.indexOfFirstOperand >= 0 && tar.indexOfSecondOperand >= 0)
                    memory[tar.indexInMemory].bool = Math.abs(memory[tar.indexOfFirstOperand].real - memory[tar.indexOfSecondOperand].real) < TOLERANCE;
                break;
            case ThreeAddressCode.Commands.BEQ:
                if (tar.indexOfFirstOperand >= 0 && tar.indexOfSecondOperand >= 0)
                    memory[tar.indexInMemory].bool = memory[tar.indexOfFirstOperand].bool == memory[tar.indexOfSecondOperand].bool;
                break;
            case ThreeAddressCode.Commands.INEQ:
                if (tar.indexOfFirstOperand >= 0 && tar.indexOfSecondOperand >= 0)
                    memory[tar.indexInMemory].bool = memory[tar.indexOfFirstOperand].integer != memory[tar.indexOfSecondOperand].integer;
                break;
            case ThreeAddressCode.Commands.RNEQ:
                if (tar.indexOfFirstOperand >= 0 && tar.indexOfSecondOperand >= 0)
                    memory[tar.indexInMemory].bool = Math.abs(memory[tar.indexOfFirstOperand].real - memory[tar.indexOfSecondOperand].real) >= TOLERANCE;
                break;
            case ThreeAddressCode.Commands.BNEQ:
                if (tar.indexOfFirstOperand >= 0 && tar.indexOfSecondOperand >= 0)
                    memory[tar.indexInMemory].bool = memory[tar.indexOfFirstOperand].bool != memory[tar.indexOfSecondOperand].bool;
                break;

            case ThreeAddressCode.Commands.CONITR:
                if (tar.indexOfFirstOperand >= 0 && tar.indexOfSecondOperand >= 0)
                    memory[tar.indexOfFirstOperand].real = memory[tar.indexOfSecondOperand].integer;
                break;

            case ThreeAddressCode.Commands.CALL:
                if(standardFunctions.containsKey(tar.label)){
                    standardFunctions.get(tar.label).run();
                    if (tar.indexInMemory >= 0) {
                        memory[tar.indexInMemory].integer = 0;
                    }
                }
                else if(labelAddresses.containsKey(tar.label)){
                    callStack.push(programCounter);
                    programCounter = labelAddresses.get(tar.label) - 1;
                }
                else throw new RuntimeException("Function " + tar.label + " not found");
                break;

            case ThreeAddressCode.Commands.PARAM:
                if (tar.indexInMemory >= 0)
                    paramStack.push(memory[tar.indexInMemory]);
                break;

            case ThreeAddressCode.Commands.IIF:
                if (tar.indexInMemory >= 0 && memory[tar.indexInMemory].bool){
                    var address = labelAddresses.get(tar.label);
                    if(address != null)
                        programCounter = address - 1;
                    else throw new RuntimeException("Label " + tar.label + " not found");
                }
                break;
            case ThreeAddressCode.Commands.IFN:
                if (tar.indexInMemory >= 0 && !memory[tar.indexInMemory].bool){
                    var address = labelAddresses.get(tar.label);
                    if(address != null)
                        programCounter = address - 1;
                    else throw new RuntimeException("Label " + tar.label + " not found");
                }
                break;

            case ThreeAddressCode.Commands.PUSH:
                if (tar.indexInMemory >= 0)
                    paramStack.push(memory[tar.indexInMemory]);
                break;
            case ThreeAddressCode.Commands.POP:
                if (paramStack.size() > 0)
                    paramStack.pop();
                break;

            case ThreeAddressCode.Commands.LABEL:
                break;
            case ThreeAddressCode.Commands.GOTO:
                var address = labelAddresses.get(tar.label);
                if(address != null)
                    programCounter = address - 1;
                else throw new RuntimeException("Label " + tar.label + " not found");
                break;
            case ThreeAddressCode.Commands.STOP:
                break;

            default: throw new RuntimeException("Command " + tar.command + " not implemented!");
        }
    }

    public static void startProgram(ArrayList<ThreeAddressCode> program) throws Exception {
        initialize();
        loadProgram(program);
        run();
    }

    private static void executePrintFunction() {
        if (!paramStack.isEmpty()) {
            ValueType value = paramStack.peek();

            if (Math.abs(value.real) > TOLERANCE) {
                outputHandler.accept(String.format("%.6f", value.real));
            } else if (value.integer != 0) {
                outputHandler.accept(Integer.toString(value.integer));
            } else {
                outputHandler.accept(Boolean.toString(value.bool));
            }
        } else {
            outputHandler.accept("No value to print");
        }
    }

    public static void memoryDump(int count) {
        System.out.println("Memory Dump:");
        for (int i = 0; i < Math.min(count, memory.length); i++) {
            if (memory[i].integer != 0 || Math.abs(memory[i].real) > TOLERANCE || memory[i].bool) {
                System.out.printf("Mem[%d] = i:%d, r:%.6f, b:%s%n",
                        i, memory[i].integer, memory[i].real, memory[i].bool);
            }
        }
    }
}