import java.io.Reader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Scanner implements AutoCloseable {

    private Reader reader;
    private char[] buf = new char[26];
    private int size;
    private int pos;
    private char saved;
    private boolean endOfFile;
    private String eol = System.lineSeparator();
    private char eolCh = eol.charAt(eol.length() - 1);
    private IsWord check;

    public Scanner(InputStream in, String encod, IsWord ch) throws IOException {
        reader = new InputStreamReader(in, encod);
        size = 0;
        pos = 0;
        saved = '.';
        endOfFile = false;
        check = ch;
    }

    public Scanner(InputStream in, IsWord ch) throws IOException {
        reader = new InputStreamReader(in);
        size = 0;
        pos = 0;
        saved = '.';
        endOfFile = false;
        check = ch;
    }

    private boolean read() throws IOException {
        if (endOfFile) {
            return false;
        }
        if (pos >= size) {
            size = reader.read(buf);
            pos = 0;
            if (size < 0) {
                endOfFile = true;
                return false;
            }
        }
        return true;
    }

    private boolean compareToNewLine() {
        if (eol.length() == 2) {
            return (saved == eol.charAt(0) && eol.charAt(1) == buf[pos]);
        }
        return eolCh == buf[pos];
    }

    private void skipSeprExpNextLine(IsWord ch) throws IOException {
        while (read() && !ch.isAllowed(buf[pos])) {
            if (compareToNewLine()) {
                break;
            }
            saved = buf[pos];
            pos++;
        }
    }

    private void skipAllSepr(IsWord ch) throws IOException {
        while (read() && !ch.isAllowed(buf[pos])) {
            saved = buf[pos];
            pos++;
        }
    }

    private boolean isLineEnd() throws IOException {
        return isEmpty() || compareToNewLine();
    }

    public char next() throws IOException {
        read();
        if (isEmpty()) {
            throw new IOException("Error! File is empty.");
        }
        saved = buf[pos];
        return buf[pos++];
    }

    public void close() throws IOException {
        reader.close();
    }

    public boolean isEmpty() {
        return endOfFile;
    }

    public boolean isEndOfLine() throws IOException {
        skipSeprExpNextLine(check);
        return isLineEnd();
    }

    public void skipLine() throws IOException {
        skipSeprExpNextLine(check);
        saved = buf[pos];
        pos++;
    }

    public boolean hasNextLine() throws IOException {
        skipSeprExpNextLine(check);
        return !isEmpty();
    }

    public boolean hasNext() throws IOException {
        skipAllSepr(check);
        return !isEmpty();
    }

    public String nextWord(IsWord ch) throws IOException {
        skipAllSepr(check);
        StringBuilder res = new StringBuilder();
        while (read() && !isLineEnd() && ch.isAllowed(buf[pos])) {
            res.append(next());
        }
        return res.toString();
    }

    public int nextInt() throws IOException {
        return Integer.parseInt(nextWord(check));
    }
}
