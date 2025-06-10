package at.aau.crawler.model;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WebsiteTest {

    @Test
    void constructorShouldInitializeAllFieldsCorrectly() {
        Heading heading1 = new Heading("h1", "Test Heading");
        Heading heading2 = new Heading("h2", "Another Heading");
        List<Heading> headings = List.of(heading1, heading2);
        List<String> links = List.of("https://example.com", "https://example.com/page2");

        Website website = new Website(2, headings, links, "https://example.com");

        assertEquals(2, website.getDepth());
        assertEquals("https://example.com", website.getOwnUrl());
        assertEquals(headings, website.getHeadings());
        assertEquals(links, website.getLinks());
        assertFalse(website.isBroken());
    }

    @Test
    void brokenWebsiteConstructorShouldMarkWebsiteAsBroken() {
        Website website = new Website("https://broken.example.com");

        assertTrue(website.isBroken());
        assertEquals(-1, website.getDepth());
        assertEquals("https://broken.example.com", website.getOwnUrl());
        assertNull(website.getHeadings());
        assertNull(website.getLinks());
    }

    @Test
    void setParentUrlShouldAssignParentCorrectly() {
        Website website = new Website("https://child.example.com");
        website.setParentUrl("https://parent.example.com");

        assertEquals("https://parent.example.com", website.getParentUrl());
    }

    @Test
    void toStringShouldContainKeyFields() {
        Heading heading = new Heading("h2", "Another Heading");
        Website website = new Website(0, List.of(heading), List.of("https://link.example.com"), "https://own.example.com");

        String result = website.toString();

        assertTrue(result.contains("depth=0"));
        assertTrue(result.contains("ownUrl='https://own.example.com'"));
        assertTrue(result.contains("links=[https://link.example.com]"));
    }

    @Test
    void formatsAllPartsCorrectly() {
        Website site = new Website(1, List.of(new Heading("h1", "Header")), List.of("http://link.com"), "http://test.com");
        site.setParentUrl("http://parent.com");

        String tabs = "\t";

        assertTrue(site.getFormattedUrl(tabs).contains("Ownurl: http://test.com"));
        assertTrue(site.getFormattedParentUrl(tabs).contains("Parenturl: http://parent.com"));
        assertTrue(site.getFormattedDepth(tabs).contains("Depth: 1"));
        assertTrue(site.getFormattedHeadings(tabs).contains("<h1>Header</h1>"));
    }

    @Test
    void formatsBrokenWebsiteNotice() {
        Website broken = new Website("http://broken.com");
        assertTrue(broken.getFormattedBrokenInfo("").contains("Broken Website"));
    }
}
