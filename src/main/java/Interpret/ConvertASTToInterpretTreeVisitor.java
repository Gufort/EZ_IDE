package Interpret;

import Basic.ASTNodes;
import SemanticCheckLogic.CalcTypes;
import SemanticCheckLogic.SymbolTable;

import java.math.BigInteger;

public class ConvertASTToInterpretTreeVisitor implements ASTNodes.IVisitor<InterpretTree.NodeI> {

    @Override
    public InterpretTree.NodeI visitNode(ASTNodes.Node n) throws Exception {
        return null;
    }

    @Override
    public InterpretTree.NodeI visitExprNode(ASTNodes.ExprNode ex) throws Exception {
        return null;
    }

    @Override
    public InterpretTree.NodeI visitStatementNode(ASTNodes.StatementNode st) throws Exception {
        return null;
    }

    @Override
    public InterpretTree.NodeI visitStatementList(ASTNodes.StatementListNode stl) throws Exception {
        InterpretTree.StatementListNodeI result = new InterpretTree.StatementListNodeI();
        for (ASTNodes.StatementNode x : stl.statements) {
            result.add((InterpretTree.StatementNodeI) x.visit(this));
        }
        return result;
    }

    @Override
    public InterpretTree.NodeI visitExprList(ASTNodes.ExprListNode exlist) throws Exception {
        InterpretTree.ExprListNodeI result = new InterpretTree.ExprListNodeI();
        for (ASTNodes.ExprNode x : exlist.lst) {
            result.add((InterpretTree.ExprNodeI) x.visit(this));
        }
        return result;
    }

    @Override
    public InterpretTree.NodeI visitInt(ASTNodes.IntNode n) throws Exception {
        return new InterpretTree.IntNodeI(n.value);
    }

    @Override
    public InterpretTree.NodeI visitDouble(ASTNodes.DoubleNode d) throws Exception {
        return new InterpretTree.DoubleNodeI(d.value);
    }

    @Override
    public InterpretTree.BigIntegerNodeI visitBigInt(ASTNodes.BigIntNode b) throws Exception {
        return new InterpretTree.BigIntegerNodeI(b.value);
    }

    @Override
    public InterpretTree.NodeI visitWhile(ASTNodes.WhileNode whn) throws Exception {
        return new InterpretTree.WhileNodeI(
                (InterpretTree.ExprNodeI) whn.cond.visit(this),
                (InterpretTree.StatementNodeI) whn.stat.visit(this)
        );
    }

    @Override
    public InterpretTree.NodeI visitFor(ASTNodes.ForNode forn) throws Exception{
        return new InterpretTree.ForNodeI(
                (InterpretTree.StatementNodeI) forn.start.visit(this),
                (InterpretTree.ExprNodeI) forn.condition.visit(this),
                (InterpretTree.StatementNodeI) forn.increment.visit(this),
                (InterpretTree.StatementNodeI) forn.body.visit(this)
        );
    }

    @Override
    public InterpretTree.NodeI visitIf(ASTNodes.IfNode ifn) throws Exception {
        InterpretTree.StatementNodeI thenStat = (InterpretTree.StatementNodeI) ifn.then.visit(this);
        InterpretTree.StatementNodeI elseStat = null;
        if (ifn.elseif != null) {
            elseStat = (InterpretTree.StatementNodeI) ifn.elseif.visit(this);
        }
        return new InterpretTree.IfNodeI(
                (InterpretTree.ExprNodeI) ifn.cond.visit(this),
                thenStat,
                elseStat
        );
    }

    @Override
    public InterpretTree.NodeI visitId(ASTNodes.IdNode id) throws Exception {
        SymbolTable.SymbolInfo sym = SymbolTable.SymTable.get(id.name);
        if (sym == null) return null;

        switch (sym.semanticType) {
            case IntType:
                return new InterpretTree.IdNodeI(sym.address);
            case DoubleType:
                return new InterpretTree.IdNodeR(sym.address);
            case BoolType:
                return new InterpretTree.IdNodeB(sym.address);
            case BigIntegerType:
                return new InterpretTree.IdNodeBI(sym.address);
            default:
                return null;
        }
    }

    @Override
    public InterpretTree.NodeI visitAssign(ASTNodes.AssignNode ass) throws Exception {
        SymbolTable.SymbolInfo sym = SymbolTable.SymTable.get(ass.id.name);
        if (sym == null) return null;

        switch (sym.semanticType) {
            case IntType:
                return new InterpretTree.AssignIntNodeI(
                        sym.address,
                        (InterpretTree.ExprNodeI) ass.expr.visit(this)
                );
            case DoubleType:
                return new InterpretTree.AssignRealNodeI(
                        sym.address,
                        (InterpretTree.ExprNodeI) ass.expr.visit(this)
                );
            case BoolType:
                return new InterpretTree.AssignBoolNodeI(
                        sym.address,
                        (InterpretTree.ExprNodeI) ass.expr.visit(this)
                );
            case BigIntegerType:
                return new InterpretTree.AssignBigIntegerNodeI(
                        sym.address,
                        (InterpretTree.ExprNodeI) ass.expr.visit(this)
                );
            default:
                return null;
        }
    }

    @Override
    public InterpretTree.NodeI visitAssignOperation(ASTNodes.AssignOperationNode ass) throws Exception {
        SymbolTable.SymbolInfo sym = SymbolTable.SymTable.get(ass.id.name);
        if (sym == null) return null;

        if (ass.op == '+') {
            switch (sym.semanticType) {
                case IntType:
                    if (ass.expr instanceof ASTNodes.IntNode) {
                        ASTNodes.IntNode intNode = (ASTNodes.IntNode) ass.expr;
                        return new InterpretTree.AssignPlusIntCNodeI(sym.address, intNode.value);
                    } else {
                        return new InterpretTree.AssignPlusIntNodeI(
                                sym.address,
                                (InterpretTree.ExprNodeI) ass.expr.visit(this)
                        );
                    }
                case DoubleType:
                    if (ass.expr instanceof ASTNodes.IntNode) {
                        ASTNodes.IntNode intNode = (ASTNodes.IntNode) ass.expr;
                        return new InterpretTree.AssignPlusRealIntCNodeI(sym.address, intNode.value);
                    } else if (ass.expr instanceof ASTNodes.DoubleNode) {
                        ASTNodes.DoubleNode doubleNode = (ASTNodes.DoubleNode) ass.expr;
                        return new InterpretTree.AssignPlusRealCNodeI(sym.address, doubleNode.value);
                    } else {
                        return new InterpretTree.AssignPlusRealNodeI(
                                sym.address,
                                (InterpretTree.ExprNodeI) ass.expr.visit(this)
                        );
                    }
                case BigIntegerType:
                    if (ass.expr instanceof ASTNodes.BigIntNode) {
                        ASTNodes.BigIntNode bigIntNode = (ASTNodes.BigIntNode) ass.expr;
                        return new InterpretTree.AssignPlusBigIntegerCNodeI(sym.address, new BigInteger(bigIntNode.value));
                    } else {
                        return new InterpretTree.AssignPlusBigIntegerNodeI(
                                sym.address,
                                (InterpretTree.ExprNodeI) ass.expr.visit(this)
                        );
                    }
            }
        }
        return null;
    }

    @Override
    public InterpretTree.NodeI visitFuncCall(ASTNodes.FuncCallNode f) throws Exception {
        return null;
    }

    @Override
    public InterpretTree.NodeI visitBinOp(ASTNodes.BinOpNode bin) throws Exception {
        SymbolTable.SemanticType lt = CalcTypes.calcType(bin.left);
        SymbolTable.SemanticType rt = CalcTypes.calcType(bin.right);
        InterpretTree.ExprNodeI linterpr = (InterpretTree.ExprNodeI) bin.left.visit(this);
        InterpretTree.ExprNodeI rinterpr = (InterpretTree.ExprNodeI) bin.right.visit(this);

        int sit = 0;
        if (rt == SymbolTable.SemanticType.DoubleType) {
            sit += 1;
        } else if (rt == SymbolTable.SemanticType.BoolType) {
            sit += 2;
        }
        if (lt == SymbolTable.SemanticType.DoubleType) {
            sit += 3;
        } else if (lt == SymbolTable.SemanticType.BoolType) {
            sit += 6;
        }

        switch (bin.op) {
            case PLUS:
                switch (sit) {
                    case 0:
                        if (rinterpr instanceof InterpretTree.IntNodeI) {
                            InterpretTree.IntNodeI ric = (InterpretTree.IntNodeI) rinterpr;
                            return new InterpretTree.PlusIC(linterpr, ric.val);
                        } else {
                            return new InterpretTree.PlusII(linterpr, rinterpr);
                        }
                    case 4:
                        return new InterpretTree.PlusRR(linterpr, rinterpr);
                    case 1:
                        return new InterpretTree.PlusIR(linterpr, rinterpr);
                    case 3:
                        return new InterpretTree.PlusRI(linterpr, rinterpr);
                }
                break;

            case MINUS:
                switch (sit) {
                    case 0: return new InterpretTree.MinusII(linterpr, rinterpr);
                    case 4: return new InterpretTree.MinusRR(linterpr, rinterpr);
                    case 1: return new InterpretTree.MinusIR(linterpr, rinterpr);
                    case 3: return new InterpretTree.MinusRI(linterpr, rinterpr);
                }
                break;

            case MULTIPLE:
                switch (sit) {
                    case 0: return new InterpretTree.MultII(linterpr, rinterpr);
                    case 4: return new InterpretTree.MultRR(linterpr, rinterpr);
                    case 1: return new InterpretTree.MultIR(linterpr, rinterpr);
                    case 3: return new InterpretTree.MultRI(linterpr, rinterpr);
                }
                break;

            case DIVIDE:
                switch (sit) {
                    case 0: return new InterpretTree.DivII(linterpr, rinterpr);
                    case 4: return new InterpretTree.DivRR(linterpr, rinterpr);
                    case 1: return new InterpretTree.DivIR(linterpr, rinterpr);
                    case 3: return new InterpretTree.DivRI(linterpr, rinterpr);
                }
                break;

            case EQUAL:
                switch (sit) {
                    case 0: return new InterpretTree.EqII(linterpr, rinterpr);
                    case 4: return new InterpretTree.EqRR(linterpr, rinterpr);
                    case 1: return new InterpretTree.EqIR(linterpr, rinterpr);
                    case 3: return new InterpretTree.EqRI(linterpr, rinterpr);
                    case 8: return new InterpretTree.EqBB(linterpr, rinterpr);
                }
                break;

            case NOTEQUAL:
                switch (sit) {
                    case 0: return new InterpretTree.NotEqII(linterpr, rinterpr);
                    case 4: return new InterpretTree.NotEqRR(linterpr, rinterpr);
                    case 1: return new InterpretTree.NotEqIR(linterpr, rinterpr);
                    case 3: return new InterpretTree.NotEqRI(linterpr, rinterpr);
                    case 8: return new InterpretTree.NotEqBB(linterpr, rinterpr);
                }
                break;

            case LESS:
                switch (sit) {
                    case 0: return new InterpretTree.LessII(linterpr, rinterpr);
                    case 4: return new InterpretTree.LessRR(linterpr, rinterpr);
                    case 1: return new InterpretTree.LessIR(linterpr, rinterpr);
                    case 3: return new InterpretTree.LessRI(linterpr, rinterpr);
                }
                break;

            case LESSEQUAL:
                switch (sit) {
                    case 0: return new InterpretTree.LessEqII(linterpr, rinterpr);
                    case 4: return new InterpretTree.LessEqRR(linterpr, rinterpr);
                    case 1: return new InterpretTree.LessEqIR(linterpr, rinterpr);
                    case 3: return new InterpretTree.LessEqRI(linterpr, rinterpr);
                }
                break;

            case GREATER:
                switch (sit) {
                    case 0: return new InterpretTree.GreaterII(linterpr, rinterpr);
                    case 4: return new InterpretTree.GreaterRR(linterpr, rinterpr);
                    case 1: return new InterpretTree.GreaterIR(linterpr, rinterpr);
                    case 3: return new InterpretTree.GreaterRI(linterpr, rinterpr);
                }
                break;

            case GREATEREQUAL:
                switch (sit) {
                    case 0: return new InterpretTree.GreaterEqII(linterpr, rinterpr);
                    case 4: return new InterpretTree.GreaterEqRR(linterpr, rinterpr);
                    case 1: return new InterpretTree.GreaterEqIR(linterpr, rinterpr);
                    case 3: return new InterpretTree.GreaterEqRI(linterpr, rinterpr);
                }
                break;
        }

        return null;
    }

    @Override
    public InterpretTree.NodeI visitProcCall(ASTNodes.ProcCallNode p) throws Exception {
        return new InterpretTree.ProcCallNodeI(
                p.name.name,
                (InterpretTree.ExprListNodeI) p.pars.visit(this)
        );
    }
}