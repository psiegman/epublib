package nl.siegmann.epublib.bookprocessor;


import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.ByteArrayResource;
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
		Collection<Resource> cleanupResources = new ArrayList<Resource>(book.getResources().size());
		for(Resource resource: book.getResources().getAll()) {
			Resource cleanedUpResource;
			try {
				cleanedUpResource = createCleanedUpResource(resource, book, epubProcessor);
				cleanupResources.add(cleanedUpResource);
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}
		book.getResources().set(cleanupResources);
		return book;
	}

	private Resource createCleanedUpResource(Resource resource, Book book, EpubProcessor epubProcessor) throws IOException {
		Resource result = resource;
		if(resource.getMediaType() == MediatypeService.XHTML) {
			byte[] cleanedHtml = processHtml(resource, book, epubProcessor, Constants.ENCODING);
			result = new ByteArrayResource(resource.getId(), cleanedHtml, resource.getHref(), resource.getMediaType(), Constants.ENCODING);
		}
		return result;
	}

	protected abstract byte[] processHtml(Resource resource, Book book, EpubProcessor epubProcessor, Charset encoding) throws IOException;

}
