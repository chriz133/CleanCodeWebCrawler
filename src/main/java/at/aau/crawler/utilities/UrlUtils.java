package at.aau.crawler.utilities;

public class UrlUtils {
    private UrlUtils() {}

    public static String trimUrl(String url) {
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
