package expression;

public class L0 extends UnaryExpression {
    public L0(CurrentExpression expr) {
        super(expr);
    }

    @Override
    public String getSign() {
        return "l0";
    }

    @Override
    public int calculate(int x) {
        return Integer.numberOfLeadingZeros(x);
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
