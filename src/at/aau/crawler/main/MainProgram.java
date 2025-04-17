package at.aau.crawler.main;

import at.aau.crawler.model.Website;
import at.aau.crawler.utilities.Crawler;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MainProgram {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        Website website = Crawler.extractLinksAndHeading("https://www.orf.at", 1);
        List<String> domains = Arrays.stream(new String[]{"wien.orf.at", "wetter.orf.at"}).collect(Collectors.toList());
//        Crawler.trackVisitedWebsites(website, 3, domains);

        System.out.println(website);
        List<Website> websites = Crawler.trackVisitedWebsites(website, 2, domains);
        for (Website website2 : websites) {
            System.out.println(website2);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Time taken: " + (endTime - startTime) / 1000 + "s");
    }
}
