package Basic;

public class Position{
    public int line;
    public int column;
    public Position(int line, int column){
        this.line = line;
        this.column = column;
    }

    @Override
    public String toString() {
        return "(" + line + ", " + column + ")";
    }
}
