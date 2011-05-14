package nl.siegmann.epublib.bookprocessor;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Identifier;
import nl.siegmann.epublib.epub.BookProcessor;

/**
 * If the book has no identifier it adds a generated UUID as identifier.
 * 
 * @author paul
 *
 */
public class FixIdentifierBookProcessor implements BookProcessor {

	@Override
	public Book processBook(Book book) {
		if(book.getMetadata().getIdentifiers().isEmpty()) {
			book.getMetadata().addIdentifier(new Identifier());
		}
		return book;
	}
}
