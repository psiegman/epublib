package nl.siegmann.epublib.domain;

import java.util.Objects;

public class Title {

    public static final Title EMPTY = new Title("");

    String value;
    String type;

    public Title(String value) {
        this.value = value;
    }

    public Title(String value, String type) {
        this.value = value;
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Title title = (Title) o;
        return Objects.equals(value, title.value) && Objects.equals(type, title.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, type);
    }
}
