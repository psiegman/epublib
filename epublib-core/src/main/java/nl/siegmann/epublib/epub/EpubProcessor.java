package nl.siegmann.epublib.epub;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathFactory;

import nl.siegmann.epublib.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

public class EpubProcessor {
	
	private static final Logger log = LoggerFactory.getLogger(EpubProcessor.class);
	
	protected DocumentBuilderFactory documentBuilderFactory;
	protected XPathFactory xPathFactory;
	
	private EntityResolver entityResolver = new EntityResolver() {
		
		private String previousLocation;
		
		@Override
		public InputSource resolveEntity(String publicId, String systemId)
				throws SAXException, IOException {
			String resourcePath;
			if (systemId.startsWith("http:")) {
				URL url = new URL(systemId);
				resourcePath = "dtd/" + url.getHost() + url.getPath();
				previousLocation = resourcePath.substring(0, resourcePath.lastIndexOf('/'));
			} else {
				resourcePath = previousLocation + systemId.substring(systemId.lastIndexOf('/'));
			}
			
			if (this.getClass().getClassLoader().getResource(resourcePath) == null) {
				throw new RuntimeException("remote resource is not cached : [" + systemId + "] cannot continue");
			}

			InputStream in = EpubProcessor.class.getClassLoader().getResourceAsStream(resourcePath);
			return new InputSource(in);
		}
	};
	
	
	public EpubProcessor() {
		this.documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(true);
		documentBuilderFactory.setValidating(false);
		this.xPathFactory = XPathFactory.newInstance();
	}
	
	XmlSerializer createXmlSerializer(OutputStream out) {
		XmlSerializer result = null;
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
//			factory.setNamespaceAware(true);
			factory.setValidating(true);
			result = factory.newSerializer();
//			result.setProperty("SERIALIZER_INDENTATION", "\t");
			result.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
			// indentation as 3 spaces
//			result.setProperty(
//			   "http://xmlpull.org/v1/doc/properties.html#serializer-indentation", "   ");
//			// also set the line separator
//			result.setProperty(
//			   "http://xmlpull.org/v1/doc/properties.html#serializer-line-separator", "\n");

			result.setOutput(out, Constants.ENCODING);
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public DocumentBuilderFactory getDocumentBuilderFactory() {
		return documentBuilderFactory;
	}

	public DocumentBuilder createDocumentBuilder() {
		DocumentBuilder result = null;
		try {
			result = documentBuilderFactory.newDocumentBuilder();
			result.setEntityResolver(entityResolver);
		} catch (ParserConfigurationException e) {
			log.error(e.getMessage());
		}
		return result;
	}

	public XPathFactory getXPathFactory() {
		return xPathFactory;
	}

}
