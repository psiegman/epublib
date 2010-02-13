package nl.siegmann.epublib;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileResource extends ResourceBase implements Resource {

	private File file;
	
	public FileResource(String id, File file, String href, String mediaType) {
		super(id, href, mediaType);
		this.file = file;
	}
	
	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new FileInputStream(file);
	}
}
