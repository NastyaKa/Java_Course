package info.kgeorgiy.ja.kaimakova.concurrent;

import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * Class for parallelization.
 *
 * @author Anastasiia Kaimakova
 * @see ParallelMapperImpl#map(Function, List)
 * @see ParallelMapperImpl#close()
 */
public class ParallelMapperImpl implements ParallelMapper {

    private final List<Thread> thread;
    private final SyncQueue queue;

    /**
     * Create worker threads that are used for parallelization.
     *
     * @param threads number of creating threads.
     */
    public ParallelMapperImpl(final int threads) {
        thread = new ArrayList<>();
        queue = new SyncQueue();

        IntStream.range(0, threads).forEach(i -> {
            thread.add(new Thread(() -> {
                try {
                    while (!Thread.interrupted()) {
                        queue.syncPoll().run();
                    }
                } catch (InterruptedException ignored) {
                } finally {
                    Thread.currentThread().interrupt();
                }
            }));
            thread.get(i).start();
        });
    }

    /**
     * Maps function {@code function} over specified {@code list}.
     * Mapping for each element performed in parallel.
     *
     * @throws InterruptedException if calling thread was interrupted
     */
    @Override
    public <T, R> List<R> map(final Function<? super T, ? extends R> function,
                              final List<? extends T> list) throws InterruptedException {
        final SyncList<R> res = new SyncList<>(list.size());
        List<RuntimeException> exceptions = new ArrayList<>();
        IntStream.range(0, list.size()).forEach(i -> {
                    try {
                        queue.syncAdd(() -> res.set(i, function.apply(list.get(i))));
                    } catch (RuntimeException e) {
                        exceptions.add(e);
                    }
                }
        );
        if (!exceptions.isEmpty()) {
            var e = new RuntimeException("RE when applying function");
            exceptions.forEach(e::addSuppressed);
            throw e;
        }
        return res.toList();
    }

    /**
     * Stops all threads. All unfinished mappings are left in undefined state.
     */
    @Override
    public void close() {
        thread.forEach(Thread::interrupt);
        for (final Thread thread : thread) {
            while (true) {
                try {
                    thread.join();
                    break;
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    private static class SyncList<T> {
        private final List<T> res;
        private int curSz;

        /**
         * Default constructor.
         *
         * @param sz size of {@code res} List.
         */
        public SyncList(final int sz) {
            res = new ArrayList<>(Collections.nCopies(sz, null));
            curSz = 0;
        }

        private synchronized void set(final int pos, final T item) {
            res.set(pos, item);
            if (++curSz == res.size()) {
                notify();
            }
        }

        private synchronized List<T> toList() throws InterruptedException {
            while (curSz < res.size()) {
                wait();
            }
            return res;
        }
    }

    private static class SyncQueue {
        private final Queue<Runnable> queue;

        public SyncQueue() {
            queue = new ArrayDeque<>();
        }

        public synchronized void syncAdd(final Runnable newThread) {
            queue.add(newThread);
            notify();
        }

        public synchronized Runnable syncPoll() throws InterruptedException {
            while (queue.isEmpty()) {
                wait();
            }
            return queue.poll();
        }
    }
}
