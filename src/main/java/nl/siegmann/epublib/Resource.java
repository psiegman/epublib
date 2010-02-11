package nl.siegmann.epublib;

import java.io.IOException;
import java.io.InputStream;

public interface Resource {
	String getId();
	String getInputEncoding();
	String getHref();
	String getMediaType();
	InputStream getInputStream() throws IOException;
}
