package nl.siegmann.epublib.service;

import nl.siegmann.epublib.domain.MediaType;

import org.apache.commons.lang.StringUtils;


public class MediatypeService {

	public static MediaType XHTML = new MediaType("application/xhtml+xml", ".xhtml", new String[] {".htm", ".html", ".xhtml"});
	public static MediaType EPUB = new MediaType("application/epub+zip", ".epub", new String[] {".epub"});
	public static MediaType JPG = new MediaType("image/jpg", ".jpg", new String[] {".jpg", ".jpeg"});
	public static MediaType PNG = new MediaType("image/png", ".png", new String[] {".png"});
	public static MediaType GIF = new MediaType("image/gif", ".gif", new String[] {".gif"});
	public static MediaType CSS = new MediaType("text/css", ".css", new String[] {".css"});
	public static MediaType SVG = new MediaType("image/svg+xml", ".svg", new String[] {".svg"});
	public static MediaType TTF = new MediaType("application/x-truetype-font", ".ttf", new String[] {".ttf"});
	
	public static MediaType[] mediatypes = new MediaType[] {
		XHTML, EPUB, JPG, PNG, GIF, CSS, SVG, TTF
	};
	
	
	/**
	 * Gets the MediaType based on the file extension.
	 * Null of no matching extension found.
	 * 
	 * @param filename
	 * @return
	 */
	public static MediaType determineMediaType(String filename) {
		for(int i = 0; i < mediatypes.length; i++) {
			MediaType mediatype = mediatypes[i];
			for(String extension: mediatype.getExtensions()) {
				if(StringUtils.endsWithIgnoreCase(filename, extension)) {
					return mediatype;
				}
			}
		}
		return null;
	}

}
