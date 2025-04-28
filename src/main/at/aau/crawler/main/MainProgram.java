package aau.crawler.main;


import aau.crawler.model.Website;
import aau.crawler.utilities.CrawlerImpl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MainProgram {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Please enter: java MainProgram <URL> <depth> <domain1,domain2,...>");
            System.out.println("Hint: depth must be an integer!");
            return;
        }
        String url = args[0];
        List<String> domains = Arrays.stream(args[2].split(",")).map(String::trim).collect(Collectors.toList());
        int depth;
        try {
            depth = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid, depth must be an integer!");
            return;
        }
        CrawlerImpl crawler = new CrawlerImpl();

        List<Website> websites = crawler.crawlWebsite(url, depth, domains);

        String filename = String.format("output_%s.md", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));
        boolean status = crawler.printWebsitesToFile(websites, filename, "src/main/at/aau/crawler/files");

        if (status) {
            System.out.println("Successfully printed websites to file!");
            System.exit(0);
        } else {
            System.err.println("Failed to print websites to file!");
            System.exit(1);
        }
    }
}
