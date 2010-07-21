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
import nl.siegmann.epublib.domain.Identifier;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Section;
import nl.siegmann.epublib.service.MediatypeService;
import nl.siegmann.epublib.utilities.IndentingXMLStreamWriter;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Writes the opf package document as defined by namespace http://www.idpf.org/2007/opf
 *  
 * @author paul
 *
 */
public class PackageDocumentWriter extends PackageDocumentBase {

	private static final Logger log = Logger.getLogger(PackageDocumentWriter.class);

	public static void write(EpubWriter epubWriter, XMLStreamWriter writer, Book book) throws XMLStreamException {
		writer = new IndentingXMLStreamWriter(writer);
		writer.writeStartDocument(Constants.ENCODING.name(), "1.0");
		writer.setDefaultNamespace(NAMESPACE_OPF);
		writer.writeCharacters("\n");
		writer.writeStartElement(NAMESPACE_OPF, OPFTags.packageTag);
//		writer.writeNamespace(PREFIX_DUBLIN_CORE, NAMESPACE_DUBLIN_CORE);
//		writer.writeNamespace("ncx", NAMESPACE_NCX);
		writer.writeAttribute("xmlns", NAMESPACE_OPF);
		writer.writeAttribute("version", "2.0");
		writer.writeAttribute(OPFAttributes.uniqueIdentifier, BOOK_ID_ID);

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
		writer.writeStartElement(NAMESPACE_OPF, OPFTags.metadata);
		writer.writeNamespace("dc", NAMESPACE_DUBLIN_CORE);
		writer.writeNamespace("opf", NAMESPACE_OPF);
		
		writeIdentifiers(book.getMetadata().getIdentifiers(), writer);
		
		for(String title: book.getMetadata().getTitles()) {
			writer.writeStartElement(NAMESPACE_DUBLIN_CORE, DCTags.title);
			writer.writeCharacters(title);
			writer.writeEndElement(); // dc:title
		}
		
		for(Author author: book.getMetadata().getAuthors()) {
			writer.writeStartElement(NAMESPACE_DUBLIN_CORE, "creator");
			writer.writeAttribute(NAMESPACE_OPF, "role", "aut");
			writer.writeAttribute(NAMESPACE_OPF, "file-as", author.getLastname() + ", " + author.getFirstname());
			writer.writeCharacters(author.getFirstname() + " " + author.getLastname());
			writer.writeEndElement(); // dc:creator
		}

		for(String subject: book.getMetadata().getSubjects()) {
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

		for(String right: book.getMetadata().getRights()) {
			if(StringUtils.isNotEmpty(right)) {
				writer.writeStartElement(NAMESPACE_DUBLIN_CORE, "rights");
				writer.writeCharacters(right);
				writer.writeEndElement(); // dc:rights
			}
		}
		
		
		if(book.getMetadata().getOtherProperties() != null) {
			for(Map.Entry<QName, String> mapEntry: book.getMetadata().getOtherProperties().entrySet()) {
				writer.writeStartElement(mapEntry.getKey().getNamespaceURI(), mapEntry.getKey().getLocalPart());
				writer.writeCharacters(mapEntry.getValue());
				writer.writeEndElement();
				
			}
		}

		if(book.getCoverImage() != null) { // write the cover image
			writer.writeEmptyElement(OPFTags.meta);
			writer.writeAttribute(OPFAttributes.name, OPFValues.meta_cover);
			writer.writeAttribute(OPFAttributes.content, book.getCoverImage().getHref());
		}

		writer.writeEmptyElement(OPFTags.meta);
		writer.writeAttribute(OPFAttributes.name, "generator");
		writer.writeAttribute(OPFAttributes.content, Constants.EPUBLIB_GENERATOR_NAME);
		
		writer.writeEndElement(); // dc:metadata
	}


	/**
	 * Writes out the complete list of Identifiers to the package document.
	 * The first identifier for which the bookId is true is made the bookId identifier.
	 * If no identifier has bookId == true then the first bookId identifier is written as the primary.
	 * 
	 * @param identifiers
	 * @param writer
	 * @throws XMLStreamException
	 */
	private static void writeIdentifiers(List<Identifier> identifiers, XMLStreamWriter writer) throws XMLStreamException {
		Identifier bookIdIdentifier = Identifier.getBookIdIdentifier(identifiers);
		if(bookIdIdentifier == null) {
			return;
		}
		
		writer.writeStartElement(NAMESPACE_DUBLIN_CORE, DCTags.identifier);
		writer.writeAttribute("id", BOOK_ID_ID);
		writer.writeAttribute(NAMESPACE_OPF, "scheme", bookIdIdentifier.getScheme());
		writer.writeCharacters(bookIdIdentifier.getValue());
		writer.writeEndElement(); // dc:identifier

		for(Identifier identifier: identifiers.subList(1, identifiers.size())) {
			if(identifier == bookIdIdentifier) {
				continue;
			}
			writer.writeStartElement(NAMESPACE_DUBLIN_CORE, DCTags.identifier);
			writer.writeAttribute(NAMESPACE_OPF, "scheme", identifier.getScheme());
			writer.writeCharacters(identifier.getValue());
			writer.writeEndElement(); // dc:identifier
		}
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
		writeSections(book.getSpineSections(), writer);
		writer.writeEndElement(); // spine
	}

	
	private static void writeManifest(Book book, EpubWriter epubWriter, XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartElement("manifest");

		writer.writeEmptyElement("item");
		writer.writeAttribute("id", epubWriter.getNcxId());
		writer.writeAttribute("href", epubWriter.getNcxHref());
		writer.writeAttribute("media-type", epubWriter.getNcxMediaType());

		writeCoverResources(book, writer);
		writeItem(book, book.getNcxResource(), writer);
		for(Resource resource: book.getResources().getAll()) {
			writeItem(book, resource, writer);
		}
		
		writer.writeEndElement(); // manifest
	}

	/**
	 * Writes a resources as an item element
	 * @param resource
	 * @param writer
	 * @throws XMLStreamException
	 */
	private static void writeItem(Book book, Resource resource, XMLStreamWriter writer)
			throws XMLStreamException {
		if(resource == null ||
				(resource.getMediaType() == MediatypeService.NCX
				&& book.getNcxResource() != null)) {
			return;
		}
		if(StringUtils.isBlank(resource.getId())) {
			log.error("resource id must not be empty (href: " + resource.getHref() + ", mediatype:" + resource.getMediaType() + ")");
			return;
		}
		if(StringUtils.isBlank(resource.getHref())) {
			log.error("resource href must not be empty (id: " + resource.getId() + ", mediatype:" + resource.getMediaType() + ")");
			return;
		}
		if(resource.getMediaType() == null) {
			log.error("resource mediatype must not be empty (id: " + resource.getId() + ", href:" + resource.getHref() + ")");
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
		writeItem(book, book.getMetadata().getCoverImage(), writer);
		writeItem(book, book.getMetadata().getCoverPage(), writer);
	}

	/**
	 * Recursively list the entire section tree.
	 */
	private static void writeSections(List<Section> sections, XMLStreamWriter writer) throws XMLStreamException {
		for(Section section: sections) {
			writer.writeEmptyElement(OPFTags.itemref);
			writer.writeAttribute(OPFAttributes.idref, section.getItemId());
			if(section.getChildren() != null && ! section.getChildren().isEmpty()) {
				writeSections(section.getChildren(), writer);
			}
		}
	}

	private static void writeGuide(Book book, EpubWriter epubWriter, XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartElement(OPFTags.guide);
		if(book.getMetadata().getCoverPage() != null) {
			writer.writeEmptyElement(OPFTags.reference);
			writer.writeAttribute(OPFAttributes.type, OPFValues.reference_cover);
			writer.writeAttribute(OPFAttributes.href, book.getMetadata().getCoverPage().getHref());
		}
		for (Reference reference: book.getMetadata().getGuide().getReferences()) {
			writer.writeEmptyElement(OPFTags.reference);
			writer.writeAttribute(OPFAttributes.type, reference.getType());
			writer.writeAttribute(OPFAttributes.href, reference.getResource().getHref());
			if (StringUtils.isNotBlank(reference.getTitle())) {
				writer.writeAttribute(OPFAttributes.title, reference.getTitle());
			}
		}
		writer.writeEndElement(); // guide
	}
}