package expression.exceptions;

public class StringCharSource implements CharSource {
    private final String string;
    private int pos;

    public StringCharSource(String string) {
        this.string = string;
        pos = 0;
    }

    @Override
    public char next() {
        return string.charAt(pos++);
    }

    @Override
    public boolean hasNext() {
        return pos < string.length();
    }
}
