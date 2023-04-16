package expression;

public class Multiply extends AnyExpression {
    public Multiply(CurrentExpression first, CurrentExpression second) {
        super(first, second);
    }

    @Override
    protected String getOperation() {
        return " * ";
    }

    @Override 
    protected int calculate(int a, int b) {
        return a * b;
    }

    @Override
    public boolean surround() {
        return false;
    }
    
    @Override 
    public int priority() {
        return 1;
    }
}
