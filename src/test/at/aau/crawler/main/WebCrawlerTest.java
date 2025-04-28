package aau.crawler.main;

import org.junit.jupiter.api.Test;

public class WebCrawlerTest {


    @Test
    public void tooFewArgumentsTest() {
        WebCrawler.main(new String[]{});

    }
}
