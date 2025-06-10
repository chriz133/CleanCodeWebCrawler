package at.aau.crawler.writer;

import at.aau.crawler.model.Website;
import at.aau.crawler.services.FileWriterService;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MarkdownWebsiteWriterTest {

    @Test
    void writeWebsites_ShouldReturnTrue_WhenWriteSucceeds() throws IOException {
        WebsiteTraverser mockTraverser = mock(WebsiteTraverser.class);
        FileWriterService mockWriter = mock(FileWriterService.class);

        when(mockTraverser.traverse(anyList())).thenReturn("Mocked content");

        MarkdownWebsiteWriter writer = new MarkdownWebsiteWriter(mockWriter, mockTraverser);
        boolean result = writer.writeWebsites(List.of(new Website("http://test.com")), "out.md", "path/");

        assertTrue(result);
        verify(mockWriter).writeToFile("Mocked content", "out.md", "path/");
    }

    @Test
    void writeWebsites_ShouldReturnFalse_WhenExceptionOccurs() {
        WebsiteTraverser mockTraverser = mock(WebsiteTraverser.class);
        FileWriterService mockWriter = mock(FileWriterService.class);

        when(mockTraverser.traverse(anyList())).thenThrow(new RuntimeException("Fail"));

        MarkdownWebsiteWriter writer = new MarkdownWebsiteWriter(mockWriter, mockTraverser);
        boolean result = writer.writeWebsites(List.of(new Website("http://test.com")), "out.md", "path/");

        assertFalse(result);
    }
}
