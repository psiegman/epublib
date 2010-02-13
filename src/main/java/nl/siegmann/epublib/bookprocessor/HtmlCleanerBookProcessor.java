package nl.siegmann.epublib.bookprocessor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;

import nl.siegmann.epublib.ByteArrayResource;
import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.EpubWriter;
import nl.siegmann.epublib.Resource;
import nl.siegmann.epublib.domain.Book;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.SimpleXmlSerializer;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XmlSerializer;

public class HtmlCleanerBookProcessor implements BookProcessor {

	private final static Logger logger = Logger.getLogger(HtmlCleanerBookProcessor.class); 
	public static final String OUTPUT_ENCODING = "UTF-8";
	private HtmlCleaner htmlCleaner;
	private XmlSerializer newXmlSerializer;

	public HtmlCleanerBookProcessor() {
		this.htmlCleaner = createHtmlCleaner();
		this.newXmlSerializer = new SimpleXmlSerializer(htmlCleaner.getProperties());
	}

	@Override
	public Book processBook(Book book, EpubWriter epubWriter) {
		Collection<Resource> cleanupResources = new ArrayList<Resource>(book.getResources().size());
		for(Resource resource: book.getResources()) {
			Resource cleanedUpResource;
			try {
				cleanedUpResource = createCleanedUpResource(resource);
				cleanupResources.add(cleanedUpResource);
			} catch (IOException e) {
				logger.error(e);
			}
		}
		book.setResources(cleanupResources);
		return book;
	}

	private Resource createCleanedUpResource(Resource resource) throws IOException {
		Resource result = resource;
		if(resource.getMediaType().equals(Constants.MediaTypes.xhtml)) {
			byte[] cleanedHtml = cleanHtml(resource.getInputStream(), resource.getInputEncoding());
			result = new ByteArrayResource(resource.getId(), cleanedHtml, resource.getHref(), resource.getMediaType(), "UTF-8");
		}
		return result;
	}

	private static HtmlCleaner createHtmlCleaner() {
		HtmlCleaner result = new HtmlCleaner();
//		CleanerProperties cleanerProperties = result.getProperties();
//		cleanerProperties.setTranslateSpecialEntities(false);
		return result;
	}
	

	public byte[] cleanHtml(InputStream in, String encoding) throws IOException {
		byte[] result = null;
		Reader reader;
		if(! StringUtils.isEmpty(encoding)) {
			reader = new InputStreamReader(in, Charset.forName(encoding));
		} else {
			reader = new InputStreamReader(in);
		}
		TagNode node = htmlCleaner.clean(reader);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		newXmlSerializer.writeXmlToStream(node, out, OUTPUT_ENCODING);
		result = out.toByteArray();
		return result;
	}

}
