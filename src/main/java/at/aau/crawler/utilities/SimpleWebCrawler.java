package at.aau.crawler.utilities;

import at.aau.crawler.interfaces.Crawler;
import at.aau.crawler.model.Heading;
import at.aau.crawler.model.Website;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class SimpleWebCrawler implements Crawler {

    private final Set<String> alreadyVisitedUrls = ConcurrentHashMap.newKeySet();
    private final ExecutorService executor = new ThreadPoolExecutor(
            100, 100,
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>()
    );

    private final AtomicInteger activeTasks = new AtomicInteger(0);
    private final Object lock = new Object();
    private final List<Website> result = Collections.synchronizedList(new ArrayList<>());

    @Override
    public List<Website> crawlWebsite(String url, int maxDepth, List<String> domains) {
        if (maxDepth < 1 || url == null || url.isEmpty()) return null;

        submitCrawl(url, 1, maxDepth, domains, null);

        waitForCompletion();
        shutdownExecutor();

        return result;
    }

    private void submitCrawl(String url, int currentDepth, int maxDepth, List<String> domains, Website parent) {
        String sanitizedUrl = UrlUtils.trimUrl(url);
        if (!shouldVisit(sanitizedUrl)) return;

        activeTasks.incrementAndGet();

        executor.submit(() -> {
            try {
                Website site = extractWebsite(sanitizedUrl, currentDepth);
                updateParent(site, parent);
                result.add(site);
                scheduleSubLinks(site, currentDepth, maxDepth, domains);
            } catch (Exception e) {
                logError(sanitizedUrl, e);
            } finally {
                finishTask();
            }
        });
    }

    private Website extractWebsite(String url, int depth) {
        try {
            Document doc = Jsoup.parse(new URL(url).openStream(), "UTF-8", url);
            List<Heading> headings = extractHeadings(doc);
            List<String> links = extractLinks(doc);
            return new Website(depth, headings, links, url);
        } catch (IOException | IllegalArgumentException e) {
            return new Website(url); // Broken website
        }
    }

    private List<Heading> extractHeadings(Document doc) {
        List<Heading> headings = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            for (Element el : doc.select("h" + i)) {
                headings.add(new Heading("h" + i, el.text()));
            }
        }
        return headings;
    }

    private List<String> extractLinks(Document doc) {
        return doc.select("a[href]")
                .stream()
                .map(link -> link.attr("abs:href"))
                .collect(Collectors.toList());
    }

    private List<String> filterLinksToVisit(List<String> links, List<String> domains) {
        if (domains == null || links == null) return List.of();
        return links.stream()
                .map(String::toLowerCase)
                .filter(link -> domains.stream().anyMatch(domain -> link.contains(domain.toLowerCase())))
                .distinct()
                .collect(Collectors.toList());
    }

    private void scheduleSubLinks(Website site, int currentDepth, int maxDepth, List<String> domains) {
        if (!site.isBroken() && currentDepth < maxDepth) {
            List<String> linksToVisit = filterLinksToVisit(site.getLinks(), domains);
            for (String link : linksToVisit) {
                submitCrawl(link, currentDepth + 1, maxDepth, domains, site);
            }
        }
    }

    private boolean shouldVisit(String sanitizedUrl) {
        return alreadyVisitedUrls.add(sanitizedUrl);
    }

    private void updateParent(Website site, Website parent) {
        if (parent != null) {
            site.setParentUrl(parent.getOwnUrl());
        }
    }

    private void logError(String url, Exception e) {
        System.err.printf("Error crawling: %s - %s%n", url, e.getMessage());
    }

    private void finishTask() {
        if (activeTasks.decrementAndGet() == 0) {
            synchronized (lock) {
                lock.notifyAll();
            }
        }
    }

    private void waitForCompletion() {
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
    }

    private void shutdownExecutor() {
        executor.shutdown();
        try {
            executor.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
