package at.aau.crawler.interfaces;


import at.aau.crawler.model.Website;

import java.util.List;

public interface Crawler {
    /**
     * First all headings, links and the maximum depth of th given url is
     * stored as a Website. This will be the root website from which the
     * crawler will visit all links of this and all subsequent websites
     * up to maximum depth.
     * @param url               the starting URL to crawl
     * @param maxDepth          the maximum recursion depth to crawl
     * @param domains           the list of allowed domains to include in the crawl
     * @return List<Website>    the list of websites to write
     */
    List<Website> crawlWebsite(String url, int maxDepth, List<String> domains);
}
