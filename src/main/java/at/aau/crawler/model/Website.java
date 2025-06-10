package at.aau.crawler.model;

import at.aau.crawler.utilities.UrlUtils;

import java.util.List;

public class Website {
    int depth;
    String ownUrl;
    String parentUrl;
    List<Heading> headings;
    List<String> links;
    boolean isBroken;

    public Website(int depth, List<Heading> headings, List<String> links, String ownUrl) {
        this.depth = depth;
        this.headings = headings;
        this.links = links;
        this.ownUrl = ownUrl;
    }

    public Website(String ownUrl) {
        this(-1, null, null, ownUrl);
        this.isBroken = true;
    }

    public int getDepth() {
        return depth;
    }

    public List<Heading> getHeadings() {
        return headings;
    }

    public List<String> getLinks() {
        return links;
    }

    public String getParentUrl() {
        return parentUrl;
    }

    public String getOwnUrl() {
        return ownUrl;
    }

    public void setParentUrl(String parentUrl) {
        this.parentUrl = parentUrl;
    }

    public boolean isBroken() {
        return isBroken;
    }

    @Override
    public String toString() {
        return "Website{" +
                "depth=" + depth +
                ", ownUrl='" + ownUrl + '\'' +
                ", parentUrl='" + parentUrl + '\'' +
                ", headings=" + headings +
                ", links=" + links +
                '}';
    }

    public String getFormattedUrl(String tabs) {
        return tabs + "Ownurl: " + ownUrl + "\n";
    }

    public String getFormattedParentUrl(String tabs) {
        return parentUrl != null ? tabs + "Parenturl: " + parentUrl + "\n" : "";
    }

    public String getFormattedDepth(String tabs) {
        return tabs + "Depth: " + depth + "\n";
    }

    public String getFormattedBrokenInfo(String tabs) {
        return isBroken ? tabs + "Broken Website\n\n" : "";
    }

    public String getFormattedHeadings(String tabs) {
        if (headings == null) return "";
        StringBuilder sb = new StringBuilder();
        for (Heading heading : headings) {
            sb.append(tabs)
                    .append("<").append(heading.getType()).append(">")
                    .append(heading.getValue())
                    .append("</").append(heading.getType()).append(">\n");
        }
        return sb.toString();
    }

    public String getFormattedLinks(String tabs) {
        if (links == null) return "";
        StringBuilder sb = new StringBuilder();
        for (String link : links) {
            sb.append(tabs)
                    .append("Link --> ")
                    .append(UrlUtils.trimUrl(link))
                    .append("\n");
        }
        return sb.toString();
    }
}
