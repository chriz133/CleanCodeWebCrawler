package aau.crawler.main;


import aau.crawler.model.Website;
import aau.crawler.utilities.CrawlerImpl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MainProgram {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Please enter: java MainProgram <URL> <depth> <domain1,domain2,...>");
            System.out.println("Hint: depth must me an integer!");
            return;
        }
        String url = args[0];
        List<String> domains = Arrays.stream(args[2].split(",")).map(String::trim).collect(Collectors.toList());
        int depth;
        try {
            depth = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid, depth must me an integer!");
            return;
        }

        long startTime = System.currentTimeMillis();
        //List<String> domains = Arrays.stream(new String[]{"wetter.orf.at", "sport.orf.at"}).collect(Collectors.toList());

        CrawlerImpl crawler = new CrawlerImpl();
        //List<Website> websites = crawler.crawlWebsite("https://www.orf.at", 5, domains);
        List<Website> websites = crawler.crawlWebsite(url, depth, domains);

        for (Website website2 : websites) {
            System.out.println(website2);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Time taken: " + (endTime - startTime) / 1000 + "s");

        System.out.println(crawler.printWebsitesToFile(websites, "test.md", "Z:\\UNI\\CleanCode\\CleanCodeWebCrawler\\src\\main\\at\\aau\\crawler\\files"));
    }
}
