package nl.siegmann.epublib.bookprocessor;

import java.util.LinkedHashMap;
import java.util.Map;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;

public class BookProcessorUtil {

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
