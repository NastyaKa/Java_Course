package expression.exceptions.errors;

public class MissedOperationException extends ParsingExceptions {
    public MissedOperationException(String e) {
        super("Operation was not found: " + e);
    }
}
