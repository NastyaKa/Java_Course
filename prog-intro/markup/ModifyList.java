package markup;

import java.util.List;

public abstract class ModifyList implements ListElement {
    private List<ListItem> list;
    private String surrF;
    private String surrL;

    public ModifyList(List<ListItem> list, String surrF, String surrL) {
        this.list = list;
        this.surrF = surrF;
        this.surrL = surrL;
    }

    @Override
    public void toHtml(StringBuilder text) {
        text.append(surrF);
        for (ListItem cur : list) {
            cur.toHtml(text);
        }
        text.append(surrL);
    }
}
