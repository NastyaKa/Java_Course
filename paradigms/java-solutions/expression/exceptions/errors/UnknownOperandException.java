package expression.exceptions.errors;

public class UnknownOperandException extends ParsingExceptions {
    public UnknownOperandException(String oper) {
        super("Unknown operation " + oper);
    }
}

