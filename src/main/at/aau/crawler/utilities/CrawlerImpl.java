package aau.crawler.utilities;

import aau.crawler.interfaces.Crawler;
import aau.crawler.model.Heading;
import aau.crawler.model.Website;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CrawlerImpl implements Crawler {
    private List<String> alreadyVisitedUrls;

    public CrawlerImpl() {
        this.alreadyVisitedUrls = new ArrayList<>();
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
        return websites;
    }

    @Override
    public boolean printWebsitesToFile(List<Website> websites, String filename, String path) {
        try {
            Path directory = Paths.get(path);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }

            Path filePath = directory.resolve(filename);

            BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
            for (Website website : websites) {
                bw.write(website.printDetails());
            }
            bw.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private Website extractLinksAndHeading(String url, int newDepth) {
        try {
            Document doc = Jsoup.connect(url).get();
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

    private List<Website> trackVisitedWebsites(Website website, int maxDepth, List<String> domains) {
       List<Website> visitedWebsites = new ArrayList<>();

        List<String> linksToVisit = this.filterLinksToVisit(website.getLinks(), domains);

        linksToVisit.forEach(url -> {
            url = this.trimUrl(url);

            if (!this.alreadyVisitedUrls.contains(url)) {
                this.alreadyVisitedUrls.add(url);
                Website visitedWebsite = this.extractLinksAndHeading(url, website.getDepth() + 1);
                visitedWebsite.setParentUrl(website.getOwnUrl());
                visitedWebsites.add(visitedWebsite);
            }
        });

        if (!visitedWebsites.isEmpty() && visitedWebsites.get(0).getDepth() < maxDepth) {
            List<Website> newVisitedWebsites = new ArrayList<>();
            visitedWebsites.forEach(w -> {
                if (!w.isBroken()) {
                    newVisitedWebsites.addAll(trackVisitedWebsites(w, maxDepth, domains));
                }
            });
            visitedWebsites.addAll(newVisitedWebsites);
        }
        return visitedWebsites;
    }

    private List<String> filterLinksToVisit(List<String> links, List<String> domains) {
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

    private String trimUrl(String url) {
        if (url.charAt(url.length() -1) == '/' ||
                url.charAt(url.length() -1) == '#') {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }
}
