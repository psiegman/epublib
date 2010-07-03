package nl.siegmann.epublib.domain;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.service.MediatypeService;

/**
 * Utility base class for several types of resources.
 * 
 * @author paul
 *
 */
public abstract class ResourceBase implements Resource {
	
	private String id;
	private String href;
	private MediaType mediaType;
	private Charset inputEncoding = Constants.ENCODING;

	public ResourceBase(String href) {
		this(null, href, MediatypeService.determineMediaType(href));
	}
	
	public ResourceBase(String id, String href, MediaType mediaType) {
		this(id, href, mediaType, Constants.ENCODING);
	}
	
	
	public ResourceBase(String id, String href, MediaType mediaType, Charset inputEncoding) {
		super();
		this.id = id;
		this.href = href;
		this.mediaType = mediaType;
		this.inputEncoding = inputEncoding;
	}

	public String getId() {
		return id;
	}
	
	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}
	
	@Override
	public abstract InputStream getInputStream() throws IOException;

	public Charset getInputEncoding() {
		return inputEncoding;
	}

	public void setInputEncoding(Charset inputEncoding) {
		this.inputEncoding = inputEncoding;
	}


	public void setId(String id) {
		this.id = id;
	}


	public MediaType getMediaType() {
		return mediaType;
	}


	public void setMediaType(MediaType mediaType) {
		this.mediaType = mediaType;
	}
}
