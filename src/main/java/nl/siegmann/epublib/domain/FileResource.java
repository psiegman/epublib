package nl.siegmann.epublib.domain;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import nl.siegmann.epublib.service.MediatypeService;

/**
 * Wraps the Resource interface around a file on disk.
 * 
 * @author paul
 *
 */
public class FileResource extends ResourceBase implements Resource {

	private File file;
	
	public FileResource(File file) {
		super(null, file.getName(), MediatypeService.determineMediaType(file.getName()));
		this.file = file;
	}
	
	public FileResource(String id, File file, String href, MediaType mediaType) {
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
