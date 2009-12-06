package nl.siegmann.epublib;

import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class NCXDocument {
	
	public static final String NAMESPACE_NCX = "http://www.daisy.org/z3986/2005/ncx/";
	public static final String PREFIX_NCX = "ncx";

	public static void write(Book book, ZipOutputStream resultStream) throws IOException, XMLStreamException, FactoryConfigurationError {
		resultStream.putNextEntry(new ZipEntry("OEBPS/toc.ncx"));
		XMLStreamWriter out = XMLOutputFactory.newInstance().createXMLStreamWriter(resultStream);
		write(out, book);
		out.flush();
	}
	
	
	public static void write(XMLStreamWriter writer, Book book) throws XMLStreamException {
		writer.writeStartDocument(Constants.encoding, "1.0");
		writer.setDefaultNamespace(NAMESPACE_NCX);
		writer.writeStartElement(NAMESPACE_NCX, "ncx");
//		writer.writeNamespace("ncx", NAMESPACE_NCX);
		writer.writeAttribute("xmlns", NAMESPACE_NCX);
		writer.writeAttribute("version", "2005-1");
		writer.writeStartElement(NAMESPACE_NCX, "head");

		writer.writeEmptyElement(NAMESPACE_NCX, "meta");
		writer.writeAttribute("name", "dtb:uid");
		writer.writeAttribute("content", book.getUid());

		writer.writeEmptyElement(NAMESPACE_NCX, "meta");
		writer.writeAttribute("name", "dtb:depth");
		writer.writeAttribute("content", "1");

		writer.writeEmptyElement(NAMESPACE_NCX, "meta");
		writer.writeAttribute("name", "dtb:totalPageCount");
		writer.writeAttribute("content", "0");

		writer.writeEmptyElement(NAMESPACE_NCX, "meta");
		writer.writeAttribute("name", "dtb:maxPageNumber");
		writer.writeAttribute("content", "0");

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
		writeNavPoints(book.getSections(), 1, writer);
		writer.writeEndElement();
		writer.writeEndElement();
		writer.writeEndDocument();
	}


	private static int writeNavPoints(List<Section> sections, int playOrder,
			XMLStreamWriter writer) throws XMLStreamException {
		for(Section section: sections) {
			writer.writeStartElement(NAMESPACE_NCX, "navPoint");
			writer.writeAttribute("id", "navPoint-" + playOrder);
			writer.writeAttribute("playOrder", String.valueOf(playOrder));
			writer.writeAttribute("class", "chapter");
			writer.writeStartElement(NAMESPACE_NCX, "navLabel");
			writer.writeStartElement(NAMESPACE_NCX, "text");
			writer.writeCharacters(section.getName());
			writer.writeEndElement(); // text
			writer.writeEndElement(); // navLabel
			writer.writeEmptyElement(NAMESPACE_NCX, "content");
			writer.writeAttribute("src", section.getHref());
			playOrder++;
			if(! section.getChildren().isEmpty()) {
				playOrder = writeNavPoints(section.getChildren(), playOrder, writer);
			}
			writer.writeEndElement(); // navPoint
		}
		return playOrder;
	}
}
