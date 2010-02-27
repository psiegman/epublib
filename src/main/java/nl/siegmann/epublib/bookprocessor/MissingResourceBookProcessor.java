package nl.siegmann.epublib.bookprocessor;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Section;
import nl.siegmann.epublib.domain.SectionResource;
import nl.siegmann.epublib.epub.EpubWriter;

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
		for(Resource resource: book.getResources()) {
			if(StringUtils.isBlank(resource.getId())) {
				resource.setId(itemIdGenerator.getNextItemId());
			}
		}
		Map<String, Resource> resourceMap = BookProcessorUtil.createResourceByHrefMap(book);
		matchSectionsAndResources(itemIdGenerator, book.getSections(), resourceMap);
		book.setResources(resourceMap.values());
		return book;
	}

	/**
	 * For every section in the list of sections it finds a resource with a matching href or it creates a new SectionResource and adds it to the sections.
	 * 
	 * @param itemIdGenerator
	 * @param sections
	 * @param resources
	 */
	private static void matchSectionsAndResources(ItemIdGenerator itemIdGenerator, List<Section> sections,
			Map<String, Resource> resources) {
		for(Section section: sections) {
			Resource resource = BookProcessorUtil.getResourceByHref(section.getHref(), resources);
			if(resource == null) {
				resource = createNewSectionResource(itemIdGenerator, section, resources);
				resources.put(resource.getHref(), resource);
			}
			section.setItemId(resource.getId());
			section.setHref(resource.getHref());
			matchSectionsAndResources(itemIdGenerator, section.getChildren(), resources);
		}
	}

	
	private static Resource createNewSectionResource(ItemIdGenerator itemIdGenerator, Section section, Map<String, Resource> resources) {
		String href = calculateSectionResourceHref(section, resources);
		SectionResource result = new SectionResource(itemIdGenerator.getNextItemId(), section.getName(), href);
		return result;
	}
	
	
	/**
	 * Tries to create a section with as href the name of the section + '.html'.
	 * If that one already exists in the resources it tries to create a section called 'section_' + i + '.html' and 
	 * keeps incrementing that 'i' until no resource is found.
	 * 
	 * @param section
	 * @param resources
	 * @return
	 */
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
