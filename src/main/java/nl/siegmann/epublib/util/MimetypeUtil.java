package nl.siegmann.epublib.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nl.siegmann.epublib.Constants;

import org.apache.commons.lang.StringUtils;

public class MimetypeUtil {

	public static final Set<String> IMAGE_MEDIA_TYPES = new HashSet<String>(Arrays.asList(new String[] {
			Constants.MediaTypes.JPG,
			Constants.MediaTypes.PNG,
			Constants.MediaTypes.GIF
	}));

	public static final Map<String, String> MIMETYPE_DEFAULT_EXTENSIONS = new HashMap<String, String>() {{
		put(Constants.MediaTypes.JPG, ".jpg");
		put(Constants.MediaTypes.PNG, ".png");
		put(Constants.MediaTypes.GIF, ".gif");
		put(Constants.MediaTypes.CSS, ".css");
		put(Constants.MediaTypes.EPUB, ".epub");
		put(Constants.MediaTypes.SVG, ".svg");
		put(Constants.MediaTypes.XHTML, ".xhtml");
	}};
	
	public static boolean isImageMediaType(String mediaType) {
		return IMAGE_MEDIA_TYPES.contains(mediaType);
	}
	
	public static String getDefaultExtensionForMimetype(String mimeType) {
		return MIMETYPE_DEFAULT_EXTENSIONS.get(mimeType);
	}
	
	/**
	 * Determines the files mediatype based on its file extension.
	 * 
	 * @param filename
	 * @return
	 */
	public static String determineMediaType(String filename) {
		String result = "";
		filename = filename.toLowerCase();
		if (StringUtils.endsWithIgnoreCase(filename, ".html") || StringUtils.endsWithIgnoreCase(filename, ".htm")) {
			result = Constants.MediaTypes.XHTML;
		} else if (StringUtils.endsWithIgnoreCase(filename, ".jpg") || StringUtils.endsWithIgnoreCase(filename, ".jpeg")) {
			result = Constants.MediaTypes.JPG;
		} else if (StringUtils.endsWithIgnoreCase(filename, ".png")) {
			result = Constants.MediaTypes.PNG;
		} else if (StringUtils.endsWithIgnoreCase(filename, ".gif")) {
			result = Constants.MediaTypes.GIF;
		} else if (StringUtils.endsWithIgnoreCase(filename, ".css")) {
			result = Constants.MediaTypes.CSS;
		} else if (StringUtils.endsWithIgnoreCase(filename, ".svg")) {
			result = Constants.MediaTypes.SVG;
		}
		return result;
	}

}
