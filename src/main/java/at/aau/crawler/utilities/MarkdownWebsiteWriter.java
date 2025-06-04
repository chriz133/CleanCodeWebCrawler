package at.aau.crawler.utilities;


import at.aau.crawler.interfaces.WebsiteWriter;
import at.aau.crawler.model.Heading;
import at.aau.crawler.model.Website;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A concrete implementation of {@link WebsiteWriter} that writes website crawl data
 * into a Markdown (.md) formatted file.
 * <p>
 * Each {@link Website} is written using its {@code printDetails()} output.
 * </p>
 */
public class MarkdownWebsiteWriter implements WebsiteWriter {
    private BufferedWriter bw;
    List<String> visitedUrls;

    @Override
    public boolean writeWebsites(List<Website> websites, String filename, String path) {
        try {
            Path directory = Paths.get(path);

            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }

            Path filePath = directory.resolve(filename);

            bw = Files.newBufferedWriter(filePath);
            visitedUrls = new ArrayList<>();
            test(websites.get(0), websites);
            for (Website website : websites) {
//                bw.write(website.printDetails());
            }
            bw.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

//    void test(Website website, List<Website> websites) {
//        StringBuilder sb = new StringBuilder();
//        visitedUrls.add(website.getOwnUrl());
//
//        sb.append("Ownurl: ").append(website.getOwnUrl()).append("\n");
//        if (website.getParentUrl() != null){
//            sb.append("Parenturl: ").append(website.getParentUrl()).append("\n");
//        }
//        sb.append("Depth: ").append(website.getDepth()).append("\n\n");
//
//        if (website.isBroken()){
//            sb.append("Broken Website").append("\n\n\n");
//            try {
//                bw.write(sb.toString());
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            return;
//        }
//
//        for (Heading heading : website.getHeadings()){
//            sb.append("<").append(heading.getType()).append(">");
//            sb.append(heading.getValue());
//            sb.append("</").append(heading.getType()).append(">\n");
//        }
//
//        sb.append("\n");
//        for (String link : website.getLinks()) {
//            sb.append("Link -->").append(link).append("\n");
//           List<String> ownUrls = websites.stream().map(Website::getOwnUrl).collect(Collectors.toList());
//            System.out.println(website.getOwnUrl() + " - " + link + " - " + (!trimUrl(link).equals(website.getOwnUrl()) && ownUrls.contains(trimUrl(link)) ? "true" : "false"));
//           if (!trimUrl(link).equals(website.getOwnUrl()) &&
//               ownUrls.contains(trimUrl(link)) &&
//               !visitedUrls.contains(trimUrl(link))) {
//               Website linkedWebsite = websites.stream().filter(w -> w.getOwnUrl().equals(trimUrl(link))).findFirst().orElse(null);
//               if (linkedWebsite != null) {
//                   test(linkedWebsite, websites);
//               }
//           }
//           sb.append("\n");
//        }
//        try {
//            bw.write(sb.toString());
//            sb = new StringBuilder();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    void test(Website website, List<Website> websites) {
        StringBuilder sb = new StringBuilder();

        sb.append(getTabs(website.getDepth())).append("Ownurl: ").append(website.getOwnUrl()).append("\n");
        if (website.getParentUrl() != null) {
            sb.append(getTabs(website.getDepth())).append("Parenturl: ").append(website.getParentUrl()).append("\n");
        }
        sb.append(getTabs(website.getDepth())).append("Depth: ").append(website.getDepth()).append("\n\n");

        if (website.isBroken()) {
            sb.append(getTabs(website.getDepth())).append("Broken Website").append("\n\n\n");
            try {
                bw.write(sb.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            visitedUrls.add(trimUrl(website.getOwnUrl())); // ✅ mark as visited
            return;
        }

        for (Heading heading : website.getHeadings()) {
            sb.append(getTabs(website.getDepth())).append("<").append(heading.getType()).append(">");
            sb.append(getTabs(website.getDepth())).append(heading.getValue());
            sb.append(getTabs(website.getDepth())).append("</").append(heading.getType()).append(">\n");
        }

        sb.append("\n");

        List<String> ownUrls = websites.stream()
                .map(w -> trimUrl(w.getOwnUrl()))
                .collect(Collectors.toList());

        for (String link : website.getLinks()) {
            String trimmedLink = trimUrl(link);
            sb.append(getTabs(website.getDepth())).append("Link --> ").append(trimmedLink).append("\n");

            try {
                bw.write(sb.toString());
                sb = new StringBuilder();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (!trimmedLink.equals(trimUrl(website.getOwnUrl())) &&
                    ownUrls.contains(trimmedLink) &&
                    !visitedUrls.contains(trimmedLink)) {

                Website linkedWebsite = websites.stream()
                        .filter(w -> trimUrl(w.getOwnUrl()).equals(trimmedLink))
                        .findFirst()
                        .orElse(null);

                if (linkedWebsite != null && linkedWebsite.getDepth() > website.getDepth()) {
                    visitedUrls.add(trimmedLink);
                    test(linkedWebsite, websites);
                }
            }

            sb.append("\n");
        }

        try {
            bw.write(sb.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        visitedUrls.add(trimUrl(website.getOwnUrl()));
    }

    private String getTabs(int depth) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < depth; i++) {
            sb.append("\t");
        }
        return sb.toString();
    }

    private String trimUrl(String url) {
        if (url == null || url.isEmpty()) return url;
        if (url.endsWith("/") || url.endsWith("#")) {
            return url.substring(0, url.length() - 1);
        }
        if (url.contains("#")) {
            return url.substring(0, url.indexOf("#"));
        }
        return url;
    }
}
