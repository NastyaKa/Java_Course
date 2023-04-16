package expression.exceptions.errors;

public class ZeroDividingException extends CalculatingException {
    public ZeroDividingException(String e) {
        super("division by zero: " + e);
    }
}
