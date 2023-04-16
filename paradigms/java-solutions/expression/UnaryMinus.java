package expression;

public class UnaryMinus extends UnaryExpression {
    public UnaryMinus(CurrentExpression expr) {
        super(expr);
    }

    @Override
    public String getSign() {
        return "-";
    }

    @Override
    public int calculate(int x) {
        return -1 * x;
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
