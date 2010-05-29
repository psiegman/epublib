package nl.siegmann.epublib.domain;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.service.MediatypeService;

/**
 * A Section resource that is used to generate new Sections from scratch.
 * 
 * @author paul
 *
 */
public class SectionResource implements Resource {

	private String id;
	private String inputEncoding = Constants.ENCODING;
	private String sectionName;
	private String href;
	private MediaType mediaType = MediatypeService.XHTML;
	
	public SectionResource(String id, String sectionName, String href) {
		this.id = id;
		this.sectionName = sectionName;
		this.href = href;
	}

	public String getId() {
		return id;
	}
	
	@Override
	public String getHref() {
		return href;
	}

	@Override
	public String getInputEncoding() {
		return inputEncoding;
	}

	@Override
	public void setInputEncoding(String inputEncoding) {
		this.inputEncoding = inputEncoding;
	}
	
	@Override
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(getContent().getBytes(inputEncoding));
	}

	@Override
	public MediaType getMediaType() {
		return mediaType;
	}
	
	private String getContent() {
		return "<html><head><title>" + sectionName + "</title></head><body><h1>" + sectionName + "</h1></body></html>";
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public void setMediaType(MediaType mediaType) {
		this.mediaType = mediaType;
	}
}
