package nl.siegmann.epublib.domain;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ByteArrayResource extends ResourceBase implements Resource {

	private byte[] data;
	
	public ByteArrayResource(String id, byte[] data, String href, MediaType mediaType) {
		this(id, data, href, mediaType, null);
	}

	public ByteArrayResource(String id, byte[] data, String href, MediaType mediaType, String inputEncoding) {
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
