package markup;

import java.util.List;

public abstract class ModifyText implements TextElement {

    private List<TextElement> list;
    private String surrFirstHtml;
    private String surrLastHtml;
    private String surrMarkdown;

    protected ModifyText(List<TextElement> list, String surrFirstHtml, String surrLastHtml, String surrMarkdown) {
        this.list = list;
        this.surrFirstHtml = surrFirstHtml;
        this.surrLastHtml = surrLastHtml;
        this.surrMarkdown = surrMarkdown;
    }

    @Override
    public void toMarkdown(StringBuilder text) {
        text.append(surrMarkdown);
        for (TextElement curEl : list) {
            curEl.toMarkdown(text);
        }
        text.append(surrMarkdown);
    }

    @Override
    public void toHtml(StringBuilder text) {
        text.append(surrFirstHtml);
        for (TextElement curEl : list) {
            curEl.toHtml(text);
        }
        text.append(surrLastHtml);
    }
}
