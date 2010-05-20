package nl.siegmann.epublib.bookprocessor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubWriter;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.cyberneko.html.HTMLConfiguration;
import org.cyberneko.html.HTMLTagBalancer;
import org.cyberneko.html.filters.Purifier;
import org.cyberneko.html.filters.Writer;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Cleans up regular html into xhtml.
 * Uses NekoHtml to do this.
 * 
 * @author paul
 *
 */
public class NekoHtmlCleanerBookProcessor extends HtmlBookProcessor implements BookProcessor {

	@SuppressWarnings("unused")
	private final static Logger log = Logger.getLogger(NekoHtmlCleanerBookProcessor.class);
	
	public static final String OUTPUT_ENCODING = "UTF-8";
	private TransformerFactory transformerFactory = TransformerFactory.newInstance();
	
	public NekoHtmlCleanerBookProcessor() {
	}

	public byte[] processHtml(Resource resource, Book book, EpubWriter epubWriter, String encoding) throws IOException {
		Reader reader;
		if(StringUtils.isNotBlank(resource.getInputEncoding())) {
			reader = new InputStreamReader(resource.getInputStream(), Charset.forName(resource.getInputEncoding()));
		} else {
			reader = new InputStreamReader(resource.getInputStream());
		}
		return foo(reader);
//		Document document = parseXml(reader);
//		byte[] result = null;
//		try {
//			result = serializeXml(document);
//		} catch (TransformerException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return result;
	}

	private byte[] serializeXml(Document document) throws TransformerException {
		Transformer transformer = transformerFactory.newTransformer();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		transformer.transform(new DOMSource(document), new StreamResult(out));
//		return out.toByteArray();
		byte[] result = out.toByteArray();
		String foo = new String(result);
		return result;
	}


	private byte[] foo(Reader reader) {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		try {
			Writer nekoWriter = new Writer(result, "UTF-8");
			nekoWriter.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
			nekoWriter.setProperty("http://cyberneko.org/html/properties/names/attrs", "lower");
			nekoWriter.setProperty("http://cyberneko.org/html/properties/namespaces-uri", true);
			nekoWriter.setFeature("http://cyberneko.org/html/features/override-namespaces", true);
			nekoWriter.setFeature("http://cyberneko.org/html/features/augmentations", true);
			nekoWriter.setFeature("http://cyberneko.org/html/features/balance-tags", true);
			nekoWriter.setFeature("http://cyberneko.org/html/features/scanner/fix-mswindows-refs", true);
			nekoWriter.setFeature("http://cyberneko.org/html/features/scanner/ignore-specified-charset", true);
			
			Purifier purifier = new Purifier();
			purifier.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
			purifier.setProperty("http://cyberneko.org/html/properties/names/attrs", "lower");
			purifier.setProperty("http://cyberneko.org/html/properties/namespaces-uri", true);
			purifier.setFeature("http://cyberneko.org/html/features/override-namespaces", true);
			purifier.setFeature("http://cyberneko.org/html/features/augmentations", true);
			purifier.setFeature("http://cyberneko.org/html/features/balance-tags", true);
			purifier.setFeature("http://cyberneko.org/html/features/scanner/fix-mswindows-refs", true);
			purifier.setFeature("http://cyberneko.org/html/features/scanner/ignore-specified-charset", true);

			
			//			HTMLTagBalancer htmlTagBalancer = new HTMLTagBalancer();
//			htmlTagBalancer.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
//			htmlTagBalancer.setProperty("http://cyberneko.org/html/properties/names/attrs", "lower");
//			htmlTagBalancer.setFeature("http://cyberneko.org/html/features/augmentations", true);
//			htmlTagBalancer.setFeature("http://cyberneko.org/html/features/balance-tags", true);
			XMLDocumentFilter[] filters = { /* htmlTagBalancer, */ purifier, nekoWriter };
			
			XMLParserConfiguration parser = new HTMLConfiguration();
			parser.setProperty("http://cyberneko.org/html/properties/filters", filters);
			parser.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
			parser.setProperty("http://cyberneko.org/html/properties/names/attrs", "lower");
			parser.setProperty("http://cyberneko.org/html/properties/namespaces-uri", true);
			parser.setFeature("http://cyberneko.org/html/features/override-namespaces", true);
			parser.setFeature("http://cyberneko.org/html/features/augmentations", true);
			parser.setFeature("http://cyberneko.org/html/features/balance-tags", true);
			parser.setFeature("http://cyberneko.org/html/features/scanner/fix-mswindows-refs", true);
			parser.setFeature("http://cyberneko.org/html/features/scanner/ignore-specified-charset", true);
			XMLInputSource source = new XMLInputSource(null, "myTest", null, reader, "UTF-8");
			parser.parse(source);
			result.flush();
			return result.toByteArray();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	
	private Document parseXml(Reader reader) throws IOException {
//		Purifier purifierFilter = new Purifier();
//		XMLDocumentFilter[] filters = { purifierFilter };
//
//		XMLParserConfiguration parserConfiguration = new HTMLConfiguration();
//		parserConfiguration.setProperty("http://cyberneko.org/html/properties/filters", filters);
		DOMParser parser = new DOMParser();
		
		try {
			parser.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
			parser.setProperty("http://cyberneko.org/html/properties/names/attrs", "lower");
			parser.setFeature("http://cyberneko.org/html/features/augmentations", true);
			parser.setFeature("http://cyberneko.org/html/features/balance-tags", true);
			parser.parse(new InputSource(reader));
			Document document = parser.getDocument();
			return document;
		} catch (SAXException e) {
			throw new IOException(e);
		}
	}

}