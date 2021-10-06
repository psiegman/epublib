package nl.siegmann.epublib.domain;

import java.io.IOException;

public class OpfResource extends Resource {

    public static final String DEFAULT_VERSION = "2.0";

    private String version;

    private String prefix;

    public OpfResource(Resource resource) throws IOException {
        super(
                resource.getId(),
                resource.getData(),
                resource.getHref(),
                resource.getMediaType(),
                resource.getInputEncoding()
        );
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
