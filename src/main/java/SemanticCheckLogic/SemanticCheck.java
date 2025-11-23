package SemanticCheckLogic;

import Basic.ASTNodes;
import ExceptionLogic.CompilerException;

public class SemanticCheck extends AutoVisitorUnit {

    @Override
    public void visitAssign(ASTNodes.AssignNode node) throws Exception {
        node.expr.visitP(this);

        if (SymbolTable.SymTable.get(node.id.name) == null) {
            SymbolTable.SemanticType type = CalcTypes.calcTypeVis(node.expr);

            Object initialValue;
            switch(type) {
                case IntType:
                    initialValue = 0;
                    break;
                case BoolType:
                    initialValue = false;
                    break;
                case DoubleType:
                    initialValue = 0.0;
                    break;
                default:
                    CompilerException.semanticError("Неподдерживаемый тип для переменной: " + type, node.id.position);
                    return;
            }

            int address = SymbolTable.allocateVariable(type, initialValue);

            SymbolTable.SymTable.put(node.id.name,
                    new SymbolTable.SymbolInfo(
                            node.id.name,
                            SymbolTable.KindType.VarName,
                            type,
                            address
                    )
            );

            node.id.ind = address;

        } else {
            SymbolTable.SymbolInfo symInfo = SymbolTable.SymTable.get(node.id.name);

            if (symInfo.kindType == SymbolTable.KindType.FuncName) {
                CompilerException.semanticError("Имени стандартной функции " + node.id.name + " нельзя присвоить значение", node.id.position);
                return;
            }

            SymbolTable.SemanticType exprType = CalcTypes.calcTypeVis(node.expr);
            SymbolTable.SemanticType varType = symInfo.semanticType;

            if (!CalcTypes.assignComparable(varType, exprType)) {
                CompilerException.semanticError("Переменной " + node.id.name + " типа " + varType +
                        " нельзя присвоить значение типа " + exprType, node.id.position);
                return;
            }

            node.id.ind = symInfo.address;
        }
    }

    @Override
    public void visitAssignOperation(ASTNodes.AssignOperationNode node) throws Exception {
        node.expr.visitP(this);

        if (SymbolTable.SymTable.get(node.id.name) == null) {
            CompilerException.semanticError("Переменная " + node.id.name + " не определена", node.id.position);
            return;
        }

        SymbolTable.SymbolInfo symInfo = SymbolTable.SymTable.get(node.id.name);

        if (symInfo.kindType == SymbolTable.KindType.FuncName) {
            CompilerException.semanticError("Имени стандартной функции " + node.id.name + " нельзя присвоить значение", node.id.position);
            return;
        }

        SymbolTable.SemanticType exprType = CalcTypes.calcTypeVis(node.expr);
        SymbolTable.SemanticType varType = symInfo.semanticType;

        if (varType != SymbolTable.SemanticType.IntType && varType != SymbolTable.SemanticType.DoubleType) {
            CompilerException.semanticError("Операция " + node.op + " не определена для типа " + varType, node.id.position);
            return;
        }

        if (node.op == '/' && varType == SymbolTable.SemanticType.IntType && exprType == SymbolTable.SemanticType.IntType) {
            CompilerException.semanticError("Операция /= не определена для целочисленных типов", node.id.position);
            return;
        }

        if (!CalcTypes.assignComparable(varType, exprType)) {
            CompilerException.semanticError("Переменной " + node.id.name + " типа " + varType +
                    " нельзя присвоить значение типа " + exprType, node.id.position);
            return;
        }

        node.id.ind = symInfo.address;
    }

    @Override
    public void visitIf(ASTNodes.IfNode node) throws Exception {
        node.cond.visitP(this);
        SymbolTable.SemanticType type = CalcTypes.calcTypeVis(node.cond);
        if (type != SymbolTable.SemanticType.BoolType) {
            CompilerException.semanticError("Ожидалось выражение логического типа, а встречено выражение типа " + type, node.cond.position);
        }
        node.then.visitP(this);
        if (node.elseif != null) {
            node.elseif.visitP(this);
        }
    }

    @Override
    public void visitWhile(ASTNodes.WhileNode node) throws Exception {
        node.cond.visitP(this);
        SymbolTable.SemanticType type = CalcTypes.calcTypeVis(node.cond);
        if (type != SymbolTable.SemanticType.BoolType) {
            CompilerException.semanticError("Ожидалось выражение логического типа, а встречено выражение типа " + type, node.cond.position);
        }
        node.stat.visitP(this);
    }

    @Override
    public void visitFor(ASTNodes.ForNode node) throws Exception {
        node.start.visitP(this);
        node.condition.visitP(this);
        SymbolTable.SemanticType type = CalcTypes.calcTypeVis(node.condition);
        if (type != SymbolTable.SemanticType.BoolType) {
            CompilerException.semanticError("Ожидалось выражение логического типа, а встречено выражение типа " + type, node.condition.position);
        }
        node.increment.visitP(this);
        node.body.visitP(this);
    }

    @Override
    public void visitId(ASTNodes.IdNode node) throws Exception {
        SymbolTable.SymbolInfo symInfo = SymbolTable.SymTable.get(node.name);
        if (symInfo == null) {
            CompilerException.semanticError("Идентификатор " + node.name + " не определен", node.position);
            return;
        }

        node.ind = symInfo.address;
    }
}