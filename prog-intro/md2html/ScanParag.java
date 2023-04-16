package md2html;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class ScanParag implements AutoCloseable {
    private BufferedReader reader;
    private String line;
    private boolean isEndOfFile;

    public ScanParag(String in, Charset encod) throws UnsupportedEncodingException, IOException {
        reader =  new BufferedReader(new FileReader(in, encod));
        isEndOfFile = false;
    }

    public boolean hasNextLine() throws IOException {
        if (isEndOfFile) {
            return false;
        }
        line = reader.readLine();
        if (line == null) {
            isEndOfFile = true;
            return false;
        }
        return true;
    }

    public boolean isEmpty() {
        return isEndOfFile;
    }

    public String getLine() {
        return line;
    }

    public boolean isNextLine() {
        return line.isEmpty();
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
