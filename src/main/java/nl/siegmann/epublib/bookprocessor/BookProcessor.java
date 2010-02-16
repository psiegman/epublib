package nl.siegmann.epublib.bookprocessor;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubWriter;

/**
 * Post-processes a book. Intended to be called before writing it.
 * 
 * @author paul
 *
 */
public interface BookProcessor {
	Book processBook(Book book, EpubWriter epubWriter);
}
