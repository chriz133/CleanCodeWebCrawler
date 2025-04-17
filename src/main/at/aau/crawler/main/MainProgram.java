package aau.crawler.main;


import aau.crawler.model.Website;
import aau.crawler.utilities.Crawler;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MainProgram {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        List<String> domains = Arrays.stream(new String[]{"wetter.orf.at", "sport.orf.at"}).collect(Collectors.toList());

        List<Website> websites = Crawler.crawlWebsite("https://www.orf.at", 3, domains);
        for (Website website2 : websites) {
            System.out.println(website2);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Time taken: " + (endTime - startTime) / 1000 + "s");
    }
}
