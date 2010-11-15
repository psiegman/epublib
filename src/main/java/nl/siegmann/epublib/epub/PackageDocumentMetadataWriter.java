package nl.siegmann.epublib.epub;

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Date;
import nl.siegmann.epublib.domain.Identifier;

import org.apache.commons.lang.StringUtils;

public class PackageDocumentMetadataWriter extends PackageDocumentBase {

	/**
	 * Writes the book's metadata.
	 * 
	 * @param book
	 * @param writer
	 * @throws XMLStreamException
	 */
	public static void writeMetaData(Book book, XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartElement(NAMESPACE_OPF, OPFTags.metadata);
		writer.writeNamespace(PREFIX_DUBLIN_CORE, NAMESPACE_DUBLIN_CORE);
		writer.writeNamespace(PREFIX_OPF, NAMESPACE_OPF);
		
		writeIdentifiers(book.getMetadata().getIdentifiers(), writer);
		writeSimpleMetdataElements(DCTags.title, book.getMetadata().getTitles(), writer);
		writeSimpleMetdataElements(DCTags.subject, book.getMetadata().getSubjects(), writer);
		writeSimpleMetdataElements(DCTags.description, book.getMetadata().getDescriptions(), writer);
		writeSimpleMetdataElements(DCTags.publisher, book.getMetadata().getPublishers(), writer);
		writeSimpleMetdataElements(DCTags.type, book.getMetadata().getTypes(), writer);
		writeSimpleMetdataElements(DCTags.rights, book.getMetadata().getRights(), writer);

		// write authors
		for(Author author: book.getMetadata().getAuthors()) {
			writer.writeStartElement(NAMESPACE_DUBLIN_CORE, DCTags.creator);
			writer.writeAttribute(NAMESPACE_OPF, OPFAttributes.role, author.getRelator().getCode());
			writer.writeAttribute(NAMESPACE_OPF, OPFAttributes.file_as, author.getLastname() + ", " + author.getFirstname());
			writer.writeCharacters(author.getFirstname() + " " + author.getLastname());
			writer.writeEndElement(); // dc:creator
		}

		// write contributors
		for(Author author: book.getMetadata().getContributors()) {
			writer.writeStartElement(NAMESPACE_DUBLIN_CORE, DCTags.contributor);
			writer.writeAttribute(NAMESPACE_OPF, OPFAttributes.role, author.getRelator().getCode());
			writer.writeAttribute(NAMESPACE_OPF, OPFAttributes.file_as, author.getLastname() + ", " + author.getFirstname());
			writer.writeCharacters(author.getFirstname() + " " + author.getLastname());
			writer.writeEndElement(); // dc:contributor
		}
		
		// write dates
		for (Date date: book.getMetadata().getDates()) {
			writer.writeStartElement(NAMESPACE_DUBLIN_CORE, DCTags.date);
			if (date.getEvent() != null) {
				writer.writeAttribute(PREFIX_OPF, NAMESPACE_OPF, OPFAttributes.event, date.getEvent().toString());
			}
			writer.writeCharacters(date.getValue());
			writer.writeEndElement(); // dc:date
		}

		// write language
		if(StringUtils.isNotEmpty(book.getMetadata().getLanguage())) {
			writer.writeStartElement(NAMESPACE_DUBLIN_CORE, "language");
			writer.writeCharacters(book.getMetadata().getLanguage());
			writer.writeEndElement(); // dc:language
		}

		// write other properties
		if(book.getMetadata().getOtherProperties() != null) {
			for(Map.Entry<QName, String> mapEntry: book.getMetadata().getOtherProperties().entrySet()) {
				writer.writeStartElement(mapEntry.getKey().getNamespaceURI(), mapEntry.getKey().getLocalPart());
				writer.writeCharacters(mapEntry.getValue());
				writer.writeEndElement();
				
			}
		}

		// write coverimage
		if(book.getMetadata().getCoverImage() != null) { // write the cover image
			writer.writeEmptyElement(OPFTags.meta);
			writer.writeAttribute(OPFAttributes.name, OPFValues.meta_cover);
			writer.writeAttribute(OPFAttributes.content, book.getMetadata().getCoverImage().getId());
		}

		// write generator
		writer.writeEmptyElement(OPFTags.meta);
		writer.writeAttribute(OPFAttributes.name, OPFValues.generator);
		writer.writeAttribute(OPFAttributes.content, Constants.EPUBLIB_GENERATOR_NAME);
		
		writer.writeEndElement(); // dc:metadata
	}
	
	private static void writeSimpleMetdataElements(String tagName, List<String> values, XMLStreamWriter writer) throws XMLStreamException {
		for(String value: values) {
			if (StringUtils.isBlank(value)) {
				continue;
			}
			writer.writeStartElement(NAMESPACE_DUBLIN_CORE, tagName);
			writer.writeCharacters(value);
			writer.writeEndElement();
		}
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

}
