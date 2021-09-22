package nl.siegmann.epublib.bookprocessor;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.BookProcessor;

/**
 * In the future this will split up too large html documents into smaller ones.
 * 
 * @author paul
 *
 */
public class HtmlSplitterBookProcessor implements BookProcessor {

	@Override
	public Book processBook(Book book) {
		return book;
	}

}
