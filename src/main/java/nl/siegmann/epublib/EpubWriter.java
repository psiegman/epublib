package nl.siegmann.epublib;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * Generates an epub file. Not thread-safe, single use object.
 * 
 * @author paul
 *
 */
public class EpubWriter {
	
	public void write(Book book, OutputStream out) throws IOException, XMLStreamException, FactoryConfigurationError {
		XMLOutputFactory xmlOutputFactory = createXMLOutputFactory();
		NCXDocument.write(book, new File("/home/paul/testncx.xml"));
		XMLStreamWriter xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(new FileWriter("/home/paul/testpackage.xml"));
		PackageDocument.write(this, xmlStreamWriter, book);
		xmlStreamWriter.close();
	}
	
	XMLEventFactory createXMLEventFactory() {
		return XMLEventFactory.newInstance();
	}
	
	XMLOutputFactory createXMLOutputFactory() {
		return XMLOutputFactory.newInstance();
	}
	
	String getNcxId() {
		return "ncx";
	}
	
	String getNcxHref() {
		return "toc.ncx";
	}

	String getNcxMediaType() {
		return "application/x-dtbncx+xml";
	}
}
