package expression.exceptions;

import expression.*;
import expression.exceptions.errors.OverflowException;

public class CheckedNegate extends UnaryMinus {
    public CheckedNegate(CurrentExpression expr) {
        super(expr);
    }

    @Override
    public int calculate(int x) throws OverflowException {
        if (x == Integer.MIN_VALUE) {
            throw new OverflowException(getSign() + x + " in " + this.toString());
        } else {
            return -x;
        }
    }
}
