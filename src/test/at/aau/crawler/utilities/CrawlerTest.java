package aau.crawler.utilities;


import aau.crawler.interfaces.Crawler;
import aau.crawler.model.Website;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CrawlerTest {

    private Crawler crawler;
    private String urlFirstPage;

    @BeforeEach
    public void setUp() throws URISyntaxException {
        crawler = new CrawlerImpl();
        URL resource = getClass().getClassLoader().getResource("assets/page1.html");
        urlFirstPage = resource.toURI().toString();
    }

    @Test
    public void passCrawlBasicWebsite(){
        String heading = "Example Domain";
        List<String> links = List.of("https://www.iana.org/domains/example");
        List<Website> websites = crawler.crawlWebsite("https://example.com/", 1, links);

        assertEquals(2, websites.size());
        assertEquals(heading, websites.get(0)
                                       .getHeadings()
                                       .get(0).getValue());
    }

    @Test
    public void passCrawlLocalWebsite() {
        List<String> allowedDomains = List.of("file", "NOMATCH");

        List<Website> websites = crawler.crawlWebsite(urlFirstPage, 5, allowedDomains);

        String[] expectedLinks = {"page1.html", "page2.html", "page3.html"};
        String[] expectedHeadings = {"Welcome to Page 1"};

        checkWebsites(expectedHeadings, expectedLinks, 5, websites);
    }

    @Test
    void shouldExtractLinksAndHeadings() {
        List<Website> result = crawler.crawlWebsite(urlFirstPage, 1, null);

        String[] expectedHeadings = {"Welcome to Page 1"};
        String[] expectedLinks = {"page12.html", "page3.html"};

        checkWebsites(expectedHeadings, expectedLinks, 1, result);
    }

    @Test
    void shouldReturnNullForInvalidUrl() {
        List<Website> result = crawler.crawlWebsite("invalid_url", 1, List.of());
        assertNull(result);
    }

    @Test
    void shouldReturnNullIfDepthIsLessThanZero() {
        List<Website> result = crawler.crawlWebsite(urlFirstPage, -1, List.of());
        assertNull(result);
    }

    @Test
    void testPrintWebsitesToFileValidPath() throws IOException {
        List<Website> websites = List.of(
                new Website(1, List.of(), List.of(), "https://example.com"),
                new Website(2, List.of(), List.of(), "https://testWebseite.com")
        );
        String testDirectory = "src/test/at/aau/crawler/testFiles";

        boolean result = crawler.printWebsitesToFile(websites, "websites.md", testDirectory);
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
        boolean result = crawler.printWebsitesToFile(null, "websites.md", testDirectory);
        assertFalse(result);
    }

    @Test
    void testFilterLinksToVisitNullInputs() throws URISyntaxException {
        URL resource = getClass().getClassLoader().getResource("assets/pageNoLinks.html");
        String url = resource.toURI().toString();
        crawler.crawlWebsite(url, 1, null);
    }

    private void checkWebsites(String[] expectedHeadings, String[] expectedLinks, int expectedSize, List<Website> websites) {
        assertEquals(expectedSize, websites.size());

        assertTrue(websites.stream().anyMatch(website ->
                website.getHeadings().stream().anyMatch(heading ->
                        Arrays.asList(expectedHeadings).contains(heading.getValue())
                )
        ));

        assertTrue(websites.stream().anyMatch(website ->
                website.getLinks().stream().anyMatch(link ->
                        Arrays.stream(expectedLinks).anyMatch(expectedLink -> link.contains(expectedLink))
                )
        ));
    }
}
