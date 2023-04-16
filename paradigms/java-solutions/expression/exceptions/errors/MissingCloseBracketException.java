package expression.exceptions.errors;

public class MissingCloseBracketException extends ParsingExceptions {
    public MissingCloseBracketException() {
        super("Close bracket missed");
    }
}
