package markup;

import java.util.List;

public class Emphasis extends ModifyText {

    private static final String SURRFIRSTHTML = "<em>";
    private static final String SURRLASTHTML = "</em>";
    private static final String SURRMARKDOWN = "*";

    public Emphasis(List<TextElement> list) {
        super(list, SURRFIRSTHTML, SURRLASTHTML, SURRMARKDOWN);
    }

    @Override
    public void toMarkdown(StringBuilder text) {
        super.toMarkdown(text);
    }

    @Override
    public void toHtml(StringBuilder text) {
        super.toHtml(text);
    }
}