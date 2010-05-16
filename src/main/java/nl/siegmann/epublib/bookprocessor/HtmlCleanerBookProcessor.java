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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.htmlcleaner.CleanerProperties;
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
	
	private HtmlCleaner htmlCleaner;
	private XmlSerializer newXmlSerializer;

	public HtmlCleanerBookProcessor() {
		this.htmlCleaner = createHtmlCleaner();
		this.newXmlSerializer = new SimpleXmlSerializer(htmlCleaner.getProperties());
	}

	private static HtmlCleaner createHtmlCleaner() {
		HtmlCleaner result = new HtmlCleaner();
		CleanerProperties cleanerProperties = result.getProperties();
//		cleanerProperties.setTranslateSpecialEntities(false);
		cleanerProperties.setNamespacesAware(true);
		cleanerProperties.setOmitDoctypeDeclaration(false);
		return result;
	}
	

	@SuppressWarnings("unchecked")
	public byte[] processHtml(Resource resource, Book book, EpubWriter epubWriter, String encoding) throws IOException {
		Reader reader;
		if(StringUtils.isNotBlank(resource.getInputEncoding())) {
			reader = new InputStreamReader(resource.getInputStream(), Charset.forName(resource.getInputEncoding()));
		} else {
			reader = new InputStreamReader(resource.getInputStream());
		}
		TagNode node = htmlCleaner.clean(reader);
		node.getAttributes().put("xmlns", Constants.NAMESPACE_XHTML);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		newXmlSerializer.writeXmlToStream(node, out, encoding);
		return out.toByteArray();
	}
}
