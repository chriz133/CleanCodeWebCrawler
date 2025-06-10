package at.aau.crawler.writer;

import at.aau.crawler.interfaces.WebsiteWriter;
import at.aau.crawler.model.Website;
import at.aau.crawler.services.FileWriterService;

import java.util.List;

public class MarkdownWebsiteWriter implements WebsiteWriter {
    private final FileWriterService writerService;
    private final WebsiteTraverser traverser;


    public MarkdownWebsiteWriter(FileWriterService writerService, WebsiteTraverser traverser) {
        this.writerService = writerService;
        this.traverser = traverser;
    }

    public MarkdownWebsiteWriter() {
        this.writerService = new FileWriterService();
        this.traverser = new WebsiteTraverser(new MarkdownFormatter());
    }

    @Override
    public boolean writeWebsites(List<Website> websites, String filename, String path) {
        String content = traverser.traverse(websites);
        return writerService.writeToFile(content, filename, path);
    }
}
