package nl.siegmann.epublib.bookprocessor;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubWriter;

/**
 * Post-processes a book. Intended to be on a Book before writing the Book as an epub.
 * 
 * @author paul
 *
 */
public interface BookProcessor {
	Book processBook(Book book, EpubWriter epubWriter);
}
