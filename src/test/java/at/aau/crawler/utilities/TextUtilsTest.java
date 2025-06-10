package at.aau.crawler.utilities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TextUtilsTest {

    @Test
    void returnsCorrectTabsForDepth() {
        assertEquals("", TextUtils.getTabs(1));
        assertEquals("\t", TextUtils.getTabs(2));
        assertEquals("\t\t", TextUtils.getTabs(3));
    }

    @Test
    void handlesZeroDepthGracefully() {
        assertEquals("", TextUtils.getTabs(0));
        assertEquals("", TextUtils.getTabs(-1)); // defensive case
    }
}
