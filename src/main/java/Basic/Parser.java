package Basic;

import ExceptionLogic.CompilerException;

import java.math.BigInteger;
import java.util.ArrayList;

public class Parser extends ParserBase {
    public Parser(LexerUnit.Lexer lexer) throws Exception {
        super(lexer);
    }

    /// Program := StatementList
    public ASTNodes.StatementNode mainProgram() throws Exception {
        current = 0;
        var res = statementList();
        requires(LexerUnit.TokenType.EOF);
        return res;
    }

    /// StatementList := Statement (';' Statement)*
    public ASTNodes.StatementNode statementList() throws Exception {
        var res = new ASTNodes.StatementListNode();
        res.add(statement());
        while (at(LexerUnit.TokenType.SEMICOLON)) {
            nextLexem();
            res.add(statement());
        }
        return res;
    }

    /// id = expr
    /// id += expr
    /// id(exprlist)
    /// if expr then stat [else stat]
    /// while expr do stat
    /// { statlist }
    public ASTNodes.StatementNode statement() throws Exception {
        var pos = currentToken().position;
        if(at(LexerUnit.TokenType.IF)){
            nextLexem();
            var cond = expr();
            requires(LexerUnit.TokenType.THEN);
            var thenStatement = statement();
            ASTNodes.StatementNode elseStatement = null;
            if(at(LexerUnit.TokenType.ELSE)){
                nextLexem();
                elseStatement = statement();
            }
            return new ASTNodes.IfNode(cond, thenStatement, elseStatement, pos);
        }
        else if(at(LexerUnit.TokenType.WHILE)){
            nextLexem();
            var cond = expr();
            requires(LexerUnit.TokenType.DO);
            var statement = statement();
            return new ASTNodes.WhileNode(cond, statement, pos);
        }
        else if(at(LexerUnit.TokenType.FOR)){
            nextLexem();

            boolean hasParen = at(LexerUnit.TokenType.LEFT_PAREN);
            if (hasParen) nextLexem();

            var start = statement();
            requires(LexerUnit.TokenType.SEMICOLON);
            var cond = expr();
            requires(LexerUnit.TokenType.SEMICOLON);
            var increment = statement();

            if (hasParen) requires(LexerUnit.TokenType.RIGHT_PAREN);

            requires(LexerUnit.TokenType.DO);
            var body = statement();
            return new ASTNodes.ForNode(start, cond, increment, body, pos);
        }
        else if(at(LexerUnit.TokenType.LEFT_BRACE)){
            nextLexem();
            var stl = statementList();
            requires(LexerUnit.TokenType.RIGHT_BRACE);
            stl.position = pos;
            return stl;
        }

        if(at(LexerUnit.TokenType.ID) && peekNextTokenType() == LexerUnit.TokenType.LEFT_BRACKET){
            var access = arrayAccess();
            if(at(LexerUnit.TokenType.ASSIGN,
                    LexerUnit.TokenType.ASSIGNPLUS,
                    LexerUnit.TokenType.ASSIGNMULTIPLE,
                    LexerUnit.TokenType.ASSIGNMINUS,
                    LexerUnit.TokenType.ASSIGNDIVIDE)){

                var operator = nextLexem();
                var expr = expr();

                if(operator.type == LexerUnit.TokenType.ASSIGN) {
                    return new ASTNodes.ArrayAssignNode(access.array, access.index, expr, pos);
                } else {
                    var op = getAssignOpChar(operator.type);
                    return new ASTNodes.ArrayAssignOperationNode(access.array, access.index, expr, op, pos);
                }
            }
        }

        var id = ident();
        if(at(LexerUnit.TokenType.ASSIGN)){
            nextLexem();
            var expr = expr();
            return new ASTNodes.AssignNode(id, expr, pos);
        }
        else if(at(LexerUnit.TokenType.ASSIGNPLUS)){
            nextLexem();
            var expr = expr();
            return new ASTNodes.AssignOperationNode(id, expr, '+', pos);
        }
        else if(at(LexerUnit.TokenType.ASSIGNMINUS)){
            nextLexem();
            var expr = expr();
            return new ASTNodes.AssignOperationNode(id, expr, '-', pos);
        }
        else if(at(LexerUnit.TokenType.ASSIGNMULTIPLE)){
            nextLexem();
            var expr = expr();
            return new ASTNodes.AssignOperationNode(id, expr, '*', pos);
        }
        else if(at(LexerUnit.TokenType.ASSIGNDIVIDE)){
            nextLexem();
            var expr = expr();
            return new ASTNodes.AssignOperationNode(id, expr, '/', pos);
        }
        else if(at(LexerUnit.TokenType.LEFT_PAREN)){
            nextLexem();
            var expr = exprList();
            requires(LexerUnit.TokenType.RIGHT_PAREN);
            return new ASTNodes.ProcCallNode(id, expr, pos);
        }
        else expectedError(LexerUnit.TokenType.ASSIGN, LexerUnit.TokenType.LEFT_PAREN);
        return null;
    }

    private LexerUnit.TokenType peekNextTokenType() {
        if (current + 1 < tokens.size()) {
            return tokens.get(current + 1).type;
        }
        return LexerUnit.TokenType.EOF;
    }

    private char getAssignOpChar(LexerUnit.TokenType type) {
        switch (type) {
            case ASSIGNPLUS: return '+';
            case ASSIGNMINUS: return '-';
            case ASSIGNMULTIPLE: return '*';
            case ASSIGNDIVIDE: return '/';
            default: return '=';
        }
    }

    private ASTNodes.ArrayAccessNode arrayAccess() throws Exception {
        var pos = currentToken().position;
        var id = ident();
        requires(LexerUnit.TokenType.LEFT_BRACKET);
        var index = expr();
        requires(LexerUnit.TokenType.RIGHT_BRACKET);
        return new ASTNodes.ArrayAccessNode(id, index, pos);
    }

    public ASTNodes.ArrayLiteralNode arrayLiteral() throws Exception {
        var pos = currentToken().position;
        requires(LexerUnit.TokenType.LEFT_BRACKET);
        var elements = new ArrayList<ASTNodes.ExprNode>();

        if(!at(LexerUnit.TokenType.RIGHT_BRACKET)){
            elements.add(expr());
            while(at(LexerUnit.TokenType.COMMA)){
                nextLexem();
                elements.add(expr());
            }
        }

        requires(LexerUnit.TokenType.RIGHT_BRACKET);
        return new ASTNodes.ArrayLiteralNode(elements, pos);
    }


    /// ArrayDeclaration := 'array' Id ('[' Expr ']')? ('=' ArrayLiteral)?
    public ASTNodes.ArrayDeclarationNode arrayDeclaration() throws Exception {
        var pos = currentToken().position;
        var id = ident();
        ASTNodes.ExprNode size = null;
        var initialElements = new ArrayList<ASTNodes.ExprNode>();

        // Обработка размера массива
        if(at(LexerUnit.TokenType.LEFT_BRACKET)){
            nextLexem();
            size = expr();
            requires(LexerUnit.TokenType.RIGHT_BRACKET);
        }

        // Обработка инициализации arr[3] = [1, 2, 3]
        if(at(LexerUnit.TokenType.ASSIGN)){
            nextLexem();
            initialElements = arrayLiteral().elements;
        }

        return new  ASTNodes.ArrayDeclarationNode(id, size, initialElements, pos);
    }

    public ASTNodes.ExprNode expr() throws Exception {
        var expr = comp();
        while (at(LexerUnit.TokenType.LESS, LexerUnit.TokenType.GREATER,
                LexerUnit.TokenType.GREATEREQUAL, LexerUnit.TokenType.LESSEQUAL,
                LexerUnit.TokenType.EQUAL, LexerUnit.TokenType.NOTEQUAL)) {
            var op = nextLexem();
            var right = comp();
            expr = new ASTNodes.BinOpNode(expr, right, op.type, expr.position);
        }
        return expr;
    }

    public ASTNodes.IdNode ident() throws Exception {
        var id = requires(LexerUnit.TokenType.ID);
        return new ASTNodes.IdNode(id.value.toString(), id.position);
    }

    public ASTNodes.ExprListNode exprList() throws Exception {
        var exprList = new ASTNodes.ExprListNode();
        exprList.add(expr());
        while (at(LexerUnit.TokenType.COMMA)) {
            nextLexem();
            exprList.add(expr());
        }
        return exprList;
    }

    public ASTNodes.ExprNode comp() throws Exception {
        if (isAtEnd()) return null;
        var expr = term();
        while (at(LexerUnit.TokenType.PLUS, LexerUnit.TokenType.MINUS, LexerUnit.TokenType.OR)) {
            var op = nextLexem();
            var right = term();
            expr = new ASTNodes.BinOpNode(expr, right, op.type, expr.position);
        }
        return expr;
    }

    public ASTNodes.ExprNode term() throws Exception {
        var expr = factor();
        while (at(LexerUnit.TokenType.MULTIPLE, LexerUnit.TokenType.DIVIDE, LexerUnit.TokenType.AND)) {
            var op = nextLexem();
            var right = factor();
            expr = new ASTNodes.BinOpNode(expr, right, op.type, expr.position);
        }
        return expr;
    }

    public ASTNodes.ExprNode factor() throws Exception {
        var position = currentToken().position;

        if(at(LexerUnit.TokenType.LEFT_BRACKET))
            return arrayLiteral();
        else if (at(LexerUnit.TokenType.INT))
            return new ASTNodes.IntNode(Integer.parseInt(nextLexem().value.toString()), position);
        else if (at(LexerUnit.TokenType.DOUBLELITERAL))
            return new ASTNodes.DoubleNode(Double.parseDouble(nextLexem().value.toString()), position);
        else if(at(LexerUnit.TokenType.BIGINTEGERLITERAL))
            return new ASTNodes.BigIntNode(nextLexem().value.toString(), position);

        else if (at(LexerUnit.TokenType.LEFT_PAREN)) {
            nextLexem();
            var res = expr();
            requires(LexerUnit.TokenType.RIGHT_PAREN);
            return res;
        }

        else if (at(LexerUnit.TokenType.ID)) {
            var id = ident();

            // Доступ по индексу
            if(at(LexerUnit.TokenType.LEFT_BRACKET)){
                nextLexem();
                var index = expr();
                requires(LexerUnit.TokenType.RIGHT_BRACKET);
                return new ASTNodes.ArrayAccessNode(id, index, position);
            }
            else if (at(LexerUnit.TokenType.LEFT_PAREN)) {
                nextLexem();
                var exprlst = exprList();
                var res = new ASTNodes.FuncCallNode(id, exprlst, position);
                requires(LexerUnit.TokenType.RIGHT_PAREN);
                return res;
            }
            return id;
        }

        else
            CompilerException.syntaxError("Exc" + peekToken().type.toString() + " найдено.", peekToken().position);
        return null;
    }
}
/*
Program := StatementList
StatementList := Statement (';' Statement)*
Statement := Assign | ProcCall | IfStatement | WhileStatement
  | BlockStatement
Assign := Id ('=' | '+=' | '-=' | '*=' | '/=') Expr
ProcCall := Id '(' ExprList ')
FuncCall := Id '(' ExprList ')
WhileStatement := while Expr do Statement
ForStatement := for Assign, Expr, AssignPlus do
IfStatement := if Expr then Statement [else Statement]
BlockStatement := '{' StatementList '}'
Expr := Comp (CompOp Comp)*
CompOp := '<' | '>' | '<=' | '>=' | '==' | '!='
Comp := Term (AddOp Term)*
AddOp := '+' | '-' | '||'
Term := Factor (MultOp Factor)*
MultOp := '*' | '/' | '&&'
Factor := IntNum | DoubleNum | FuncCall | '(' Expr ')
ExprList := Expr (',' Expr)*
ArrayDeclaration := 'array' Id ('[' Expr ']')? ('=' ArrayLiteral)?
*/
