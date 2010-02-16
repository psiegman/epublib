package nl.siegmann.epublib.bookprocessor;

import java.util.List;
import java.util.Map;

import nl.siegmann.epublib.EpubWriter;
import nl.siegmann.epublib.Resource;
import nl.siegmann.epublib.SectionResource;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Section;

import org.apache.commons.lang.StringUtils;

/**
 * For sections with empty or non-existing resources it creates a html file with just the name of the section.
 * 
 * @author paul
 *
 */
public class MissingResourceBookProcessor implements BookProcessor {

	private static class ItemIdGenerator {
		private int itemCounter = 1;
		
		public String getNextItemId() {
			return "item_" + itemCounter++;
		}
	}

	@Override
	public Book processBook(Book book, EpubWriter epubWriter) {
		ItemIdGenerator itemIdGenerator = new ItemIdGenerator();
		Map<String, Resource> resourceMap = BookProcessorUtil.createResourceByHrefMap(book);
		matchSectionsAndResources(itemIdGenerator, book.getSections(), resourceMap);
		book.setResources(resourceMap.values());
		return book;
	}

	/**
	 * For every section in the list of sections it finds a resource with a matching href or it creates a new SectionResource and adds it to the sections.
	 * 
	 * @param sectionIdGenerator
	 * @param sections
	 * @param resources
	 */
	private static void matchSectionsAndResources(ItemIdGenerator sectionIdGenerator, List<Section> sections,
			Map<String, Resource> resources) {
		for(Section section: sections) {
			Resource resource = getResourceByHref(section.getHref(), resources);
			if(resource == null) {
				resource = createNewSectionResource(sectionIdGenerator, section, resources);
				resources.put(resource.getHref(), resource);
			}
			section.setItemId(resource.getId());
			section.setHref(resource.getHref());
			matchSectionsAndResources(sectionIdGenerator, section.getChildren(), resources);
		}
	}

	private static Resource getResourceByHref(String href, Map<String, Resource> resources) {
		return resources.get(StringUtils.substringBefore(href, "#"));
	}
	

	private static Resource createNewSectionResource(ItemIdGenerator itemIdGenerator, Section section, Map<String, Resource> resources) {
		String href = calculateSectionResourceHref(section, resources);
		SectionResource result = new SectionResource(itemIdGenerator.getNextItemId(), section.getName(), href);
		return result;
	}
	
	
	private static String calculateSectionResourceHref(Section section,
			Map<String, Resource> resources) {
		String result = section.getName() + ".html";
		if(! resources.containsKey(result)) {
			return result;
		}
		int i = 1;
		String href = "section_" + i + ".html";
		while(! resources.containsKey(href)) {
			href = "section_" + (i++) + ".html";
		}
		return href;
	}

}
