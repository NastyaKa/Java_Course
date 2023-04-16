package queue;

import java.util.function.Predicate;

public interface Queue {
    // Pred: queue != null
    // Post: R == n && n' == n && immutable(n)
    public int size();

    // Pred: queue != null
    // Post: R == (size == 0) && n' == n && immutable(n)
    public boolean isEmpty();

    // Pred: queue != null && size() != 0
    // Post:  R = queue[0] && n' == n - 1 && immutable(n')
    public Object dequeue();

    // Pred: element != null && queue != null
    // Post: queue[n'] == element && n' == n + 1 && immutable(n)
    public void enqueue(final Object element);

    // Pred: size != 0 && queue != null
    // Post: R = queue[0] && immutable(n) && n' == n
    public Object element();

    // Pred: queue != null
    // Post: h' == 0 && t' == 0 && n' == 0
    public void clear();


    // new_queue() : queue'[] == a[] && forall k=0..x a[i_k] in queue[] && forall(k) i_k < i_(k+1) && h' == 0 && funk:exist(x) && t' == x && n' == x && immutable(x)


    // Pred: p != null && queue != null
    // Post: new_queue() && exist(x): forall i=0..x !p.test(a[i]) && forall j = queue[]\a[] p.test(j)
    public void removeIf(final Predicate<Object> p);

    // Pred: p != null && queue != null
    // Post: new_queue() && exist(x): forall i=0..x p.test(a[i]) && forall j = queue[]\a[] !p.test(j)
    public void retainIf(final Predicate<Object> p);
    
    // Pred: p != null && queue != null
    // Post: h' == h && exist(x): forall i=[0..x) p.test(a[i]) && !p.test(a[x]) && t' == x - 1 && n' == x && immutable(n')
    public void takeWhile(final Predicate<Object> p);

    // Pred: p != null && queue != null
    // Post: t' == t  && exist(x): forall i=[0..x) !p.test(a[i]) && p.test(a[x]) && h' == x && n' == n - x + 1 && immutable(n')
    public void dropWhile(final Predicate<Object> p);
}
