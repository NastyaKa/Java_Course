package queue;

import java.util.Arrays;
import java.util.Objects;

public class ArrayQueue extends AbstractQueue {
    private Object[] elements;
    private int h, t;

    public ArrayQueue() {
        h = t = 0;
        elements = new Object[1];
    }

    @Override
    protected void enqueueImpl(final Object element) {
        ensureCapacity((t + 1) % elements.length);  
        elements[t] = element; 
        t = (t + 1) % elements.length;
    }

    private void ensureCapacity(final int nw) {
        if (nw == h) {
            elements = Arrays.copyOf(elements, elements.length * 2);
            for (int i = elements.length / 2, j = 0; j < nw; j++, i++) {
                elements[i] = elements[j];
                elements[j] = null;
            }
            t = h + size();
        }
    }

    public void push(final Object element) {
        Objects.requireNonNull(element);
        h = (h + elements.length - 1) % elements.length;
        size++;      
        ensureCapacity(t);  
        elements[h] = element; 
    }

    public Object peek() {
        assert size() > 0;
        return elements[(t - 1 + elements.length) % elements.length];
    }

    public Object remove() {
        assert size() > 0;
        t = (t - 1 + elements.length) % elements.length;
        Object ans = elements[t];
        elements[t] = null;
        size--;
        return ans;
    }

    public int indexOf(final Object x) {
        int i = h;
        for (int j = 0; j < size(); j++) {
            if (elements[i].equals(x)) {
                return j;
            }
            i = (i + 1) % elements.length;
        }
        return -1;
    }

    public int lastIndexOf(final Object x) {
        int i = (t - 1 + elements.length) % elements.length;
        for (int j = size() - 1; j >= 0; j--) {
            if (elements[i].equals(x)) {
                return j;
            }
            i = (i - 1 + elements.length) % elements.length;
        }
        return -1;
    }

    @Override
    protected Object dequeueImpl() {
        Object ans = element();
        elements[h] = null;
        h = (h + 1) % elements.length;
        return ans;
    }

    @Override
    public Object elementImpl() {
        return elements[h];
    }

    @Override
    protected void clearImpl() {
        for (int i = h, j = 0; j < size(); i++, j++) {
            if (i == elements.length) {
                i = 0;
            }
            elements[i] = null;
        }
        h = t = 0;
    }
}
