package nl.siegmann.epublib;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FileResource implements Resource {
	private File file;
	private String href;
	private String mediaType = Constants.MediaTypes.xhtml;
	
	public FileResource(File file, String href, String mediaType) {
		super();
		this.file = file;
		this.href = href;
		this.mediaType = mediaType;
	}

	public OutputStream getOutputStream() throws IOException {
		return new FileOutputStream(file);
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
}
