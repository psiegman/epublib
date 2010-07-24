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
public class SectionResource extends ResourceBase implements Resource {

	private String sectionName;
	
	public SectionResource(String id, String sectionName, String href) {
		super(id, href, MediatypeService.XHTML);
		this.sectionName = sectionName;
	}

	
	@Override
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(getContent().getBytes(getInputEncoding()));
	}

	
	private String getContent() {
		return "<html><head><title>" + sectionName + "</title></head><body><h1>" + sectionName + "</h1></body></html>";
	}


	public String getSectionName() {
		return sectionName;
	}


	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}
}
