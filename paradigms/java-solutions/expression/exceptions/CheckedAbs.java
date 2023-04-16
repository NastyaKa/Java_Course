package expression.exceptions;

import expression.*;
import expression.exceptions.errors.OverflowException;

public class CheckedAbs extends Abs {
    public CheckedAbs(CurrentExpression exrp) {
        super(exrp);
    }

    @Override 
    public int calculate(int a) throws OverflowException {
        if (a == Integer.MIN_VALUE) {
            throw new OverflowException(getSign() + " " + a + " in " + this.toString());
        } else {
            return a < 0 ? -a : a;
        }
    }
}
