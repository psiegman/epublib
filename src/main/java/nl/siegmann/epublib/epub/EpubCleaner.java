package nl.siegmann.epublib.epub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.siegmann.epublib.bookprocessor.BookProcessor;
import nl.siegmann.epublib.bookprocessor.CoverpageBookProcessor;
import nl.siegmann.epublib.bookprocessor.FixIdentifierBookProcessor;
import nl.siegmann.epublib.bookprocessor.HtmlCleanerBookProcessor;
import nl.siegmann.epublib.bookprocessor.SectionHrefSanityCheckBookProcessor;
import nl.siegmann.epublib.domain.Book;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cleans up the epub in various ways.
 * 
 * Fixes coverpage/coverimage.
 * Cleans up the XHTML.
 * 
 * @author paul.siegmann
 *
 */
public class EpubCleaner extends EpubProcessor {

	private Logger log = LoggerFactory.getLogger(EpubCleaner.class);
	private List<BookProcessor> bookProcessingPipeline;

	public EpubCleaner(){
		this(createDefaultBookProcessingPipeline());
	}
	
	public EpubCleaner(List<BookProcessor> bookProcessingPipeline) {
		this.bookProcessingPipeline = bookProcessingPipeline;
	}

	public Book cleanEpub(Book book) {
		if (bookProcessingPipeline == null) {
			return book;
		}
		for(BookProcessor bookProcessor: bookProcessingPipeline) {
			try {
				book = bookProcessor.processBook(book, this);
			} catch(Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		return book;
	}
	
	private static List<BookProcessor> createDefaultBookProcessingPipeline() {
		List<BookProcessor> result = new ArrayList<BookProcessor>();
		result.addAll(Arrays.asList(new BookProcessor[] {
			new SectionHrefSanityCheckBookProcessor(),
			new HtmlCleanerBookProcessor(),
			new CoverpageBookProcessor(),
			new FixIdentifierBookProcessor()
		}));
		return result;
	}
	



	public List<BookProcessor> getBookProcessingPipeline() {
		return bookProcessingPipeline;
	}


	public void setBookProcessingPipeline(List<BookProcessor> bookProcessingPipeline) {
		this.bookProcessingPipeline = bookProcessingPipeline;
	}

}
