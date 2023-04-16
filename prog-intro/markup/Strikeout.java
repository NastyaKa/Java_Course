package markup;

import java.util.List;

public class Strikeout extends ModifyText {

    private static final String SURRFIRSTHTML = "<s>";
    private static final String SURRLASTHTML = "</s>";
    private static final String SURRMARKDOWN = "~";

    public Strikeout(List<TextElement> list) {
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