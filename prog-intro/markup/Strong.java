package markup;

import java.util.List;

public class Strong extends ModifyText {

    private static final String SURRFIRSTHTML = "<strong>";
    private static final String SURRLASTHTML = "</strong>";
    private static final String SURRMARKDOWN = "__";

    public Strong(List<TextElement> list) {
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