package aau.crawler.main;

import aau.crawler.model.Website;
import aau.crawler.utilities.WebCrawlerService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WebCrawler {
    public static void main(String[] args) {
        new WebCrawler().run(args);
    }

    private void run(String[] args) {
        validateArguments(args);

        String url = args[0];
        int depth = parseDepth(args[1]);
        List<String> domains = parseDomains(args[2]);

        List<Website> websites = new WebCrawlerService().crawlWebsite(url, depth, domains);
        saveWebsitesToFile(websites);
    }

    private void validateArguments(String[] args) {
        if (args.length < 3) {
            throw new IllegalArgumentException("Usage: java MainProgram <URL> <depth> <domain1,domain2,...>");
        }
    }

    private int parseDepth(String depthArg) {
        try {
            return Integer.parseInt(depthArg);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Depth must be an integer!", e);
        }
    }

    private List<String> parseDomains(String domainArg) {
        return Arrays.stream(domainArg.split(",")).map(String::trim).collect(Collectors.toList());
    }

    private void saveWebsitesToFile(List<Website> websites) {
        String filename = "output_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".md";
        boolean status = new WebCrawlerService().printWebsitesToFile(websites, filename, "src/main/at/aau/crawler/files");

        if (status) {
            System.out.println("Successfully printed websites to file!");
        } else {
            System.err.println("Failed to print websites to file!");
        }
    }
}
