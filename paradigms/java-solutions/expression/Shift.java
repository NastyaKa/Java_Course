package expression;

public class Shift extends AnyExpression {
    public Shift(CurrentExpression first, CurrentExpression second) {
        super(first, second);
    }

    @Override
    protected String getOperation() {
        return " >>> ";
    }

    @Override 
    protected int calculate(int a, int b) {
        return a >>> b;
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
