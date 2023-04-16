package markup;

public class Text implements TextElement {

    private String current;

    public Text(final String text) {
        current = text;
    }

    @Override 
    public void toMarkdown(StringBuilder text) {
        text.append(current);
    }

    @Override 
    public void toHtml(StringBuilder text) {
        text.append(current);
    }
}
