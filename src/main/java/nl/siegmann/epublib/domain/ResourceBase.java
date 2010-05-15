package nl.siegmann.epublib.domain;

import java.io.IOException;
import java.io.InputStream;

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
	private String inputEncoding;

	public ResourceBase(String href) {
		this(null, href, MediatypeService.determineMediaType(href));
	}
	
	public ResourceBase(String id, String href, MediaType mediaType) {
		this(id, href, mediaType, null);
	}
	
	
	public ResourceBase(String id, String href, MediaType mediaType, String inputEncoding) {
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

	public String getInputEncoding() {
		return inputEncoding;
	}

	public void setInputEncoding(String inputEncoding) {
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
