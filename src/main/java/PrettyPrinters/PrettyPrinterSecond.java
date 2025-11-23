package PrettyPrinters;

import Basic.ASTNodes;

public class PrettyPrinterSecond implements ASTNodes.IVisitor<String>{
    private int indent = 0;

    public String ind(){ return " ".repeat(indent); }
    public String indInc(){
        var res = "";
        indent += 4;
        return res;
    }
    public String indDec(){
        var res = "";
        indent -= 4;
        return res;
    }

    @Override
    public String visitNode(ASTNodes.Node node) throws Exception {
        return ind() + node.visit(this);
    }
    @Override
    public String visitExprNode(ASTNodes.ExprNode node)throws Exception {
        return node.visit(this);
    }
    @Override
    public String visitStatementNode(ASTNodes.StatementNode node) throws Exception {
        return ind() + node.visit(this);
    }
    @Override
    public String visitBinOp(ASTNodes.BinOpNode node) throws Exception {
        return node.left.visit(this) + " " + node.operationToString() + " " + node.right.visit(this);
    }
    @Override
    public String visitStatementList(ASTNodes.StatementListNode lst) throws Exception{
        StringBuilder res = new StringBuilder();

        for(int i = 0; i < lst.statements.size(); i++){
            if(i > 0) res.append("\n");
            res.append(lst.statements.get(i).visit(this));
        }

        return res.toString();
    }
    @Override
    public String visitExprList(ASTNodes.ExprListNode lst) throws Exception {
        StringBuilder res = new StringBuilder();

        for(int i = 0; i < lst.lst.size(); i++){
            if(i > 0) res.append(", ");
            res.append(ind() + lst.lst.get(i).visit(this));
        }

        return res.toString();
    }
    @Override
    public String visitInt(ASTNodes.IntNode node) {
        return Integer.toString(node.value);
    }
    @Override
    public String visitDouble(ASTNodes.DoubleNode node) {
        return Double.toString(node.value);
    }
    @Override
    public String visitBigInt(ASTNodes.BigIntNode node) throws Exception { return String.valueOf(node.value); }
    @Override
    public String visitId(ASTNodes.IdNode node) {
        return node.name.toString();
    }
    @Override
    public String visitArrayAccess(ASTNodes.ArrayAccessNode node) throws Exception {
        return node.array.visit(this) + "[" + node.index.visit(this) + "]";
    }
    @Override
    public String visitArrayAssign(ASTNodes.ArrayAssignNode node) throws Exception {
        return ind() + node.array.visit(this) + "[" + node.index.visit(this) + "] = " + node.expr.visit(this);
    }
    @Override
    public String visitArrayAssignOperation(ASTNodes.ArrayAssignOperationNode node) throws Exception {
        return ind() + node.array.visit(this) + "[" + node.index.visit(this) + "] " + node.op + "= " + node.expr.visit(this);
    }
    @Override
    public String visitArrayLiteral(ASTNodes.ArrayLiteralNode node) throws Exception {
        StringBuilder res = new StringBuilder();
        res.append("[");

        for (int i = 0; i < node.elements.size(); i++) {
            if (i > 0) res.append(", ");
            res.append(node.elements.get(i).visit(this));
        }

        res.append("]");
        return res.toString();
    }
    @Override
    public String visitArrayDeclaration(ASTNodes.ArrayDeclarationNode node) throws Exception {
        StringBuilder res = new StringBuilder();
        res.append(ind()).append("array ").append(node.id.name);

        if (node.size != null)
            res.append("[").append(node.size.visit(this)).append("]");

        if (node.initialElements != null && !node.initialElements.isEmpty()) {
            res.append(" := [");
            for (int i = 0; i < node.initialElements.size(); i++) {
                if (i > 0) res.append(", ");
                res.append(node.initialElements.get(i).visit(this));
            }
            res.append("]");
        }

        return res.toString();
    }
    @Override
    public String visitAssign(ASTNodes.AssignNode node) throws Exception{
        return ind() + node.id.name + " := " + node.expr.visit(this);
    }
    @Override
    public String visitAssignOperation(ASTNodes.AssignOperationNode node) throws Exception{
        return ind() + node.id.name + ' ' + node.op + "= " + node.expr.visit(this);
    }
    @Override
    public String visitIf(ASTNodes.IfNode node) throws Exception {
        StringBuilder res = new StringBuilder();

        res.append("if").append(" ").append(node.cond.visit(this)).append(" ").append("then:").append("\n")
                .append(indInc()).append(node.then.visit(this)).append(indDec()).append('\n');
        if(node.elseif != null){
            res.append("else:").append('\n').append(indInc()).append(node.elseif.visit(this)).append(indDec());
        }

        return res.toString();
    }
    @Override
    public String visitWhile(ASTNodes.WhileNode node) throws Exception {
        StringBuilder res = new StringBuilder();

        res.append("while").append(" ").append(node.cond.visit(this)).append(" ").append("do:").append("\n")
                .append(indInc()).append(node.stat.visit(this)).append(indDec());

        return res.toString();
    }

    @Override
    public String visitFor(ASTNodes.ForNode node) throws Exception {
        StringBuilder res = new StringBuilder();
        res.append(ind()).append("for").append('(').append(node.start.visit(this)).append("; ").
                append(node.condition.visit(this)).append("; ").append(node.increment.visit(this)).append(") do: ").
                append(ind()).append('\n');
        indInc();
        res.append(node.body.visit(this));
        indDec();
        res.append(ind());
        return res.toString();
    }
    @Override
    public String visitProcCall(ASTNodes.ProcCallNode node) throws Exception {
        if (node.pars == null || node.pars.lst.isEmpty()) {
            return ind() + node.name.name + "();";
        }

        StringBuilder res = new StringBuilder();
        res.append(ind()).append(node.name.name).append("(");

        for(int i = 0; i < node.pars.lst.size(); i++){
            if(i > 0) res.append(", ");
            res.append(node.pars.lst.get(i).visit(this));
        }

        res.append(")");
        return res.toString();
    }
    @Override
    public String visitFuncCall(ASTNodes.FuncCallNode node) throws Exception {
        if (node.pars == null || node.pars.lst.isEmpty()) {
            return ind() + node.name.name + "();";
        }

        StringBuilder res = new StringBuilder();
        res.append(ind()).append(node.name.name).append("(");

        for(int i = 0; i < node.pars.lst.size(); i++){
            if(i > 0) res.append(", ");
            res.append(node.pars.lst.get(i).visit(this));
        }

        res.append(")");
        return res.toString();
    }
}
