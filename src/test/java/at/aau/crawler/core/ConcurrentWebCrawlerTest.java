package at.aau.crawler.core;

import at.aau.crawler.model.Heading;
import at.aau.crawler.model.Website;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class ConcurrentWebCrawlerTest {

    private ConcurrentWebCrawler crawler;
    private String urlFirstPage;

    @BeforeEach
    public void setUp() throws URISyntaxException {
        crawler = new ConcurrentWebCrawler();
        URL resource = getClass().getClassLoader().getResource("assets/page1.html");
        urlFirstPage = resource.toURI().toString();
    }

    @Test
    public void passCrawlBasicWebsite(){
        String heading = "Example Domain";
        List<String> links = List.of("https://www.iana.org/domains/example");
        List<Website> websites = crawler.crawlWebsite("https://example.com/", 1, links);

        assertEquals(1, websites.size());
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
        for (Website website : websites) {
            System.out.println(website);
        }
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
    void shouldReturnBrokenWebsiteForInvalidUrl() {
        List<Website> result = crawler.crawlWebsite("invalid_url", 1, List.of());
        assertEquals(1, result.size());
        assertTrue(result.get(0).isBroken());
    }

    @Test
    void shouldReturnNullIfDepthIsLessThanZero() {
        List<Website> result = crawler.crawlWebsite(urlFirstPage, -1, List.of());
        assertNull(result);
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
                        Arrays.stream(expectedLinks).anyMatch(link::contains)
                )
        ));
    }

    @Test
    void extractHeadings_shouldReturnAllLevels() {
        String html = "<h1>Title</h1><h2>Sub</h2><h4>Detail</h4>";
        Document doc = Jsoup.parse(html);
        List<Heading> headings = crawler.extractHeadings(doc);

        assertEquals(3, headings.size());
        assertEquals("h1", headings.get(0).getType());
        assertEquals("Title", headings.get(0).getValue());
    }

    @Test
    void extractLinks_shouldFindAnchors() {
        String html = "<a href='http://example.com'>Example</a><a href='/internal'>Internal</a>";
        Document doc = Jsoup.parse(html, "http://base.com");
        List<String> links = crawler.extractLinks(doc);

        assertEquals(2, links.size());
        assertTrue(links.get(0).contains("example.com") || links.get(1).contains("example.com"));
    }

    @Test
    void filterLinksToVisit_shouldFilterByDomain() {
        List<String> links = List.of(
                "http://example.com/page",
                "http://other.com/page"
        );
        List<String> filtered = crawler.filterLinksToVisit(links, List.of("example.com"));
        assertEquals(1, filtered.size());
        assertTrue(filtered.get(0).contains("example.com"));
    }

    @Test
    void shouldVisit_shouldWorkCorrectly() {
        String url = "http://unique.com";
        assertTrue(crawler.shouldVisit(url));
        assertFalse(crawler.shouldVisit(url)); // already added
    }


    @Test
    void domainFilterMatchesCorrectly() {
        List<String> domains = List.of("example.com", "aau.at");
        Predicate<String> filter = crawler.domainFilter(domains);

        assertTrue(filter.test("http://www.example.com/page"));
        assertTrue(filter.test("https://aau.at/news"));
        assertFalse(filter.test("http://otherdomain.org"));
    }

    @Test
    void domainFilterIsCaseSensitive() {
        List<String> domains = List.of("ExAmPlE.CoM");
        Predicate<String> filter = crawler.domainFilter(domains);

        assertTrue(filter.test("http://example.com"));
        assertFalse(filter.test("http://other.com"));
        assertFalse(filter.test("http://EXAMPLE.com/page"));
    }

    @Test
    void domainFilterWithEmptyListMatchesNothing() {
        Predicate<String> filter = crawler.domainFilter(List.of());

        assertFalse(filter.test("http://example.com"));
        assertFalse(filter.test("http://anything.com"));
    }
}
