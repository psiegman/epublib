package nl.siegmann.epublib.epub;

import java.io.OutputStream;

import nl.siegmann.epublib.domain.Resource;

public interface HtmlProcessor {
	
	void processHtmlResource(Resource resource, OutputStream out);
}
