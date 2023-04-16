package md2html;

import java.util.List;
import java.util.ArrayList;

public class MyStack {
    private List<MyPair<String, Integer>> st;
    private int size;
    private static final MyPair<String, Integer> DEFAULT = new MyPair<String, Integer> (null, -1);

    public MyStack() {
        st = new ArrayList<MyPair<String, Integer>>();
        size = 0;
    }

    public void remove() {
        size--;
        st.remove(size);
    }

    public void toEmpty() {
        st.clear();
        size = 0;
    }

    public void removeUntil(String key) {
        while (size > 1 && !top().getX().equals(key)) {
            System.out.println(top().getX());
            remove();
        }
        if (size > 0 && top().getX().equals(key)) {
            remove();
        }
    }

    public MyPair<String, Integer> top() {
        return st.get(size - 1);
    }

    public void add(MyPair<String, Integer> id) {
        size++;
        st.add(id);
    }

    public MyPair<String, Integer> get(String key) {
        for (MyPair<String, Integer> cur : st) {
            if (cur.getX().equals(key)) {
                return cur;
            }
        }
        return DEFAULT;
    }

    public boolean isEmpty() {
        return st.isEmpty();
    }
}
