package at.aau.crawler.interfaces;

import at.aau.crawler.model.Website;

public interface WebsiteFormatter {
    String formatMetadata(Website website, String tabs);
    String formatBrokenNotice(Website website, String tabs);
    String formatHeadings(Website website, String tabs);
    String formatLinkLine(String link, String tabs);
}
