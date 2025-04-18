package aau.crawler.model;

import java.util.List;

public class Website {
    int depth;
    String ownUrl;
    String parentUrl;
    List<String> headings;
    List<String> links;
    boolean isBroken;

    public Website() {}

    public Website(int depth, List<String> headings, List<String> links, String ownUrl) {
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

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public List<String> getHeadings() {
        return headings;
    }

    public void setHeadings(List<String> headings) {
        this.headings = headings;
    }

    public List<String> getLinks() {
        return links;
    }

    public void setLinks(List<String> links) {
        this.links = links;
    }

    public String getOwnUrl() {
        return ownUrl;
    }

    public void setOwnUrl(String ownUrl) {
        this.ownUrl = ownUrl;
    }

    public String getParentUrl() {
        return parentUrl;
    }

    public void setParentUrl(String parentUrl) {
        this.parentUrl = parentUrl;
    }

    public boolean isBroken() {
        return isBroken;
    }

    public void setBroken(boolean broken) {
        isBroken = broken;
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
}
