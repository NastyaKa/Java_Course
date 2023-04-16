package markup;

import java.util.List;

public class ListItem implements ToHtml {

    private List<ListElement> list;
    private static final String SURRF = "<li>";
    private static final String SURRL = "</li>";

    public ListItem(List<ListElement> list) {
        this.list = list;
    }

    @Override
    public void toHtml(StringBuilder text) {
        text.append(SURRF);
        for (ListElement cur : list) {
            cur.toHtml(text);
        }
        text.append(SURRL);
    }

}