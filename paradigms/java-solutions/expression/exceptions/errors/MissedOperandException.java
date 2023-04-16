package expression.exceptions.errors;

public class MissedOperandException extends ParsingExceptions {
    public MissedOperandException(String e) {
        super("Operand was not found: " + e);
    }
}
