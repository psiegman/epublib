package nl.siegmann.epublib;

import java.io.IOException;
import java.io.OutputStream;

public interface Resource {
	
	public String getHref();
	public String getMediaType();
	public void writeResource(OutputStream resultStream, EpubWriter epubWriter) throws IOException;
}
