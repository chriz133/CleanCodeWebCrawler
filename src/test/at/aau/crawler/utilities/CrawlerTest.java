package aau.crawler.utilities;


import aau.crawler.interfaces.Crawler;
import aau.crawler.model.Website;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class CrawlerTest {

    private Crawler crawler;

    @BeforeEach
    public void setUp() {
        crawler = new CrawlerImpl();
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
    void shouldExtractLinksAndHeadings() {
        String url = "https://example.com/";
        List<String> expectedLinks = List.of("https://www.iana.org/domains/example");
        List<String> allowedDomains = List.of("https://www.iana.org/domains/example");

        List<Website> result = crawler.crawlWebsite(url, 1, allowedDomains);

        assertNotNull(result);
        assertFalse(result.isEmpty());

        Website website = result.get(0);
        System.out.println(website);

        assertTrue(website.getLinks().containsAll(expectedLinks));
        assertTrue(website.getHeadings().get(0).getValue().equals("Example Domain"));
    }

    @Test
    void shouldReturnEmptyListForInvalidUrl() {
        List<Website> result = crawler.crawlWebsite("invalid_url", 1, List.of());
        assertNull(result);
    }

    @Test
    void shouldNotCrawlIfDepthIsLessThanZero() {
        List<Website> result = crawler.crawlWebsite("https://example.com/", -1, List.of());
        assertNull(result);
    }

}
