package nl.siegmann.epublib.epub;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class EpubProcessor {
	
	private static final Logger LOG = Logger.getLogger(EpubProcessor.class);
	
	protected DocumentBuilderFactory documentBuilderFactory;

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
			InputStream in = EpubProcessor.class.getClassLoader().getResourceAsStream(resourcePath);
			return new InputSource(in);
		}
	};
	
	
	public EpubProcessor() {
		this.documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(true);
		documentBuilderFactory.setValidating(false);
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
			LOG.error(e);
		}
		return result;
	}
}
