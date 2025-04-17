package at.aau.crawler.utilities;

import at.aau.crawler.model.Website;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Crawler {
    private Crawler() {}


    public static Website extractLinksAndHeading(String url, int newDepth) {
        try {
            Document doc = Jsoup.connect(url).get();
            Elements newsHeadlines = new Elements();
            for (int i = 1; i <= 4; i++) {
                newsHeadlines.addAll(doc.select("h" + i));
            }
            Elements links = doc.select("a[href]");

            Website website = new Website(newDepth,
                                          newsHeadlines.stream().map(Element::text).collect(Collectors.toList()),
                                          links.stream().map(l -> l.attr("abs:href")).collect(Collectors.toList()),
                                          url);
            return website;
        } catch (IOException e) {
            return null;
        }
    }

    public static List<Website> trackVisitedWebsites(Website website, int maxDepth, List<String> domains) {
       List<Website> visitedWebsites = new ArrayList<>();

        List<String> linksToVisit = new ArrayList<>();

        for (String link : website.getLinks()) {
            for (String domain : domains) {
                if (link.contains(domain)) {
                    linksToVisit.add(link);
                }
            }
        }

        linksToVisit.forEach(link -> visitedWebsites.add(extractLinksAndHeading(link, website.getDepth() + 1)));\

        if (!visitedWebsites.isEmpty() && visitedWebsites.get(0).getDepth() < maxDepth) {
            List<Website> w2 = new ArrayList<>();
            visitedWebsites.forEach(w -> w2.addAll(trackVisitedWebsites(w, maxDepth, domains)));
            visitedWebsites.addAll(w2);
        } else {
            return visitedWebsites;
        }

        return visitedWebsites;
    }
}
