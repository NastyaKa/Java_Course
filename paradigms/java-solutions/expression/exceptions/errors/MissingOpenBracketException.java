package expression.exceptions.errors;

public class MissingOpenBracketException extends ParsingExceptions {
    public MissingOpenBracketException() {
        super("Open bracket missed");
    }
}
