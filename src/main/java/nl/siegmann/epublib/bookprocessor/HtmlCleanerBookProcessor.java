package nl.siegmann.epublib.bookprocessor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import nl.siegmann.epublib.EpubWriter;
import nl.siegmann.epublib.Resource;
import nl.siegmann.epublib.domain.Book;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.SimpleXmlSerializer;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XmlSerializer;

/**
 * Cleans up regular html into xhtml.
 * Uses HtmlCleaner to do this.
 * 
 * @author paul
 *
 */
public class HtmlCleanerBookProcessor extends HtmlBookProcessor implements BookProcessor {

	@SuppressWarnings("unused")
	private final static Logger log = Logger.getLogger(HtmlCleanerBookProcessor.class);
	
	public static final String OUTPUT_ENCODING = "UTF-8";
	private HtmlCleaner htmlCleaner;
	private XmlSerializer newXmlSerializer;

	public HtmlCleanerBookProcessor() {
		this.htmlCleaner = createHtmlCleaner();
		this.newXmlSerializer = new SimpleXmlSerializer(htmlCleaner.getProperties());
	}

	private static HtmlCleaner createHtmlCleaner() {
		HtmlCleaner result = new HtmlCleaner();
//		CleanerProperties cleanerProperties = result.getProperties();
//		cleanerProperties.setTranslateSpecialEntities(false);
		return result;
	}
	

	public byte[] cleanHtml(Resource resource) throws IOException {
		byte[] result = null;
		Reader reader;
		if(! StringUtils.isEmpty(resource.getInputEncoding())) {
			reader = new InputStreamReader(resource.getInputStream(), Charset.forName(resource.getInputEncoding()));
		} else {
			reader = new InputStreamReader(resource.getInputStream());
		}
		TagNode node = htmlCleaner.clean(reader);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		newXmlSerializer.writeXmlToStream(node, out, OUTPUT_ENCODING);
		result = out.toByteArray();
		return result;
	}
	
	public byte[] processHtml(Resource resource, Book book, EpubWriter epubWriter) throws IOException {
		byte[] result = null;
		Reader reader;
		if(! StringUtils.isEmpty(resource.getInputEncoding())) {
			reader = new InputStreamReader(resource.getInputStream(), Charset.forName(resource.getInputEncoding()));
		} else {
			reader = new InputStreamReader(resource.getInputStream());
		}
		TagNode node = htmlCleaner.clean(reader);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		newXmlSerializer.writeXmlToStream(node, out, OUTPUT_ENCODING);
		result = out.toByteArray();
		return result;
	}
}
