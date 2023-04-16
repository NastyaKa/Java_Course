package expression.exceptions;

import expression.*;
import expression.exceptions.errors.CalculatingException;
import expression.exceptions.errors.OverflowException;

public class CheckedLog extends Log {
    public CheckedLog(CurrentExpression first, CurrentExpression second) {
        super(first, second);
    }

    @Override 
    protected int calculate(int a, int b) throws CalculatingException {
        if (a < 1 || b <= 0 || b == 1) {
            throw new CalculatingException("not valid arguments for operation: " + a + " log " + b);
        }
        int ans = 0, cur = 1;
        while (cur <= a / b) {
            int res = b * cur;
            if (res / b != cur || res / cur != b) {
                throw new OverflowException(this.toString());
            } else {
                cur *= b;
                ans++;
            }
        }
        return ans;
    }
}
