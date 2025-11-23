package PrettyPrinters;

import Basic.ASTNodes;

public class PrettyPrinterFirst implements ASTNodes.IVisitor<String> {
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
    public String visitExprNode(ASTNodes.ExprNode node) throws Exception {
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
    public String visitStatementList(ASTNodes.StatementListNode stl) throws Exception {
        StringBuilder res = new StringBuilder();

        for(int i = 0; i < stl.statements.size(); i++) {
            var curr = stl.statements.get(i);
            res.append(curr.visit(this));
            if(i < stl.statements.size() - 1) {
                res.append(";").append('\n');
            }
        }

        res.append(ind());
        return res.toString();
    }
    @Override
    public String visitExprList(ASTNodes.ExprListNode stl) throws Exception {
        StringBuilder res = new StringBuilder();
        for(int i = 0; i < stl.lst.size(); i++) {
            if(i > 0) res.append(", ");
            res.append(ind() + stl.lst.get(i).visit(this));
        }
        return res.toString();
    }
    @Override
    public String visitInt(ASTNodes.IntNode node) throws Exception {
        return String.valueOf(node.value);
    }
    @Override
    public String visitDouble(ASTNodes.DoubleNode node) throws Exception {
        return String.valueOf(node.value);
    }
    @Override
    public String visitBigInt(ASTNodes.BigIntNode node) throws Exception {
        return String.valueOf(node.value);
    }
    @Override
    public String visitId(ASTNodes.IdNode node) throws Exception {
        return node.name.toString();
    }
    @Override
    public String visitAssign(ASTNodes.AssignNode node) throws Exception {
        return ind() + node.id.name + " = " + node.expr.visit(this);
    }
    @Override
    public String visitAssignOperation(ASTNodes.AssignOperationNode node) throws Exception{
        return ind() + node.id.name + ' ' + node.op + "= " + node.expr.visit(this);
    }
    @Override
    public String visitIf(ASTNodes.IfNode node) throws Exception {
        StringBuilder res = new StringBuilder();
        res.append(ind()).append("if").append("(").append(node.cond.visit(this)).append(")").append('\n').append('{').append(indInc()).append('\n').append(node.then.visit(this)).append(indDec()).append('\n').append('}').append('\n');
        if(node.elseif != null){
            res.append(ind()).append("else").append('\n').append('{').append(indInc()).append('\n').append(node.elseif.visit(this))
                    .append(indDec()).append('\n').append('}');
        }
        return res.toString();
    }
    @Override
    public String visitWhile(ASTNodes.WhileNode node) throws Exception {
        StringBuilder res = new StringBuilder();
        res.append(ind()).append("while").append('(').append(node.cond.visit(this)).append(") \n");
        res.append(ind()).append('{').append('\n');
        indInc();
        res.append(node.stat.visit(this)).append("\n");
        indDec();
        res.append(ind()).append("}");
        return res.toString();
    }
    @Override
    public String visitFor(ASTNodes.ForNode node) throws Exception {
        StringBuilder res = new StringBuilder();
        res.append(ind()).append("for").append('(').append(node.start.visit(this)).append("; ").
        append(node.condition.visit(this)).append("; ").append(node.increment.visit(this)).append(") \n").
        append(ind()).append('{').append('\n');
        indInc();
        res.append(node.body.visit(this)).append("\n");
        indDec();
        res.append(ind()).append("}");
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
            return ind() + node.name.name + "()";
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
