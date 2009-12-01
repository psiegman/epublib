package nl.siegmann.epublib;

import java.io.IOException;
import java.io.OutputStream;

public interface Resource {
	
	public String getHref();
	public String getMediaType();
	public OutputStream getOutputStream() throws IOException;
}
