package nl.siegmann.epublib;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;


public class SectionResource implements Resource {

	private String id;
	private String inputEncoding = "UTF-8";
	private String sectionName;
	private String href;
	
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
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(getContent().getBytes(inputEncoding));
	}

	@Override
	public String getMediaType() {
		return Constants.MediaTypes.xhtml;
	}
	
	private String getContent() {
		return "<html><head><title>" + sectionName + "</title></head><body><h1>" + sectionName + "</h1></body></html>";
	}
}
