package nl.siegmann.epublib.domain;

import java.io.IOException;
import java.io.InputStream;

/**
 * A Resource that is useful during parsing: It will hold the href to the later to be retrieved real resource.
 * 
 * @author paul
 *
 */
public class PlaceholderResource extends ResourceBase {

	public PlaceholderResource(String href) {
		super(href);
	}

	@Override
	public InputStream getInputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
