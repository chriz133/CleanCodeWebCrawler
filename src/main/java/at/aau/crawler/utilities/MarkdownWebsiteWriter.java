package at.aau.crawler.utilities;

import at.aau.crawler.interfaces.WebsiteWriter;
import at.aau.crawler.model.Website;
import at.aau.crawler.services.FileWriterService;

import java.util.List;

public class MarkdownWebsiteWriter implements WebsiteWriter {
    private final FileWriterService writerService;
    private final WebsiteTraverser traverser;

    public MarkdownWebsiteWriter() {
        this.writerService = new FileWriterService();
        this.traverser = new WebsiteTraverser(new MarkdownFormatter());
    }

    @Override
    public boolean writeWebsites(List<Website> websites, String filename, String path) {
        try {
            String content = traverser.traverse(websites);
            writerService.writeToFile(content, filename, path);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
