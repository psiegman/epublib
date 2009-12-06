package nl.siegmann.epublib;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.htmlcleaner.HtmlCleaner;

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

	public void writeResource(OutputStream resultStream, EpubWriter epubWriter) throws IOException {
		InputStream in = new FileInputStream(file);
		if(mediaType.equals(Constants.MediaTypes.xhtml)) {
			epubWriter.cleanupHtml(in, resultStream);
		} else {
			IOUtils.copy(in, resultStream);
			in.close();
		}
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
