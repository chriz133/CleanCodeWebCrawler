package aau.crawler.interfaces;

import aau.crawler.model.Website;

import java.util.List;

public interface Crawler {
    List<Website> crawlWebsite(String url, int maxDepth, List<String> domains);
    boolean printWebsitesToFile(List<Website> websites, String filename, String path);
}
