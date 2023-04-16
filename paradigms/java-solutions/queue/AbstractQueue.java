package queue;

import java.util.Objects;
import java.util.function.Predicate;

public abstract class AbstractQueue implements Queue {
    protected int size;
    protected abstract Object dequeueImpl();
    protected abstract void enqueueImpl(final Object element);
    protected abstract Object elementImpl();
    protected abstract void clearImpl();
    
    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public Object dequeue() {
        size--;
        return dequeueImpl();
    }

    @Override
    public void enqueue(final Object element) {
        Objects.requireNonNull(element);
        enqueueImpl(element);
        size++;      
    } 

    @Override
    public Object element() {
        assert size() >= 0;
        return elementImpl();
    }

    @Override
    public void clear() {
        size = 0;
        clearImpl();
    }

    @Override
    public void removeIf(final Predicate<Object> p) {
        int startSize = size();
        for (int i = 0; i < startSize; i++) {
            Object cur = dequeue();
            if (!p.test(cur)) {
                enqueue(cur);
            }
        }
    }

    @Override
    public void retainIf(final Predicate<Object> p) {
        removeIf(p.negate());
    }

    @Override
    public void takeWhile(final Predicate<Object> p) {
        anyWhile(p, true);
    }

    @Override
    public void dropWhile(final Predicate<Object> p) {
        anyWhile(p, false);
    }

    private void anyWhile(final Predicate<Object> p, final boolean save) {
        int startSize = size();
        for (int i = 0; i < startSize; i++) {
            if (p.test(element())) {
                if (save) {
                    enqueue(dequeue());
                } else {
                    dequeue();
                }
            } else {
                startSize = i;
                break;
            }
        }
        if (save) {
            while (startSize != size) {
                dequeue();
            }
        }
    }
}
