package at.aau.crawler.writer;

import at.aau.crawler.model.Heading;
import at.aau.crawler.model.Website;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MarkdownFormatterTest {

    MarkdownFormatter formatter = new MarkdownFormatter();

    @Test
    void formatsMetadataCorrectly() {
        Website site = new Website(2, List.of(), List.of(), "http://test.com");
        site.setParentUrl("http://parent.com");
        String result = formatter.formatMetadata(site, "\t");
        assertTrue(result.contains("Ownurl: http://test.com"));
        assertTrue(result.contains("Parenturl: http://parent.com"));
        assertTrue(result.contains("Depth: 2"));
    }

    @Test
    void formatsBrokenNotice() {
        Website broken = new Website("http://broken.com");
        String result = formatter.formatBrokenNotice(broken, "");
        assertTrue(result.contains("Broken Website"));
    }

    @Test
    void formatsHeadingsCorrectly() {
        Website site = new Website(1, List.of(new Heading("h1", "Title")), List.of(), "http://test.com");
        String result = formatter.formatHeadings(site, "");
        assertTrue(result.contains("<h1>Title</h1>"));
    }

    @Test
    void formatsLinkLine() {
        String result = formatter.formatLinkLine("http://abc.com/", "\t");
        assertTrue(result.contains("Link --> http://abc.com"));
    }
}
