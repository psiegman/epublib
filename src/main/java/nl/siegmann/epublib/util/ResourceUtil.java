package nl.siegmann.epublib.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.domain.MediaType;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubProcessor;
import nl.siegmann.epublib.service.MediatypeService;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Various resource utility methods
 * 
 * @author paul
 *
 */
public class ResourceUtil {
	
	private static Logger log = LoggerFactory.getLogger(ResourceUtil.class);
	
	public static Resource createResource(File file) throws IOException {
		if (file == null) {
			return null;
		}
		MediaType mediaType = MediatypeService.determineMediaType(file.getName());
		byte[] data = IOUtils.toByteArray(new FileInputStream(file));
		Resource result = new Resource(data, mediaType);
		return result;
	}
	
	
	/**
	 * Creates a resource with as contents a html page with the given title.
	 * 
	 * @param title
	 * @param href
	 * @return
	 */
	public static Resource createResource(String title, String href) {
		String content = "<html><head><title>" + title + "</title></head><body><h1>" + title + "</h1></body></html>";
		return new Resource(null, content.getBytes(), href, MediatypeService.XHTML, Constants.ENCODING);
	}

	/**
	 * Creates a resource out of the given zipEntry and zipInputStream.
	 * 
	 * @param zipEntry
	 * @param zipInputStream
	 * @return
	 * @throws IOException
	 */
	public static Resource createResource(ZipEntry zipEntry, ZipInputStream zipInputStream) throws IOException {
		return new Resource(zipInputStream, zipEntry.getName());

	}
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
		if (resource.getTitle() != null) {
			return resource.getTitle();
		}
		Pattern h_tag = Pattern.compile("^h\\d\\s*", Pattern.CASE_INSENSITIVE);
		String title = null;
		try {
			Reader content = resource.getReader();
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
		resource.setTitle(title);
		return title;
	}
	
	
	
	/**
	 * Gets the contents of the Resource as an InputSource in a null-safe manner.
	 * 
	 */
	public static InputSource getInputSource(Resource resource) throws IOException {
		if (resource == null) {
			return null;
		}
		Reader reader = resource.getReader();
		if (reader == null) {
			return null;
		}
		InputSource inputSource = new InputSource(reader);
		return inputSource;
	}
	
	
	/**
	 * Reads parses the xml therein and returns the result as a Document
	 */
	public static Document getAsDocument(Resource resource, EpubProcessor epubProcessor) throws UnsupportedEncodingException, SAXException, IOException, ParserConfigurationException {
		return getAsDocument(resource, epubProcessor.createDocumentBuilder());
	}
	
	
	/**
	 * Reads the given resources inputstream, parses the xml therein and returns the result as a Document
	 * 
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
