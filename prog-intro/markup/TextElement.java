package markup;

public interface TextElement extends ToMarkdown, ToHtml {
    @Override
    public void toMarkdown(StringBuilder text);
    @Override
    public void toHtml(StringBuilder text);
}
