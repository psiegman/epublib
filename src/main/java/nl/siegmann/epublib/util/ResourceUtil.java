package nl.siegmann.epublib.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nl.siegmann.epublib.domain.Resource;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Various resource utility methods
 * @author paul
 *
 */
public class ResourceUtil {
	
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
	public static Document getAsDocument(Resource resource, DocumentBuilderFactory documentBuilderFactory) throws UnsupportedEncodingException, SAXException, IOException, ParserConfigurationException {
		InputSource inputSource;
		if(StringUtils.isBlank(resource.getInputEncoding())) {
			inputSource = new InputSource(resource.getInputStream());
		} else {
			inputSource = new InputSource(new InputStreamReader(resource.getInputStream(), resource.getInputEncoding()));
		}
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document result = documentBuilder.parse(inputSource);
		result.setXmlStandalone(true);
		return result;
	}
}
