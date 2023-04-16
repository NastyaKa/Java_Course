package expression;

public class Pow extends AnyExpression {
    public Pow(CurrentExpression first, CurrentExpression second) {
        super(first, second);
    }

    @Override
    protected String getOperation() {
        return " ** ";
    }

    @Override 
    protected int calculate(int a, int b) {
        if (b < 0) {
            if (b == -1 && a == 1) {
                return 1;
            } else {
                return 0;
            }
        }
        int ans = 1;
        while (b > 0) {
            if (b % 2 == 1) {
                ans *= a;
            }
            a *= a;
            b /= 2;
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
