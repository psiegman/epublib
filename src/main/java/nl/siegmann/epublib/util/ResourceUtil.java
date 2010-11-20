package nl.siegmann.epublib.util;

import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.gdata.util.io.base.UnicodeReader;

/**
 * Various resource utility methods
 * @author paul
 *
 */
public class ResourceUtil {
	
	private static Logger log = LoggerFactory.getLogger(ResourceUtil.class);
	
	/**
	 * Gets the contents of the Resource as Reader.
	 * 
	 * Does all sorts of smart things (courtesy of commons io XmlStreamReader) to handle encodings, byte order markers, etc.
	 * 
	 * @see http://commons.apache.org/io/api-release/org/apache/commons/io/input/XmlStreamReader.html
	 * 
	 * @param resource
	 * @return
	 * @throws IOException
	 */
	public static Reader getReader(Resource resource) throws IOException  {
		if (resource == null) {
			log.error("null resource passed to getReader");
			return null;
		}
//		XmlStreamReader xmlStreamReader = new XmlStreamReader(resource.getInputStream(), false, resource.getInputEncoding().name());
//		System.out.println("file contents:");
//		IOUtils.copy(xmlStreamReader, System.out);
//		xmlStreamReader = new XmlStreamReader(resource.getInputStream(), true, resource.getInputEncoding().name());
//		return xmlStreamReader;
		return new UnicodeReader(resource.getInputStream(), resource.getInputEncoding().name());
	}
	
	/**
	 * Gets the contents of the Resource as an InputSource
	 */
	public static InputSource getInputSource(Resource resource) throws IOException {
		Reader reader = getReader(resource);
		if (reader == null) {
			return null;
		}
		InputSource inputSource = new InputSource(reader);
		return inputSource;
	}
	
	
	public static Document getAsDocument(Resource resource, EpubProcessor epubProcessor) throws UnsupportedEncodingException, SAXException, IOException, ParserConfigurationException {
		return getAsDocument(resource, epubProcessor.createDocumentBuilder());
	}
	
	
	/**
	 * Reads the given resources inputstream, parses the xml therein and returns the result as a Document
	 * @param resource
	 * @param documentBuilderFactory
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public static Document getAsDocument(Resource resource, DocumentBuilder documentBuilder) throws UnsupportedEncodingException, SAXException, IOException, ParserConfigurationException {
		InputSource inputSource = getInputSource(resource);
		if (inputSource == null) {
			return null;
		}
		Document result = documentBuilder.parse(inputSource);
		result.setXmlStandalone(true);
		return result;
	}
}
