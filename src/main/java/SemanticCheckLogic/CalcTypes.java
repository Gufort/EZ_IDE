package SemanticCheckLogic;

import Basic.ASTNodes;
import Basic.LexerUnit;
import ExceptionLogic.CompilerException;

import java.util.Arrays;

public class CalcTypes {
    public static boolean assignComparable(SymbolTable.SemanticType left, SymbolTable.SemanticType right) {
        if(left == right)
            return true;
        else if(left == SymbolTable.SemanticType.DoubleType && right == SymbolTable.SemanticType.IntType)
            return true;
        else if(left == SymbolTable.SemanticType.ObjectType && right != SymbolTable.SemanticType.NoType
                && right != SymbolTable.SemanticType.BadType)
            return true;
        return false;
    }

    public static class CalcTypeVisitor implements ASTNodes.IVisitor<SymbolTable.SemanticType>{
        private SymbolTable.SemanticType calcTypeVisitor(ASTNodes.ExprNode expr) throws Exception{
            return expr.visit(new CalcTypeVisitor());
        }

        @Override public SymbolTable.SemanticType visitNode(ASTNodes.Node node) throws Exception { return SymbolTable.SemanticType.NoType; }
        @Override public SymbolTable.SemanticType visitExprNode(ASTNodes.ExprNode node) throws Exception {  return SymbolTable.SemanticType.NoType; }
        @Override public SymbolTable.SemanticType visitStatementNode(ASTNodes.StatementNode node) throws Exception {   return SymbolTable.SemanticType.NoType; }

        @Override public SymbolTable.SemanticType visitBinOp(ASTNodes.BinOpNode node) throws Exception{
            var left = node.left.visit(this);
            var right = node.right.visit(this);

            if(Arrays.stream(LexerUnit.ArithmeticOperations).anyMatch(op -> op == node.op)){
                if(!Arrays.stream(SymbolTable.NumTypes).anyMatch(l -> l == left) ||
                !Arrays.stream(SymbolTable.NumTypes).anyMatch(l -> l == right))
                    CompilerException.semanticError("Операция " + node.operationToString() + " не определена для типов " + left + " и " + right, node.left.position);
                else if(node.op == LexerUnit.TokenType.DIVIDE)
                    return SymbolTable.SemanticType.DoubleType;
                else if(left == right)
                    return left;
                return SymbolTable.SemanticType.DoubleType;
            }

            else if(Arrays.stream(LexerUnit.LogicalOperators).anyMatch(op -> op == node.op)){
                if(left != SymbolTable.SemanticType.BoolType || right != SymbolTable.SemanticType.BoolType)
                    CompilerException.semanticError("Операция " + node.operationToString() + " не определена для типов " + left + " и " + right, node.left.position);
                return SymbolTable.SemanticType.BoolType;
            }

            else if(Arrays.stream(LexerUnit.CompareOperations).anyMatch(op -> op == node.op)){
                if(!Arrays.stream(SymbolTable.NumTypes).anyMatch(l -> l == left) ||
                !Arrays.stream(SymbolTable.NumTypes).anyMatch(l -> l == right))
                    CompilerException.semanticError("Операция " + node.operationToString() + " не определена для типов " + left + " и " + right, node.left.position);
                return SymbolTable.SemanticType.BoolType;
            }
            return null;
        }

        @Override public SymbolTable.SemanticType visitStatementList(ASTNodes.StatementListNode node) throws Exception {
            return SymbolTable.SemanticType.NoType;
        }
        @Override public SymbolTable.SemanticType visitExprList(ASTNodes.ExprListNode node) throws Exception {
            return SymbolTable.SemanticType.NoType;
        }
        @Override public SymbolTable.SemanticType visitInt(ASTNodes.IntNode node) throws Exception {
            return SymbolTable.SemanticType.IntType;
        }
        @Override public SymbolTable.SemanticType visitDouble(ASTNodes.DoubleNode node) {
            return SymbolTable.SemanticType.DoubleType;
        }
        @Override public SymbolTable.SemanticType visitBigInt(ASTNodes.BigIntNode node) throws Exception {
            return SymbolTable.SemanticType.IntType;
        }
        @Override public SymbolTable.SemanticType visitId(ASTNodes.IdNode node) throws Exception{
            if(SymbolTable.SymTable.get(node.name) == null)
                CompilerException.semanticError("Идентификатор " + node.name + " не определен", node.position);
            return SymbolTable.SymTable.get(node.name).semanticType;
        }
        @Override public SymbolTable.SemanticType visitAssign(ASTNodes.AssignNode node) {
            return SymbolTable.SemanticType.NoType;
        }
        @Override public SymbolTable.SemanticType visitAssignOperation(ASTNodes.AssignOperationNode node) {
            return SymbolTable.SemanticType.NoType;
        }
        @Override public SymbolTable.SemanticType visitIf(ASTNodes.IfNode node) {
            return SymbolTable.SemanticType.NoType;
        }
        @Override public SymbolTable.SemanticType visitWhile(ASTNodes.WhileNode node) {
            return SymbolTable.SemanticType.NoType;
        }
        @Override
        public SymbolTable.SemanticType visitFor(ASTNodes.ForNode forn) throws Exception {
            return SymbolTable.SemanticType.NoType;
        }
        @Override public SymbolTable.SemanticType visitProcCall(ASTNodes.ProcCallNode node) throws Exception{
            if(SymbolTable.SymTable.get(node.name) == null)
                CompilerException.semanticError("Функция с именем " + node.name.name + " не определена", node.name.position);
            var sym =  SymbolTable.SymTable.get(node.name.name);
            if(sym.kindType != SymbolTable.KindType.FuncName)
                CompilerException.semanticError("Данное имя " + node.name.name + " не является именем функции",  node.name.position);
            if(sym.semanticType != SymbolTable.SemanticType.NoType)
                CompilerException.semanticError("Попытка вызвать функцию " + node.name.name + " как процедуру", node.name.position);
            if(sym.params.length != node.pars.lst.size())
                CompilerException.semanticError("Несоответствие количества параметров при вызове процедуры" + node.name.name, node.name.position);

            for(int i = 0; i < sym.params.length; i++){
                var tp = calcTypeVis(node.pars.lst.get(i));
                if(!assignComparable(sym.params[i], tp))
                    CompilerException.semanticError("Тип аргумента функции " + node.name.name + 
                            " не соответствует типу формального параметра ", node.name.position);
            }
            return SymbolTable.SemanticType.NoType;
        }
        @Override public SymbolTable.SemanticType visitFuncCall(ASTNodes.FuncCallNode node) throws Exception{
            if(SymbolTable.SymTable.get(node.name) == null)
                CompilerException.semanticError("Функция с именем " + node.name.name + " не определена", node.name.position);
            var sym =  SymbolTable.SymTable.get(node.name.name);
            if(sym.kindType != SymbolTable.KindType.FuncName)
                CompilerException.semanticError("Данное имя " + node.name.name + " не является именем функции",  node.name.position);
            if(sym.semanticType == SymbolTable.SemanticType.NoType)
                CompilerException.semanticError("Попытка вызвать процедуру " + node.name.name + " как функцию", node.name.position);
            if(sym.params.length != node.pars.lst.size())
                CompilerException.semanticError("Несоответствие количества параметров при вызове процедуры" + node.name.name, node.name.position);

            for(int i = 0; i < sym.params.length; i++){
                var tp = calcTypeVis(node.pars.lst.get(i));
                if(!assignComparable(sym.params[i], tp))
                    CompilerException.semanticError("Тип аргумента функции " + node.name.name +
                            " не соответствует типу формального параметра ", node.name.position);
            }
            return sym.semanticType;
        }
    }

    public static SymbolTable.SemanticType calcTypeVis(ASTNodes.ExprNode expr) throws Exception{
        return expr.visit(new CalcTypeVisitor());
    }

    public static SymbolTable.SemanticType calcType(ASTNodes.ExprNode expr) throws Exception{
        return switch (expr){
            case ASTNodes.IdNode id -> checkSymbolTable(id.name);
            case ASTNodes.IntNode node -> SymbolTable.SemanticType.IntType;
            case ASTNodes.DoubleNode node -> SymbolTable.SemanticType.DoubleType;
            case ASTNodes.BinOpNode bin-> {
                var left = calcType(bin.left);
                var right = calcType(bin.right);
                if(Arrays.stream(LexerUnit.ArithmeticOperations).anyMatch(op -> op == bin.op)){
                    if(Arrays.stream(SymbolTable.NumTypes).anyMatch(l -> l == left) ||
                    Arrays.stream(SymbolTable.NumTypes).anyMatch(l -> l == right))
                        yield  SymbolTable.SemanticType.BadType;
                    else if(bin.op == LexerUnit.TokenType.DIVIDE)
                        yield  SymbolTable.SemanticType.NoType;
                    else if (left == right)
                        yield left;
                    yield SymbolTable.SemanticType.DoubleType;
                }
                else if(Arrays.stream(LexerUnit.LogicalOperators).anyMatch(op -> op == bin.op)){
                    if(left != SymbolTable.SemanticType.BoolType ||
                    right != SymbolTable.SemanticType.BoolType)
                        yield  SymbolTable.SemanticType.BadType;
                    yield  SymbolTable.SemanticType.BoolType;
                }
                else if(Arrays.stream(LexerUnit.CompareOperations).anyMatch(op -> op == bin.op)){
                    if(!Arrays.stream(SymbolTable.NumTypes).anyMatch(l -> l == left) ||
                    !Arrays.stream(SymbolTable.NumTypes).anyMatch(l -> l == right))
                        yield SymbolTable.SemanticType.BadType;
                    yield  SymbolTable.SemanticType.BoolType;
                }
                yield null;
            }
            default -> null;
        };
    }

    public static SymbolTable.SemanticType checkSymbolTable(String name) {
        return SymbolTable.SymTable.get(name) != null?
                SymbolTable.SymTable.get(name).semanticType :
                SymbolTable.SemanticType.BadType;
    }
}
