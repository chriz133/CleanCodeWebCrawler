package aau.crawler.model;

public class Heading {
    String type;
    String value;

    public Heading(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Heading{" +
                "type='" + type + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
