package at.aau.crawler.services;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileWriterService {

    public boolean writeToFile(String content, String filename, String path) {
        Path directory = Paths.get(path);
        try {
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
            Path filePath = directory.resolve(filename);
            try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
                writer.write(content);
            }
            return true;
        }catch (IOException e) {
            return false;
        }



    }
}
