package aau.crawler.utilities;

import aau.crawler.model.Website;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

public class CrawlerTest {

    @Test
    public void passExtractLinksAndHeading(){
        List<String> headings = List.of("Example Domain");
        List<String> links = List.of("https://www.iana.org/domains/example");
//        Website website = Crawler.extractLinksAndHeading("https://example.com/", 1);

//        assertEquals(headings, website.getHeadings());
//        assertEquals(links, website.getLinks());
    }

}
