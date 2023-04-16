package queue;

import java.util.Arrays;
import java.util.Objects;

/*
    Model: a[h % MOD]..a[t % MOD], n = t - h + 1, n <= MOD
    Invariant: for i=h..t a[i % MOD] != null
    Let immutable(n): for i=h..t: a'[i % MOD] == a[i % MOD]

    Pred: element != null
    Post: t' = (t + 1) % MOD && a[t'] == element && n' == n + 1 && immutable(n')
        enqueue(element)

    Pred: size != 0
    Post: R = a[h] && immutable(n) && n' == n
        element()

    Pred: size != 0
    Post: h' == (h + 1) % MOD && n' == n - 1 && immutable(n') && R = a[h]
        dequeue()

    Pred: true
    Post: R == n && n' == n && immutable(n)
        size()

    Pred: true
    Post: R == (size == 0) && n' == n && immutable(n)
        isEmpty() 

    Pred: true
    Post: for i=h..t a[i] == null && h' == 0 && t' == 0 && n' == 0
        clear()
*/

public class ArrayQueueModule {
    private static Object[] elements = new Object[1];
    private static int h, t, size;

    // Pred: element != null
    // Post: t' = (t + 1) % MOD && a[t'] == element && n' == n + 1 && immutable(n')
    //     enqueue(element)
    public static void enqueue(final Object element) {
        Objects.requireNonNull(element);
        ensureCapacity((t + 1) % elements.length);  
        elements[t] = element; 
        size++;      
        t = (t + 1) % elements.length;
    } 
    
    // Pred: element != null
    // Post: t' = (h + MOD - 1) % MOD && a[h'] == element && n' == n + 1 && immutable(n')
    public static void push(final Object element) {
        Objects.requireNonNull(element);
        h = (h + elements.length - 1) % elements.length;
        size++;      
        ensureCapacity(t);  
        elements[h] = element; 
    }

    // Pred: true
    // Post: R = a[(h + 1) % MOD] && n' == n && immutable(n')
    public static Object peek() {
        return elements[(t - 1 + elements.length) % elements.length];
    }
    
    // Pred: size != 0
    // Post: t' == (t - 1 + MOD) % MOD && n' == n - 1 && immutable(n') && R = a[t']
    public static Object remove() {
        assert size > 0;
        t = (t - 1 + elements.length) % elements.length;
        Object ans = elements[t];
        elements[t] = null;
        size--;
        return ans;
    }

    // Pred: true
    // Post: n' == n && immutable(n') && R = a[h] && i = min && a[i] == x
    public static int indexOf(final Object x) {
        int i = h;
        for (int j = 0; j < size; j++) {
            if (elements[i].equals(x)) {
                return j;
            }
            i = (i + 1) % elements.length;
        }
        return -1;
    }

    // Pred: true
    // Post: n' == n && immutable(n') && R = a[h] && i = max && a[i] == x
    public static int lastIndexOf(final Object x) {
        int i = (t - 1 + elements.length) % elements.length;
        for (int j = size - 1; j >= 0; j--) {
            if (elements[i].equals(x)) {
                return j;
            }
            i = (i - 1 + elements.length) % elements.length;
        }
        return -1;
    }
    
    private static void ensureCapacity(final int nw) {
        if (nw == h) {
            elements = Arrays.copyOf(elements, elements.length * 2);
            for (int i = elements.length / 2, j = 0; j < nw; j++, i++) {
                elements[i] = elements[j];
                elements[j] = null;
            }
            t = h + size;
        }
    }
    
    // Pred: size != 0
    // Post: h' == (h + 1) % MOD && n' == n - 1 && immutable(n') && R = a[h]
    //     dequeue()
    public static Object dequeue() {        
        Object ans = elements[h];
        elements[h] = null;
        h = (h + 1) % elements.length;
        size--;
        return ans;
    }

    // Pred: size != 0
    // Post: R = a[h] && immutable(n) && n' == n
    //     element()
    public static Object element() {
        return elements[h];
    }

    // Pred: true
    // Post: R == n && n' == n && immutable(n)
    //     size()
    public static int size() {
        return size;
    }

    // Pred: true
    // Post: R == (size == 0) && n' == n && immutable(n)
    //     isEmpty() 
    public static boolean isEmpty() {
        return size == 0;
    }

    // Pred: true
    // Post: for i=h..t a[i] == null && h' == 0 && t' == 0 && n' == 0
    //     clear()
    public static void clear() {
        for (int i = h; size > 0; i++, size--) {
            if (i >= elements.length) {
                i = 0;
            }
            elements[i] = null;
        }
        h = t = 0;
    }
}
