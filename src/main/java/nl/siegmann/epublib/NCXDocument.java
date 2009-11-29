package nl.siegmann.epublib;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class NCXDocument {
	
	public static final String NAMESPACE_NCX = "http://www.daisy.org/z3986/2005/ncx/";
	public static final String PREFIX_NCX = "ncx";

	public static void write(Book book, File file) throws IOException, XMLStreamException, FactoryConfigurationError {
		FileWriter out = new FileWriter(file);
		write(XMLEventFactory.newInstance(), XMLOutputFactory.newInstance().createXMLStreamWriter(out), book);
		out.close();
	}
	
	
	public static void write(XMLEventFactory eventFactory, XMLStreamWriter writer, Book book) throws XMLStreamException {
		writer.writeStartDocument(Constants.encoding, "1.0");
		writer.setDefaultNamespace(NAMESPACE_NCX);
		writer.writeStartElement(NAMESPACE_NCX, "ncx");
//		writer.writeNamespace("ncx", NAMESPACE_NCX);
		writer.writeAttribute("xmlns", NAMESPACE_NCX);
		writer.writeAttribute("version", "2005-1");
		writer.writeStartElement(NAMESPACE_NCX, "head");

		writer.writeStartElement(NAMESPACE_NCX, "meta");
		writer.writeAttribute("name", "dtb:uid");
		writer.writeAttribute("content", book.getUid());
		writer.writeEndElement();

		writer.writeStartElement(NAMESPACE_NCX, "meta");
		writer.writeAttribute("name", "dtb:depth");
		writer.writeAttribute("content", "1");
		writer.writeEndElement();

		writer.writeStartElement(NAMESPACE_NCX, "meta");
		writer.writeAttribute("name", "dtb:totalPageCount");
		writer.writeAttribute("content", "0");
		writer.writeEndElement();

		writer.writeStartElement(NAMESPACE_NCX, "meta");
		writer.writeAttribute("name", "dtb:maxPageNumber");
		writer.writeAttribute("content", "0");
		writer.writeEndElement();

		writer.writeEndElement();
		
		writer.writeStartElement(NAMESPACE_NCX, "docTitle");
		writer.writeStartElement(NAMESPACE_NCX, "text");
		writer.writeCharacters(book.getTitle());
		writer.writeEndElement();
		writer.writeEndElement();
		for(Author author: book.getAuthors()) {
			writer.writeStartElement(NAMESPACE_NCX, "docAuthor");
			writer.writeStartElement(NAMESPACE_NCX, "text");
			writer.writeCharacters(author.getLastname() + ", " + author.getFirstname());
			writer.writeEndElement();
			writer.writeEndElement();
		}
		writer.writeStartElement(NAMESPACE_NCX, "navMap");
		for(int i = 0; i < book.getSections().size(); i++) {
			Section section = book.getSections().get(i);
			writer.writeStartElement(NAMESPACE_NCX, "navPoint");
			writer.writeAttribute("id", "navPoint-" + (i + 1));
			writer.writeAttribute("playOrder", String.valueOf(i + 1));
			writer.writeAttribute("class", "chapter");
			writer.writeStartElement(NAMESPACE_NCX, "navLabel");
			writer.writeStartElement(NAMESPACE_NCX, "text");
			writer.writeCharacters(section.getName());
			writer.writeEndElement();
			writer.writeEndElement();
			writer.writeStartElement(NAMESPACE_NCX, "content");
			writer.writeCharacters(section.getHref());
			writer.writeEndElement();
			writer.writeEndElement();
		}
		writer.writeEndElement();
		writer.writeEndElement();
		writer.writeEndDocument();
	}
}
