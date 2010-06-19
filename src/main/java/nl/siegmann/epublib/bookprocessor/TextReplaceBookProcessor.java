package nl.siegmann.epublib.bookprocessor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.List;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubWriter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Cleans up regular html into xhtml.
 * Uses HtmlCleaner to do this.
 * 
 * @author paul
 *
 */
public class TextReplaceBookProcessor extends HtmlBookProcessor implements BookProcessor {

	@SuppressWarnings("unused")
	private final static Logger log = Logger.getLogger(TextReplaceBookProcessor.class);
	
	public TextReplaceBookProcessor() {
	}

	@SuppressWarnings("unchecked")
	public byte[] processHtml(Resource resource, Book book, EpubWriter epubWriter, String outputEncoding) throws IOException {
		Reader reader;
		if(StringUtils.isNotBlank(resource.getInputEncoding())) {
			reader = new InputStreamReader(resource.getInputStream(), Charset.forName(resource.getInputEncoding()));
		} else {
			reader = new InputStreamReader(resource.getInputStream());
		}
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
