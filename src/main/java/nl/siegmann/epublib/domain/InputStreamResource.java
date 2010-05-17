package nl.siegmann.epublib.domain;

import java.io.IOException;
import java.io.InputStream;

import nl.siegmann.epublib.service.MediatypeService;

import org.apache.commons.io.IOUtils;

/**
 * Wraps the Resource interface around a file on disk.
 * 
 * @author paul
 *
 */
public class InputStreamResource extends ByteArrayResource implements Resource {

	public InputStreamResource(InputStream in, String href) throws IOException {
		super(href, IOUtils.toByteArray(in));
		setMediaType(MediatypeService.determineMediaType(href));
	}
}
