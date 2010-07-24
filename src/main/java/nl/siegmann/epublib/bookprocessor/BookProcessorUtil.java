package nl.siegmann.epublib.bookprocessor;

import java.util.Map;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.domain.Resource;

import org.apache.commons.lang.StringUtils;

/**
 * Utility methods shared by various BookProcessors.
 * 
 * @author paul
 *
 */
public class BookProcessorUtil {

	/**
	 * From the href it takes the part before '#' or the whole href otherwise and uses that as the key to find the resource in the resource map.
	 * 
	 * @param href
	 * @param resources
	 * @return
	 */
	public static Resource getResourceByHref(String href, Map<String, Resource> resources) {
		return resources.get(StringUtils.substringBefore(href, Constants.FRAGMENT_SEPARATOR));
	}
}
