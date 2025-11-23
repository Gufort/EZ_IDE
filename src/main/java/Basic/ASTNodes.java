package Basic;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

public abstract class ASTNodes {
    public interface IVisitor<T>{
        T visitNode(Node bin)  throws Exception ;
        T visitExprNode(ExprNode bin) throws Exception;
        T visitStatementNode(StatementNode bin) throws Exception;
        T visitBinOp(BinOpNode bin) throws Exception;
        T visitStatementList(StatementListNode stl) throws Exception;
        T visitExprList(ExprListNode exlist) throws Exception;
        T visitInt(IntNode n) throws Exception;
        T visitBigInt(BigIntNode n) throws Exception;
        T visitDouble(DoubleNode d) throws Exception;
        T visitId(IdNode id) throws Exception;
        T visitAssign(AssignNode ass) throws Exception;
        T visitAssignOperation(AssignOperationNode ass) throws Exception;
        T visitIf(IfNode ifn) throws Exception;
        T visitWhile(WhileNode whl) throws Exception;
        T visitFor(ForNode forn) throws Exception;
        T visitProcCall(ProcCallNode p) throws Exception;
        T visitFuncCall(FuncCallNode f) throws Exception;
    }

    public interface IVisitorP{
        void visitNode(Node bin) throws Exception ;
        void visitExprNode(ExprNode bin) throws Exception ;
        void visitStatementNode(StatementNode bin) throws Exception ;
        void visitBinOp(BinOpNode bin) throws Exception;
        void visitStatementList(StatementListNode stl) throws Exception ;
        void visitExprList(ExprListNode exlist) throws Exception ;
        void visitInt(IntNode n) throws Exception ;
        void visitBigInt(BigIntNode n) throws Exception ;
        void visitDouble(DoubleNode d) throws Exception ;
        void visitId(IdNode id) throws Exception;
        void visitAssign(AssignNode ass) throws Exception;
        void visitAssignOperation(AssignOperationNode ass) throws Exception;
        void visitIf(IfNode ifn) throws Exception;
        void visitFor(ForNode forn) throws Exception;
        void visitWhile(WhileNode whn) throws Exception;
        void visitProcCall(ProcCallNode p) throws Exception;
        void visitFuncCall(FuncCallNode f) throws Exception;
    }
    
    public static abstract class Node{
        public Position position;
        public abstract <T> T visit(IVisitor<T> v) throws Exception ;
        public abstract void visitP(IVisitorP v) throws Exception;
    }

    public static abstract class ExprNode extends Node{
        public Position position;
        @Override
        public <T> T visit(IVisitor<T> v) throws Exception { return v.visitExprNode(this); };
        @Override
        public void visitP(IVisitorP v) throws Exception { v.visitExprNode(this); }
    }

    public static abstract class StatementNode extends Node{
        public Position position;
        @Override
        public <T> T visit(IVisitor<T> v) throws Exception { return v.visitStatementNode(this); };
        @Override
        public void visitP(IVisitorP v) throws Exception { v.visitStatementNode(this); }
        public void setPos(Position pos){
            position = pos;
        }
    }

    public static class BinOpNode extends ExprNode{
        public ExprNode left;
        public ExprNode right;
        public LexerUnit.TokenType op;

        public BinOpNode(ExprNode left, ExprNode right, LexerUnit.TokenType op, Position position) {
            this.left = left;
            this.right = right;
            this.op = op;
            this.position = position;
        }

        public String operationToString(){
            switch(op){
                case PLUS: return "+";
                case MINUS: return "-";
                case MULTIPLE: return "*";
                case DIVIDE: return "/";
                case LESS: return "<";
                case LESSEQUAL: return "<=";
                case GREATER: return ">";
                case GREATEREQUAL: return ">=";
                case EQUAL: return "==";
                case NOTEQUAL: return "!=";
                case AND: return "&&";
                case OR: return "||";
                default: return "";
            }
        }

        @Override
        public <T> T visit(IVisitor<T> v) throws Exception{return v.visitBinOp(this); };
        @Override
        public void visitP(IVisitorP v) throws Exception { v.visitBinOp(this); }
        @Override
        public String toString() {
            return "(" + op + ",(" + left + "),(" + right + "))";
        }
    }

    public static class StatementListNode extends StatementNode{
        public ArrayList<StatementNode> statements = new ArrayList<StatementNode>();
        public void add(StatementNode statement){ statements.add(statement); }
        @Override
        public <T> T visit(IVisitor<T> v) throws Exception { return v.visitStatementList(this); };
        @Override
        public void visitP(IVisitorP v) throws Exception { v.visitStatementList(this); }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (int i = 0; i < statements.size(); i++) {
                if (i > 0) sb.append(",");
                sb.append(statements.get(i).toString());
            }
            sb.append("]");
            return sb.toString();
        }
    }

    public static class ExprListNode extends Node{
        public ArrayList<ExprNode> lst = new ArrayList<ExprNode>();
        public void add(ExprNode expr){ lst.add(expr); }
        @Override
        public <T> T visit(IVisitor<T> v) throws Exception { return v.visitExprList(this); };
        @Override
        public void visitP(IVisitorP v) throws Exception { v.visitExprList(this); }
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (int i = 0; i < lst.size(); i++) {
                if (i > 0) sb.append(",");
                sb.append("(" + lst.get(i) + ")");
            }
            sb.append("]");
            return sb.toString();
        }
    }

    public static class IntNode extends ExprNode{
        public int value;
        public Position position;

        public IntNode(int value, Position position) {
            this.value = value;
            this.position = position;
        }

        public IntNode(int value) { this.value = value; }

        @Override
        public <T> T visit(IVisitor<T> v) throws Exception { return v.visitInt(this); }

        @Override
        public void visitP(IVisitorP v) throws Exception { v.visitInt(this); }

        @Override
        public String toString() { return String.valueOf(value); }
    }

    public static class DoubleNode extends ExprNode{
        public double value;
        public Position position;

        public DoubleNode(double value, Position position) {
            this.value = value;
            this.position = position;
        }

        public DoubleNode(double value) throws Exception { this.value = value; }

        @Override
        public <T> T visit(IVisitor<T> v) throws Exception { return v.visitDouble(this); }

        @Override
        public void visitP(IVisitorP v) throws Exception { v.visitDouble(this); }

        @Override
        public String toString() { return String.valueOf(value); }
    }

    public static class BigIntNode extends ExprNode{
        public String value;
        public Position position;

        public BigIntNode(String value, Position position) {
            this.value = value;
            this.position = position;
        }

        public BigIntNode(String value) throws Exception { this.value = value; }

        @Override
        public <T> T visit(IVisitor<T> v) throws Exception { return v.visitBigInt(this); }

        @Override
        public void visitP(IVisitorP v) throws Exception { v.visitBigInt(this); }

        @Override
        public String toString() { return value.toString(); }
    }

    public static class IdNode extends ExprNode{
        public String name;
        public int ind; // индекс в таблице varValues

        public IdNode(String name, Position position) {
            this.name = name;
            this.position = position;
        }

        public IdNode(String name) { this.name = name; }

        @Override
        public <T> T visit(IVisitor<T> v) throws Exception { return v.visitId(this); }

        @Override
        public void visitP(IVisitorP v) throws Exception { v.visitId(this); }

        @Override
        public String toString() { return name; }
    }

    public static class AssignNode extends StatementNode{
        public IdNode id;
        public ExprNode expr;

        public AssignNode(IdNode id, ExprNode expr, Position position) {
            this.id = id;
            this.expr = expr;
            this.position = position;
        }
        @Override
        public <T> T visit(IVisitor<T> v) throws Exception { return v.visitAssign(this); }

        @Override
        public void visitP(IVisitorP v) throws Exception { v.visitAssign(this); }

        @Override
        public String toString() {
            return "((" + expr + "),(" + id + "))";
        }
    }

    public static class AssignOperationNode extends StatementNode{
        public IdNode id;
        public ExprNode expr;
        public char op;

        public AssignOperationNode(IdNode id, ExprNode expr, char op, Position position) {
            this.id = id;
            this.expr = expr;
            this.op = op;
            this.position = position;
        }

        @Override
        public <T> T visit(IVisitor<T> v) throws Exception { return v.visitAssignOperation(this); }

        @Override
        public void visitP(IVisitorP v) throws Exception { v.visitAssignOperation(this); }

        @Override
        public String toString() {
            return "((+=,(" + expr + "),(" + id + ")))";
        }
    }

    public static class IfNode extends StatementNode{
        public ExprNode cond;
        public StatementNode then;
        public StatementNode elseif;

        public IfNode(ExprNode cond, StatementNode then, StatementNode elseif, Position position) {
            this.cond = cond;
            this.then = then;
            this.elseif = elseif;
            this.position = position;
        }

        @Override
        public <T> T visit(IVisitor<T> v) throws Exception { return v.visitIf(this); }

        @Override
        public void visitP(IVisitorP v) throws Exception { v.visitIf(this); }

        @Override
        public String toString() {
            if (elseif != null) {
                return "(if,(" + cond + "),(" + then + "),(" + elseif + "))";
            } else {
                return "(if,(" + cond + "),(" + then + "))";
            }
        }
    }

    public static class ForNode extends StatementNode{
        public ExprNode condition;
        public StatementNode body;
        public StatementNode start;
        public StatementNode increment;

        public ForNode(StatementNode start, ExprNode condition, StatementNode increment, StatementNode body, Position position) {
            this.condition = condition;
            this.body = body;
            this.start = start;
            this.increment = increment;
            this.position = position;
        }

        @Override
        public <T> T visit(IVisitor<T> v) throws Exception { return v.visitFor(this); }
        @Override
        public void visitP(IVisitorP v) throws Exception { v.visitFor(this); }

        @Override
        public String toString() {
            return "(" + start + ";" + condition + ";" + increment + ")" + "((" + body + "))";
        }
    }

    public static class WhileNode extends StatementNode{
        public ExprNode cond;
        public StatementNode stat;

        public WhileNode(ExprNode cond, StatementNode stat, Position position) {
            this.cond = cond;
            this.stat = stat;
            this.position = position;
        }

        @Override
        public <T> T visit(IVisitor<T> v) throws Exception { return v.visitWhile(this); }

        @Override
        public void visitP(IVisitorP v) throws Exception { v.visitWhile(this); }

        @Override
        public String toString() {
            return "((" + stat + "),(" + cond + "))";
        }
    }

    public static class ProcCallNode extends StatementNode{
        public IdNode name;
        public ExprListNode pars;

        public ProcCallNode(IdNode name, ExprListNode pars, Position position) {
            this.name = name;
            this.pars = pars;
            this.position = position;
        }


        @Override
        public <T> T visit(IVisitor<T> v) throws Exception{ return v.visitProcCall(this); }

        @Override
        public void visitP(IVisitorP v) throws Exception { v.visitProcCall(this); }

        @Override
        public String toString() {
            return "((" + pars + "),(" + name + "))";
        }
    }

    public static class FuncCallNode extends ExprNode{
        public IdNode name;
        public ExprListNode pars;

        public FuncCallNode(IdNode name, ExprListNode pars, Position position) {
            this.name = name;
            this.pars = pars;
            this.position = position;
        }

        @Override
        public <T> T visit(IVisitor<T> v) throws Exception { return v.visitFuncCall(this); }

        @Override
        public void visitP(IVisitorP v) throws Exception { v.visitFuncCall(this); }

        @Override
        public String toString() { return "((" + pars + "),(" + name + "))"; }
    }
}
