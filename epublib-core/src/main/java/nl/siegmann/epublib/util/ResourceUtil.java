package nl.siegmann.epublib.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.domain.MediaType;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubProcessorSupport;
import nl.siegmann.epublib.service.MediatypeService;

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
	
	public static Resource createResource(File file) throws IOException {
		if (file == null) {
			return null;
		}
		MediaType mediaType = MediatypeService.determineMediaType(file.getName());
		byte[] data = IOUtil.toByteArray(new FileInputStream(file));
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
	public static Document getAsDocument(Resource resource) throws UnsupportedEncodingException, SAXException, IOException, ParserConfigurationException {
		return getAsDocument(resource, EpubProcessorSupport.createDocumentBuilder());
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
