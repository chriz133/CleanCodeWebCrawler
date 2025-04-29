package aau.crawler.interfaces;

import aau.crawler.model.Website;

import java.util.List;

public interface Crawler {
    /**
     * First all headings, links and the maximum depth of th given url is
     * stored as a Website. This will be the root website from which the
     * crawler will visit all links of this and all subsequent websites
     * up to maximum depth.
     * @param url
     * @param maxDepth
     * @param domains
     * @return List<Website>
     */
    List<Website> crawlWebsite(String url, int maxDepth, List<String> domains);

    /**
     * @param path gets converted to an actual Path and if it not already exists
     * a corresponding directory is created. A file is created with all visited
     * websites.
     * @param websites
     * @param filename
     * @param path
     * @return boolean
     */
    boolean printWebsitesToFile(List<Website> websites, String filename, String path);
}
