package expression.exceptions;

import expression.Const;
import expression.exceptions.errors.OverflowException;

public class CheckedConst extends Const {
    public CheckedConst(int expr) {
        super(expr);
    }

    @Override
    public int getConst() throws OverflowException {
        String cur = getAnyConst().toString();
        if (cur.startsWith("-") && cur.compareTo(String.valueOf(Integer.MAX_VALUE)) > 0 || !cur.startsWith("-") && cur.compareTo(String.valueOf(Integer.MIN_VALUE)) < 0) {
            throw new OverflowException("-" + cur + " in " + this.toString());
        } else {
            return getAnyConst().intValue();
        }
    }
}
