package SemanticCheckLogic;

import Basic.ASTNodes;

public class SemanticVisitorUnit {

    public interface AbstractVisitor<T> {
        T visitBinOp(ASTNodes.BinOpNode bin) throws Exception;
        T visitStatementList(ASTNodes.StatementListNode stl) throws Exception;
        T visitExprList(ASTNodes.ExprListNode exlist) throws Exception;
        T visitInt(ASTNodes.IntNode n) throws Exception;
        T visitDouble(ASTNodes.DoubleNode d) throws Exception;
        T visitId(ASTNodes.IdNode id) throws Exception;
        T visitAssign(ASTNodes.AssignNode ass) throws Exception;
        T visitAssignOperation(ASTNodes.AssignOperationNode ass) throws Exception;
        T visitIf(ASTNodes.IfNode ifn) throws Exception;
        T visitWhile(ASTNodes.WhileNode whn) throws Exception;
        T visitFor(ASTNodes.ForNode forn) throws Exception;
        T visitProcCall(ASTNodes.ProcCallNode p) throws Exception;
        T visitFuncCall(ASTNodes.FuncCallNode f) throws Exception;
        T visitNode(ASTNodes.Node n) throws Exception;
        T visitExprNode(ASTNodes.ExprNode ex) throws Exception;
        T visitStatementNode(ASTNodes.StatementNode st) throws Exception;
    }
}