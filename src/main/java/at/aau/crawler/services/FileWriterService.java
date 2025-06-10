package at.aau.crawler.services;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileWriterService {

    public boolean writeToFile(String content, String filename, String path) {
        try {
            Path directory = Paths.get(path);

            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
            Path filePath = directory.resolve(filename);

            BufferedWriter writer = Files.newBufferedWriter(filePath);
            writer.write(content);
            writer.close();

            return true;
        }catch (Exception e) {
            return false;
        }
    }
}
