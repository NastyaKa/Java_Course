package expression.exceptions;

import expression.*;
import expression.exceptions.errors.OverflowException;

public class CheckedAdd extends Add {
    public CheckedAdd(CurrentExpression first, CurrentExpression second) {
        super(first, second);
    }

    @Override 
    protected int calculate(int a, int b) throws OverflowException {
        if (a < Integer.MIN_VALUE - b && a < 0 && b < 0 || a > Integer.MAX_VALUE - b && a > 0 && b > 0) {
            throw new OverflowException(a + getOperation() + b + " in " + this.toString());
        } else {
            return a + b;
        }
    }
}
