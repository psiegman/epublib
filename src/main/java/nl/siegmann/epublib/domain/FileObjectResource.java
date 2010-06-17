package nl.siegmann.epublib.domain;

import java.io.IOException;
import java.io.InputStream;

import nl.siegmann.epublib.service.MediatypeService;

import org.apache.commons.vfs.FileObject;

/**
 * Wraps the Resource interface around a commons-vfs FileObject
 * 
 * @author paul
 *
 */
public class FileObjectResource extends ResourceBase implements Resource {

	private FileObject file;
	
	public FileObjectResource(FileObject file) {
		super(null, file.getName().getBaseName(), MediatypeService.determineMediaType(file.getName().getBaseName()));
		this.file = file;
	}
	
	public FileObjectResource(String id, FileObject file, String href, MediaType mediaType) {
		super(id, href, mediaType);
		this.file = file;
	}
	
	@Override
	public InputStream getInputStream() throws IOException {
		return file.getURL().openStream();
	}
}
