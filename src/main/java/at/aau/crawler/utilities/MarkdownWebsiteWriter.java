package at.aau.crawler.utilities;


import at.aau.crawler.interfaces.WebsiteWriter;
import at.aau.crawler.model.Website;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * A concrete implementation of {@link WebsiteWriter} that writes website crawl data
 * into a Markdown (.md) formatted file.
 * <p>
 * Each {@link Website} is written using its {@code printDetails()} output.
 * </p>
 */
public class MarkdownWebsiteWriter implements WebsiteWriter {


    @Override
    public boolean writeWebsites(List<Website> websites, String filename, String path) {
        try {
            Path directory = Paths.get(path);

            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }

            Path filePath = directory.resolve(filename);

            BufferedWriter bw = Files.newBufferedWriter(filePath);
            for (Website website : websites) {
                bw.write(website.printDetails());
            }
            bw.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
