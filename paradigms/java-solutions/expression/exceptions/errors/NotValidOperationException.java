package expression.exceptions.errors;

public class NotValidOperationException extends ParsingExceptions {
    public NotValidOperationException(String e) {
        super("Not a valid operation: " + e);
    }
}
