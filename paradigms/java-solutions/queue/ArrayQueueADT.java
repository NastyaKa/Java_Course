package queue;

import java.util.Arrays;
import java.util.Objects;

/*
    Model: a[h % MOD]..a[t % MOD], n = t - h + 1, n <= MOD
    Invariant: for i=h..t a[i % MOD] != null
    Let immutable(n): for i=h..t: a'[i % MOD] == a[i % MOD]

    Pred: element != null && queue != null
    Post: t' = (t + 1) % MOD && a[t'] == element && n' == n + 1 && immutable(n')
        enqueue(element)

    Pred: size != 0 && queue != null
    Post: R = a[h] && immutable(n) && n' == n
        element()

    Pred: size != 0 && queue != null
    Post: h' == (h + 1) % MOD && n' == n - 1 && immutable(n') && R = a[h]
        dequeue()

    Pred: queue != null
    Post: R == n && n' == n && immutable(n)
        size()

    Pred: queue != null
    Post: R == (h != t) && n' == n && immutable(n)
        isEmpty() 

    Pred: queue != null
    Post: for i=h..t a[i] == null && h' == 0 && t' == 0 && n' == 0
        clear()
*/

public class ArrayQueueADT {
    private Object[] elements;
    private int h, t, size;

    public ArrayQueueADT() {
        elements = new Object[1];
    }

    // Pred: true
    // Post: t == 0 && h == 0 && sz == 0 && immutable(n')
    //     create()
    public static ArrayQueueADT create() {
        final ArrayQueueADT queue = new ArrayQueueADT();
        queue.elements = new Object[1];
        return queue;
    } 

    // Pred: element != null && queue != null
    // Post: t' = (t + 1) % MOD && a[t'] == element && n' == n + 1 && immutable(n')
    //     enqueue(element)
    public static void enqueue(final ArrayQueueADT queue, final Object element) {
        Objects.requireNonNull(element);
        ensureCapacity(queue, (queue.t + 1) % queue.elements.length);  
        queue.elements[queue.t] = element; 
        queue.size++;      
        queue.t = (queue.t + 1) % queue.elements.length;
    } 

    private static void ensureCapacity(ArrayQueueADT queue, int nw) {
        if (nw == queue.h) {
            queue.elements = Arrays.copyOf(queue.elements, queue.elements.length * 2);
            for (int i = queue.elements.length / 2, j = 0; j < nw; j++, i++) {
                queue.elements[i] = queue.elements[j];
                queue.elements[j] = null;
            }
            queue.t = queue.h + queue.size;
        }
    }

    public static void push(ArrayQueueADT queue, final Object element) {
        Objects.requireNonNull(element);
        queue.h = (queue.h + queue.elements.length - 1) % queue.elements.length;
        queue.size++;      
        ensureCapacity(queue, queue.t);  
        queue.elements[queue.h] = element; 
    }

    public static Object peek(ArrayQueueADT queue) {
        return queue.elements[(queue.t - 1 + queue.elements.length) % queue.elements.length];
    }

    public static Object remove(ArrayQueueADT queue) {
        assert queue.size > 0;
        queue.t = (queue.t - 1 + queue.elements.length) % queue.elements.length;
        Object ans = queue.elements[queue.t];
        queue.elements[queue.t] = null;
        queue.size--;
        return ans;
    }

    public static int indexOf(ArrayQueueADT queue, Object x) {
        int i = queue.h;
        for (int j = 0; j < queue.size; j++) {
            if (queue.elements[i].equals(x)) {
                return j;
            }
            i = (i + 1) % queue.elements.length;
        }
        return -1;
    }

    public static int lastIndexOf(ArrayQueueADT queue, Object x) {
        int i = (queue.t - 1 + queue.elements.length) % queue.elements.length;
        for (int j = queue.size - 1; j >= 0; j--) {
            if (queue.elements[i].equals(x)) {
                return j;
            }
            i = (i - 1 + queue.elements.length) % queue.elements.length;
        }
        return -1;
    }
    
    // Pred: size > 0 && queue != null
    // Post: h' == (h + 1) % MOD && n' == n - 1 && immutable(n') && R = a[h]
    //     dequeue()
    public static Object dequeue(ArrayQueueADT queue) {
        if (queue.size == 0) {
            throw new AssertionError("not a valid move");
        } else {
            Object ans = queue.elements[queue.h];
            queue.elements[queue.h] = null;
            queue.h = (queue.h + 1) % queue.elements.length;
            queue.size--;
            return ans;
        }
    }

    // Pred: size > 0 && queue != null
    // Post: R = a[h] && immutable(n) && n' == n
    //     element()
    public static Object element(ArrayQueueADT queue) {
        return queue.elements[queue.h];
    }

    // Pred: queue != null
    // Post: R == n && n' == n && immutable(n)
    //     size()
    public static int size(ArrayQueueADT queue) {
        return queue.size;
    }

    // Pred: queue != null
    // Post: R == (h != t) && n' == n && immutable(n)
    //     isEmpty() 
    public static boolean isEmpty(ArrayQueueADT queue) {
        return queue.size <= 0;
    }

    // Pred: queue != null
    // Post: for i=h..t a[i] == null && h' == 0 && t' == 0 && n' == 0
    //     clear()
    public static void clear(ArrayQueueADT queue) {
        for (int i = queue.h; queue.size > 0; i++, queue.size--) {
            if (i >= queue.elements.length) {
                i = 0;
            }
            queue.elements[i] = null;
        }
        queue.h = queue.t = 0;
    }
}
