package expression;

public class Subtract extends AnyExpression {
    public Subtract(CurrentExpression first, CurrentExpression second) {
        super(first, second);
    }

    @Override
    protected String getOperation() {
        return " - ";
    }

    @Override 
    protected int calculate(int a, int b) {
        return a - b;
    }

    @Override 
    public int priority() {
        return 0;
    }
    
    @Override
    public boolean surround() {
        return true;
    }
}
