package nl.siegmann.epublib.domain;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * A resource that stores it's data as a byte[]
 * 
 * @author paul
 *
 */
public class ByteArrayResource extends ResourceBase {

	private byte[] data;
	
	public ByteArrayResource(String href, byte[] data) {
		super(href);
		this.data = data;
	}

	public ByteArrayResource(byte[] data, MediaType mediaType) {
		this(null, data, null, mediaType);
	}
	
	
	public ByteArrayResource(String id, byte[] data, String href, MediaType mediaType) {
		this(id, data, href, mediaType, null);
	}

	public ByteArrayResource(String id, byte[] data, String href, MediaType mediaType, Charset inputEncoding) {
		super(id, href, mediaType, inputEncoding);
		this.data = data;
	}
	
	@Override
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(data);
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
}
