package expression.exceptions;

import expression.*;
import expression.exceptions.errors.OverflowException;
import expression.exceptions.errors.ZeroDividingException;

public class CheckedDivide extends Divide {
    public CheckedDivide(CurrentExpression first, CurrentExpression second) {
        super(first, second);
    }

    @Override 
    protected int calculate(int a, int b) throws OverflowException {
        if (b == 0) {
            throw new ZeroDividingException(this.toString());
        } else if (a == Integer.MIN_VALUE && b == -1) {
            throw new OverflowException(a + getOperation() + b + " in " + this.toString());
        } else {
            return a / b;
        }
    }
}   
