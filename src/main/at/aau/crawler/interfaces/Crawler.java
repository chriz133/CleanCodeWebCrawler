package aau.crawler.interfaces;

import aau.crawler.model.Website;

import java.util.List;

public interface Crawler {
    List<Website> crawlWebsite(String url, int maxDepth, List<String> domains);
    String convertWebsiteToString(Website website);
}
