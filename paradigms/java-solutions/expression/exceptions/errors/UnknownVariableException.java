package expression.exceptions.errors;

public class UnknownVariableException extends ParsingExceptions {
    public UnknownVariableException(String var) {
        super("Unknown variable: " + var);
    }
}
