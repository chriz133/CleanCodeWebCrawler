package at.aau.crawler.utilities;

import at.aau.crawler.interfaces.Crawler;
import at.aau.crawler.model.Heading;
import at.aau.crawler.model.Website;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class SimpleWebCrawler implements Crawler {
    private final Set<String> alreadyVisitedUrls = ConcurrentHashMap.newKeySet();
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    private final AtomicInteger activeTasks = new AtomicInteger(0);
    private final Object lock = new Object();

    private final List<Website> result = Collections.synchronizedList(new ArrayList<>());

    @Override
    public List<Website> crawlWebsite(String url, int maxDepth, List<String> domains) {
        if (maxDepth < 1 || url == null || url.isEmpty()) return List.of();

        // Submit root URL
        submitCrawl(url, 1, maxDepth, domains, null);

        // Wait until all tasks finish
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
        if (!alreadyVisitedUrls.add(sanitizedUrl)) return; // Skip already visited

        activeTasks.incrementAndGet(); // Track this task

        executor.submit(() -> {
            try {
                Website site = extractLinksAndHeading(sanitizedUrl, currentDepth);
                if (parent != null) {
                    site.setParentUrl(parent.getOwnUrl());
                }
                result.add(site);

                if (!site.isBroken() && currentDepth < maxDepth) {
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
            return new Website(url); // Mark as broken
        }
    }

    private List<String> filterLinksToVisit(List<String> links, List<String> domains) {
        if (domains == null || links == null) return List.of();
        return links.stream()
                .filter(link -> domains.stream().anyMatch(link::contains))
                .distinct()
                .collect(Collectors.toList());
    }

    private String trimUrl(String url) {
        if (url == null) return null;
        return url.replaceAll("[/#]+$", "");
    }
}
