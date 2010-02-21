package nl.siegmann.epublib.domain;

import java.io.IOException;
import java.io.InputStream;

public interface Resource {
	void setId(String id);
	String getId();
	String getInputEncoding();
	String getHref();
	void setHref(String href);
	String getMediaType();
	InputStream getInputStream() throws IOException;
}
