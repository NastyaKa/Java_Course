package markup;

import java.util.List;

public class UnorderedList extends ModifyList {

    private static final String SURRF = "<ul>";
    private static final String SURRL = "</ul>";

    public UnorderedList(List<ListItem> list) {
        super(list, SURRF, SURRL);
    }

    @Override
    public void toHtml(StringBuilder text) {
        super.toHtml(text);
    }
}
