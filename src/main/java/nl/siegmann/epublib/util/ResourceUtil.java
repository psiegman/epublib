package nl.siegmann.epublib.util;

import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubProcessor;
import nl.siegmann.epublib.service.MediatypeService;

import org.apache.commons.lang.StringEscapeUtils;
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
	
	public static String getTitle(Resource resource) {
		if (resource == null) {
			return "";
		}
		if (resource.getMediaType() != MediatypeService.XHTML) {
			return resource.getHref();
		}
		String title = findTitleFromXhtml(resource);
		if (title == null) {
			title = "";
		}
		return title;
	}
	
	/**
	 * Retrieves whatever it finds between <title>...</title> or <h1-7>...</h1-7>.
	 * The first match is returned, even if it is a blank string.
	 * If it finds nothing null is returned.
	 * @param resource
	 * @return
	 */
	public static String findTitleFromXhtml(Resource resource) {
		if (resource == null) {
			return "";
		}
		Pattern h_tag = Pattern.compile("^h\\d\\s*", Pattern.CASE_INSENSITIVE);
		String title = null;
		try {
			Reader content = getReader(resource);
			Scanner scanner = new Scanner(content);
			scanner.useDelimiter("<");
			while(scanner.hasNext()) {
				String text = scanner.next();
				int closePos = text.indexOf('>');
				String tag = text.substring(0, closePos);
				if (tag.equalsIgnoreCase("title")
					|| h_tag.matcher(tag).find()) {

					title = text.substring(closePos + 1).trim();
					title = StringEscapeUtils.unescapeHtml(title);
					break;
				}
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		return title;
	}
	
	
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
