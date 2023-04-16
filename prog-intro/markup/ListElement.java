package markup;

public interface ListElement extends ToHtml {
    @Override
    public void toHtml(StringBuilder text);
}
