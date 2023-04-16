package queue;

public class LinkedQueue extends AbstractQueue {
    private static class Node {
        private final Object val;
        private Node next;

        public Node(final Object val, final Node next) {
            this.val = val;
            this.next = next;
        }

        public void setNext(final Node next) {
            this.next = next;
        }

        public Object getVal() {
            return val;
        }

        public Node getNext() {
            return next;
        }
    }
    
    private Node h, t;

    public LinkedQueue() {
        clearImpl();
    }

    @Override
    protected void enqueueImpl(final Object element) {
        Node last = new Node(element, null);
        t.setNext(last);
        if (size() == 0) {
            h.setNext(last);
        }
        t = last;
    }

    @Override
    protected Object dequeueImpl() {
        h = h.getNext();
        Object ans = h.getVal();
        return ans;
    }

    @Override
    public Object elementImpl() {
        return h.getNext().getVal();
    }

    @Override
    protected void clearImpl() {
        h = new Node(null, null);
        t = h;
    }
}
