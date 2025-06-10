package at.aau.crawler.adapter;

import at.aau.crawler.interfaces.WebDocument;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class JsoupHtmlParserTest {

    @Test
    void parse_shouldReturnParsedDocument() throws Exception {
        File tempHtml = File.createTempFile("test", ".html");
        String content = "<html><body><h1>Test</h1><a href='http://example.com'>Link</a></body></html>";
        Files.write(tempHtml.toPath(), content.getBytes(StandardCharsets.UTF_8));

        String fileUrl = tempHtml.toURI().toURL().toString();

        JsoupHtmlParser parser = new JsoupHtmlParser();
        WebDocument doc = parser.parse(fileUrl);

        assertNotNull(doc);
        assertEquals("Test", doc.getTextFromHeading("h1"));
        assertTrue(doc.getLinks().contains("http://example.com"));

        tempHtml.deleteOnExit();
    }
}
