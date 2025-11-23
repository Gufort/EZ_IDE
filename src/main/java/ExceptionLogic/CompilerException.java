package ExceptionLogic;

import Basic.Position;

public class CompilerException{
    public static class BaseCompilerException extends Exception {
        public Position position;
        public BaseCompilerException(String message,Position position){
            super(message);
            this.position = position;
        }
    }

    public static class LexerException extends BaseCompilerException {
        public LexerException(String message,Position position){
            super(message,position);
        }
    }

    public static class SyntaxException extends BaseCompilerException {
        public SyntaxException(String message,Position position){
            super(message,position);
        }
    }

    public static class SemanticException extends BaseCompilerException {
        public SemanticException(String message,Position position){
            super(message,position);
        }
    }

    public static void lexerError(String message, Position position) throws LexerException{
        throw new LexerException(message, position);
    }

    public static void syntaxError(String message, Position position) throws SyntaxException{
        throw new SyntaxException(message, position);
    }

    public static void semanticError(String message, Position position) throws SemanticException{
        throw new SemanticException(message, position);
    }

    public static void outputError(String message, BaseCompilerException exception, String[] lines) throws Exception{
        var line = lines[exception.position.line - 1];
        System.out.println(message + " " + exception.position + ": " + exception.getMessage());
    }
}
