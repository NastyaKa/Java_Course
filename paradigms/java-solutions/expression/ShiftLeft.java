package expression;

public class ShiftLeft extends AnyExpression {
    public ShiftLeft(CurrentExpression first, CurrentExpression second) {
        super(first, second);
    }

    @Override
    protected String getOperation() {
        return " << ";
    }

    @Override 
    protected int calculate(int a, int b) {
        return a << b;
    }

    @Override 
    public int priority() {
        return -1;
    }

    @Override
    public boolean surround() {
        return true;
    }
}
