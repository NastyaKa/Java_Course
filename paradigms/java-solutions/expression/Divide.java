package expression;

public class Divide extends AnyExpression {
    public Divide(CurrentExpression first, CurrentExpression second) {
        super(first, second);
    }

    @Override
    protected String getOperation() {
        return " / ";
    }

    @Override 
    public int priority() {
        return 1;
    }

    @Override
    public boolean surround() {
        return true;
    }

    @Override 
    protected int calculate(int a, int b) {
        return a / b;
    }
}
