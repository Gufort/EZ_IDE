package Basic;

import ExceptionLogic.CompilerException;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class ParserBase {
    protected LexerUnit.Lexer lexer;
    protected ArrayList<LexerUnit.Token> tokens;
    protected int current;

    public ParserBase(LexerUnit.Lexer lexer) throws Exception{
        this.lexer = lexer;
        lexer.analize();
        tokens = lexer.tokens;
    }

    /// Проверить, что тип текущего токена совпадает с данным типом
    public boolean check(LexerUnit.TokenType type){
        return peekToken().type == type;
    }

    public LexerUnit.Token nextLexem() {
        if (!isAtEnd()) current++;
        return previousToken();
    }


    /// Проверить, что тип текущего токена совпадает с одним из данных типов и перейти к следующему токену
    public boolean at(LexerUnit.TokenType... types){
        return Arrays.stream(types).anyMatch(this::check);
    }

    /// Проверить на соответствие и вернуть токен или выбросить ошибку
    public LexerUnit.Token requires(LexerUnit.TokenType... types) throws Exception{
        if(at(types))
            return nextLexem();
        expectedError(types);
        return null;
    }

    public boolean isAtEnd() {
        return peekToken().type == LexerUnit.TokenType.EOF;
    }
    public LexerUnit.Token peekToken() {
        return tokens.get(current);
    }
    public LexerUnit.Token currentToken() { return tokens.get(current); }
    public LexerUnit.Token previousToken() { return tokens.get(current-1); }
    public void expectedError(LexerUnit.TokenType... types) throws Exception{
        String expected = String.join(" или ", Arrays.stream(types).map(Enum::name).toArray(String[]::new));
        CompilerException.syntaxError(expected + " ожидалось, но " + peekToken().type.name() + " найдено", peekToken().position);
    }
}
