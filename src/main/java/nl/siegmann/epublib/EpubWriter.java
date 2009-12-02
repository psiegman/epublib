package nl.siegmann.epublib;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.io.FileUtils;

/**
 * Generates an epub file. Not thread-safe, single use object.
 * 
 * @author paul
 *
 */
public class EpubWriter {
	
	public void write(Book book, OutputStream out) throws IOException, XMLStreamException, FactoryConfigurationError {
		File resultDir = new File("/home/paul/tmp/epublib");
		writeMimeType(resultDir);
		File oebpsDir = new File(resultDir.getAbsolutePath() + File.separator + "OEBPS");
		FileUtils.forceMkdir(oebpsDir);
		writeContainer(resultDir);
		writeNcxDocument(book, oebpsDir);
		writePackageDocument(book, oebpsDir);
	}
	
	private void writePackageDocument(Book book, File oebpsDir) throws XMLStreamException, IOException {
		XMLOutputFactory xmlOutputFactory = createXMLOutputFactory();
		Writer out = new FileWriter(oebpsDir.getAbsolutePath() + File.separator + "content.opf");
		XMLStreamWriter xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(out);
		PackageDocument.write(this, xmlStreamWriter, book);
		xmlStreamWriter.close();
	}

	private void writeNcxDocument(Book book, File oebpsDir) throws IOException, XMLStreamException, FactoryConfigurationError {
		NCXDocument.write(book, new File(oebpsDir.getAbsolutePath() + File.separator + "toc.ncx"));
	}

	private void writeContainer(File resultDir) throws IOException {
		File containerDir = new File(resultDir.getAbsolutePath() + File.separator + "META-INF");
		FileUtils.forceMkdir(containerDir);
		File containerFile = new File(containerDir + File.separator + "container.xml");
		Writer out = new FileWriter(containerFile);
		out.write("<?xml version=\"1.0\"?>\n");
		out.write("<container version=\"1.0\" xmlns=\"urn:oasis:names:tc:opendocument:xmlns:container\">\n");
		out.write("\t<rootfiles>\n");
		out.write("\t\t<rootfile full-path=\"OEBPS/content.opf\" media-type=\"application/oebps-package+xml\"/>\n");
		out.write("\t</rootfiles>\n");
		out.write("</container>");
		out.close();
	}

	private void writeMimeType(File resultDir) throws IOException {
		Writer out = new FileWriter(resultDir.getAbsolutePath() + File.separator + "mimetype");
		out.write(Constants.MediaTypes.epub);
		out.close();
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
