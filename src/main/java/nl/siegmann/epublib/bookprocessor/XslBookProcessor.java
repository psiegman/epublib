package nl.siegmann.epublib.bookprocessor;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.Charset;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubWriter;

public class XslBookProcessor extends HtmlBookProcessor implements BookProcessor {

	private final static Logger log = Logger.getLogger(XslBookProcessor.class); 

	private Transformer transformer;
	
	public XslBookProcessor(String xslFileName) throws TransformerConfigurationException {
		File xslFile = new File(xslFileName);
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		transformer = transformerFactory.newTransformer(new StreamSource(xslFile));
	}

	@Override
	public byte[] processHtml(Resource resource, Book book, EpubWriter epubWriter) throws IOException {
		Source htmlSource = new StreamSource(new InputStreamReader(resource.getInputStream(), Charset.forName(resource.getInputEncoding())));
		StringWriter out = new StringWriter();
		Result result = new StreamResult(out);
		try {
			transformer.transform(htmlSource, result);
		} catch (TransformerException e) {
			log.error(e);
			throw new IOException(e);
		}
		return out.toString().getBytes();
	}
}
