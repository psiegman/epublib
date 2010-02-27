package nl.siegmann.epublib.epub;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Section;

import org.apache.commons.lang.StringUtils;

/**
 * Writes the opf package document as defined by namespace http://www.idpf.org/2007/opf
 *  
 * @author paul
 *
 */
public class PackageDocument {
	public static final String BOOK_ID_ID = "BookId";
	public static final String NAMESPACE_OPF = "http://www.idpf.org/2007/opf";
	public static final String NAMESPACE_DUBLIN_CORE = "http://purl.org/dc/elements/1.1/";
	public static final String PREFIX_DUBLIN_CORE = "dc";
	public static final String dateFormat = "yyyy-MM-dd";
	
	public static void write(EpubWriter epubWriter, XMLStreamWriter writer, Book book) throws XMLStreamException {
		writer.writeStartDocument(Constants.ENCODING, "1.0");
		writer.setDefaultNamespace(NAMESPACE_OPF);
		writer.writeCharacters("\n");
		writer.writeStartElement(NAMESPACE_OPF, "package");
//		writer.writeNamespace(PREFIX_DUBLIN_CORE, NAMESPACE_DUBLIN_CORE);
//		writer.writeNamespace("ncx", NAMESPACE_NCX);
		writer.writeAttribute("xmlns", NAMESPACE_OPF);
		writer.writeAttribute("version", "2.0");
		writer.writeAttribute("unique-identifier", BOOK_ID_ID);

		writeMetaData(book, writer);

		writeManifest(book, epubWriter, writer);

		writeSpine(book, epubWriter, writer);

		writeGuide(book, epubWriter, writer);
		
		writer.writeEndElement(); // package
		writer.writeEndDocument();
	}

	/**
	 * Writes the book's metadata.
	 * 
	 * @param book
	 * @param writer
	 * @throws XMLStreamException
	 */
	private static void writeMetaData(Book book, XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartElement(NAMESPACE_OPF, "metadata");
		writer.writeNamespace("dc", NAMESPACE_DUBLIN_CORE);
		writer.writeNamespace("opf", NAMESPACE_OPF);
		
		writer.writeStartElement(NAMESPACE_DUBLIN_CORE, "identifier");
		writer.writeAttribute("id", BOOK_ID_ID);
		writer.writeAttribute(NAMESPACE_OPF, "scheme", book.getMetadata().getIdentifier().getScheme());
		writer.writeCharacters(book.getMetadata().getIdentifier().getValue());
		writer.writeEndElement(); // dc:identifier

		writer.writeStartElement(NAMESPACE_DUBLIN_CORE, "title");
		writer.writeCharacters(book.getMetadata().getTitle());
		writer.writeEndElement(); // dc:title
		
		for(Author author: book.getMetadata().getAuthors()) {
			writer.writeStartElement(NAMESPACE_DUBLIN_CORE, "creator");
			writer.writeAttribute(NAMESPACE_OPF, "role", "aut");
			writer.writeAttribute(NAMESPACE_OPF, "file-as", author.getLastname() + ", " + author.getFirstname());
			writer.writeCharacters(author.getFirstname() + " " + author.getLastname());
			writer.writeEndElement(); // dc:creator
		}

		for(String subject: book.getSubjects()) {
			writer.writeStartElement(NAMESPACE_DUBLIN_CORE, "subject");
			writer.writeCharacters(subject);
			writer.writeEndElement(); // dc:subject
		}

		writer.writeStartElement(NAMESPACE_DUBLIN_CORE, "date");
		writer.writeCharacters((new SimpleDateFormat(dateFormat)).format(book.getMetadata().getDate()));
		writer.writeEndElement(); // dc:date

		if(StringUtils.isNotEmpty(book.getMetadata().getLanguage())) {
			writer.writeStartElement(NAMESPACE_DUBLIN_CORE, "language");
			writer.writeCharacters(book.getMetadata().getLanguage());
			writer.writeEndElement(); // dc:language
		}

		if(StringUtils.isNotEmpty(book.getMetadata().getRights())) {
			writer.writeStartElement(NAMESPACE_DUBLIN_CORE, "rights");
			writer.writeCharacters(book.getMetadata().getRights());
			writer.writeEndElement(); // dc:rights
		}

		if(book.getMetadata().getOtherProperties() != null) {
			for(Map.Entry<QName, String> mapEntry: book.getMetadata().getOtherProperties().entrySet()) {
				writer.writeStartElement(mapEntry.getKey().getNamespaceURI(), mapEntry.getKey().getLocalPart());
				writer.writeCharacters(mapEntry.getValue());
				writer.writeEndElement();
				
			}
		}

		if(book.getCoverPage() != null) { // write the cover image
			writer.writeEmptyElement("meta");
			writer.writeAttribute("name", "cover");
			writer.writeAttribute("content", book.getCoverPage().getHref());
		}
		writer.writeEndElement(); // dc:metadata
	}


	/**
	 * Writes the package's spine.
	 * 
	 * @param book
	 * @param epubWriter
	 * @param writer
	 * @throws XMLStreamException
	 */
	private static void writeSpine(Book book, EpubWriter epubWriter, XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartElement("spine");
		writer.writeAttribute("toc", epubWriter.getNcxId());

		if(book.getCoverPage() != null) { // write the cover html file
			writer.writeEmptyElement("itemref");
			writer.writeAttribute("idref", book.getCoverPage().getId());
			writer.writeAttribute("linear", "no");
		}
		writeSections(book.getSections(), writer);
		writer.writeEndElement(); // spine
	}

	private static void writeManifest(Book book, EpubWriter epubWriter, XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartElement("manifest");

		writer.writeEmptyElement("item");
		writer.writeAttribute("id", epubWriter.getNcxId());
		writer.writeAttribute("href", epubWriter.getNcxHref());
		writer.writeAttribute("media-type", epubWriter.getNcxMediaType());

		writeCoverResources(book, writer);
		
		for(Resource resource: book.getResources()) {
			writeItem(resource, writer);
		}
		
		writer.writeEndElement(); // manifest
	}

	/**
	 * Writes a resources as an item element
	 * @param resource
	 * @param writer
	 * @throws XMLStreamException
	 */
	private static void writeItem(Resource resource, XMLStreamWriter writer)
			throws XMLStreamException {
		if(resource == null) {
			return;
		}
		writer.writeEmptyElement("item");
		writer.writeAttribute("id", resource.getId());
		writer.writeAttribute("href", resource.getHref());
		writer.writeAttribute("media-type", resource.getMediaType().getName());
	}

	/**
	 * Writes the cover resource items.
	 * 
	 * @param book
	 * @param writer
	 * @throws XMLStreamException
	 */
	private static void writeCoverResources(Book book, XMLStreamWriter writer) throws XMLStreamException {
		writeItem(book.getCoverImage(), writer);
		writeItem(book.getCoverPage(), writer);
	}

	/**
	 * Recursively list the entire section tree.
	 */
	private static void writeSections(List<Section> sections, XMLStreamWriter writer) throws XMLStreamException {
		for(Section section: sections) {
			if(section.isPartOfPageFlow()) {
				writer.writeEmptyElement("itemref");
				writer.writeAttribute("idref", section.getItemId());
			}
			if(section.getChildren() != null && ! section.getChildren().isEmpty()) {
				writeSections(section.getChildren(), writer);
			}
		}
	}

	private static void writeGuide(Book book, EpubWriter epubWriter, XMLStreamWriter writer) throws XMLStreamException {
		if(book.getCoverPage() == null) {
			return;
		}
		writer.writeStartElement("guide");
		writer.writeEmptyElement("reference");
		writer.writeAttribute("type", "cover");
		writer.writeAttribute("href", book.getCoverPage().getHref());
		writer.writeEndElement(); // guide
	}
}