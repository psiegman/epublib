package nl.siegmann.epublib;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import nl.siegmann.epublib.html.htmlcleaner.XmlSerializer;

import org.apache.log4j.Logger;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

/**
 * Generates an epub file. Not thread-safe, single use object.
 * 
 * @author paul
 *
 */
public class EpubWriter {
	
	private final static Logger logger = Logger.getLogger(EpubWriter.class); 
	
	private HtmlCleaner htmlCleaner;
	private XmlSerializer xmlSerializer;
	private XMLOutputFactory xmlOutputFactory;
	
	public EpubWriter() {
		this.htmlCleaner = new HtmlCleaner();
		xmlSerializer = new XmlSerializer(htmlCleaner.getProperties());
		xmlOutputFactory = XMLOutputFactory.newInstance();
	}
	
	
	public void write(Book book, OutputStream out) throws IOException, XMLStreamException, FactoryConfigurationError {
		ZipOutputStream resultStream = new ZipOutputStream(out);
		writeMimeType(resultStream);
		writeContainer(resultStream);
		writeResources(book, resultStream);
		writeNcxDocument(book, resultStream);
		writePackageDocument(book, resultStream);
		resultStream.close();
	}
	
	private void writeResources(Book book, ZipOutputStream resultStream) throws IOException {
		for(Resource resource: book.getResources()) {
			resultStream.putNextEntry(new ZipEntry("OEBPS/" + resource.getHref()));
			resource.writeResource(resultStream, this);
		}
	}
	
	private void writePackageDocument(Book book, ZipOutputStream resultStream) throws XMLStreamException, IOException {
		resultStream.putNextEntry(new ZipEntry("OEBPS/content.opf"));
		XMLOutputFactory xmlOutputFactory = createXMLOutputFactory();
		Writer out = new OutputStreamWriter(resultStream);
		XMLStreamWriter xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(out);
		PackageDocument.write(this, xmlStreamWriter, book);
		xmlStreamWriter.flush();
	}

	private void writeNcxDocument(Book book, ZipOutputStream resultStream) throws IOException, XMLStreamException, FactoryConfigurationError {
		NCXDocument.write(book, resultStream);
	}

	private void writeContainer(ZipOutputStream resultStream) throws IOException {
		resultStream.putNextEntry(new ZipEntry("META-INF/container.xml"));
		Writer out = new OutputStreamWriter(resultStream);
		out.write("<?xml version=\"1.0\"?>\n");
		out.write("<container version=\"1.0\" xmlns=\"urn:oasis:names:tc:opendocument:xmlns:container\">\n");
		out.write("\t<rootfiles>\n");
		out.write("\t\t<rootfile full-path=\"OEBPS/content.opf\" media-type=\"application/oebps-package+xml\"/>\n");
		out.write("\t</rootfiles>\n");
		out.write("</container>");
		out.flush();
	}

	public void cleanupHtml(InputStream in, OutputStream out) {
		try {
			TagNode node = htmlCleaner.clean(in);
			XMLStreamWriter writer = xmlOutputFactory.createXMLStreamWriter(out);
			writer.writeStartDocument();
			xmlSerializer.writeXml(node, writer);
			writer.writeEndDocument();
			writer.flush();
		} catch (IOException e) {
			logger.error(e);
		} catch (XMLStreamException e) {
			logger.error(e);
		}
		
	}
	private void writeMimeType(ZipOutputStream resultStream) throws IOException {
		resultStream.putNextEntry(new ZipEntry("mimetype"));
		resultStream.write((Constants.MediaTypes.epub).getBytes());
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
