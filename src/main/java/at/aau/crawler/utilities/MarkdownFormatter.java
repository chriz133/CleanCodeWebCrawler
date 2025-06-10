package at.aau.crawler.utilities;

import at.aau.crawler.model.Website;

public class MarkdownFormatter {

    public String formatMetadata(Website website, String tabs) {
        return website.getFormattedUrl(tabs)
                + website.getFormattedParentUrl(tabs)
                + website.getFormattedDepth(tabs);
    }

    public String formatBrokenNotice(Website website, String tabs) {
        return website.getFormattedBrokenInfo(tabs);
    }

    public String formatHeadings(Website website, String tabs) {
        return website.getFormattedHeadings(tabs) + "\n";
    }

    public String formatLinkLine(String link, String tabs) {
        return tabs + "Link --> " + UrlUtils.trimUrl(link) + "\n";
    }

    public static String getTabs(int depth) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < depth; i++) {
            sb.append("\t");
        }
        return sb.toString();
    }
}
