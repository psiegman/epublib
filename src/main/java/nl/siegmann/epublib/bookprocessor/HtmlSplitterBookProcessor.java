package nl.siegmann.epublib.bookprocessor;

import java.util.Arrays;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubWriter;

public class HtmlSplitterBookProcessor implements BookProcessor {

	@Override
	public Book processBook(Book book, EpubWriter epubWriter) {
		// TODO Auto-generated method stub
		return null;
	}

	List<Resource> splitHtml(Resource resource) {
		return Arrays.asList(new Resource[] {resource});
	}
}
