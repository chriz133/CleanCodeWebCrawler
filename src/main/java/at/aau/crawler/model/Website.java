package at.aau.crawler.model;

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

    public String printDetails(){
        StringBuilder sb = new StringBuilder();

        sb.append("Ownurl: ").append(ownUrl).append("\n");
        if (this.parentUrl != null){
            sb.append("Parenturl: ").append(parentUrl).append("\n");
        }
        sb.append("Depth: ").append(depth).append("\n\n");

        if (isBroken){
            sb.append("Broken Website").append("\n\n\n");
            return sb.toString();
        }

        for (Heading heading : headings){
            sb.append("<").append(heading.type).append(">");
            sb.append(heading.value);
            sb.append("</").append(heading.type).append(">\n");
        }

        sb.append("\n");

        for (String link : links){
            sb.append("<").append("a").append(">");
            sb.append(link);
            sb.append("</").append("a").append(">\n");
        }

        sb.append("\n\n");

        return sb.toString();
    }
}
