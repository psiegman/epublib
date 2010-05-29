package nl.siegmann.epublib.domain;

import java.io.IOException;
import java.io.InputStream;

/**
 * Represents a resource that is part of the epub.
 * A resource can be a html file, image, xml, etc.
 * 
 * @author paul
 *
 */
public interface Resource {
	Resource NULL_RESOURCE = new Resource() {

		@Override
		public String getHref() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getId() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getInputEncoding() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public InputStream getInputStream() throws IOException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public MediaType getMediaType() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setHref(String href) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setId(String id) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setInputEncoding(String encoding) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setMediaType(MediaType mediaType) {
			// TODO Auto-generated method stub
			
		}
		
	};
	void setId(String id);
	String getId();
	String getInputEncoding();
	String getHref();
	void setInputEncoding(String encoding);
	void setHref(String href);
	MediaType getMediaType();
	void setMediaType(MediaType mediaType);
	InputStream getInputStream() throws IOException;
}
