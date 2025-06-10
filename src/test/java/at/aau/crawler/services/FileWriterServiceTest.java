package at.aau.crawler.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

class FileWriterServiceTest {

    private final FileWriterService service = new FileWriterService();
    private final String testDir = "temp_test_output";
    private final String testFile = "testfile.md";
    private final Path filePath = Paths.get(testDir).resolve(testFile);

    @AfterEach
    void cleanup() throws IOException {
        Files.deleteIfExists(filePath);

        Path dirPath = Paths.get(testDir);
        if (Files.exists(dirPath) && Files.isDirectory(dirPath)) {
            Files.deleteIfExists(dirPath);
        }
    }

    @Test
    void writeToFile_ShouldCreateFileWithContent() throws IOException {
        String content = "This is a test string.";
        service.writeToFile(content, testFile, testDir);

        assertTrue(Files.exists(filePath));
        String actualContent = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
        assertEquals(content, actualContent);
    }

    @Test
    void writeToFile_ShouldThrowException_WhenPathIsInvalid() {
        // Ungültiger Pfad, sollte IOException werfen
        String invalidPath = "///invalid///path///";
        assertThrows(IOException.class, () -> service.writeToFile("text", testFile, invalidPath));
    }

    @Test
    void writeToFile_ShouldOverwriteExistingFile() throws IOException {
        String firstContent = "First version";
        String secondContent = "Overwritten";

        service.writeToFile(firstContent, testFile, testDir);
        service.writeToFile(secondContent, testFile, testDir);

        String actual = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
        assertEquals(secondContent, actual);
    }
}
