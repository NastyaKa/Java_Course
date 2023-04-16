package expression;

public class T0 extends UnaryExpression {
    public T0(CurrentExpression expr) {
        super(expr);
    }

    @Override
    public String getSign() {
        return "t0";
    }

    @Override
    public int calculate(int x) {
        return Integer.numberOfTrailingZeros(x);
    }
    
    @Override 
    public int priority() {
        return 2;
    }

    @Override
    public boolean surround() {
        return true;
    }
}
