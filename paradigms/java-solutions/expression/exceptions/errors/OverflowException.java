package expression.exceptions.errors;

public class OverflowException extends CalculatingException {
    public OverflowException(String e) {
        super("overflow: " + e);
    }
}
