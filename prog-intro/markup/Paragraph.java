package markup;

import java.util.List;

public class Paragraph implements ToMarkdown, ListElement {
    List<TextElement> list;

    public Paragraph(List<TextElement> list) {
        this.list = list;
    }

    @Override
    public void toMarkdown(StringBuilder text) {
        for (TextElement cur : list) {
            cur.toMarkdown(text);
        }
    }

    @Override
    public void toHtml(StringBuilder text) {
        for (TextElement cur : list) {
            cur.toHtml(text);
        }
    }
}
