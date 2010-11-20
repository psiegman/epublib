package nl.siegmann.epublib.domain;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Represents a resource that is part of the epub.
 * A resource can be a html file, image, xml, etc.
 * 
 * @author paul
 *
 */
public interface Resource {
	
	void setId(String id);
	
	/**
	 * The resources Id.
	 * 
	 * Must be both unique within all the resources of this book and a valid identifier.
	 * @return
	 */
	String getId();

	/**
	 * The location of the resource within the contents folder of the epub file.
	 * 
	 * Example:<br/>
	 * images/cover.jpg<br/>
	 * content/chapter1.xhtml<br/>
	 * 
	 * @return
	 */
	String getHref();

	void setHref(String href);

	/**
	 * The encoding of the resource.
	 * Is allowed to be null for non-text resources like images.
	 * 
	 * @return
	 */
	Charset getInputEncoding();
	
	void setInputEncoding(Charset encoding);
	
	/**
	 * This resource's mediaType.
	 * 
	 * @return
	 */
	MediaType getMediaType();
	
	void setMediaType(MediaType mediaType);
	
	/**
	 * The contents of this resource.
	 * 
	 * @return
	 * @throws IOException
	 */
	InputStream getInputStream() throws IOException;
}
