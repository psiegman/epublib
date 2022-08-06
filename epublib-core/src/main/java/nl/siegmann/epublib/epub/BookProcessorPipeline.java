package nl.siegmann.epublib.epub;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.siegmann.epublib.domain.Book;


/**
 * A book processor that combines several other bookprocessors
 * 
 * Fixes coverpage/coverimage.
 * Cleans up the XHTML.
 * 
 * @author paul.siegmann
 *
 */
public class BookProcessorPipeline implements BookProcessor {

	private Logger log = Logger.getLogger(BookProcessorPipeline.class.getName());
	private List<BookProcessor> bookProcessors;

	public BookProcessorPipeline() {
		this(null);
	}
	
	public BookProcessorPipeline(List<BookProcessor> bookProcessingPipeline) {
		this.bookProcessors = bookProcessingPipeline;
	}

	
	@Override
	public Book processBook(Book book) {
		if (bookProcessors == null) {
			return book;
		}
		for(BookProcessor bookProcessor: bookProcessors) {
			try {
				book = bookProcessor.processBook(book);
			} catch(Exception e) {
				log.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		return book;
	}

	public void addBookProcessor(BookProcessor bookProcessor) {
		if (this.bookProcessors == null) {
			bookProcessors = new ArrayList<BookProcessor>();
		}
		this.bookProcessors.add(bookProcessor);
	}

	public void addBookProcessors(Collection<BookProcessor> bookProcessors) {
		if (this.bookProcessors == null) {
			this.bookProcessors = new ArrayList<BookProcessor>();
		}
		this.bookProcessors.addAll(bookProcessors);
	}
	
	
	public List<BookProcessor> getBookProcessors() {
		return bookProcessors;
	}


	public void setBookProcessingPipeline(List<BookProcessor> bookProcessingPipeline) {
		this.bookProcessors = bookProcessingPipeline;
	}

}
