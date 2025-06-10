package at.aau.crawler.writer;

import at.aau.crawler.interfaces.WebsiteFormatter;
import at.aau.crawler.model.Website;
import at.aau.crawler.utilities.UrlUtils;

public class MarkdownFormatter implements WebsiteFormatter {

    @Override
    public String formatMetadata(Website website, String tabs) {
        return website.getFormattedUrl(tabs)
                + website.getFormattedParentUrl(tabs)
                + website.getFormattedDepth(tabs);
    }

    @Override
    public String formatBrokenNotice(Website website, String tabs) {
        return website.getFormattedBrokenInfo(tabs);
    }

    @Override
    public String formatHeadings(Website website, String tabs) {
        return website.getFormattedHeadings(tabs) + "\n";
    }

    @Override
    public String formatLinkLine(String link, String tabs) {
        return tabs + "Link --> " + UrlUtils.trimUrl(link) + "\n";
    }
}
