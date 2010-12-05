package nl.siegmann.epublib.bookprocessor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubProcessor;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cleans up regular html into xhtml.
 * Uses HtmlCleaner to do this.
 * 
 * @author paul
 *
 */
public class TextReplaceBookProcessor extends HtmlBookProcessor implements BookProcessor {

	@SuppressWarnings("unused")
	private final static Logger log = LoggerFactory.getLogger(TextReplaceBookProcessor.class);
	
	public TextReplaceBookProcessor() {
	}

	public byte[] processHtml(Resource resource, Book book, EpubProcessor epubProcessor, String outputEncoding) throws IOException {
		Reader reader = resource.getReader();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Writer writer = new OutputStreamWriter(out, Constants.ENCODING);
		for(String line: (List<String>) IOUtils.readLines(reader)) {
			writer.write(processLine(line));
			writer.flush();
		}
		return out.toByteArray();
	}

	private String processLine(String line) {
		return line.replace("&apos;", "'");
	}
}
