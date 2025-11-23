package SemanticCheckLogic;

import Basic.ASTNodes;

public class AutoVisitorUnit implements ASTNodes.IVisitorP {
    @Override public void visitNode(ASTNodes.Node node){}
    @Override public void visitExprNode(ASTNodes.ExprNode node){}
    @Override public void visitStatementNode(ASTNodes.StatementNode node){}
    @Override public void visitInt(ASTNodes.IntNode node){}
    @Override public void visitBigInt(ASTNodes.BigIntNode node){}
    @Override public void visitDouble(ASTNodes.DoubleNode node){}
    @Override public void visitId(ASTNodes.IdNode node) throws Exception{}
    @Override public void visitBinOp(ASTNodes.BinOpNode node) throws Exception{
        node.left.visitP(this);
        node.right.visitP(this);
    }
    @Override public void visitStatementList(ASTNodes.StatementListNode stl) throws Exception {
        for(var curr: stl.statements)
            curr.visitP(this);
    }
    @Override public void visitExprList(ASTNodes.ExprListNode el) throws Exception {
        for(var curr: el.lst)
            curr.visitP(this);
    }
    @Override public void visitAssign (ASTNodes.AssignNode node) throws Exception{
        node.expr.visitP(this);
        node.id.visitP(this);
    }
    @Override public void visitAssignOperation(ASTNodes.AssignOperationNode node) throws Exception{
        node.expr.visitP(this);
        node.id.visitP(this);
    }
    @Override public void visitIf(ASTNodes.IfNode node) throws Exception{
        node.cond.visitP(this);
        node.then.visitP(this);
        if(node.elseif != null)
            node.elseif.visitP(this);
    }
    @Override public void visitWhile(ASTNodes.WhileNode node) throws Exception{
        node.cond.visitP(this);
        node.stat.visitP(this);
    }
    @Override public void visitFor(ASTNodes.ForNode node) throws Exception{
        node.start.visitP(this);
        node.condition.visitP(this);
        node.body.visitP(this);
        node.increment.visitP(this);
    }
    @Override public void visitProcCall(ASTNodes.ProcCallNode node) throws Exception{
        node.pars.visitP(this);
    }
    @Override public void visitFuncCall(ASTNodes.FuncCallNode node) throws Exception{
        node.pars.visitP(this);
    }
}
