package nl.siegmann.epublib.bookprocessor;

import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Section;
import nl.siegmann.epublib.domain.SectionResource;
import nl.siegmann.epublib.epub.EpubWriter;

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
		for(Resource resource: book.getResources().getAll()) {
			if(StringUtils.isBlank(resource.getId())) {
				resource.setId(itemIdGenerator.getNextItemId());
			}
		}
		matchSectionsAndResources(itemIdGenerator, book.getSpineSections(), book);
		matchSectionsAndResources(itemIdGenerator, book.getTocSections(), book);
		return book;
	}

	/**
	 * For every section in the list of sections it finds a resource with a matching href or it creates a new SectionResource and adds it to the sections.
	 * 
	 * @param itemIdGenerator
	 * @param sections
	 * @param resources
	 */
	private static void matchSectionsAndResources(ItemIdGenerator itemIdGenerator, List<Section> sections, Book book) {
		for(Section section: sections) {
			Resource resource = section.getResource();
			if(resource == null) {
				resource = createNewSectionResource(itemIdGenerator, section, book);
				book.getResources().add(resource);
			}
			section.setResource(resource);
			matchSectionsAndResources(itemIdGenerator, section.getChildren(), book);
		}
	}

	
	private static Resource createNewSectionResource(ItemIdGenerator itemIdGenerator, Section section, Book book) {
		String href = calculateSectionResourceHref(section, book);
		SectionResource result = new SectionResource(itemIdGenerator.getNextItemId(), section.getTitle(), href);
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
	private static String calculateSectionResourceHref(Section section, Book book) {
		String result = section.getTitle() + ".html";
		if(! book.getResources().containsByHref(result)) {
			return result;
		}
		int i = 1;
		String href = "section_" + i + ".html";
		while (book.getResources().containsByHref(href)) {
			href = "section_" + (i++) + ".html";
		}
		return href;
	}

}
