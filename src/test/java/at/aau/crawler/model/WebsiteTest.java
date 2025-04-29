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

        assertEquals("https://parent.example.com", website.parentUrl); // Direct field access (or via getter if you add one)
    }

    @Test
    void printDetailsShouldFormatWebsiteDetailsCorrectly() {
        Heading heading = new Heading("h1", "Sample Heading");
        List<Heading> headings = List.of(heading);
        List<String> links = List.of("https://example.com/link");

        Website website = new Website(1, headings, links, "https://example.com");
        website.setParentUrl("https://parent.example.com");

        String output = website.printDetails();

        assertTrue(output.contains("Ownurl: https://example.com"));
        assertTrue(output.contains("Parenturl: https://parent.example.com"));
        assertTrue(output.contains("Depth: 1"));
        assertTrue(output.contains("<h1>Sample Heading</h1>"));
        assertTrue(output.contains("<a>https://example.com/link</a>"));
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
    void printDetailsShouldReturnBrokenWebsiteDetailsCorrectly() {
        Website website = new Website("https://child.example.com");

        String output = website.printDetails();

        assertTrue(website.isBroken());
        assertTrue(output.contains("Broken Website"));
    }
}
