package expression.exceptions;

public class BaseParser {
    private final CharSource source;
    private char ch;
    private final char END = '\0';
    private boolean isEof;

    public BaseParser(CharSource source) {
        this.source = source;
        isEof = false;
        take();
    }

    protected boolean test(char expected) {
        return ch == expected;
    }

    protected char peek() {
        return ch;
    }

    protected char take() {
        final char cur = ch;
        if (source.hasNext()) {
            ch = source.next();
        } else {
            ch = END;
            isEof = true;
        }
        return cur;
    }

    protected boolean take(char expected) {
        if (test(expected)) {
            take();
            return true;
        }
        return false;
    }

    protected boolean eof() {
        return isEof;
    }

    protected boolean between(final char from, final char to) {
        return from <= ch && ch <= to;
    }
}
