package nl.siegmann.epublib.domain;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.service.MediatypeService;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * Utility base class for several types of resources.
 * 
 * @author paul
 *
 */
public abstract class ResourceBase implements Resource {
	
	private String id;
	private String title;
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

	public String getTitle() {
		if (title != null) {
			return title;
		}
		if (MediatypeService.XHTML == mediaType) {
			try {
				String content = IOUtils.toString(getInputStream(), getInputEncoding().name());
				String lowerContent = content.toLowerCase();
				int titleStart = lowerContent.indexOf("<title>");
				if (titleStart >= 0) {
					int titleEnd = lowerContent.indexOf("<", titleStart + "<title>".length());
					if (titleEnd < 0) {
						titleEnd = lowerContent.length();
					}
					title = content.substring(titleStart + "<title>".length(), titleEnd);
					title = StringEscapeUtils.unescapeHtml(title);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (title == null) {
			title = href;
		}
		return title;
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
	
	public String toString() {
		return String.valueOf(id) + " (" + String.valueOf(mediaType) + "): '" + href + "'";
	}
	
	public int hashCode() {
		if (href == null) {
			return 0;
		}
		return href.hashCode();
	}
	
	public boolean equals(Object resourceObject) {
		if (! (resourceObject instanceof Resource)) {
			return false;
		}
		if (href == null) {
			return false;
		}
		return href.equals(((Resource) resourceObject).getHref());
	}
}
