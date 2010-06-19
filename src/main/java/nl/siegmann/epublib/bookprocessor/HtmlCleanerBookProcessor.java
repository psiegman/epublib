package nl.siegmann.epublib.bookprocessor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubWriter;

import org.apache.log4j.Logger;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.EpublibXmlSerializer;
import org.htmlcleaner.HtmlCleaner;
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
	
	private HtmlCleaner htmlCleaner;
	private XmlSerializer newXmlSerializer;
	private boolean addXmlNamespace = true;
	
	public HtmlCleanerBookProcessor() {
		this.htmlCleaner = createHtmlCleaner();
		this.newXmlSerializer = new EpublibXmlSerializer(htmlCleaner.getProperties());
	}

	private static HtmlCleaner createHtmlCleaner() {
		HtmlCleaner result = new HtmlCleaner();
		CleanerProperties cleanerProperties = result.getProperties();
		cleanerProperties.setOmitXmlDeclaration(true);
		cleanerProperties.setRecognizeUnicodeChars(true);
		cleanerProperties.setTranslateSpecialEntities(true);
		cleanerProperties.setIgnoreQuestAndExclam(true);
		return result;
	}
	

	@SuppressWarnings("unchecked")
	public byte[] processHtml(Resource resource, Book book, EpubWriter epubWriter, Charset outputEncoding) throws IOException {
		Reader reader;
		if(resource.getInputEncoding() == null) {
			reader = new InputStreamReader(resource.getInputStream());
		} else {
			reader = new InputStreamReader(resource.getInputStream(), resource.getInputEncoding());
		}
		TagNode node = htmlCleaner.clean(reader);
		node.removeAttribute("xmlns:xml");
		if(isAddXmlNamespace()) {
			node.getAttributes().put("xmlns", Constants.NAMESPACE_XHTML);
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		newXmlSerializer.writeXmlToStream(node, out, outputEncoding.name());
		return out.toByteArray();
	}

	public void setAddXmlNamespace(boolean addXmlNamespace) {
		this.addXmlNamespace = addXmlNamespace;
	}

	public boolean isAddXmlNamespace() {
		return addXmlNamespace;
	}
}
