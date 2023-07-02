package info.kgeorgiy.ja.kaimakova.walk;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import static java.nio.file.FileVisitResult.*;

public final class FileVisitorImpl extends SimpleFileVisitor<Path> {

    private final BufferedWriter output;
    private final String SEPARATOR = System.lineSeparator();
    private static final int BUF_SIZE = 4096;
    private final byte[] buffer = new byte[BUF_SIZE];

    private final MessageDigest digest;

    private final int HSH_LENGTH = 64;
    private final String ZERO_HSH = "0".repeat(HSH_LENGTH);

    public FileVisitorImpl(BufferedWriter output) throws NoSuchAlgorithmException {
        this.output = output;
        digest = MessageDigest.getInstance("SHA-256");
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        digest.reset();
        String ans = "";
        try (BufferedInputStream reader = new BufferedInputStream(Files.newInputStream(file))) {
            int size;
            while ((size = reader.read(buffer)) >= 0) {
                digest.update(buffer, 0, size);
            }
            ans = HexFormat.of().formatHex(digest.digest());
        } catch (IOException e) {
            System.err.println("IOException: recursiveWalk: read exception: " + e.getMessage());
        } finally {
            if (ans.length() == 0) {
                onFailureFormattedOut(file.toString());
            } else {
                formattedOut(ans, file.toString());
            }
        }
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        onFailureFormattedOut(file.toString());
        return CONTINUE;
    }

    private void formattedOut(String hsh, String fileName) throws IOException {
        output.write(hsh + " " + fileName + SEPARATOR);
    }

    public void onFailureFormattedOut(String fileName) throws IOException {
        formattedOut(ZERO_HSH, fileName);
    }
}
