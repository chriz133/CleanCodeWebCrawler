package at.aau.crawler.adapter;

import at.aau.crawler.interfaces.HtmlParser;
import at.aau.crawler.interfaces.WebDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;

public class JsoupHtmlParser implements HtmlParser {
    @Override
    public WebDocument parse(String url) throws IOException {
        Document doc = Jsoup.parse(new URL(url).openStream(), "UTF-8", url);
        return new JsoupWebDocument(doc);
    }
}
