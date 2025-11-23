package Interpret;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import Interpret.*;


public class InterpretTree {
    public static abstract class NodeI {
    }

    public static abstract class ExprNodeI extends NodeI {
        public int evalInt() { return 0; }
        public double evalReal() { return 0.0; }
        public boolean evalBool() { return false; }
        public BigInteger evalBigInteger() { return new BigInteger("0"); }
    }

    public static abstract class StatementNodeI extends NodeI {
        public void execute() {}
    }

    public static abstract class BinOpNodeI extends ExprNodeI {
        public ExprNodeI left;
        public ExprNodeI right;

        public BinOpNodeI(ExprNodeI left, ExprNodeI right) {
            this.left = left;
            this.right = right;
        }
    }

    // Бинарные операции
    public static class PlusII extends BinOpNodeI {
        public PlusII(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public int evalInt() { return left.evalInt() + right.evalInt(); }
    }

    public static class PlusIR extends BinOpNodeI {
        public PlusIR(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public double evalReal() { return left.evalInt() + right.evalReal(); }
    }

    public static class PlusRI extends BinOpNodeI {
        public PlusRI(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public double evalReal() { return left.evalReal() + right.evalInt(); }
    }

    public static class PlusRR extends BinOpNodeI {
        public PlusRR(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public double evalReal() { return left.evalReal() + right.evalReal(); }
    }

    public static class PlusIC extends BinOpNodeI {
        public int value;
        public PlusIC(ExprNodeI left, int value) {
            super(left, null);
            this.value = value;
        }
        @Override public int evalInt() { return left.evalInt() + value; }
    }

    public static class PlusRC extends BinOpNodeI {
        public double value;
        public PlusRC(ExprNodeI left, double value) {
            super(left, null);
            this.value = value;
        }
        @Override public double evalReal() { return left.evalReal() + value; }
    }

    public static class MinusII extends BinOpNodeI {
        public MinusII(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public int evalInt() { return left.evalInt() - right.evalInt(); }
    }

    public static class MinusIR extends BinOpNodeI {
        public MinusIR(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public double evalReal() { return left.evalInt() - right.evalReal(); }
    }

    public static class MinusRI extends BinOpNodeI {
        public MinusRI(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public double evalReal() { return left.evalReal() - right.evalInt(); }
    }

    public static class MinusRR extends BinOpNodeI {
        public MinusRR(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public double evalReal() { return left.evalReal() - right.evalReal(); }
    }

    public static class MultII extends BinOpNodeI {
        public MultII(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public int evalInt() { return left.evalInt() * right.evalInt(); }
    }

    public static class MultIR extends BinOpNodeI {
        public MultIR(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public double evalReal() { return left.evalInt() * right.evalReal(); }
    }

    public static class MultRI extends BinOpNodeI {
        public MultRI(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public double evalReal() { return left.evalReal() * right.evalInt(); }
    }

    public static class MultRR extends BinOpNodeI {
        public MultRR(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public double evalReal() { return left.evalReal() * right.evalReal(); }
    }

    public static class DivII extends BinOpNodeI {
        public DivII(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public double evalReal() { return (double) left.evalInt() / right.evalInt(); }
    }

    public static class DivIR extends BinOpNodeI {
        public DivIR(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public double evalReal() { return left.evalInt() / right.evalReal(); }
    }

    public static class DivRI extends BinOpNodeI {
        public DivRI(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public double evalReal() { return left.evalReal() / right.evalInt(); }
    }

    public static class DivRR extends BinOpNodeI {
        public DivRR(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public double evalReal() { return left.evalReal() / right.evalReal(); }
    }

    public static class LessII extends BinOpNodeI {
        public LessII(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalInt() < right.evalInt(); }
    }

    public static class LessIR extends BinOpNodeI {
        public LessIR(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalInt() < right.evalReal(); }
    }

    public static class LessRI extends BinOpNodeI {
        public LessRI(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalReal() < right.evalInt(); }
    }

    public static class LessRR extends BinOpNodeI {
        public LessRR(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalReal() < right.evalReal(); }
    }

    public static class GreaterII extends BinOpNodeI {
        public GreaterII(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalInt() > right.evalInt(); }
    }

    public static class GreaterIR extends BinOpNodeI {
        public GreaterIR(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalInt() > right.evalReal(); }
    }

    public static class GreaterRI extends BinOpNodeI {
        public GreaterRI(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalReal() > right.evalInt(); }
    }

    public static class GreaterRR extends BinOpNodeI {
        public GreaterRR(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalReal() > right.evalReal(); }
    }

    public static class LessEqII extends BinOpNodeI {
        public LessEqII(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalInt() <= right.evalInt(); }
    }

    public static class LessEqIR extends BinOpNodeI {
        public LessEqIR(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalInt() <= right.evalReal(); }
    }

    public static class LessEqRI extends BinOpNodeI {
        public LessEqRI(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalReal() <= right.evalInt(); }
    }

    public static class LessEqRR extends BinOpNodeI {
        public LessEqRR(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalReal() <= right.evalReal(); }
    }

    public static class GreaterEqII extends BinOpNodeI {
        public GreaterEqII(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalInt() >= right.evalInt(); }
    }

    public static class GreaterEqIR extends BinOpNodeI {
        public GreaterEqIR(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalInt() >= right.evalReal(); }
    }

    public static class GreaterEqRI extends BinOpNodeI {
        public GreaterEqRI(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalReal() >= right.evalInt(); }
    }

    public static class GreaterEqRR extends BinOpNodeI {
        public GreaterEqRR(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalReal() >= right.evalReal(); }
    }

    public static class EqII extends BinOpNodeI {
        public EqII(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalInt() == right.evalInt(); }
    }

    public static class EqIR extends BinOpNodeI {
        public EqIR(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalInt() == right.evalReal(); }
    }

    public static class EqRI extends BinOpNodeI {
        public EqRI(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalReal() == right.evalInt(); }
    }

    public static class EqRR extends BinOpNodeI {
        public EqRR(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalReal() == right.evalReal(); }
    }

    public static class EqBB extends BinOpNodeI {
        public EqBB(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalBool() == right.evalBool(); }
    }

    public static class NotEqII extends BinOpNodeI {
        public NotEqII(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalInt() != right.evalInt(); }
    }

    public static class NotEqIR extends BinOpNodeI {
        public NotEqIR(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalInt() != right.evalReal(); }
    }

    public static class NotEqRI extends BinOpNodeI {
        public NotEqRI(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalReal() != right.evalInt(); }
    }

    public static class NotEqRR extends BinOpNodeI {
        public NotEqRR(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalReal() != right.evalReal(); }
    }

    public static class NotEqBB extends BinOpNodeI {
        public NotEqBB(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() { return left.evalBool() != right.evalBool(); }
    }

    // Контейнеры
    public static class StatementListNodeI extends StatementNodeI {
        public List<StatementNodeI> lst = new ArrayList<>();

        public void add(StatementNodeI st) { lst.add(st); }

        @Override
        public void execute() {
            for (StatementNodeI statement : lst) {
                statement.execute();
            }
        }
    }

    public static class ExprListNodeI extends NodeI {
        public ArrayList<ExprNodeI> lst = new ArrayList<>();

        public void add(ExprNodeI ex) { lst.add(ex); }
    }

    // Литералы
    public static class IntNodeI extends ExprNodeI {
        public int val;
        public IntNodeI(int value) { this.val = value; }
        @Override public int evalInt() { return val; }
    }

    public static class DoubleNodeI extends ExprNodeI {
        public double val;
        public DoubleNodeI(double value) { this.val = value; }
        @Override public double evalReal() { return val; }
    }

    // Идентификаторы
    public static class IdNodeI extends ExprNodeI {
        public int address;
        public IdNodeI(int address) { this.address = address; }
        @Override public int evalInt() { return Memory.getInt(address); }
    }

    public static class IdNodeR extends ExprNodeI {
        public int address;
        public IdNodeR(int address) { this.address = address; }
        @Override public double evalReal() { return Memory.getDouble(address); }
    }

    public static class IdNodeB extends ExprNodeI {
        public int address;
        public IdNodeB(int address) { this.address = address; }
        @Override public boolean evalBool() { return Interpret.Memory.getBoolean(address); }
    }

    // Операторы присваивания
    public static class AssignIntNodeI extends StatementNodeI {
        public int address;
        public ExprNodeI expr;

        public AssignIntNodeI(int address, ExprNodeI expr) {
            this.address = address;
            this.expr = expr;
        }

        @Override
        public void execute() {
            Memory.setInt(address, expr.evalInt());
        }
    }

    public static class AssignRealNodeI extends StatementNodeI {
        public int address;
        public ExprNodeI expr;

        public AssignRealNodeI(int address, ExprNodeI expr) {
            this.address = address;
            this.expr = expr;
        }

        @Override
        public void execute() {
            Memory.setDouble(address, expr.evalReal());
        }
    }

    public static class AssignBoolNodeI extends StatementNodeI {
        public int address;
        public ExprNodeI expr;

        public AssignBoolNodeI(int address, ExprNodeI expr) {
            this.address = address;
            this.expr = expr;
        }

        @Override
        public void execute() {
            Memory.setBoolean(address, expr.evalBool());
        }
    }

    // Присваивание с константами
    public static class AssignIntCNodeI extends StatementNodeI {
        public int address;
        public int val;

        public AssignIntCNodeI(int address, int val) {
            this.address = address;
            this.val = val;
        }

        @Override
        public void execute() {
            Memory.setInt(address, val);
        }
    }

    public static class AssignRealCNodeI extends StatementNodeI {
        public int address;
        public double val;

        public AssignRealCNodeI(int address, double val) {
            this.address = address;
            this.val = val;
        }

        @Override
        public void execute() {
            Memory.setDouble(address, val);
        }
    }

    public static class AssignRealIntCNodeI extends StatementNodeI {
        public int address;
        public int val;

        public AssignRealIntCNodeI(int address, int val) {
            this.address = address;
            this.val = val;
        }

        @Override
        public void execute() {
            Memory.setDouble(address, (double) val);
        }
    }

    // Составные операторы присваивания
    public static class AssignPlusIntNodeI extends StatementNodeI {
        public int address;
        public ExprNodeI expr;

        public AssignPlusIntNodeI(int address, ExprNodeI expr) {
            this.address = address;
            this.expr = expr;
        }

        @Override
        public void execute() {
            int current = Memory.getInt(address);
            Memory.setInt(address, current + expr.evalInt());
        }
    }

    public static class AssignPlusRealNodeI extends StatementNodeI {
        public int address;
        public ExprNodeI expr;

        public AssignPlusRealNodeI(int address, ExprNodeI expr) {
            this.address = address;
            this.expr = expr;
        }

        @Override
        public void execute() {
            double current = Memory.getDouble(address);
            Memory.setDouble(address, current + expr.evalReal());
        }
    }

    public static class AssignPlusIntCNodeI extends StatementNodeI {
        public int address;
        public int val;

        public AssignPlusIntCNodeI(int address, int val) {
            this.address = address;
            this.val = val;
        }

        @Override
        public void execute() {
            int current = Memory.getInt(address);
            Memory.setInt(address, current + val);
        }
    }

    public static class AssignPlusRealCNodeI extends StatementNodeI {
        public int address;
        public double val;

        public AssignPlusRealCNodeI(int address, double val) {
            this.address = address;
            this.val = val;
        }

        @Override
        public void execute() {
            double current = Memory.getDouble(address);
            Memory.setDouble(address, current + val);
        }
    }

    public static class AssignPlusRealIntCNodeI extends StatementNodeI {
        public int address;
        public int val;

        public AssignPlusRealIntCNodeI(int address, int val) {
            this.address = address;
            this.val = val;
        }

        @Override
        public void execute() {
            double current = Memory.getDouble(address);
            Memory.setDouble(address, current + val);
        }
    }

    // Управляющие конструкции
    public static class IfNodeI extends StatementNodeI {
        public ExprNodeI condition;
        public StatementNodeI thenStat;
        public StatementNodeI elseStat;

        public IfNodeI(ExprNodeI condition, StatementNodeI thenStat, StatementNodeI elseStat) {
            this.condition = condition;
            this.thenStat = thenStat;
            this.elseStat = elseStat;
        }

        @Override
        public void execute() {
            if (condition.evalBool()) {
                thenStat.execute();
            } else if (elseStat != null) {
                elseStat.execute();
            }
        }
    }

    public static class WhileNodeI extends StatementNodeI {
        public ExprNodeI condition;
        public StatementNodeI stat;

        public WhileNodeI(ExprNodeI condition, StatementNodeI stat) {
            this.condition = condition;
            this.stat = stat;
        }

        @Override
        public void execute() {
            while (condition.evalBool()) {
                stat.execute();
            }
        }
    }

    public static class ForNodeI extends StatementNodeI {
        public StatementNodeI start;
        public ExprNodeI condition;
        public StatementNodeI increment;
        public StatementNodeI body;

        public ForNodeI(StatementNodeI start, ExprNodeI condition, StatementNodeI increment, StatementNodeI body) {
            this.start = start;
            this.condition = condition;
            this.increment = increment;
            this.body = body;
        }

        @Override
        public void execute() {
            start.execute();
            while (condition.evalBool()) {
                body.execute();
                increment.execute();
            }
        }
    }

    public static class ProcCallNodeI extends StatementNodeI {
        public String name;
        public ExprListNodeI pars;

        public ProcCallNodeI(String name, ExprListNodeI pars) {
            this.name = name;
            this.pars = pars;
        }

        @Override
        public void execute() {
            if ("print".equals(name) && pars != null) {
                for (ExprNodeI expr : pars.lst) {
                    // Пробуем разные типы по порядку (добавить BigInteger)
                    try {
                        System.out.print(expr.evalInt());
                    } catch (Exception e1) {
                        try {
                            System.out.print(expr.evalReal());
                        } catch (Exception e2) {
                            try {
                                System.out.print(expr.evalBool());
                            } catch (Exception e3) {
                                try {
                                    System.out.print(expr.evalBigInteger()); // ДОБАВИТЬ
                                } catch (Exception e4) {
                                    System.out.print("?");
                                }
                            }
                        }
                    }
                    System.out.print(" ");
                }
                System.out.println();
            }
        }
    }





    public static class BigIntegerNodeI extends ExprNodeI {
        public BigInteger val;
        public BigIntegerNodeI(BigInteger value) { this.val = value; }
        public BigIntegerNodeI(String value) { this.val = new BigInteger(value); }
        @Override public BigInteger evalBigInteger() { return val; }
    }

    // Идентификатор для BigInteger
    public static class IdNodeBI extends ExprNodeI {
        public int address;
        public IdNodeBI(int address) { this.address = address; }
        @Override public BigInteger evalBigInteger() { return Memory.getBigInteger(address); }
    }

    // Бинарные операции для BigInteger
    public static class PlusBIBI extends BinOpNodeI {
        public PlusBIBI(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public BigInteger evalBigInteger() {
            return left.evalBigInteger().add(right.evalBigInteger());
        }
    }

    public static class PlusBIC extends BinOpNodeI {
        public BigInteger value;
        public PlusBIC(ExprNodeI left, BigInteger value) {
            super(left, null);
            this.value = value;
        }
        @Override public BigInteger evalBigInteger() {
            return left.evalBigInteger().add(value);
        }
    }

    public static class MinusBIBI extends BinOpNodeI {
        public MinusBIBI(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public BigInteger evalBigInteger() {
            return left.evalBigInteger().subtract(right.evalBigInteger());
        }
    }

    public static class MinusBIC extends BinOpNodeI {
        public BigInteger value;
        public MinusBIC(ExprNodeI left, BigInteger value) {
            super(left, null);
            this.value = value;
        }
        @Override public BigInteger evalBigInteger() {
            return left.evalBigInteger().subtract(value);
        }
    }

    public static class MultBIBI extends BinOpNodeI {
        public MultBIBI(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public BigInteger evalBigInteger() {
            return left.evalBigInteger().multiply(right.evalBigInteger());
        }
    }

    public static class MultBIC extends BinOpNodeI {
        public BigInteger value;
        public MultBIC(ExprNodeI left, BigInteger value) {
            super(left, null);
            this.value = value;
        }
        @Override public BigInteger evalBigInteger() {
            return left.evalBigInteger().multiply(value);
        }
    }

    public static class DivBIBI extends BinOpNodeI {
        public DivBIBI(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public BigInteger evalBigInteger() {
            return left.evalBigInteger().divide(right.evalBigInteger());
        }
    }

    public static class DivBIC extends BinOpNodeI {
        public BigInteger value;
        public DivBIC(ExprNodeI left, BigInteger value) {
            super(left, null);
            this.value = value;
        }
        @Override public BigInteger evalBigInteger() {
            return left.evalBigInteger().divide(value);
        }
    }

    public static class ModBIBI extends BinOpNodeI {
        public ModBIBI(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public BigInteger evalBigInteger() {
            return left.evalBigInteger().mod(right.evalBigInteger());
        }
    }

    // Операции сравнения для BigInteger
    public static class LessBIBI extends BinOpNodeI {
        public LessBIBI(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() {
            return left.evalBigInteger().compareTo(right.evalBigInteger()) < 0;
        }
    }

    public static class GreaterBIBI extends BinOpNodeI {
        public GreaterBIBI(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() {
            return left.evalBigInteger().compareTo(right.evalBigInteger()) > 0;
        }
    }

    public static class LessEqBIBI extends BinOpNodeI {
        public LessEqBIBI(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() {
            return left.evalBigInteger().compareTo(right.evalBigInteger()) <= 0;
        }
    }

    public static class GreaterEqBIBI extends BinOpNodeI {
        public GreaterEqBIBI(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() {
            return left.evalBigInteger().compareTo(right.evalBigInteger()) >= 0;
        }
    }

    public static class EqBIBI extends BinOpNodeI {
        public EqBIBI(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() {
            return left.evalBigInteger().equals(right.evalBigInteger());
        }
    }

    public static class NotEqBIBI extends BinOpNodeI {
        public NotEqBIBI(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public boolean evalBool() {
            return !left.evalBigInteger().equals(right.evalBigInteger());
        }
    }

    // Присваивание для BigInteger
    public static class AssignBigIntegerNodeI extends StatementNodeI {
        public int address;
        public ExprNodeI expr;

        public AssignBigIntegerNodeI(int address, ExprNodeI expr) {
            this.address = address;
            this.expr = expr;
        }

        @Override
        public void execute() {
            Memory.setBigInteger(address, expr.evalBigInteger());
        }
    }

    public static class AssignBigIntegerCNodeI extends StatementNodeI {
        public int address;
        public BigInteger val;

        public AssignBigIntegerCNodeI(int address, BigInteger val) {
            this.address = address;
            this.val = val;
        }

        @Override
        public void execute() {
            Memory.setBigInteger(address, val);
        }
    }

    // Составные операторы присваивания для BigInteger
    public static class AssignPlusBigIntegerNodeI extends StatementNodeI {
        public int address;
        public ExprNodeI expr;

        public AssignPlusBigIntegerNodeI(int address, ExprNodeI expr) {
            this.address = address;
            this.expr = expr;
        }

        @Override
        public void execute() {
            BigInteger current = Memory.getBigInteger(address);
            Memory.setBigInteger(address, current.add(expr.evalBigInteger()));
        }
    }

    public static class AssignPlusBigIntegerCNodeI extends StatementNodeI {
        public int address;
        public BigInteger val;

        public AssignPlusBigIntegerCNodeI(int address, BigInteger val) {
            this.address = address;
            this.val = val;
        }

        @Override
        public void execute() {
            BigInteger current = Memory.getBigInteger(address);
            Memory.setBigInteger(address, current.add(val));
        }
    }

    public static class AssignMinusBigIntegerNodeI extends StatementNodeI {
        public int address;
        public ExprNodeI expr;

        public AssignMinusBigIntegerNodeI(int address, ExprNodeI expr) {
            this.address = address;
            this.expr = expr;
        }

        @Override
        public void execute() {
            BigInteger current = Memory.getBigInteger(address);
            Memory.setBigInteger(address, current.subtract(expr.evalBigInteger()));
        }
    }

    public static class AssignMultBigIntegerNodeI extends StatementNodeI {
        public int address;
        public ExprNodeI expr;

        public AssignMultBigIntegerNodeI(int address, ExprNodeI expr) {
            this.address = address;
            this.expr = expr;
        }

        @Override
        public void execute() {
            BigInteger current = Memory.getBigInteger(address);
            Memory.setBigInteger(address, current.multiply(expr.evalBigInteger()));
        }
    }

    // Преобразования типов
    public static class IntToBigIntegerNodeI extends ExprNodeI {
        public ExprNodeI expr;
        public IntToBigIntegerNodeI(ExprNodeI expr) { this.expr = expr; }
        @Override public BigInteger evalBigInteger() {
            return BigInteger.valueOf(expr.evalInt());
        }
    }

    public static class BigIntegerToIntNodeI extends ExprNodeI {
        public ExprNodeI expr;
        public BigIntegerToIntNodeI(ExprNodeI expr) { this.expr = expr; }
        @Override public int evalInt() {
            return expr.evalBigInteger().intValue();
        }
    }

    // Специальные функции для BigInteger
    public static class BigIntegerPowNodeI extends BinOpNodeI {
        public BigIntegerPowNodeI(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public BigInteger evalBigInteger() {
            return left.evalBigInteger().pow(right.evalInt());
        }
    }

    public static class BigIntegerGcdNodeI extends BinOpNodeI {
        public BigIntegerGcdNodeI(ExprNodeI left, ExprNodeI right) { super(left, right); }
        @Override public BigInteger evalBigInteger() {
            return left.evalBigInteger().gcd(right.evalBigInteger());
        }
    }


}