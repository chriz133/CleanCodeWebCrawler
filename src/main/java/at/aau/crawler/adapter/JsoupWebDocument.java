package at.aau.crawler.adapter;

import at.aau.crawler.interfaces.WebDocument;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

public class JsoupWebDocument implements WebDocument {
    private final Document document;

    public JsoupWebDocument(Document document) {
        this.document = document;
    }

    @Override
    public String getTextFromHeading(String tag) {
        return document.select(tag).text();
    }

    @Override
    public List<String> getLinks() {
        List<String> links = new ArrayList<>();
        for (Element el : document.select("a[href]")) {
            links.add(el.attr("abs:href"));
        }
        return links;
    }
}
