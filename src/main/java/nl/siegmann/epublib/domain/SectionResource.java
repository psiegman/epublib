package nl.siegmann.epublib.domain;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import nl.siegmann.epublib.service.MediatypeService;

/**
 * A Section resource that is used to generate new Sections from scratch.
 * 
 * @author paul
 *
 */
public class SectionResource extends ResourceBase {

	private String title;
	
	public SectionResource(String id, String title, String href) {
		super(id, href, MediatypeService.XHTML);
		this.title = title;
	}

	
	@Override
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(getContent().getBytes(getInputEncoding()));
	}

	
	private String getContent() {
		return "<html><head><title>" + title + "</title></head><body><h1>" + title + "</h1></body></html>";
	}


	public String getSectionName() {
		return title;
	}


	public void setSectionName(String sectionName) {
		this.title = sectionName;
	}
}
