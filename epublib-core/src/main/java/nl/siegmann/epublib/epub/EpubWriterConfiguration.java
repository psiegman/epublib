package nl.siegmann.epublib.epub;

/**
 * Allows for the configuration of an {@link EpubWriter}.
 */
public class EpubWriterConfiguration {
    public static final String DEFAULT_CONTENT_DIRECTORY_NAME = "OEBPS";

    private String contentDirectoryName = DEFAULT_CONTENT_DIRECTORY_NAME;

    /**
     * Creates a default configuration.
     */
    public EpubWriterConfiguration() {
    }

    /**
     * Builder-style method to change the directory name.
     *
     * @param contentDirectoryName New directory name.
     * @return EpubWriterConfiguration
     */
    public EpubWriterConfiguration withContentDirectoryName(String contentDirectoryName) {
        this.contentDirectoryName = contentDirectoryName;
        return this;
    }

    /**
     * Returns the directory name for the content directory.
     *
     * @return The directory name for the content directory.
     */
    public String getContentDirectoryName() {
        return contentDirectoryName;
    }

    /**
     * Sets the directory name for the content directory.
     *
     * @param contentDirectoryName The directory name for the content directory.
     */
    public void setContentDirectoryName(String contentDirectoryName) {
        this.contentDirectoryName = contentDirectoryName;
    }
}
