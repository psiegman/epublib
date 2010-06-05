package nl.siegmann.epublib.bookprocessor;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.ByteArrayResource;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubWriter;
import nl.siegmann.epublib.service.MediatypeService;

import org.apache.log4j.Logger;

/**
 * Helper class for BookProcessors that only manipulate html type resources.
 * 
 * @author paul
 *
 */
public abstract class HtmlBookProcessor implements BookProcessor {

	private final static Logger logger = Logger.getLogger(HtmlBookProcessor.class); 
	public static final String OUTPUT_ENCODING = "UTF-8";

	public HtmlBookProcessor() {
	}

	@Override
	public Book processBook(Book book, EpubWriter epubWriter) {
		Collection<Resource> cleanupResources = new ArrayList<Resource>(book.getResources().size());
		for(Resource resource: book.getResources().values()) {
			Resource cleanedUpResource;
			try {
				cleanedUpResource = createCleanedUpResource(resource, book, epubWriter);
				cleanupResources.add(cleanedUpResource);
			} catch (IOException e) {
				logger.error(e);
			}
		}
		book.setResources(cleanupResources);
		return book;
	}

	private Resource createCleanedUpResource(Resource resource, Book book, EpubWriter epubWriter) throws IOException {
		Resource result = resource;
		if(resource.getMediaType() == MediatypeService.XHTML) {
			byte[] cleanedHtml = processHtml(resource, book, epubWriter, Constants.ENCODING);
			result = new ByteArrayResource(resource.getId(), cleanedHtml, resource.getHref(), resource.getMediaType(), Constants.ENCODING);
		}
		return result;
	}

	protected abstract byte[] processHtml(Resource resource, Book book, EpubWriter epubWriter, String encoding) throws IOException;

}
