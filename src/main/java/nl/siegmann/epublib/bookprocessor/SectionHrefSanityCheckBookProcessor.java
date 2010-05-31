package nl.siegmann.epublib.bookprocessor;

import java.util.ArrayList;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Section;
import nl.siegmann.epublib.epub.EpubWriter;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Removes Sections from the page flow that differ only from the previous section's href by the '#' in the url.
 * 
 * @author paul
 *
 */
public class SectionHrefSanityCheckBookProcessor implements BookProcessor {

	@Override
	public Book processBook(Book book, EpubWriter epubWriter) {
		book.setSpineSections(checkSections(book.getSpineSections(), null));
		return book;
	}

	private static List<Section> checkSections(List<Section> sections, String previousSectionHref) {
		List<Section> result = new ArrayList<Section>(sections.size());
		for(Section section: sections) {
			if(StringUtils.isBlank(section.getHref())) {
				continue;
			}
			String href = StringUtils.substringBefore(section.getHref(), "#");
			if(! (href.equals(previousSectionHref))) {
				result.add(section);
			}
			previousSectionHref = href;
			if(CollectionUtils.isNotEmpty(section.getChildren())) {
				section.setChildren(checkSections(section.getChildren(), previousSectionHref));
			}
		}
		return result;
	}
}
