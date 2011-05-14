package nl.siegmann.epublib.html.htmlcleaner;

import junit.framework.TestCase;
import nl.siegmann.epublib.bookprocessor.FixIdentifierBookProcessor;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Identifier;
import nl.siegmann.epublib.epub.EpubWriter;
import nl.siegmann.epublib.util.CollectionUtil;

public class FixIdentifierBookProcessorTest extends TestCase {

	public void test_empty_book() {
		Book book = new Book();
		FixIdentifierBookProcessor fixIdentifierBookProcessor = new FixIdentifierBookProcessor();
		Book resultBook = fixIdentifierBookProcessor.processBook(book);
		assertEquals(1, resultBook.getMetadata().getIdentifiers().size());
		Identifier identifier = CollectionUtil.first(resultBook.getMetadata().getIdentifiers());
		assertEquals(Identifier.Scheme.UUID, identifier.getScheme());
	}

	public void test_single_identifier() {
		Book book = new Book();
		Identifier identifier = new Identifier(Identifier.Scheme.ISBN, "1234");
		book.getMetadata().addIdentifier(identifier);
		FixIdentifierBookProcessor fixIdentifierBookProcessor = new FixIdentifierBookProcessor();
		Book resultBook = fixIdentifierBookProcessor.processBook(book);
		assertEquals(1, resultBook.getMetadata().getIdentifiers().size());
		Identifier actualIdentifier = CollectionUtil.first(resultBook.getMetadata().getIdentifiers());
		assertEquals(identifier, actualIdentifier);
	}
}
