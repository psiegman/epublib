package nl.siegmann.epublib.domain;

import java.io.IOException;
import java.io.InputStream;

public abstract class ResourceBase implements Resource {
	
	private String id;
	private String href;
	private String mediaType;
	private String inputEncoding;

	public ResourceBase(String id, String href, String mediaType) {
		this(id, href, mediaType, null);
	}
	
	
	public ResourceBase(String id, String href, String mediaType, String inputEncoding) {
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
	
	public String getMediaType() {
		return mediaType;
	}
	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

	@Override
	public abstract InputStream getInputStream() throws IOException;

	public String getInputEncoding() {
		return inputEncoding;
	}

	public void setInputEncoding(String inputEncoding) {
		this.inputEncoding = inputEncoding;
	}
}
