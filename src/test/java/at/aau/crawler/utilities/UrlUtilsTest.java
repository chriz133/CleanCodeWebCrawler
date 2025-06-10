package at.aau.crawler.utilities;

import at.aau.crawler.utilities.UrlUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UrlUtilsTest {

    @Test
    void trimsTrailingSlash() {
        assertEquals("http://example.com", UrlUtils.trimUrl("http://example.com/"));
    }

    @Test
    void trimsFragment() {
        assertEquals("http://example.com/page", UrlUtils.trimUrl("http://example.com/page#section"));
    }

    @Test
    void nullOrEmptyReturnsSame() {
        assertNull(UrlUtils.trimUrl(null));
        assertEquals("", UrlUtils.trimUrl(""));
    }

    @Test
    void returnsCleanUrlIfNoTrimmingNeeded() {
        assertEquals("http://example.com", UrlUtils.trimUrl("http://example.com"));
    }
}
