package nl.siegmann.epublib.domain;

import java.io.IOException;
import java.io.InputStream;

/**
 * Represents a resource that is part of the epub.
 * A resource can be a html file, image, xml, etc.
 * 
 * @author paul
 *
 */
public interface Resource {
	void setId(String id);
	String getId();
	String getInputEncoding();
	String getHref();
	void setHref(String href);
	MediaType getMediaType();
	void setMediaType(MediaType mediaType);
	InputStream getInputStream() throws IOException;
}
