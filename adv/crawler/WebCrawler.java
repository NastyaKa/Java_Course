package info.kgeorgiy.ja.kaimakova.crawler;

import info.kgeorgiy.java.advanced.crawler.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Predicate;

public class WebCrawler implements AdvancedCrawler {
    private final Downloader downloader;
    private final ExecutorService downloadService;
    private final ExecutorService extractService;
    private final Map<String, HostDownloader> hostMap;
    private final int perHost;
    private static final int DEFAULT_INT = 1;

    /**
     * Initializing constructor for class.
     *
     * @param downloader  download pages and extract links from.
     * @param downloaders maximum number of simultaneously loaded pages.
     * @param extractors  maximum number of pages from which links are simultaneously retrieved.
     * @param perHost     maximum number of pages that can be simultaneously downloaded from one host.
     */
    public WebCrawler(final Downloader downloader, final int downloaders, final int extractors, final int perHost) {
        this.downloader = downloader;
        downloadService = Executors.newFixedThreadPool(downloaders);
        extractService = Executors.newFixedThreadPool(extractors);
        hostMap = new ConcurrentHashMap<>();
        this.perHost = perHost;
    }

    /**
     * Method for running from command line.
     * <ul>
     *     <li> {@code url} is a root for crawler. </li>
     *     <li> {@code depth} is a maximum level from root to download links. </li>
     *     <li> {@code downloads} is a maximum number of simultaneously loaded pages. </li>
     *     <li> {@code extractors} is a maximum number of pages from which links are simultaneously retrieved. </li>
     *     <li> {@code perHost} is a maximum number of pages that can be simultaneously downloaded from one host. </li>
     * </ul>
     *
     * @param args "url [depth [downloads [extractors [perHost]]]]"
     */
    public static void main(final String[] args) {
        if (Objects.isNull(args) || args.length < 1 || args.length > 5 || Objects.isNull(args[0])) {
            System.err.println("Error: expected non-null url [depth [downloads [extractors [perHost]]]]");
        } else {
            int[] param = new int[4];
            for (int i = 0; i < 4; i++) {
                try {
                    param[i] = i < args.length ? Integer.parseInt(args[i + 1]) : DEFAULT_INT;
                } catch (final NumberFormatException e) {
                    System.err.println("Error: not an integer value: " + args[i + 1]);
                    param[i] = DEFAULT_INT;
                }
            }

            try (final Crawler crawler =
                         new WebCrawler(new CachingDownloader(1.0), param[1], param[2], param[3])) {
                crawler.download(args[0], param[0]);
            } catch (IOException e) {
                System.err.println("Error: IOException when creating Downloader: " + e.getMessage());
            }
        }
    }

    /**
     * Downloads website up to specified depth.
     *
     * @param url   start <a href="http://tools.ietf.org/html/rfc3986">URL</a>.
     * @param depth download depth.
     * @return download result.
     */
    @Override
    public Result download(final String url, final int depth) {
        return download(url, depth, element -> true);
    }

    /**
     * Downloads website up to specified depth.
     *
     * @param url   start <a href="http://tools.ietf.org/html/rfc3986">URL</a>.
     * @param depth download depth.
     * @param hosts domains to follow, pages on another domains should be ignored.
     * @return download result.
     */
    @Override
    public Result download(final String url, final int depth, final List<String> hosts) {
        return download(url, depth, hosts::contains);
    }

    private Result download(final String url, final int depth, final Predicate<String> allowed) {
        final Set<String> used = ConcurrentHashMap.newKeySet();
        final Set<String> downloaded = ConcurrentHashMap.newKeySet();
        final Map<String, IOException> errors = new ConcurrentHashMap<>();

        List<String> queue = Collections.synchronizedList(new ArrayList<>());
        queue.add(url);
        final Phaser phaser = new Phaser(1);

        while (phaser.getPhase() < depth && !queue.isEmpty()) {
            final List<String> newQueue = Collections.synchronizedList(new ArrayList<>());
            queue.forEach(curUrl ->
                    downloadAndAdd(curUrl, depth - 1, allowed, phaser, used, downloaded, errors, newQueue));
            phaser.arriveAndAwaitAdvance();
            queue = newQueue;
        }
        return new Result(new ArrayList<>(downloaded), errors);
    }

    private void downloadAndAdd(final String url, final int depth, final Predicate<String> allowed,
                                final Phaser phaser, final Set<String> used, final Set<String> downloaded,
                                final Map<String, IOException> errors, final List<String> queue) {
        if (!used.add(url)) {
            return;
        }
        try {
            final String hostName = URLUtils.getHost(url);
            if (!allowed.test(hostName)) {
                return;
            }
            final HostDownloader host = hostMap.computeIfAbsent(hostName, newHost -> new HostDownloader());
            phaser.register();
            host.submit(() -> {
                try {
                    final Document document = downloader.download(url);
                    if (phaser.getPhase() < depth) {
                        phaser.register();
                        extractService.submit(() -> {
                            try {
                                queue.addAll(document.extractLinks());
                                downloaded.add(url);
                            } catch (IOException e) {
                                errors.put(url, e);
                            } finally {
                                phaser.arriveAndDeregister();
                            }
                        });
                    } else {
                        downloaded.add(url);
                    }
                } catch (final IOException e) {
                    errors.put(url, e);
                } finally {
                    phaser.arriveAndDeregister();
                    host.next();
                }
            });
        } catch (final MalformedURLException e) {
            errors.put(url, e);
        }
    }

    /**
     * Closes this web-crawler, relinquishing any allocated resources.
     */
    @Override
    public void close() {
        downloadService.close();
        extractService.close();
    }

    private class HostDownloader {
        private final Queue<Runnable> queue = new ArrayDeque<>();
        private int counter = 0;

        public synchronized void submit(final Runnable runnable) {
            if (counter < perHost) {
                counter++;
                downloadService.submit(runnable);
            } else {
                queue.add(runnable);
            }
        }

        private synchronized void next() {
            if (queue.isEmpty()) {
                counter--;
            } else {
                downloadService.submit(queue.poll());
            }
        }
    }
}
