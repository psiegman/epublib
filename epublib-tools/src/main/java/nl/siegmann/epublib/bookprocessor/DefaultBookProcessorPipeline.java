package nl.siegmann.epublib.bookprocessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.siegmann.epublib.epub.BookProcessor;
import nl.siegmann.epublib.epub.BookProcessorPipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A book processor that combines several other bookprocessors
 * 
 * Fixes coverpage/coverimage.
 * Cleans up the XHTML.
 * 
 * @author paul.siegmann
 *
 */
public class DefaultBookProcessorPipeline extends BookProcessorPipeline {

	private Logger log = LoggerFactory.getLogger(DefaultBookProcessorPipeline.class);

	public DefaultBookProcessorPipeline() {
		super(createDefaultBookProcessors());
	}

	private static List<BookProcessor> createDefaultBookProcessors() {
		List<BookProcessor> result = new ArrayList<BookProcessor>();
		result.addAll(Arrays.asList(new BookProcessor[] {
			new SectionHrefSanityCheckBookProcessor(),
			new HtmlCleanerBookProcessor(),
			new CoverpageBookProcessor(),
			new FixIdentifierBookProcessor()
		}));
		return result;
	}
}
