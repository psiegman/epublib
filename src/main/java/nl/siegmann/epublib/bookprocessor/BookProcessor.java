package nl.siegmann.epublib.bookprocessor;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubProcessor;

/**
 * Post-processes a book. Intended to be applied to a Book before writing the Book as an epub.
 * 
 * @author paul
 *
 */
public interface BookProcessor {
	Book processBook(Book book, EpubProcessor epubProcessor);
}
