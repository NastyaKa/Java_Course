package expression.exceptions;

import expression.*;
import expression.exceptions.errors.CalculatingException;
import expression.exceptions.errors.OverflowException;
import expression.exceptions.errors.ZeroDividingException;

public class CheckedPow extends Pow {
    public CheckedPow(CurrentExpression first, CurrentExpression second) {
        super(first, second);
    }

    @Override 
    protected int calculate(int a, int b) throws CalculatingException {
        if (a == 0 && b < 0) {
            throw new ZeroDividingException(this.toString());
        } else if (b < 0 || a == 0 && b == 0) {
            throw new CalculatingException("not valid arguments for: " + a + getOperation() + b);
        } 
        int ans = 1;
        while (b > 0) {
            if (b % 2 == 1) {
                int res = ans * a;
                if (a != 0 && ans != 0 && (res / a != ans || res / ans != a)) {
                    throw new OverflowException(this.toString());
                } else {
                    ans = res;
                    b--;
                }
            }
            if (a == 0 || (a * a) / a == a) {
                a *= a;
                b /= 2;
            } else if (b != 0) {
                throw new OverflowException(a + " " + b + " " + this.toString());
            }
        }
        return ans;
    }
}
