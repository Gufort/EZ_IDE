package Basic;

import ExceptionLogic.CompilerException;
public class AssertVisitor implements ASTNodes.IVisitorP {
    private final String context;

    public AssertVisitor(String context) {
        this.context = context;
    }

    public AssertVisitor() {
        this("AST Validation");
    }

    @Override
    public void visitNode(ASTNodes.Node node) throws Exception {
        assert node != null : "BAD: Node cannot be null";
        System.out.println("GOOD: visitNode validation passed successfully");
    }

    @Override
    public void visitExprNode(ASTNodes.ExprNode node) throws Exception {
        assert node != null : "BAD: ExprNode cannot be null";
        assert node.position != null : "BAD: ExprNode position cannot be null";
        assert node.position.line > 0 : "BAD: Invalid line number: " + node.position.line;
        assert node.position.column >= 0 : "BAD: Invalid column number: " + node.position.column;
        System.out.println("GOOD: visitExprNode validation passed successfully");
    }

    @Override
    public void visitStatementNode(ASTNodes.StatementNode node) throws Exception {
        assert node != null : "BAD: StatementNode cannot be null";
        assert node.position != null : "BAD: StatementNode position cannot be null";
        assert node.position.line > 0 : "BAD: Invalid line number: " + node.position.line;
        assert node.position.column >= 0 : "BAD: Invalid column number: " + node.position.column;
        System.out.println("GOOD: visitStatementNode validation passed successfully");
    }

    @Override
    public void visitBinOp(ASTNodes.BinOpNode node) throws Exception {
        assert node != null : "BAD: BinOpNode cannot be null";
        assert node.left != null : "BAD: BinOpNode left operand cannot be null";
        assert node.right != null : "BAD: BinOpNode right operand cannot be null";
        assert node.op != null : "BAD: BinOpNode operator cannot be null";

        node.left.visitP(this);
        node.right.visitP(this);
        System.out.println("GOOD: visitBinOp validation passed successfully");
    }

    @Override
    public void visitStatementList(ASTNodes.StatementListNode node) throws Exception {
        assert node != null : "BAD: StatementListNode cannot be null";
        assert node.statements != null : "BAD: StatementList statements cannot be null";

        for (var stmt : node.statements) {
            assert stmt != null : "BAD: Statement in StatementList cannot be null";
            stmt.visitP(this);
        }
        System.out.println("GOOD: visitStatementList validation passed successfully - " + node.statements.size() + " statements validated");
    }

    @Override
    public void visitExprList(ASTNodes.ExprListNode node) throws Exception {
        assert node != null : "BAD: ExprListNode cannot be null";
        assert node.lst != null : "BAD: ExprList list cannot be null";

        for (var expr : node.lst) {
            assert expr != null : "BAD: Expression in ExprList cannot be null";
            expr.visitP(this);
        }
        System.out.println("GOOD: visitExprList validation passed successfully - " + node.lst.size() + " expressions validated");
    }

    @Override
    public void visitInt(ASTNodes.IntNode node) throws Exception {
        assert node != null : "BAD: IntNode cannot be null";
        System.out.println("GOOD: visitInt validation passed successfully - value: " + node.value);
    }

    @Override
    public void visitDouble(ASTNodes.DoubleNode node) throws Exception {
        assert node != null : "BAD: DoubleNode cannot be null";
        System.out.println("GOOD: visitDouble validation passed successfully - value: " + node.value);
    }

    @Override
    public void visitBigInt(ASTNodes.BigIntNode node) throws Exception {
        assert node != null : "BAD: BigIntNode cannot be null";
        System.out.println("GOOD: visitBigInt validation passed successfully - value: " + node.value);
    }

    @Override
    public void visitId(ASTNodes.IdNode node) throws Exception {
        assert node != null : "BAD: IdNode cannot be null";
        assert node.name != null : "BAD: IdNode name cannot be null";
        assert !node.name.trim().isEmpty() : "BAD: IdNode name cannot be empty";
        System.out.println("GOOD: visitId validation passed successfully - identifier: '" + node.name + "'");
    }

    @Override
    public void visitAssign(ASTNodes.AssignNode node) throws Exception {
        assert node != null : "BAD: AssignNode cannot be null";
        assert node.id != null : "BAD: AssignNode id cannot be null";
        assert node.expr != null : "BAD: AssignNode expr cannot be null";

        node.id.visitP(this);
        node.expr.visitP(this);
        System.out.println("GOOD: visitAssign validation passed successfully");
    }

    @Override
    public void visitAssignOperation(ASTNodes.AssignOperationNode node) throws Exception {
        assert node != null : "BAD: AssignOperationNode cannot be null";
        assert node.id != null : "BAD: AssignOperationNode id cannot be null";
        assert node.expr != null : "BAD: AssignOperationNode expr cannot be null";

        node.id.visitP(this);
        node.expr.visitP(this);
        System.out.println("GOOD: visitAssignOperation validation passed successfully");
    }

    @Override
    public void visitIf(ASTNodes.IfNode node) throws Exception {
        assert node != null : "BAD: IfNode cannot be null";
        assert node.cond != null : "BAD: IfNode condition cannot be null";
        assert node.then != null : "BAD: IfNode then branch cannot be null";

        node.cond.visitP(this);
        node.then.visitP(this);
        if (node.elseif != null) {
            node.elseif.visitP(this);
        }
        System.out.println("GOOD: visitIf validation passed successfully" + (node.elseif != null ? " (with else branch)" : ""));
    }

    @Override
    public void visitFor(ASTNodes.ForNode node) throws Exception {
        assert node != null : "BAD: ForNode cannot be null";
        assert node.start != null : "BAD: ForNode start cannot be null";
        assert node.condition != null : "BAD: ForNode condition cannot be null";
        assert node.increment != null : "BAD: ForNode increment cannot be null";
        assert node.body != null : "BAD: ForNode body cannot be null";

        node.start.visitP(this);
        node.condition.visitP(this);
        node.increment.visitP(this);
        node.body.visitP(this);
        System.out.println("GOOD: visitFor validation passed successfully");
    }

    @Override
    public void visitWhile(ASTNodes.WhileNode node) throws Exception {
        assert node != null : "BAD: WhileNode cannot be null";
        assert node.cond != null : "BAD: WhileNode condition cannot be null";
        assert node.stat != null : "BAD: WhileNode statement cannot be null";

        node.cond.visitP(this);
        node.stat.visitP(this);
        System.out.println("GOOD: visitWhile validation passed successfully");
    }

    @Override
    public void visitProcCall(ASTNodes.ProcCallNode node) throws Exception {
        assert node != null : "BAD: ProcCallNode cannot be null";
        assert node.name != null : "BAD: ProcCallNode name cannot be null";
        assert node.pars != null : "BAD: ProcCallNode parameters cannot be null";

        node.name.visitP(this);
        node.pars.visitP(this);
        System.out.println("GOOD: visitProcCall validation passed successfully - procedure: '" + node.name.name + "' with " + node.pars.lst.size() + " parameters");
    }

    @Override
    public void visitFuncCall(ASTNodes.FuncCallNode node) throws Exception {
        assert node != null : "BAD: FuncCallNode cannot be null";
        assert node.name != null : "BAD: FuncCallNode name cannot be null";
        assert node.pars != null : "BAD: FuncCallNode parameters cannot be null";

        node.name.visitP(this);
        node.pars.visitP(this);
        System.out.println("GOOD: visitFuncCall validation passed successfully - function: '" + node.name.name + "' with " + node.pars.lst.size() + " parameters");
    }
}