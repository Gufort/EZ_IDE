package Interpret;

import Basic.ASTNodes;
import ExceptionLogic.CompilerException;
import SemanticCheckLogic.SymbolTable;

import java.math.BigInteger;

public class InterpretVisitor implements ASTNodes.IVisitor<Object>{

    @Override
    public Object visitNode(ASTNodes.Node node) throws Exception {
        return null;
    }

    @Override
    public Object visitExprNode(ASTNodes.ExprNode node) throws Exception {
        return null;
    }

    @Override
    public Object visitStatementNode(ASTNodes.StatementNode node) throws Exception {
        return null;
    }

    @Override
    public Object visitBinOp(ASTNodes.BinOpNode node) throws Exception {
        var left = node.left.visit(this);
        var right = node.right.visit(this);
        var sit = 0;

        if (right instanceof Double)
            sit++;
        else if (right instanceof Boolean)
            sit += 2;
        if (left instanceof Double)
            sit += 3;
        else if (left instanceof Boolean)
            sit += 6;

        switch(node.op) {
            case PLUS -> {
                switch(sit) {
                    case 4: return (double)left + (double)right;
                    case 1: return (int)left + (double)right;
                    case 3: return (double)left + (int)right;
                    case 0: return (int)left + (int)right;
                }
            }
            case MINUS -> {
                switch(sit) {
                    case 4: return (double)left - (double)right;
                    case 1: return (int)left - (double)right;
                    case 3: return (double)left - (int)right;
                    case 0: return (int)left - (int)right;
                }
            }
            case MULTIPLE -> {
                switch(sit) {
                    case 4: return (double)left * (double)right;
                    case 1: return (int)left * (double)right;
                    case 3: return (double)left * (int)right;
                    case 0: return (int)left * (int)right;
                }
            }
            case DIVIDE -> {
                switch(sit) {
                    case 4: return (double)left / (double)right;
                    case 1: return (int)left / (double)right;
                    case 3: return (double)left / (int)right;
                    case 0: return (int)left / (int)right;
                }
            }
            case EQUAL -> {
                switch(sit) {
                    case 4: return (double)left == (double)right;
                    case 1: return (int)left == (double)right;
                    case 3: return (double)left == (int)right;
                    case 0: return (int)left == (int)right;
                    case 8: return (boolean)left == (boolean)right;
                    default: CompilerException.semanticError("Операция == не может быть выполнена над типами " + left.toString() +
                            " и " + right.toString(), node.position);
                }
            }
            case NOTEQUAL -> {
                switch(sit) {
                    case 4: return (double)left != (double)right;
                    case 1: return (int)left != (double)right;
                    case 3: return (double)left != (int)right;
                    case 0: return (int)left != (int)right;
                    case 8: return (boolean)left != (boolean)right;
                    default: CompilerException.semanticError("Операция != не может быть выполнена над типами " + left.toString() +
                            " и " + right.toString(), node.position);
                }
            }
            case LESS -> {
                switch(sit) {
                    case 4: return (double)left < (double)right;
                    case 1: return (int)left < (double)right;
                    case 3: return (double)left < (int)right;
                    case 0: return (int)left < (int)right;
                    default: CompilerException.semanticError("Операция < не может быть выполнена над типами " + left.toString() +
                            " и " + right.toString(), node.position);
                }
            }
            case LESSEQUAL -> {
                switch(sit) {
                    case 4: return (double)left <= (double)right;
                    case 1: return (int)left <= (double)right;
                    case 3: return (double)left <= (int)right;
                    case 0: return (int)left <= (int)right;
                    default: CompilerException.semanticError("Операция <= не может быть выполнена над типами " + left.toString() +
                            " и " + right.toString(), node.position);
                }
            }
            case GREATER -> {
                switch(sit) {
                    case 4: return (double)left > (double)right;
                    case 1: return (int)left > (double)right;
                    case 3: return (double)left > (int)right;
                    case 0: return (int)left > (int)right;
                    default: CompilerException.semanticError("Операция > не может быть выполнена над типами " + left.toString() +
                            " и " + right.toString(), node.position);
                }
            }
            case GREATEREQUAL -> {
                switch(sit) {
                    case 4: return (double)left >= (double)right;
                    case 1: return (int)left >= (double)right;
                    case 3: return (double)left >= (int)right;
                    case 0: return (int)left >= (int)right;
                    default: CompilerException.semanticError("Операция >= не может быть выполнена над типами " + left.toString() +
                            " и " + right.toString(), node.position);
                }
            }
            case AND -> {
                switch(sit) {
                    case 8: return (boolean)left && (boolean)right;
                    default: CompilerException.semanticError("Операция && не может быть выполнена над типами " + left.toString() +
                            " и " + right.toString(), node.position);
                }
            }
            case OR -> {
                switch(sit) {
                    case 8: return (boolean)left || (boolean)right;
                    default: CompilerException.semanticError("Операция || не может быть выполнена над типами " + left.toString() +
                            " и " + right.toString(), node.position);
                }
            }
        }
        return null;
    }

    @Override
    public Object visitStatementList(ASTNodes.StatementListNode stl) throws Exception {
        for (var curr: stl.statements) {
            curr.visit(this);
        }
        return null;
    }

    @Override
    public Object visitExprList(ASTNodes.ExprListNode elt) throws Exception {
        return null;
    }

    @Override
    public Object visitInt(ASTNodes.IntNode node) throws Exception {
        return node.value;
    }

    @Override
    public Object visitDouble(ASTNodes.DoubleNode node) throws Exception {
        return node.value;
    }

    @Override
    public Object visitBigInt(ASTNodes.BigIntNode node) throws Exception {
        return node.value;
    }

    @Override
    public Object visitId(ASTNodes.IdNode node) throws Exception {
        SymbolTable.SymbolInfo symInfo = SymbolTable.SymTable.get(node.name);
        if (symInfo == null) {
            CompilerException.semanticError("Идентификатор " + node.name + " не определен", node.position);
            return null;
        }

        switch (symInfo.semanticType) {
            case IntType:
                return Memory.getInt(symInfo.address);
            case DoubleType:
                return Memory.getDouble(symInfo.address);
            case BoolType:
                return Memory.getBoolean(symInfo.address);
            case BigIntegerType:
                return Memory.getBigInteger(symInfo.address);
            default:
                return null;
        }
    }

    @Override
    public Object visitAssign(ASTNodes.AssignNode node) throws Exception {
        Object value = node.expr.visit(this);
        SymbolTable.SymbolInfo symInfo = SymbolTable.SymTable.get(node.id.name);

        if (symInfo == null) {
            CompilerException.semanticError("Переменная " + node.id.name + " не определена", node.id.position);
            return null;
        }


        switch (symInfo.semanticType) {
            case IntType:
                Memory.setInt(symInfo.address, (Integer) value);
                break;
            case DoubleType:
                if (value instanceof Integer) {
                    Memory.setDouble(symInfo.address, (double)(Integer) value);
                } else {
                    Memory.setDouble(symInfo.address, (Double) value);
                }
                break;
            case BoolType:
                Memory.setBoolean(symInfo.address, (Boolean) value);
                break;
            case BigIntegerType:
                Memory.setBigInteger(symInfo.address, (BigInteger) value);
                break;
        }
        return null;
    }

    @Override
    public Object visitAssignOperation(ASTNodes.AssignOperationNode node) throws Exception {
        Object value = node.expr.visit(this);
        SymbolTable.SymbolInfo symInfo = SymbolTable.SymTable.get(node.id.name);

        if (symInfo == null) {
            CompilerException.semanticError("Переменная " + node.id.name + " не определена", node.id.position);
            return null;
        }

        // Обработка составных операторов присваивания
        switch (symInfo.semanticType) {
            case IntType:
                int currentInt = Memory.getInt(symInfo.address);
                int intValue = (Integer) value;
                switch (node.op) {
                    case '+': Memory.setInt(symInfo.address, currentInt + intValue); break;
                    case '-': Memory.setInt(symInfo.address, currentInt - intValue); break;
                    case '*': Memory.setInt(symInfo.address, currentInt * intValue); break;
                }
                break;

            case DoubleType:
                double currentDouble = Memory.getDouble(symInfo.address);
                double doubleValue;
                if (value instanceof Integer) {
                    doubleValue = (double)(Integer) value;
                } else {
                    doubleValue = (Double) value;
                }
                switch (node.op) {
                    case '+': Memory.setDouble(symInfo.address, currentDouble + doubleValue); break;
                    case '-': Memory.setDouble(symInfo.address, currentDouble - doubleValue); break;
                    case '*': Memory.setDouble(symInfo.address, currentDouble * doubleValue); break;
                    case '/': Memory.setDouble(symInfo.address, currentDouble / doubleValue); break;
                }
                break;
        }
        return null;
    }

    @Override
    public Object visitIf(ASTNodes.IfNode node) throws Exception {
        Object cond = node.cond.visit(this);
        if ((boolean)cond) {
            node.then.visit(this);
        } else if (node.elseif != null) {
            node.elseif.visit(this);
        }
        return null;
    }

    @Override
    public Object visitWhile(ASTNodes.WhileNode node) throws Exception {
        while ((boolean)node.cond.visit(this)) {
            node.stat.visit(this);
        }
        return null;
    }

    @Override
    public Object visitFor(ASTNodes.ForNode node) throws Exception {

        node.start.visit(this);

        while ((boolean)node.condition.visit(this)) {
            node.body.visit(this);
            node.increment.visit(this);
        }
        return null;
    }

    @Override
    public Object visitProcCall(ASTNodes.ProcCallNode node) throws Exception {
        if ("print".equals(node.name.name)) {
            for (var expr : node.pars.lst) {
                Object value = expr.visit(this);
                System.out.print(value + " ");
            }
            System.out.println();
        }
        return null;
    }

    @Override
    public Object visitFuncCall(ASTNodes.FuncCallNode node) throws Exception {
        if ("sqrt".equals(node.name.name) && node.pars.lst.size() == 1) {
            Object arg = node.pars.lst.get(0).visit(this);
            if (arg instanceof Double) {
                return Math.sqrt((Double) arg);
            } else if (arg instanceof Integer) {
                return Math.sqrt((Integer) arg);
            }
        }
        return null;
    }
}