package at.aau.crawler.interfaces;

public interface HtmlParser {
    WebDocument parse(String url) throws Exception;
}
