package md2html;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.nio.charset.StandardCharsets;

public class Md2Html {
    public static void main(String[] args) {
        List<StringBuilder> parags = new ArrayList<StringBuilder>();

        try (ScanParag in = new ScanParag(args[0], StandardCharsets.UTF_8)) {
            Paragraph parag = new Paragraph();
            while (!in.isEmpty()) {
                parag.toClean();
                while (in.hasNextLine() && !in.isNextLine()) {
                    if (!parag.isEmpty()) {
                        parag.add(System.lineSeparator());
                    }
                    parag.add(in.getLine());
                }
                if (!parag.isEmpty()) {
                    StringBuilder ans = new StringBuilder();
                    parag.toMarkdown(ans);
                    parags.add(ans);
                }
            }
        } catch (UnsupportedEncodingException e) {
            System.err.println("Problems with encoding:  " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Problems with input file:  " + e.getMessage());
        }

        try (BufferedWriter out = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(args[1]), StandardCharsets.UTF_8.name()))) {
            for (StringBuilder text : parags) {
                out.write(text.toString());
                out.newLine();
            }
        } catch (UnsupportedEncodingException e) {
            System.err.println("Problems with encoding:  " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Problems with output file:  " + e.getMessage());
        } 
    }
}