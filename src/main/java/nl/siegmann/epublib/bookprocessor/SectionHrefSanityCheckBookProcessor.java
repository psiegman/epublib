package nl.siegmann.epublib.bookprocessor;

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
		checkSections(book.getSpineSections(), null);
		return book;
	}

	private static void checkSections(List<Section> sections, String previousSectionHref) {
		for(Section section: sections) {
			if(StringUtils.isBlank(section.getHref())) {
				section.setPartOfPageFlow(false);
			} else {
				String href = StringUtils.substringBefore(section.getHref(), "#");
				if(href.equals(previousSectionHref)) {
					section.setPartOfPageFlow(false);
				} else {
					previousSectionHref = href;
				}
			}
			if(CollectionUtils.isNotEmpty(section.getChildren())) {
				checkSections(section.getChildren(), previousSectionHref);
			}
		}
	}
}
