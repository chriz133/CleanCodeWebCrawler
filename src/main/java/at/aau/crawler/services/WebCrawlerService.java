package at.aau.crawler.services;

import at.aau.crawler.interfaces.Crawler;
import at.aau.crawler.interfaces.WebsiteWriter;
import at.aau.crawler.model.Website;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service class that coordinates crawling a website and saving the results to a file.
 * <p>
 * It delegates crawling to a {@link Crawler} implementation and writing the output
 * to a {@link WebsiteWriter} implementation.
 * </p>
 */
public class WebCrawlerService {

    private final Crawler crawler;
    private final WebsiteWriter writer;

    /**
     * Constructs a new WebCrawlerService with the given crawler and writer.
     *
     * @param crawler the crawler used to retrieve websites and links
     * @param writer  the writer used to save the crawl results to a file
     */
    public WebCrawlerService(Crawler crawler, WebsiteWriter writer) {
        this.crawler = crawler;
        this.writer = writer;
    }

    /**
     * Crawls the specified URL up to a given depth, filters by allowed domains,
     * and writes the results to a markdown file at the specified path.
     *
     * @param url     the starting URL to crawl
     * @param depth   the maximum recursion depth to crawl
     * @param domains the list of allowed domains to include in the crawl
     * @param path    the directory path where the result file should be stored
     * @return true if writing the results to file was successful, false otherwise
     */
    public boolean crawlAndSave(String url, int depth, List<String> domains, String path) {
        List<Website> websites = crawler.crawlWebsite(url, depth, domains);
        for (Website website : websites) {
            System.out.println(website);
        }
        String filename = generateFilename();
        return writer.writeWebsites(websites, filename, path);
    }

    /*
     * Generates a unique filename based on the current timestamp.
     * A filename in the format "output_yyyyMMdd_HHmmss.md" is returned.
     */
    private String generateFilename() {
        return "output_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".md";
    }
}
