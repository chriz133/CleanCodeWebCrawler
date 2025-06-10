package at.aau.crawler.utilities;

public class TextUtils {
    private TextUtils() {}

    public static String getTabs(int depth) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < depth; i++) {
            sb.append("\t");
        }
        return sb.toString();
    }
}
