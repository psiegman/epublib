package nl.siegmann.epublib.bookprocessor;

import java.util.Collection;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.BookProcessor;

public class FixMissingResourceBookProcessor implements BookProcessor {

	@Override
	public Book processBook(Book book) {
		return book;
	}

	private void fixMissingResources(Collection<TOCReference> tocReferences, Book book) {
		for (TOCReference tocReference:  tocReferences) {
			if (tocReference.getResource() == null) {
				
			}
		}
	}
}
