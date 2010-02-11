package nl.siegmann.epublib;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileResource implements Resource {
	private String id;
	private File file;
	private String href;
	private String mediaType = Constants.MediaTypes.xhtml;
	private String inputEncoding;
	
	public FileResource(String id, File file, String href, String mediaType) {
		super();
		this.id = id;
		this.file = file;
		this.href = href;
		this.mediaType = mediaType;
	}

	public String getId() {
		return id;
	}
	
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
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
	public InputStream getInputStream() throws IOException {
		return new FileInputStream(file);
	}

	public String getInputEncoding() {
		return inputEncoding;
	}

	public void setInputEncoding(String inputEncoding) {
		this.inputEncoding = inputEncoding;
	}
}
