package at.aau.crawler.services;

import at.aau.crawler.interfaces.Crawler;
import at.aau.crawler.interfaces.WebsiteWriter;
import at.aau.crawler.model.Website;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WebCrawlerServiceTest {

    private Crawler mockCrawler;
    private WebsiteWriter mockWriter;
    private WebCrawlerService service;

    @BeforeEach
    void setUp() {
        mockCrawler = mock(Crawler.class);
        mockWriter = mock(WebsiteWriter.class);
        service = new WebCrawlerService(mockCrawler, mockWriter);
    }

    @Test
    void crawlAndSaveShouldReturnTrueWhenWritingSucceeds() {
        List<Website> dummyWebsites = List.of(new Website("https://example.com"));
        when(mockCrawler.crawlWebsite("https://example.com", 1, List.of("example.com")))
                .thenReturn(dummyWebsites);
        when(mockWriter.writeWebsites(any(), anyString(), anyString()))
                .thenReturn(true);

        boolean result = service.crawlAndSave("https://example.com", 1, List.of("example.com"), "/some/path");

        assertTrue(result);
        verify(mockCrawler).crawlWebsite("https://example.com", 1, List.of("example.com"));
        verify(mockWriter).writeWebsites(eq(dummyWebsites), anyString(), eq("/some/path"));
    }

    @Test
    void crawlAndSaveShouldReturnFalseWhenWritingFails() {
        List<Website> dummyWebsites = List.of(new Website("https://example.com"));
        when(mockCrawler.crawlWebsite(anyString(), anyInt(), anyList()))
                .thenReturn(dummyWebsites);
        when(mockWriter.writeWebsites(any(), anyString(), anyString()))
                .thenReturn(false);

        boolean result = service.crawlAndSave("https://example.com", 1, List.of("example.com"), "/some/path");

        assertFalse(result);
        verify(mockWriter).writeWebsites(eq(dummyWebsites), anyString(), eq("/some/path"));
    }

    @Test
    void crawlAndSaveShouldUseExpectedFilenameFormat() {
        List<Website> dummyWebsites = List.of(new Website("https://example.com"));
        when(mockCrawler.crawlWebsite(anyString(), anyInt(), anyList()))
                .thenReturn(dummyWebsites);
        ArgumentCaptor<String> filenameCaptor = ArgumentCaptor.forClass(String.class);
        when(mockWriter.writeWebsites(any(), filenameCaptor.capture(), anyString()))
                .thenReturn(true);

        service.crawlAndSave("https://example.com", 1, List.of("example.com"), "/some/path");

        String filename = filenameCaptor.getValue();
        assertTrue(filename.matches("output_\\d{8}_\\d{6}\\.md"), "Filename format should match expected pattern.");
    }
}
