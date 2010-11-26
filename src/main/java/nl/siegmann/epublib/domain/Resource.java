package nl.siegmann.epublib.domain;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.service.MediatypeService;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.google.gdata.util.io.base.UnicodeReader;

/**
 * Represents a resource that is part of the epub.
 * A resource can be a html file, image, xml, etc.
 * 
 * @author paul
 *
 */
public class Resource {
	
	private String id;
	private String title;
	private String href;
	private MediaType mediaType;
	private Charset inputEncoding = Constants.ENCODING;
	private byte[] data;
	
	public Resource(String href) {
		this(null, new byte[0], href, MediatypeService.determineMediaType(href));
	}
		
	public Resource(byte[] data, MediaType mediaType) {
		this(null, data, null, mediaType);
	}
	
	public Resource(byte[] data, String href) {
		this(null, data, href, MediatypeService.determineMediaType(href), Constants.ENCODING);
	}
	
	public Resource(InputStream in, String href) throws IOException {
		this(null, IOUtils.toByteArray(in), href, MediatypeService.determineMediaType(href));
	}
	
	public Resource(Reader in, String href) throws IOException {
		this(null, IOUtils.toByteArray(in), href, MediatypeService.determineMediaType(href), Constants.ENCODING);
	}
	
	public Resource(String id, byte[] data, String href, MediaType mediaType) {
		this(id, data, href, mediaType, Constants.ENCODING);
	}
	
	public Resource(String id, byte[] data, String href, MediaType mediaType, Charset inputEncoding) {
		this.id = id;
		this.href = href;
		this.mediaType = mediaType;
		this.inputEncoding = inputEncoding;
		this.data = data;
	}
	
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(data);
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getTitle() {
		return title;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * The resources Id.
	 * 
	 * Must be both unique within all the resources of this book and a valid identifier.
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * The location of the resource within the contents folder of the epub file.
	 * 
	 * Example:<br/>
	 * images/cover.jpg<br/>
	 * content/chapter1.xhtml<br/>
	 * 
	 * @return
	 */
	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	/**
	 * The encoding of the resource.
	 * Is allowed to be null for non-text resources like images.
	 * 
	 * @return
	 */
	public Charset getInputEncoding() {
		return inputEncoding;
	}
	
	public void setInputEncoding(Charset encoding) {
		this.inputEncoding = encoding;
	}
	
	/**
	 * Gets the contents of the Resource as Reader.
	 * 
	 * Does all sorts of smart things (courtesy of commons io XmlStreamReader) to handle encodings, byte order markers, etc.
	 * 
	 * @see http://commons.apache.org/io/api-release/org/apache/commons/io/input/XmlStreamReader.html
	 * 
	 * @param resource
	 * @return
	 * @throws IOException
	 */
	public Reader getReader() throws IOException {
		return new UnicodeReader(new ByteArrayInputStream(data), inputEncoding.name());
	}
	
	public int hashCode() {
		return href.hashCode();
	}
	
	public boolean equals(Object resourceObject) {
		if (! (resourceObject instanceof Resource)) {
			return false;
		}
		return href.equals(((Resource) resourceObject).getHref());
	}
	
	/**
	 * This resource's mediaType.
	 * 
	 * @return
	 */
	public MediaType getMediaType() {
		return mediaType;
	}
	
	public void setMediaType(MediaType mediaType) {
		this.mediaType = mediaType;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String toString() {
		return new ToStringBuilder(this).
			append("id", id).
			append("title", title).
			append("encoding", inputEncoding).
			append("mediaType", mediaType).
			append("href", href).
			append("size", data == null ? 0 : data.length).
		toString();
	}
}
