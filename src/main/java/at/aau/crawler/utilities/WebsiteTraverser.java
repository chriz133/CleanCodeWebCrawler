package at.aau.crawler.utilities;

import at.aau.crawler.model.Website;

import java.util.*;
import java.util.stream.Collectors;

public class WebsiteTraverser {
    private final MarkdownFormatter formatter;
    private final Set<String> visited = new HashSet<>();

    public WebsiteTraverser(MarkdownFormatter formatter) {
        this.formatter = formatter;
    }

    public String traverse(List<Website> websites) {
        StringBuilder result = new StringBuilder();
        if (websites.isEmpty()) return "";

        traverseRecursive(websites.get(0), websites, result);
        return result.toString();
    }

    private void traverseRecursive(Website website, List<Website> allWebsites, StringBuilder result) {
        if (!shouldVisit(website)) return;

        markAsVisited(website);
        StringBuilder currentOutput = new StringBuilder();
        String tabs = formatter.getTabs(website.getDepth());

        appendWebsiteContent(website, tabs, currentOutput);

        if (website.isBroken()) {
            handleBrokenWebsite(website, tabs, currentOutput, result);
            return;
        }

        currentOutput.append(formatter.formatHeadings(website, tabs));
        processLinks(website, allWebsites, currentOutput, tabs);

        result.append(currentOutput).append("\n");
    }


    private void processLinks(Website website, List<Website> allWebsites, StringBuilder output, String tabs) {
        String currentTrimmedUrl = UrlUtils.trimUrl(website.getOwnUrl());
        List<String> knownUrls = collectKnownUrls(allWebsites);

        for (String link : website.getLinks()) {
            String trimmedLink = UrlUtils.trimUrl(link).toLowerCase();
            output.append(formatter.formatLinkLine(trimmedLink, tabs));

            if (shouldTraverseLink(trimmedLink, knownUrls, currentTrimmedUrl)) {
                Website linkedWebsite = findLinkedWebsite(trimmedLink, allWebsites);

                if (linkedWebsite != null && linkedWebsite.getDepth() > website.getDepth()) {
                    setParentIfAbsent(linkedWebsite, website.getOwnUrl());
                    traverseRecursive(linkedWebsite, allWebsites, output);
                }
            }
        }
    }


    private boolean shouldVisit(Website website) {
        return !visited.contains(UrlUtils.trimUrl(website.getOwnUrl()));
    }

    private void markAsVisited(Website website) {
        visited.add(UrlUtils.trimUrl(website.getOwnUrl()));
    }

    private void appendWebsiteContent(Website website, String tabs, StringBuilder out) {
        out.append(formatter.formatMetadata(website, tabs));
    }

    private void handleBrokenWebsite(Website website, String tabs, StringBuilder out, StringBuilder result) {
        out.append(formatter.formatBrokenNotice(website, tabs));
        result.append(out).append("\n");
    }

    private List<String> collectKnownUrls(List<Website> allWebsites) {
        return allWebsites.stream()
                .map(w -> UrlUtils.trimUrl(w.getOwnUrl()))
                .collect(Collectors.toList());
    }

    private boolean shouldTraverseLink(String link, List<String> knownUrls, String currentUrl) {
        return !visited.contains(link)
                && knownUrls.contains(link)
                && !link.equals(currentUrl);
    }

    private Website findLinkedWebsite(String trimmedLink, List<Website> allWebsites) {
        return allWebsites.stream()
                .filter(w -> UrlUtils.trimUrl(w.getOwnUrl()).equals(trimmedLink))
                .findFirst()
                .orElse(null);
    }

    private void setParentIfAbsent(Website linkedWebsite, String parentUrl) {
        if (linkedWebsite.getParentUrl() == null) {
            linkedWebsite.setParentUrl(parentUrl);
        }
    }

}
