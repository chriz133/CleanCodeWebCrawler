package at.aau.crawler.utilities;

import at.aau.crawler.interfaces.WebsiteWriter;
import at.aau.crawler.model.Website;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MarkdownWebsiteWriterTest {

    private WebsiteWriter websiteWriter;
    private String urlFirstPage;

    @BeforeEach
    public void setUp() throws URISyntaxException {
        websiteWriter = new MarkdownWebsiteWriter();
        URL resource = getClass().getClassLoader().getResource("assets/page1.html");
        urlFirstPage = resource.toURI().toString();
    }

    @Test
    void testPrintWebsitesToFileValidPath() throws IOException {
        List<Website> websites = List.of(
                new Website(1, List.of(), List.of(), "https://example.com"),
                new Website(2, List.of(), List.of(), "https://testWebseite.com")
        );
        String testDirectory = "src/test/at/aau/crawler/testFiles";

        boolean result = websiteWriter.writeWebsites(websites, "websites.md", testDirectory);
        assertTrue(result);

        Path directoryPath = Paths.get(testDirectory);
        Path filePath = directoryPath.resolve("websites.md");

        assertTrue(Files.exists(filePath));
        Files.delete(filePath);
        Files.delete(directoryPath);
        assertFalse(Files.exists(filePath));
    }

    @Test
    void testPrintWebsitesToFileInvalidPath() {
        String testDirectory = "//src/main/at/aau/crawler/testFiles";
        boolean result = websiteWriter.writeWebsites(null, "websites.md", testDirectory);
        assertFalse(result);
    }
}
