package nl.siegmann.epublib;

import java.io.OutputStream;

public interface HtmlProcessor {
	
	void processHtmlResource(Resource resource, OutputStream out);
}
