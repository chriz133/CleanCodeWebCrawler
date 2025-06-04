package at.aau.crawler.utilities;

import at.aau.crawler.interfaces.Crawler;
import at.aau.crawler.model.Heading;
import at.aau.crawler.model.Website;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class SimpleWebCrawler implements Crawler {

    // ✅ Thread-safe for concurrent access
    private final Set<String> alreadyVisitedUrls = ConcurrentHashMap.newKeySet();

    // ✅ Executor with queue to prevent thread starvation
    private final ExecutorService executor = new ThreadPoolExecutor(
            100, 100,
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>()
    );

    private final AtomicInteger activeTasks = new AtomicInteger(0);
    private final Object lock = new Object();

    // ✅ Thread-safe result list
    private final List<Website> result = Collections.synchronizedList(new ArrayList<>());

    @Override
    public List<Website> crawlWebsite(String url, int maxDepth, List<String> domains) {
        if (maxDepth < 1 || url == null || url.isEmpty()) return null;

        submitCrawl(url, 1, maxDepth, domains, null);

        synchronized (lock) {
            while (activeTasks.get() > 0) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        executor.shutdown();
        try {
            executor.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        return result;
    }

    private void submitCrawl(String url, int currentDepth, int maxDepth, List<String> domains, Website parent) {
        String sanitizedUrl = trimUrl(url);
        System.out.println("URL: " + sanitizedUrl);

        // ✅ Avoid revisiting the same link
        if (!alreadyVisitedUrls.add(sanitizedUrl)) return;

        activeTasks.incrementAndGet();

        executor.submit(() -> {
            try {
                Website site = extractLinksAndHeading(sanitizedUrl, currentDepth);
                if (parent != null) {
                    site.setParentUrl(parent.getOwnUrl());
                }

                result.add(site);

//                System.out.println("[DEPTH " + currentDepth + "] " + sanitizedUrl); // Debug output

                if (!site.isBroken() && currentDepth <= maxDepth) {
                    List<String> links = filterLinksToVisit(site.getLinks(), domains);
                    if (links != null) {
                        for (String link : links) {
                            submitCrawl(link, currentDepth + 1, maxDepth, domains, site);
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Error crawling: " + sanitizedUrl + " - " + e.getMessage());
            } finally {
                if (activeTasks.decrementAndGet() == 0) {
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                }
            }
        });
    }

    private Website extractLinksAndHeading(String url, int depth) {
        try {
            Document doc = Jsoup.parse(new URL(url).openStream(), "UTF-8", url);
            List<Heading> headlines = new ArrayList<>();

            for (int i = 1; i <= 4; i++) {
                for (Element element : doc.select("h" + i)) {
                    headlines.add(new Heading("h" + i, element.text()));
                }
            }

            List<String> links = doc.select("a[href]").stream()
                    .map(e -> e.attr("abs:href"))
                    .collect(Collectors.toList());

            return new Website(depth, headlines, links, url);
        } catch (IOException | IllegalArgumentException e) {
            return new Website(url); // broken
        }
    }

    private List<String> filterLinksToVisit(List<String> links, List<String> domains) {
        if (domains == null || links == null) return List.of();
        return links.stream()
                .map(String::toLowerCase)
                .filter(link -> domains.stream().anyMatch(domain -> link.contains(domain.toLowerCase())))
                .distinct()
                .collect(Collectors.toList());
    }

    private String trimUrl(String url) {
        if (url == null) return null;
        try {
            return new URI(url).normalize().toString().split("#")[0]; // remove fragments
        } catch (Exception e) {
            return url;
        }
    }
}
