package at.aau.crawler.interfaces;

import java.util.List;

public interface WebDocument {
    String getTextFromHeading(String tag);
    List<String> getLinks();
}
