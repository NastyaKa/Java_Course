package info.kgeorgiy.ja.kaimakova.concurrent;

import info.kgeorgiy.java.advanced.concurrent.AdvancedIP;
import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * List ans Scalar iterative parallelism support.
 *
 * @author Anastasiia Kaimakova
 * @see info.kgeorgiy.java.advanced.concurrent.AdvancedIP
 * @see ParallelMapperImpl
 */
public class IterativeParallelism implements AdvancedIP {

    private final ParallelMapper parallelMapper;

    /**
     * Default constructor for IterativeParallelism with {@link ParallelMapper}.
     *
     * @param parallelMapper interface for {@link ParallelMapper}.
     */
    public IterativeParallelism(ParallelMapper parallelMapper) {
        this.parallelMapper = parallelMapper;
    }

    /**
     * Default constructor for IterativeParallelism.
     */
    public IterativeParallelism() {
        this.parallelMapper = null;
    }

    private <T, C> C generalApply(int threadsNum, List<T> list,
                                  final Function<Stream<T>, C> streamGroper,
                                  final Function<Stream<C>, C> filterRes) throws InterruptedException {
        if (threadsNum < 1) {
            throw new IllegalArgumentException("Number of threads should be more or equals 1");
        }
        threadsNum = Math.min(threadsNum, Math.max(list.size(), 1));
        final List<List<T>> tasks = new ArrayList<>();
        final int blockSize = list.size() / threadsNum;
        final int remainder = list.size() % threadsNum;
        for (int i = 0, left = 0; i < threadsNum; i++) {
            final int right = left + blockSize + (i < remainder ? 1 : 0);
            tasks.add(list.subList(left, Math.min(right, list.size())));
            left = right;
        }

        if (parallelMapper != null) {
            final List<C> results = parallelMapper.map(task -> streamGroper.apply(task.stream()), tasks);
            return filterRes.apply(results.stream());
        }
        final List<Thread> threads = new ArrayList<>();
        final List<C> results = new ArrayList<>(Collections.nCopies(threadsNum, null));
        IntStream.range(0, tasks.size()).forEach(TaskNum ->
            threads.add(new Thread(() ->
                    results.set(TaskNum, streamGroper.apply(tasks.get(TaskNum).stream()))))
        );
        for (final Thread thread : threads) {
            thread.start();
        }
        try {
            for (final Thread thread : threads) {
                thread.join();
            }
        } catch (final InterruptedException e) {
            for (final Thread thread : threads) {
                thread.interrupt();
            }
            throw e;
        }
        return filterRes.apply(results.stream());
    }

    /**
     * Joins {@code list} items to string.
     *
     * @param i    number of threads
     * @param list {@code List} of values to join.
     * @return {@code String} of joined values.
     * @throws IllegalArgumentException if {@code i} <= 0
     * @throws InterruptedException     if any thread was interrupted
     */
    @Override
    public String join(final int i, final List<?> list) throws InterruptedException {
        return generalApply(i, list,
                stream -> stream.map(Object::toString).collect(Collectors.joining()),
                stream -> stream.collect(Collectors.joining()));
    }

    /**
     * Filters values from {@code list} by {@code predicate}.
     *
     * @param i         number of threads
     * @param list      {@code List} of values to filter.
     * @param predicate {@code Predicate} for filter.
     * @param <T>       type of {@code list} items.
     * @return {@code List} with filtered values.
     * @throws IllegalArgumentException if {@code i} <= 0
     * @throws InterruptedException     if any thread was interrupted
     */
    @Override
    public <T> List<T> filter(final int i, final List<? extends T> list, final Predicate<? super T> predicate) throws InterruptedException {
        return generalApply(i, list,
                stream -> stream.filter(predicate).collect(Collectors.toList()),
                stream -> stream.flatMap(List::stream).collect(Collectors.toList()));
    }

    /**
     * Maps values from {@code list}.
     *
     * @param i        number of threads
     * @param list     {@code List} of values to be mapped.
     * @param function mapping function.
     * @param <T>      type of old {@code list} items.
     * @param <U>      type of new {@code list} items.
     * @return {@code List} of mapped values.
     * @throws IllegalArgumentException if {@code i} <= 0
     * @throws InterruptedException     if any thread was interrupted
     */
    @Override
    public <T, U> List<U> map(final int i, final List<? extends T> list, final Function<? super T, ? extends U> function) throws InterruptedException {
        return generalApply(i, list,
                stream -> stream.map(function).collect(Collectors.toList()),
                stream -> stream.flatMap(List::stream).collect(Collectors.toList()));
    }

    /**
     * Searches max value from {@code list}.
     *
     * @param i          number of threads
     * @param list       {@code List} of values.
     * @param comparator comparing function for {@code list} items.
     * @param <T>        type of {@code list} items.
     * @return value of max {@code list} element, or {@code null}, if value is absent.
     * @throws IllegalArgumentException if {@code i} <= 0
     * @throws InterruptedException     if any thread was interrupted
     */
    @Override
    public <T> T maximum(final int i, List<? extends T> list, final Comparator<? super T> comparator) throws InterruptedException {
        return generalApply(i, list,
                stream -> stream.max(comparator).orElse(null),
                stream -> stream.max(comparator).orElse(null));
    }

    /**
     * Searches min value from {@code list}.
     *
     * @param i          number of threads
     * @param list       {@code List} of values.
     * @param comparator comparing function for {@code list} items.
     * @param <T>        type of {@code list} items.
     * @return value of min {@code list} element, or {@code null}, if value is absent.
     * @throws IllegalArgumentException if {@code i} <= 0
     * @throws InterruptedException     if any thread was interrupted
     */
    @Override
    public <T> T minimum(final int i, List<? extends T> list, final Comparator<? super T> comparator) throws InterruptedException {
        return maximum(i, list, comparator.reversed());
    }

    /**
     * Checks if all items of {@code list} satisfies {@code predicate}.
     *
     * @param i         number of threads
     * @param list      {@code List} of values to check.
     * @param predicate {@code Predicate} for filter.
     * @param <T>       type of {@code list} items.
     * @return {@code true}, if all items satisfies {@code predicate}, and {@code false} otherwise.
     * @throws IllegalArgumentException if {@code i} <= 0
     * @throws InterruptedException     if any thread was interrupted
     */
    @Override
    public <T> boolean all(final int i, final List<? extends T> list, final Predicate<? super T> predicate) throws InterruptedException {
        return !any(i, list, predicate.negate());
    }

    /**
     * Checks if any item of {@code list} satisfies {@code predicate}.
     *
     * @param i         number of threads
     * @param list      {@code List} of values to check.
     * @param predicate {@code Predicate} for filter.
     * @param <T>       type of {@code list} items.
     * @return {@code true}, if any item satisfies {@code predicate}, and {@code false} otherwise.
     * @throws IllegalArgumentException if {@code i} <= 0
     * @throws InterruptedException     if any thread was interrupted
     */
    @Override
    public <T> boolean any(final int i, List<? extends T> list, final Predicate<? super T> predicate) throws InterruptedException {
        return generalApply(i, list,
                stream -> stream.anyMatch(predicate),
                stream -> stream.anyMatch(Boolean::booleanValue));
    }

    /**
     * Counts all {@code list} items, witch satisfies {@code predicate}.
     *
     * @param i         number of threads
     * @param list      {@code List} of values to count after filtering.
     * @param predicate {@code Predicate} for filter.
     * @param <T>       type of {@code list} items.
     * @return number of satisfying items.
     * @throws IllegalArgumentException if {@code i} <= 0
     * @throws InterruptedException     if any thread was interrupted
     */
    @Override
    public <T> int count(final int i, List<? extends T> list, final Predicate<? super T> predicate) throws InterruptedException {
        return generalApply(i, list,
                stream -> stream.filter(predicate).map(el -> 1).reduce(0, Integer::sum),
                stream -> stream.reduce(0, Integer::sum));
    }

    /**
     * General function for monoids.
     *
     * @param monoid monoid to use.
     * @return Performs a reduction on the elements of {@link Monoid#getOperator()}
     * or else return {@link Monoid#getIdentity()}.
     */
    private static <R> Function<Stream<R>, R> generalForMonoid(final Monoid<R> monoid) {
        return stream -> stream.reduce(monoid.getOperator()).orElse(monoid.getIdentity());
    }

    /**
     * Reduces values using monoid.
     *
     * @param i      number of concurrent threads.
     * @param list   values to reduce.
     * @param monoid monoid to use.
     * @return values reduced by provided monoid or {@link Monoid#getIdentity() identity} if no values specified.
     * @throws InterruptedException if executing thread was interrupted.
     */
    @Override
    public <T> T reduce(final int i, List<T> list, final Monoid<T> monoid) throws InterruptedException {
        return generalApply(i, list,
                generalForMonoid(monoid),
                generalForMonoid(monoid));
    }

    /**
     * Maps and reduces values using monoid.
     *
     * @param i        number of concurrent threads.
     * @param list     values to reduce.
     * @param function mapping function.
     * @param monoid   monoid to use.
     * @return values reduced by provided monoid or {@link Monoid#getIdentity() identity} if no values specified.
     * @throws InterruptedException if executing thread was interrupted.
     */
    @Override
    public <T, R> R mapReduce(final int i, final List<T> list,
                              final Function<T, R> function,
                              final Monoid<R> monoid) throws InterruptedException {
        return generalApply(i, list,
                stream -> stream.map(function).reduce(monoid.getOperator()).orElse(monoid.getIdentity()),
                generalForMonoid(monoid));
    }
}
