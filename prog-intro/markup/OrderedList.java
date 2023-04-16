package markup;

import java.util.List;

public class OrderedList extends ModifyList {

    private static final String SURRF = "<ol>";
    private static final String SURRL = "</ol>";

    public OrderedList(List<ListItem> list) {
        super(list, SURRF, SURRL);
    }

    @Override
    public void toHtml(StringBuilder text) {
        super.toHtml(text);
    }
}
