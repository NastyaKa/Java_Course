package expression;

public class Add extends AnyExpression {
    public Add(CurrentExpression first, CurrentExpression second) {
        super(first, second);
    }

    @Override
    protected String getOperation() {
        return " + ";
    }

    @Override 
    protected int calculate(int a, int b) {
        return a + b;
    }

    @Override 
    public int priority() {
        return 0;
    }

    @Override
    public boolean surround() {
        return false;
    }
}
