package md2html;

import java.util.Map;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class Paragraph {
    private StringBuilder text;
    private MyStack stack;
    private final String[] markup = {"<<", ">>", "{{", "}}", "**", "__", "--", "*", "_", "`"};
    private final String[] markupClose = {">>", "{{"};
    private final String[] markupOpen = {"<<", "}}"};
    private final Character[] special = {'<', '>', '&'};
    private final Map<String, String> open2Html = new HashMap<>();
    private final Map<String, String> close2Html = new HashMap<>();
    private final Map<Character, String> specialCode = new HashMap<>();
    private final static char SCRN = '\\';

    public Paragraph() {
        text = new StringBuilder();
        stack = new MyStack();

        open2Html.put("`", "<code>");
        open2Html.put("*", "<em>");
        open2Html.put("_", "<em>");
        open2Html.put("__", "<strong>");
        open2Html.put("**", "<strong>");
        open2Html.put("--", "<s>");
        open2Html.put("<<", "<ins>");
        open2Html.put("}}", "<del>");

        close2Html.put("`", "</code>");
        close2Html.put("*", "</em>");
        close2Html.put("_", "</em>");
        close2Html.put("__", "</strong>");
        close2Html.put("**", "</strong>");
        close2Html.put("--", "</s>");
        close2Html.put(">>", "</ins>");
        close2Html.put("{{", "</del>");

        specialCode.put('<', "&lt;");
        specialCode.put('>', "&gt;");
        specialCode.put('&', "&amp;");

    }   
    
    public void toClean() {
        text.setLength(0);
        stack.toEmpty();
    }

    public void add(String str) {
        text.append(str);
    }

    public boolean isEmpty() {
        return text.length() == 0 ? true : false;
    }

    private boolean isTitle() {
        int lvl = titleLvl();
        return 0 < lvl && lvl < text.length() && Character.isWhitespace(text.charAt(lvl));
    }

    private int titleLvl() {
        int lvl = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) != '#') {
                break;
            }
            lvl++;
        }
        return lvl;
    }

    private boolean isScrn(int id) {
        return id > 0 && text.charAt(id - 1) == SCRN;
    }


    private String isMarkup(int id, String[] mrk) {
        for (String mark : mrk) {
            if (mark.length() + id <= text.length()) {
                boolean isOk = true;
                for (int i = id, j = 0; isOk && i < id + mark.length(); i++, j++) {
                    if (text.charAt(i) != mark.charAt(j)) {
                        isOk = false;
                    }
                }
                if (isOk) {
                    return mark;
                }
            } 
        }
        return null;
    }

    private boolean isSpecial(int id) {
        for (Character symbol : special) {
            if (text.charAt(id) == symbol) {
                return true;
            } 
        }
        return false;
    }

    private void addSt(String mark, int pos) {
        stack.add(new MyPair<String, Integer> (mark, pos));
    }

    private int insertSt(String markOpen, String markClose, int id) {
        int posPrev = stack.get(markOpen).getY();
        if (posPrev != -1) {
            stack.removeUntil(markOpen);
            return posPrev;
        } else {
            addSt(markOpen, id);
            return -1;
        } 
    }

    private String getPairedMark(String mark) {
        int id = -1;
        for (int i = 0; i < markupClose.length; i++) {
            if (mark.equals(markupClose[i])) {
                id = i;
                break;
            }
        }
        return id == -1 ? null : markupOpen[id];
    }

    private String getOpen(String mark) {
        return open2Html.get(mark);
    }

    private String getClose(String mark) {
        return close2Html.get(mark);
    }

    private String getSpecial(int id) {
        return specialCode.get(text.charAt(id));
    }

    private void toNumMark(int id, List<Integer> openPos, List<Integer> closePos) {
        for (int i = id; i < text.length(); i++) {
            String mark = isMarkup(i, markup);
            if (mark != null && !isScrn(i)) {
                if (isMarkup(i, markupOpen) != null) {
                    addSt(mark, i);
                    i += mark.length() - 1;
                } else {
                    String markClose = mark;
                    if (isMarkup(i, markupClose) != null) {
                        mark = getPairedMark(mark);
                    } 
                    int prevPos = insertSt(mark, markClose, i);
                    if (prevPos != -1) {
                        openPos.add(prevPos);
                        closePos.add(i);
                    } 
                    i += mark.length() - 1;
                }
            } else if (isScrn(i)) {
                if (mark != null) {
                    i += mark.length() - 1;
                }
            } 
        }
    }

    private void toMark(StringBuilder parag, int id) {
        List<Integer> openPos = new ArrayList<Integer>();
        List<Integer> closePos = new ArrayList<Integer>();
        toNumMark(id, openPos, closePos);
        
        Collections.sort(openPos);
        Collections.sort(closePos);
        int open = 0;
        int close = 0;
        for (int i = id; i < text.length(); i++) {
            String mark = isMarkup(i, markup);
            if (openPos.size() > open && openPos.get(open) == i) {
                parag.append(getOpen(mark));
                open++;
            } else if (closePos.size() > close && closePos.get(close) == i) {
                parag.append(getClose(mark));
                close++;
            } else if (isSpecial(i)) {
                parag.append(getSpecial(i));
            } else if (i + 1 == text.length() || !isScrn(i + 1)) {
                parag.append(text.charAt(i));
            }
            if (mark != null && !isScrn(i)) {
                i += mark.length() - 1;
            }
        }
    }

    public void toMarkdown(StringBuilder parag) {
        if (isTitle()) {
            int lvl = titleLvl();
            parag.append("<h").append(lvl).append(">");
            toMark(parag, lvl + 1);
            parag.append("</h").append(lvl).append(">");
        } else {
            parag.append("<p>");
            toMark(parag, 0);
            parag.append("</p>");
        }
    }
}
