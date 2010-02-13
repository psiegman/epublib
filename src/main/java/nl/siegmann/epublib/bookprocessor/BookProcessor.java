package nl.siegmann.epublib.bookprocessor;

import nl.siegmann.epublib.EpubWriter;
import nl.siegmann.epublib.domain.Book;

public interface BookProcessor {
	Book processBook(Book book, EpubWriter epubWriter);
}
