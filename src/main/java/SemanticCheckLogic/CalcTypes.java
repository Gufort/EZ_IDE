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
        else if(left == SymbolTable.SemanticType.BigIntegerType && right == SymbolTable.SemanticType.IntType)
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
            return SymbolTable.SemanticType.BigIntegerType;
        }
        @Override public SymbolTable.SemanticType visitId(ASTNodes.IdNode node) throws Exception{
            if(SymbolTable.SymTable.get(node.name) == null)
                CompilerException.semanticError("Идентификатор " + node.name + " не определен", node.position);
            return SymbolTable.SymTable.get(node.name).semanticType;
        }
        @Override public SymbolTable.SemanticType visitArrayAccess(ASTNodes.ArrayAccessNode node) throws Exception {
            var arrayType = node.array.visit(this);
            var indexType = node.index.visit(this);

            var arrayInfo = SymbolTable.SymTable.get(((ASTNodes.IdNode)node.array).name);
            if(arrayInfo == null || arrayInfo.kindType != SymbolTable.KindType.ArrayName){
                CompilerException.semanticError("Ожидался массив, получено " + arrayType, node.position);
                return SymbolTable.SemanticType.BadType;
            }

            if(indexType != SymbolTable.SemanticType.IntType){
                CompilerException.semanticError("Индекс в массиве должен быть целочисленным, получен " + indexType, node.position);
                return SymbolTable.SemanticType.BadType;
            }

            return arrayInfo.elementType != null ?
                    arrayInfo.elementType :
                    SymbolTable.SemanticType.ObjectType;
        }
        @Override public SymbolTable.SemanticType visitArrayLiteral(ASTNodes.ArrayLiteralNode node) throws Exception {
            if(node.elements.isEmpty())
                return SymbolTable.SemanticType.ObjectType;

            var commonType = node.elements.get(0).visit(this);
            for(int i = 1; i < node.elements.size(); ++i){
                var currentType = node.elements.get(i).visit(this);
                if(commonType == SymbolTable.SemanticType.IntType && currentType == SymbolTable.SemanticType.DoubleType)
                    commonType = SymbolTable.SemanticType.DoubleType;
                else if(commonType == SymbolTable.SemanticType.IntType && currentType == SymbolTable.SemanticType.BigIntegerType)
                    commonType = SymbolTable.SemanticType.BigIntegerType;
                else if(commonType == SymbolTable.SemanticType.DoubleType && currentType == SymbolTable.SemanticType.BigIntegerType)
                    commonType = SymbolTable.SemanticType.DoubleType;
                else if(!assignComparable(commonType, currentType)){
                    CompilerException.semanticError("Несовместимые типы в массиве " + commonType + " и " + currentType, node.position);
                    return SymbolTable.SemanticType.BadType;
                }
            }
            return commonType;
        }
        @Override public SymbolTable.SemanticType visitArrayDeclaration(ASTNodes.ArrayDeclarationNode node) throws Exception {
            return SymbolTable.SemanticType.NoType;
        }
        @Override public SymbolTable.SemanticType visitAssign(ASTNodes.AssignNode node) {
            return SymbolTable.SemanticType.NoType;
        }
        @Override public SymbolTable.SemanticType visitAssignOperation(ASTNodes.AssignOperationNode node) {
            return SymbolTable.SemanticType.NoType;
        }

        // ДОБАВЛЕННЫЕ МЕТОДЫ ДЛЯ НОВЫХ УЗЛОВ:
        @Override
        public SymbolTable.SemanticType visitArrayAssign(ASTNodes.ArrayAssignNode node) throws Exception {
            // Проверяем типы для присваивания элементу массива
            var arrayType = node.array.visit(this);
            var indexType = node.index.visit(this);
            var exprType = node.expr.visit(this);

            // Проверяем, что индекс целочисленный
            if (indexType != SymbolTable.SemanticType.IntType) {
                CompilerException.semanticError("Индекс массива должен быть целочисленным", node.index.position);
                return SymbolTable.SemanticType.BadType;
            }

            // Проверяем, что левая часть - это массив
            if (node.array instanceof ASTNodes.IdNode) {
                String arrayName = ((ASTNodes.IdNode) node.array).name;
                SymbolTable.SymbolInfo arrayInfo = SymbolTable.SymTable.get(arrayName);
                if (arrayInfo == null || arrayInfo.kindType != SymbolTable.KindType.ArrayName) {
                    CompilerException.semanticError("Ожидался массив", node.array.position);
                    return SymbolTable.SemanticType.BadType;
                }

                // Проверяем совместимость типов
                if (!assignComparable(arrayInfo.elementType, exprType)) {
                    CompilerException.semanticError("Несовместимые типы при присваивании элементу массива: " +
                            arrayInfo.elementType + " и " + exprType, node.expr.position);
                    return SymbolTable.SemanticType.BadType;
                }
            }

            return SymbolTable.SemanticType.NoType;
        }

        @Override
        public SymbolTable.SemanticType visitArrayAssignOperation(ASTNodes.ArrayAssignOperationNode node) throws Exception {
            var arrayType = node.array.visit(this);
            var indexType = node.index.visit(this);
            var exprType = node.expr.visit(this);

            if (indexType != SymbolTable.SemanticType.IntType) {
                CompilerException.semanticError("Индекс массива должен быть целочисленным", node.index.position);
                return SymbolTable.SemanticType.BadType;
            }

            if (node.array instanceof ASTNodes.IdNode) {
                String arrayName = ((ASTNodes.IdNode) node.array).name;
                SymbolTable.SymbolInfo arrayInfo = SymbolTable.SymTable.get(arrayName);
                if (arrayInfo == null || arrayInfo.kindType != SymbolTable.KindType.ArrayName) {
                    CompilerException.semanticError("Ожидался массив", node.array.position);
                    return SymbolTable.SemanticType.BadType;
                }

                if (arrayInfo.elementType != SymbolTable.SemanticType.IntType &&
                        arrayInfo.elementType != SymbolTable.SemanticType.DoubleType &&
                        arrayInfo.elementType != SymbolTable.SemanticType.BigIntegerType) {
                    CompilerException.semanticError("Операция " + node.op + " не определена для типа " +
                            arrayInfo.elementType, node.expr.position);
                    return SymbolTable.SemanticType.BadType;
                }

                if (!assignComparable(arrayInfo.elementType, exprType)) {
                    CompilerException.semanticError("Несовместимые типы при присваивании элементу массива: " +
                            arrayInfo.elementType + " и " + exprType, node.expr.position);
                    return SymbolTable.SemanticType.BadType;
                }

                switch (node.op) {
                    case '+': break;
                    case '-': break;
                    case '*': break;
                    case '/':
                        if (arrayInfo.elementType == SymbolTable.SemanticType.IntType) {
                            CompilerException.semanticError("Операция /= не определена для целочисленных типов", node.expr.position);
                            return SymbolTable.SemanticType.BadType;
                        }
                        break;
                    default:
                        CompilerException.semanticError("Неизвестная операция " + node.op, node.expr.position);
                        return SymbolTable.SemanticType.BadType;
                }
            }

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

    public static SymbolTable.SemanticType calcType(ASTNodes.ExprNode expr) throws Exception {
        return switch (expr) {
            case ASTNodes.IdNode id -> checkSymbolTable(id.name);
            case ASTNodes.IntNode node -> SymbolTable.SemanticType.IntType;
            case ASTNodes.DoubleNode node -> SymbolTable.SemanticType.DoubleType;
            case ASTNodes.BigIntNode node -> SymbolTable.SemanticType.BigIntegerType;
            case ASTNodes.ArrayAccessNode arrayAccess -> {
                // Обработка доступа к массиву
                if (arrayAccess.array instanceof ASTNodes.IdNode) {
                    String arrayName = ((ASTNodes.IdNode) arrayAccess.array).name;
                    SymbolTable.SymbolInfo arrayInfo = SymbolTable.SymTable.get(arrayName);
                    if (arrayInfo != null && arrayInfo.kindType == SymbolTable.KindType.ArrayName) {
                        yield arrayInfo.elementType != null ? arrayInfo.elementType : SymbolTable.SemanticType.ObjectType;
                    }
                }
                yield SymbolTable.SemanticType.BadType;
            }
            case ASTNodes.ArrayLiteralNode arrayLiteral -> {
                if (arrayLiteral.elements.isEmpty()) {
                    yield SymbolTable.SemanticType.ObjectType;
                }

                SymbolTable.SemanticType commonType = calcType(arrayLiteral.elements.get(0));
                for (int i = 1; i < arrayLiteral.elements.size(); i++) {
                    SymbolTable.SemanticType currentType = calcType(arrayLiteral.elements.get(i));
                    if (commonType == SymbolTable.SemanticType.IntType && currentType == SymbolTable.SemanticType.DoubleType) {
                        commonType = SymbolTable.SemanticType.DoubleType;
                    } else if (commonType == SymbolTable.SemanticType.IntType && currentType == SymbolTable.SemanticType.BigIntegerType) {
                        commonType = SymbolTable.SemanticType.BigIntegerType;
                    } else if (commonType == SymbolTable.SemanticType.DoubleType && currentType == SymbolTable.SemanticType.BigIntegerType) {
                        commonType = SymbolTable.SemanticType.DoubleType;
                    } else if (!assignComparable(commonType, currentType)) {
                        yield SymbolTable.SemanticType.BadType;
                    }
                }
                yield commonType;
            }
            case ASTNodes.FuncCallNode funcCall -> {
                SymbolTable.SymbolInfo sym = SymbolTable.SymTable.get(funcCall.name.name);
                if (sym != null && sym.kindType == SymbolTable.KindType.FuncName) {
                    yield sym.semanticType;
                }
                yield SymbolTable.SemanticType.BadType;
            }
            case ASTNodes.BinOpNode bin -> {
                var left = calcType(bin.left);
                var right = calcType(bin.right);

                if (left == null || right == null) {
                    yield SymbolTable.SemanticType.BadType;
                }

                if (Arrays.stream(LexerUnit.ArithmeticOperations).anyMatch(op -> op == bin.op)) {
                    if (!Arrays.stream(SymbolTable.NumTypes).anyMatch(l -> l == left) ||
                            !Arrays.stream(SymbolTable.NumTypes).anyMatch(l -> l == right)) {
                        yield SymbolTable.SemanticType.BadType;
                    } else if (bin.op == LexerUnit.TokenType.DIVIDE) {
                        yield SymbolTable.SemanticType.DoubleType; // Деление всегда дает double
                    } else if (left == right) {
                        yield left;
                    } else if ((left == SymbolTable.SemanticType.IntType && right == SymbolTable.SemanticType.DoubleType) ||
                            (left == SymbolTable.SemanticType.DoubleType && right == SymbolTable.SemanticType.IntType)) {
                        yield SymbolTable.SemanticType.DoubleType;
                    } else if ((left == SymbolTable.SemanticType.IntType && right == SymbolTable.SemanticType.BigIntegerType) ||
                            (left == SymbolTable.SemanticType.BigIntegerType && right == SymbolTable.SemanticType.IntType) ||
                            (left == SymbolTable.SemanticType.DoubleType && right == SymbolTable.SemanticType.BigIntegerType) ||
                            (left == SymbolTable.SemanticType.BigIntegerType && right == SymbolTable.SemanticType.DoubleType)) {
                        yield SymbolTable.SemanticType.BigIntegerType;
                    } else {
                        yield SymbolTable.SemanticType.BadType;
                    }
                } else if (Arrays.stream(LexerUnit.LogicalOperators).anyMatch(op -> op == bin.op)) {
                    if (left != SymbolTable.SemanticType.BoolType || right != SymbolTable.SemanticType.BoolType) {
                        yield SymbolTable.SemanticType.BadType;
                    }
                    yield SymbolTable.SemanticType.BoolType;
                } else if (Arrays.stream(LexerUnit.CompareOperations).anyMatch(op -> op == bin.op)) {
                    if (!Arrays.stream(SymbolTable.NumTypes).anyMatch(l -> l == left) ||
                            !Arrays.stream(SymbolTable.NumTypes).anyMatch(l -> l == right)) {
                        yield SymbolTable.SemanticType.BadType;
                    }
                    yield SymbolTable.SemanticType.BoolType;
                } else {
                    yield SymbolTable.SemanticType.BadType;
                }
            }
            default -> {
                throw new Exception("Unsupported expression type: " + expr.getClass().getSimpleName());
            }
        };
    }

    public static SymbolTable.SemanticType checkSymbolTable(String name) {
        return SymbolTable.SymTable.get(name) != null?
                SymbolTable.SymTable.get(name).semanticType :
                SymbolTable.SemanticType.BadType;
    }
}