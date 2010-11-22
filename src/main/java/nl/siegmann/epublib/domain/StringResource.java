package nl.siegmann.epublib.domain;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.service.MediatypeService;

public class StringResource extends ByteArrayResource {

	public StringResource(String content) {
		this(content, MediatypeService.XHTML);
	}

	public StringResource(String content, MediaType mediaType) {
		super(content.getBytes(Constants.ENCODING), mediaType);
	}

}
