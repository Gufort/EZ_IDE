package Basic;

import ExceptionLogic.CompilerException;

import java.math.BigInteger;

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

            // Опциональные скобки
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
        else if(at(LexerUnit.TokenType.LEFT_PAREN)){
            nextLexem();
            var expr = exprList();
            requires(LexerUnit.TokenType.RIGHT_PAREN);
            return new ASTNodes.ProcCallNode(id, expr, pos);
        }
        else expectedError(LexerUnit.TokenType.ASSIGN, LexerUnit.TokenType.LEFT_PAREN);
        return null;
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

        if (at(LexerUnit.TokenType.INT)) {
            return new ASTNodes.IntNode(Integer.parseInt(nextLexem().value.toString()), position);
        }
        else if (at(LexerUnit.TokenType.DOUBLELITERAL)) {
            return new ASTNodes.DoubleNode(Double.parseDouble(nextLexem().value.toString()), position);
        }
        else if(at(LexerUnit.TokenType.BIGINTEGERLITERAL)){
            return new ASTNodes.BigIntNode(nextLexem().value.toString(), position);
        }
        else if (at(LexerUnit.TokenType.LEFT_PAREN)) {
            nextLexem();
            var res = expr();
            requires(LexerUnit.TokenType.RIGHT_PAREN);
            return res;
        }

        else if (at(LexerUnit.TokenType.ID)) {
            var id = ident();
            if (at(LexerUnit.TokenType.LEFT_PAREN)) {
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
/*Program := StatementList
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
ExprList := Expr (',' Expr)* */
