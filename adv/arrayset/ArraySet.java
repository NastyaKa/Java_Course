package info.kgeorgiy.ja.kaimakova.arrayset;

import java.util.*;

public class ArraySet<E extends Comparable<? super E>> extends AbstractSet<E> implements NavigableSet<E> {

    private final ReversibleList<E> elements;
    private final Comparator<? super E> comparator;

    public ArraySet(final Collection<? extends E> elements,  Comparator<? super E> comparator) {
        final Set<E> set = new TreeSet<>(comparator);
        set.addAll(elements);
        this.elements = new ReversibleList<>(set);
        if (comparator == null) {
            this.comparator = Comparator.naturalOrder();
        } else {
            this.comparator = comparator;
        }
    }

    private ArraySet(final ReversibleList<E> elements, final Comparator<? super E> comparator) {
        this.elements = elements;
        this.comparator = comparator;
    }

    public ArraySet(final Collection<? extends E> elements) {
        this(elements, null);
    }

    public ArraySet() {
        this.elements = new ReversibleList<>();
        this.comparator = Comparator.naturalOrder();
    }

    private int binSearch(E e) {
        return Collections.binarySearch(elements, e, comparator);
    }

    private int getLower(E e, boolean inclusive) {
        int index = binSearch(e);
        if (index < 0) {
            index = -index - 2;
        } else if (!inclusive) {
            index--;
        }
        return index;
    }

    private int getUpper(E e, boolean inclusive) {
        int index = binSearch(e);
        if (index < 0) {
            index = -index - 1;
        } else if (!inclusive) {
            index++;
        }
        return index;
    }

    @Override
    public E lower(E e) {
        return elements.getOrNull(getLower(e, false));
    }

    @Override
    public E floor(E e) {
        return elements.getOrNull(getLower(e, true));
    }

    @Override
    public E ceiling(E e) {
        return elements.getOrNull(getUpper(e, true));
    }

    @Override
    public E higher(E e) {
        return elements.getOrNull(getUpper(e, false));
    }

    @Override
    public E pollFirst() {
        throw new UnsupportedOperationException();
    }

    @Override
    public E pollLast() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return elements.size();
    }

    @Override
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean contains(Object o) {
        return binSearch((E) o) >= 0;
    }

    @Override
    public Iterator<E> iterator() {
        return elements.iterator();
    }

    @Override
    public NavigableSet<E> descendingSet() {
        return new ArraySet<>(new ReversibleList<>(elements.list, elements.reverse), Collections.reverseOrder(comparator));
    }

    @Override
    public Iterator<E> descendingIterator() {
        return descendingSet().iterator();
    }

    @Override
    public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
        if (compareElements(fromElement, toElement) > 0) {
            throw new IllegalArgumentException("fromKey > toKey");
        }
        int from;
        if (fromInclusive) {
            from = getUpper(fromElement, true);
        } else {
            from = getUpper(fromElement, false);
        }
        int to;
        if (toInclusive) {
            to = getLower(toElement, true);
        } else {
            to = getLower(toElement, false);
        }
        if (from > to) {
            return new ArraySet<>();
        }
        return new ArraySet<>(new ReversibleList<>(elements.subList(from, to + 1), !elements.reverse), comparator);
    }

    @Override
    public NavigableSet<E> headSet(E toElement, boolean inclusive) {
        if (elements.isEmpty()) {
            return this;
        }
        if (compareElements(first(), toElement) < 0) {
            return subSet(first(), true, toElement, inclusive);
        }
        return subSet(toElement, true, toElement, inclusive);
    }

    @Override
    public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
        if (elements.isEmpty()) {
            return this;
        }
        if (compareElements(fromElement, last()) < 0) {
            return subSet(fromElement, inclusive, last(), true);
        }
        return subSet(fromElement, inclusive, fromElement, true);
    }

    @Override
    public Comparator<? super E> comparator() {
        return comparator.equals(Comparator.naturalOrder()) ? null : comparator;
    }

    @Override
    public SortedSet<E> subSet(E fromElement, E toElement) {
        return subSet(fromElement, true, toElement, false);
    }

    @Override
    public SortedSet<E> headSet(E toElement) {
        return headSet(toElement, false);
    }

    @Override
    public SortedSet<E> tailSet(E fromElement) {
        return tailSet(fromElement, true);
    }

    @Override
    public E first() {
        return elements.get(0);
    }

    @Override
    public E last() {
        return elements.get(isEmpty() ? 0 : size() - 1);
    }

    private int compareElements(E a, E b) {
        return comparator.compare(a, b);
    }

    private static class ReversibleList<E> extends AbstractList<E> implements RandomAccess {
        private final List<E> list;
        private boolean reverse = false;

        public ReversibleList(Collection<E> elements) {
            list = List.copyOf(elements);
        }

        public ReversibleList(List<E> elements, boolean reverse) {
            list = elements;
            this.reverse = !reverse;
        }

        public ReversibleList() {
            list = List.of();
        }

        @Override
        public E get(int index) {
            if (list.isEmpty()) {
                throw new NoSuchElementException();
            }
            return getOrNull(index);
        }

        public E getOrNull(int index) {
            if (index < 0 || index >= size()) {
                return null;
            }
            if (reverse) {
                return list.get(size() - 1 - index);
            }
            return list.get(index);
        }

        @Override
        public int size() {
            return list.size();
        }
    }
}
