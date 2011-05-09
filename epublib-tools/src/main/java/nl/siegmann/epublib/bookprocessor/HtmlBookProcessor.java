package nl.siegmann.epublib.bookprocessor;


import java.io.IOException;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubProcessor;
import nl.siegmann.epublib.service.MediatypeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class for BookProcessors that only manipulate html type resources.
 * 
 * @author paul
 *
 */
public abstract class HtmlBookProcessor implements BookProcessor {

	private final static Logger log = LoggerFactory.getLogger(HtmlBookProcessor.class); 
	public static final String OUTPUT_ENCODING = "UTF-8";

	public HtmlBookProcessor() {
	}

	@Override
	public Book processBook(Book book, EpubProcessor epubProcessor) {
		for(Resource resource: book.getResources().getAll()) {
			try {
				cleanupResource(resource, book, epubProcessor);
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}
		return book;
	}

	private void cleanupResource(Resource resource, Book book, EpubProcessor epubProcessor) throws IOException {
		if(resource.getMediaType() == MediatypeService.XHTML) {
			byte[] cleanedHtml = processHtml(resource, book, epubProcessor, Constants.ENCODING);
			resource.setData(cleanedHtml);
			resource.setInputEncoding(Constants.ENCODING);
		}
	}

	protected abstract byte[] processHtml(Resource resource, Book book, EpubProcessor epubProcessor, String encoding) throws IOException;
}
