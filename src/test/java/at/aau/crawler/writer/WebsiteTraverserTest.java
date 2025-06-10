package at.aau.crawler.writer;

import at.aau.crawler.model.Website;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WebsiteTraverserTest {

    private MarkdownFormatter formatter;
    private WebsiteTraverser traverser;

    @BeforeEach
    void setup() {
        formatter = mock(MarkdownFormatter.class);
        traverser = new WebsiteTraverser(formatter);
    }

    @Test
    void traversesSingleWebsite() {
        Website website = new Website(1, List.of(), List.of(), "http://test.com");

        when(formatter.formatMetadata(eq(website), anyString())).thenReturn("META\n");
        when(formatter.formatHeadings(eq(website), anyString())).thenReturn("HEADINGS\n");

        String result = traverser.traverse(List.of(website));

        assertTrue(result.contains("META"));
        assertTrue(result.contains("HEADINGS"));
    }
}
