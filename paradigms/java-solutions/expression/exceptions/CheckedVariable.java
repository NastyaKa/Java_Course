package expression.exceptions;

import expression.*;
import expression.exceptions.errors.ParsingExceptions;
import expression.exceptions.errors.UnknownVariableException;

public class CheckedVariable extends Variable {
    public CheckedVariable(String x) throws ParsingExceptions {
        super(x);
        if (!x.equals("x") && !x.equals("y") && !x.equals("z")) {
            throw new UnknownVariableException(x);
        }
    }
}
