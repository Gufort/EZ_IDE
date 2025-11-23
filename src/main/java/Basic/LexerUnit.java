package Basic;

import ExceptionLogic.CompilerException;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.regex.Pattern;

public class LexerUnit {
    public enum TokenType {
        INT, BIGINTEGERLITERAL, DOUBLELITERAL, STRINGLITERAL, ID,
        PLUS, MINUS, MULTIPLE, DIVIDE, DOT,
        SEMICOLON, COMMA, LEFT_PAREN, RIGHT_PAREN, LEFT_BRACKET, RIGHT_BRACKET, COLON, LEFT_BRACE, RIGHT_BRACE,
        ASSIGN, ASSIGNPLUS, ASSIGNMINUS, ASSIGNMULTIPLE, ASSIGNDIVIDE,
        EQUAL, LESS, GREATER, GREATEREQUAL, LESSEQUAL, NOTEQUAL,
        AND, OR, NOT,
        EOF,
        TRUE, FALSE, IF, ELSE, WHILE, DO, THEN, FOR
    }

    public static  Dictionary<String, TokenType> KeyWords = new Hashtable<String, TokenType>();
    static {
        KeyWords.put("True", TokenType.TRUE);
        KeyWords.put("False", TokenType.FALSE);

        KeyWords.put("int", TokenType.ID);
        KeyWords.put("double", TokenType.ID);
        KeyWords.put("string", TokenType.ID);
        KeyWords.put("bool", TokenType.ID);
        KeyWords.put("bi", TokenType.ID);
        KeyWords.put("function", TokenType.ID);

        KeyWords.put("if", TokenType.IF);
        KeyWords.put("while", TokenType.WHILE);
        KeyWords.put("else", TokenType.ELSE);
        KeyWords.put("do", TokenType.DO);
        KeyWords.put("then", TokenType.THEN);
        KeyWords.put("for", TokenType.FOR);
    }

    public static TokenType[] ArithmeticOperations  = new TokenType[]{
            TokenType.PLUS, TokenType.MINUS, TokenType.MULTIPLE, TokenType.DIVIDE
    };
    public static TokenType[] CompareOperations = new TokenType[]{
            TokenType.EQUAL, TokenType.LESS, TokenType.GREATER,
            TokenType.LESSEQUAL, TokenType.NOTEQUAL, TokenType.GREATEREQUAL
    };
    public static TokenType[] LogicalOperators = new TokenType[]{
            TokenType.AND, TokenType.OR, TokenType.NOT
    };

    public static class Token{
        public TokenType type;
        public Object value;
        public Position position;
        public Token(TokenType type, Object value, int line, int column) {
            this.type = type;
            this.value = value;
            position = new Position(line, column);
        }
    }

    public static class Lexer {
        public ArrayList<Token> tokens = new ArrayList<Token>();
        public String code;//Код программы, который анализируется в конструкторе

        private int line = 1;//Текущая строкаs
        private int column = 0;//Текущий столбец
        private int currentPosition = 0;//Текущая позиция
        private int start = 0;//Стартовая позиция токена
        private int position0 = 0;//начало текущего

        private Position getCurrentPosition() { return new Position(line, column); }
        private boolean isAtEnd() {return currentPosition >= code.length(); }
        private char peekChar(){ return isAtEnd() ? '\0' :  code.charAt(currentPosition); }
        private boolean isAlpha(char c) {
            return Pattern.matches( "[A-Za-zА-Яа-яёЁ_]", String.valueOf(c));
        }
        private boolean isAlphaNumeric(char c) {
            return isAlpha(c) || Character.isDigit(c);
        }
        private char peekNextChar(){
            var pos = currentPosition + 1;
            return (pos > code.length()) ? '\0' : code.charAt(pos);
        }

        public char advance(){
            var temp = code.charAt(currentPosition);
            currentPosition++;
            return temp;
        }

        private void addToken(TokenType type, Object value){
            tokens.add(new Token(type, value, line, start + 1 - position0));
        }

        private boolean isMatch(char expected){
            if(peekChar() != expected)
                return false;
            else{
                currentPosition++;
                return true;
            }
        }

        private void identifier(){
            while(isAlphaNumeric(peekChar()))
                advance();
            var value = code.substring(start, currentPosition);
            var type = TokenType.ID;

            if(KeyWords.get(value) != null)
                type = KeyWords.get(value);

            addToken(type, value);
        }

        private void getString(){
            while(peekChar() != '"'){
                if(peekChar() == '\n'){
                    line++;
                    column = 1;
                }
                advance();
            }
            advance();
            var value = code.substring(start + 1, currentPosition - 1);
            addToken(TokenType.STRINGLITERAL, value);
        }

        private void getNumber(){
            while(Character.isDigit(peekChar()))
                advance();

            if(peekChar() == 'b' && peekNextChar() == 'i'){
                advance();
                advance();
                var value = code.substring(start, currentPosition - 2); // без bi
                addToken(TokenType.BIGINTEGERLITERAL, value);
                return;
            }

            if(peekChar() == '.' && Character.isDigit(peekNextChar())){
                advance();
                while(Character.isDigit(peekChar()))
                    advance();
                var value = code.substring(start, currentPosition);
                var toDouble = Double.parseDouble(value);
                addToken(TokenType.DOUBLELITERAL, toDouble);
                return;
            }
            var value = code.substring(start, currentPosition);
            addToken(TokenType.INT, value);
        }



        public String[] lines;
        public Lexer(String code){
            this.code = code;
            lines = code.split("\n");
        }

        public String[] getLines(){ return code.split("\n"); }

        public void analize() throws CompilerException.LexerException {
            while (!isAtEnd()) {
                start = currentPosition;
                var ch = advance();

                switch (ch) {
                    case ',':
                        addToken(TokenType.COMMA, null);
                        break;
                    case ';':
                        addToken(TokenType.SEMICOLON, null);
                        break;
                    case '(':
                        addToken(TokenType.LEFT_PAREN, null);
                        break;
                    case ')':
                        addToken(TokenType.RIGHT_PAREN, null);
                        break;
                    case '{':
                        addToken(TokenType.LEFT_BRACE, null);
                        break;
                    case '}':
                        addToken(TokenType.RIGHT_BRACE, null);
                        break;
                    case '[':
                        addToken(TokenType.LEFT_BRACKET, null);
                        break;
                    case ']':
                        addToken(TokenType.RIGHT_BRACKET, null);
                        break;

                    case '+':
                        addToken(isMatch('=') ? TokenType.ASSIGNPLUS : TokenType.PLUS, null);
                        break;
                    case '-':
                        addToken(isMatch('=') ? TokenType.ASSIGNMINUS : TokenType.MINUS, null);
                        break;
                    case '*':
                        addToken(isMatch('=') ? TokenType.ASSIGNMULTIPLE : TokenType.MULTIPLE, null);
                        break;
                    case '/':
                        if (isMatch('/')) {
                            while (peekChar() != '\n' && !isAtEnd())
                                advance();
                        } else addToken(isMatch('=') ? TokenType.ASSIGNDIVIDE : TokenType.DIVIDE, null);
                        break;

                    case '!':
                        addToken(isMatch('=') ? TokenType.NOTEQUAL : TokenType.NOT, null);
                        break;
                    case '=':
                        addToken(isMatch('=') ? TokenType.EQUAL : TokenType.ASSIGN, null);
                        break;
                    case '>':
                        addToken(isMatch('=') ? TokenType.GREATEREQUAL : TokenType.GREATER, null);
                        break;
                    case '<':
                        addToken(isMatch('=') ? TokenType.LESSEQUAL : TokenType.LESS, null);
                        break;
                    case '&':
                        if (isMatch('&'))
                            addToken(TokenType.AND, null);
                        else CompilerException.lexerError("Ожидается &&", getCurrentPosition());
                        break;
                    case '|':
                        if (isMatch('|'))
                            addToken(TokenType.OR, null);
                        else CompilerException.lexerError("Ожидается ||", getCurrentPosition());
                        break;
                    case '\r':
                        break;
                    case '\7':
                        break;
                    case ' ':
                        break;
                    case '\n':
                        line += 1;
                        column = 1;
                        position0 = currentPosition;
                        break;
                    case '"':
                        getString();
                        break;

                    default:
                        if (Character.isDigit(ch))
                            getNumber();
                        else if (isAlpha(ch))
                            identifier();
                        else CompilerException.lexerError("Неизвестный символ " + ch, getCurrentPosition());
                        break;
                }
            }
            addToken(TokenType.EOF, null);
        }
    }
}
