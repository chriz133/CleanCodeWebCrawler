package at.aau.crawler.main;


import at.aau.crawler.services.WebCrawlerService;
import at.aau.crawler.utilities.MarkdownWebsiteWriter;
import at.aau.crawler.utilities.SimpleWebCrawler;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WebCrawler {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java WebCrawler <url> <depth> <domain1,domain2,...>");
            System.exit(1);
        }

        String url = args[0];
        int depth = Integer.parseInt(args[1]);
        List<String> domains = Arrays.stream(args[2].split(","))
                .map(String::trim)
                .collect(Collectors.toList());

        System.out.println("Website gets crawled and saved... Please wait...");

        WebCrawlerService service = new WebCrawlerService(new SimpleWebCrawler(), new MarkdownWebsiteWriter());
        boolean success = service.crawlAndSave(url, depth, domains, "src/main/java/at/aau/crawler/files");

        if (success) {
            System.out.println("Crawling and saving successful!");
        } else {
            System.err.println("Crawling or saving failed.");
        }
    }
}
