package nl.siegmann.epublib.domain;

public class Scheme {

    public final static Scheme UUID = new Scheme("UUID");

    public final static Scheme ISBN = new Scheme("ISBN");

    private String name;
    private String value;

    public Scheme(String name) {
        this.name = name;
    }

    public Scheme(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
