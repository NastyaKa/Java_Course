package expression.exceptions;

import expression.*;
import expression.exceptions.errors.OverflowException;

public class CheckedMultiply extends Multiply {
    public CheckedMultiply(CurrentExpression first, CurrentExpression second) {
        super(first, second);
    }

    @Override 
    protected int calculate(int a, int b) throws OverflowException {
        if (a == 0 || b == 0 || ((a * b) / a == b && (a * b) / b == a)) {
            return a * b;
        } else {
            throw new OverflowException(a + getOperation() + b + " in " + this.toString());
        }
    }
}
