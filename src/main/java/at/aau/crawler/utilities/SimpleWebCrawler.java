package at.aau.crawler.utilities;

import at.aau.crawler.interfaces.Crawler;
import at.aau.crawler.model.Heading;
import at.aau.crawler.model.Website;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * A basic implementation of the {@link Crawler} interface that performs
 * a depth-limited recursive crawl of a website and its internal links.
 * <p>
 * This crawler collects heading elements (h1–h4) and valid links from
 * HTML documents using the Jsoup library.
 * </p>
 */
public class SimpleWebCrawler implements Crawler {
    private List<String> alreadyVisitedUrls;
    private ExecutorService executor;

    public SimpleWebCrawler() {
        this.alreadyVisitedUrls = new ArrayList<>();
//        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.executor = Executors.newFixedThreadPool(1);
    }

    @Override
    public List<Website> crawlWebsite(String url, int maxDepth, List<String> domains) {
        if (maxDepth < 0){
            return null;
        }

        Website website = this.extractLinksAndHeading(url, 1);

        if (website.isBroken()) {
            return null;
        }
        List<Website> websites = trackVisitedWebsites(website, maxDepth, domains);
        websites.add(0, website);
        this.alreadyVisitedUrls.clear();
        this.executor.shutdown();
        try {
            if (!executor.awaitTermination(300, TimeUnit.SECONDS)) {
                executor.shutdownNow(); // Force shutdown if needed
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        return websites;
    }

    /**
     * First connects to @param url via Jsoup and loads corresponding html document.
     * All headings from h1 to h4 are stored in a new List oh Headings.
     * Every link of the website is stored in an element-list and is later
     * converted to an absolute link (f.e. /sport to https://example.com/sport).
     * The stream enables sequential and parallel operation on the elements and
     * with map() each element gets addressed.
     *
     * @param url           the starting URL to crawl
     * @param newDepth      the new maximum recursion depth to crawl
     * @return Website      returning Website with links and headings
     */
    private Website extractLinksAndHeading(String url, int newDepth) {
        try {
            Document doc = Jsoup.parse(new URL(url).openStream(), "UTF-8", url);
            List<Heading> headlines = new ArrayList<>();
            for (int i = 1; i <= 4; i++) {
                for (Element element : doc.select("h" + i)) {
                    headlines.add(new Heading("h" + i, element.text()));
                }
            }
            Elements extractedLinks = doc.select("a[href]");

            Website website = new Website(newDepth,
                                          headlines,
                                          extractedLinks.stream().map(l -> l.attr("abs:href")).collect(Collectors.toList()),
                                          url);
            return website;
        } catch (IOException | IllegalArgumentException e) {
            Website brokenWebsite = new Website(url);
            return brokenWebsite;
        }
    }

    /**
     * visitedWebsites contains all websites for the current root url.
     * linksToVisit has all links of the root url that must be visited.
     * For each link to visit it is checked if it has already been visited
     * since it can only be visited once. Furthermore, each link recursively
     * calls visitWebsite() and is in this case the new root url.
     *
     * @param website           websites to retrieve its links
     * @param maxDepth          the maximum recursion depth to crawl
     * @param domains           domains to be crawled
     * @return List<Website>    visited websites are returned
     */
    private List<Website> trackVisitedWebsites(Website website, int maxDepth, List<String> domains) {
        List<Website> visitedWebsites = new ArrayList<>();

        List<String> linksToVisit = filterLinksToVisit(website.getLinks(), domains);
        if (linksToVisit == null) {
            return visitedWebsites;
        }

        for (String link : linksToVisit) {
            String sanitizedLink = trimUrl(link);

            if (isNewUrl(sanitizedLink)) {
                executor.submit(() -> {
                     Website linkedWebsite = visitWebsite(sanitizedLink, website);
                     visitedWebsites.add(linkedWebsite);
                });
//                Website linkedWebsite = visitWebsite(sanitizedLink, website);
//                visitedWebsites.add(linkedWebsite);
            }
        }

        if (shouldCrawlDeeper(visitedWebsites, maxDepth)) {
            List<Website> newVisitedWebsites = new ArrayList<>();
            for (Website childWebsite : visitedWebsites) {
                if (!childWebsite.isBroken()) {
                    newVisitedWebsites.addAll(trackVisitedWebsites(childWebsite, maxDepth, domains));
                }
            }
            visitedWebsites.addAll(newVisitedWebsites);
        }

        return visitedWebsites;
    }

    /**
     * Checks if the given URL was already visited. If not, marks it as visited.
     *
     * @param url the URL to check
     * @return true if it's a new URL, false if it was already visited
     */
    private boolean isNewUrl(String url) {
        if (!alreadyVisitedUrls.contains(url)) {
            alreadyVisitedUrls.add(url);
            return true;
        }
        return false;
    }

    /**
     * Visits the given URL, extracts its content, and links it to the parent website.
     *
     * @param url           the URL to visit
     * @param parentWebsite the parent {@link Website} object
     * @return the newly visited {@link Website}
     */
    private Website visitWebsite(String url, Website parentWebsite) {
        Website website = extractLinksAndHeading(url, parentWebsite.getDepth() + 1);
        website.setParentUrl(parentWebsite.getOwnUrl());
        return website;
    }

    /**
     * Determines if further recursive crawling should occur based on current depth and maxDepth.
     *
     * @param websites list of websites just visited
     * @param maxDepth the maximum depth allowed
     * @return true if recursion should continue, false otherwise
     */
    private boolean shouldCrawlDeeper(List<Website> websites, int maxDepth) {
        return !websites.isEmpty() && websites.get(0).getDepth() < maxDepth;
    }

    /**
     * Checks if the link contains the allowed domains.
     * @param links     links which should contain given domains
     * @param domains   domains should occur in given links
     * @return          list of links that should be visited with given domains
     */
    private List<String> filterLinksToVisit(List<String> links, List<String> domains) {
        if (domains == null) {
            return null;
        }

        List<String> linksToVisit = new ArrayList<>();

        for (String link : links) {
            for (String domain : domains) {
                if (link.contains(domain) && !linksToVisit.contains(link)) {
                    linksToVisit.add(link);
                }
            }
        }
        return linksToVisit;
    }

    /**
     * Cleans a URL by removing trailing slashes or hash symbols.
     *
     * @param url the URL to sanitize
     * @return a trimmed version of the URL without trailing slashes or hashes
     */
    private String trimUrl(String url) {
        if (url == null || url.isEmpty()) return url;
        if (url.endsWith("/") || url.endsWith("#")) {
            return url.substring(0, url.length() - 1);
        }
        return url;
    }
}
