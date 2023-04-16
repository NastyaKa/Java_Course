package expression;

public class Abs extends UnaryExpression {
    public Abs(CurrentExpression expr) {
        super(expr);
    }

    @Override
    public String getSign() {
        return "abs";
    }

    @Override
    public int calculate(int x) {
        return x < 0 ? -x : x;
    }
    
    @Override 
    public int priority() {
        return 10;
    }

    @Override
    public boolean surround() {
        return true;
    }
}
