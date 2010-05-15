package nl.siegmann.epublib.bookprocessor;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;

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
		return resources.get(StringUtils.substringBefore(href, "#"));
	}

	/**
	 * Creates a map with as key the href of the resource and as value the Resource.
	 * 
	 * @param book
	 * @return
	 */
	public static Map<String, Resource> createResourceByHrefMap(Book book) {
		Map<String, Resource> result = new LinkedHashMap<String, Resource>(book.getResources().size());
		for(Resource resource: book.getResources()) {
			result.put(resource.getHref(), resource);
		}
		return result;
	}
}
