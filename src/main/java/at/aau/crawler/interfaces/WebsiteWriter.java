package at.aau.crawler.interfaces;


import at.aau.crawler.model.Website;

import java.util.List;

public interface WebsiteWriter {
    /**
     * Writes the provided list of websites to a markdown file with the specified filename and directory path.
     *
     * @param websites the list of websites to write
     * @param filename the name of the file to create (should end with .md)
     * @param path     the directory in which to save the file
     * @return true if the writing was successful; false if an IOException occurred
     */
    boolean writeWebsites(List<Website> websites, String filename, String path);
}
