package nl.siegmann.epublib.epub;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.xpath.XPathFactory;

import nl.siegmann.epublib.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class EpubProcessor {
	
	private static final Logger log = LoggerFactory.getLogger(EpubProcessor.class);
	
	protected DocumentBuilderFactory documentBuilderFactory;
	protected XMLOutputFactory xmlOutputFactory;
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
		this.xmlOutputFactory = XMLOutputFactory.newFactory();
		this.xPathFactory = XPathFactory.newInstance();
	}
	
	XMLStreamWriter createXMLStreamWriter(OutputStream out) throws XMLStreamException {
		return xmlOutputFactory.createXMLStreamWriter(out, Constants.ENCODING);
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
