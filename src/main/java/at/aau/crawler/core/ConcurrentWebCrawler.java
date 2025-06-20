package at.aau.crawler.core;

import at.aau.crawler.interfaces.Crawler;
import at.aau.crawler.interfaces.HtmlParser;
import at.aau.crawler.interfaces.WebDocument;
import at.aau.crawler.model.Heading;
import at.aau.crawler.model.Website;
import at.aau.crawler.utilities.UrlUtils;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ConcurrentWebCrawler implements Crawler {

    private final HtmlParser parser;
    private final Set<String> alreadyVisitedUrls = ConcurrentHashMap.newKeySet();
    private final ExecutorService executor = new ThreadPoolExecutor(
            100, 100,
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>()
    );
    private final AtomicInteger activeTasks = new AtomicInteger(0);
    private final Object lock = new Object();
    private final List<Website> result = Collections.synchronizedList(new ArrayList<>());

    public ConcurrentWebCrawler(HtmlParser parser) {
        this.parser = parser;
    }

    @Override
    public List<Website> crawlWebsite(String url, int maxDepth, List<String> domains) {
        if (maxDepth < 1 || url == null || url.isEmpty()) return List.of();

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
            WebDocument doc = parser.parse(url);
            List<Heading> headings = extractHeadings(doc);
            List<String> links = doc.getLinks();
            return new Website(depth, headings, links, url);
        } catch (Exception e) {
            return new Website(url); // Broken website
        }
    }

    protected List<Heading> extractHeadings(WebDocument doc) {
        List<Heading> headings = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            String tag = "h" + i;
            String text = doc.getTextFromHeading(tag);
            if (text != null && !text.isEmpty()) {
                headings.add(new Heading(tag, text));
            }
        }
        return headings;
    }

    protected List<String> filterLinksToVisit(List<String> links, List<String> domains) {
        if (domains == null || links == null) return List.of();

        return links.stream()
                .map(String::toLowerCase)
                .filter(domainFilter(domains))
                .distinct()
                .collect(Collectors.toList());
    }

    protected Predicate<String> domainFilter(List<String> domains) {
        List<String> lowerDomains = domains.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        return link -> lowerDomains.stream().anyMatch(link::contains);
    }

    protected boolean shouldVisit(String sanitizedUrl) {
        return alreadyVisitedUrls.add(sanitizedUrl);
    }

    private void scheduleSubLinks(Website site, int currentDepth, int maxDepth, List<String> domains) {
        if (!site.isBroken() && currentDepth < maxDepth) {
            List<String> linksToVisit = filterLinksToVisit(site.getLinks(), domains);
            for (String link : linksToVisit) {
                submitCrawl(link, currentDepth + 1, maxDepth, domains, site);
            }
        }
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