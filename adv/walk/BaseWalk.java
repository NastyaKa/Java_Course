package info.kgeorgiy.ja.kaimakova.walk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;

public final class BaseWalk {
    private Path validatePath(final String arg) {
        try {
            return Paths.get(arg);
        } catch (InvalidPathException e) {
            System.err.println("Error! failed to convert the path: " + e.getMessage());
            return null;
        }
    }

    public void walk(final String[] args, final int maxDepth) {
        if (args == null || args.length != 2 || args[0] == null || args[1] == null) {
            System.err.println("Error! Expected <input_file> <output_file>");
        } else {
            final Path ofInput, ofOutput;

            if ((ofInput = validatePath(args[0])) == null || (ofOutput = validatePath(args[1])) == null) {
                return;
            }

            final Path parentDir = ofOutput.getParent();
            if (parentDir != null) {
                try {
                    if (!Files.exists(parentDir)) {
                        Files.createDirectories(parentDir);
                    }
                } catch (FileAlreadyExistsException e) {
                    System.err.println("FileAlreadyExistsException: creating output file: " + e.getMessage());
                    return;
                } catch (IOException e) {
                    // ignored
                }
            }

            try (BufferedReader input = Files.newBufferedReader(ofInput)) {
                try (BufferedWriter output = Files.newBufferedWriter(ofOutput)) {
                    FileVisitorImpl fileVisitor = new FileVisitorImpl(output);
                    String line;
                    try {
                        while ((line = input.readLine()) != null) {
                            Path getPathOfLine;
                            try {
                                if ((getPathOfLine = validatePath(line)) != null) {
                                    Files.walkFileTree(getPathOfLine, new HashSet<>(), maxDepth, fileVisitor);
                                } else {
                                    fileVisitor.onFailureFormattedOut(line);
                                }
                            } catch (IOException e) {
                                System.err.println("IOException: output when recursiveWalk: " + e.getMessage());
                                return;
                            }
                        }
                    } catch (IOException e) {
                        System.err.println("IOException : input.readline(): " + e.getMessage());
                    }
                } catch (IOException e) {
                    System.err.println("IOException: output open/create: " + e.getMessage());
                } catch (NoSuchAlgorithmException e) {
                    System.err.println("MessageDigest: recursiveWalk: " + e.getMessage());
                }
            } catch (IOException e) {
                System.err.println("IOException: input open/create: " + e.getMessage());
            }
        }
    }
}
