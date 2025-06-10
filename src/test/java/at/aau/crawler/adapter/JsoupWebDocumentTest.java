package at.aau.crawler.adapter;

import at.aau.crawler.interfaces.WebDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsoupWebDocumentTest {

    @Test
    void getTextFromHeading_shouldReturnText() {
        String html = "<html><body><h1>Main Title</h1><h2>Sub Title</h2></body></html>";
        Document doc = Jsoup.parse(html);
        WebDocument webDoc = new JsoupWebDocument(doc);

        assertEquals("Main Title", webDoc.getTextFromHeading("h1"));
        assertEquals("Sub Title", webDoc.getTextFromHeading("h2"));
        assertEquals("", webDoc.getTextFromHeading("h3")); // no h3 in HTML
    }

    @Test
    void getLinks_shouldReturnAllAbsoluteLinks() {
        String html = "<html><body>" +
                "<a href='http://example.com'>Example</a>" +
                "<a href='/relative'>Relative</a>" +
                "</body></html>";
        Document doc = Jsoup.parse(html, "http://base.com");
        WebDocument webDoc = new JsoupWebDocument(doc);

        List<String> links = webDoc.getLinks();
        assertEquals(2, links.size());
        assertTrue(links.contains("http://example.com"));
        assertTrue(links.contains("http://base.com/relative"));
    }
}
