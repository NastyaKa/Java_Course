package expression;

public class Log extends AnyExpression {
    public Log(CurrentExpression first, CurrentExpression second) {
        super(first, second);
    }

    @Override
    protected String getOperation() {
        return " // ";
    }

    @Override 
    protected int calculate(int a, int b) {
        int ans = 0, cur = 1;
        while (cur <= a / b) {
            cur *= b;
            ans++;
        }
        return ans;
    }

    @Override
    public boolean surround() {
        return true;
    }
    
    @Override 
    public int priority() {
        return 2;
    }
}
