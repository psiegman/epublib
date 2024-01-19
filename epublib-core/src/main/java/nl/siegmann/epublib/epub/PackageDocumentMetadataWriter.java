package nl.siegmann.epublib.epub;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Date;
import nl.siegmann.epublib.domain.Identifier;
import nl.siegmann.epublib.domain.Relator;
import nl.siegmann.epublib.domain.Title;
import nl.siegmann.epublib.util.StringUtil;
import org.xmlpull.v1.XmlSerializer;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class PackageDocumentMetadataWriter extends PackageDocumentBase {

	
	/**
	 * Writes the book's metadata.
	 */
	public static void writeMetaData(Book book, XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException  {
        serializer.setPrefix(PREFIX_DUBLIN_CORE, NAMESPACE_DUBLIN_CORE);

        serializer.startTag(NAMESPACE_OPF, OPFTags.metadata);

        serializer.setPrefix(PREFIX_DUBLIN_CORE, NAMESPACE_DUBLIN_CORE);
		serializer.setPrefix(PREFIX_OPF, NAMESPACE_OPF);

		if(isEpub3(book)) {
			writeIdentifiersEpub3(book.getMetadata().getIdentifiers(), serializer);
		} else {
			writeIdentifiersEpub2(book.getMetadata().getIdentifiers(), serializer);
		}
		writeTitles(book.getMetadata().getTitles(), serializer);
		writeSimpleMetadataElements(DCTags.subject, book.getMetadata().getSubjects(), serializer);
		writeSimpleMetadataElements(DCTags.description, book.getMetadata().getDescriptions(), serializer);
		writeSimpleMetadataElements(DCTags.publisher, book.getMetadata().getPublishers(), serializer);
		writeSimpleMetadataElements(DCTags.type, book.getMetadata().getTypes(), serializer);
		writeSimpleMetadataElements(DCTags.rights, book.getMetadata().getRights(), serializer);

		// write authors
		int countAuthors = 1;
		for(Author author: book.getMetadata().getAuthors()) {
			if(isEpub3(book)){
				writeAuthorEpub3Syntax(serializer, author, DCTags.creator, countAuthors++);
			} else {
				writeAuthorEpub2Syntax(serializer, author, DCTags.creator);
			}
		}

		// write contributors
		countAuthors = 1;
		for(Author author: book.getMetadata().getContributors()) {
			if(isEpub3(book)){
				writeAuthorEpub3Syntax(serializer, author, DCTags.contributor, countAuthors++);
			} else {
				writeAuthorEpub2Syntax(serializer, author, DCTags.contributor);
			}
		}
		
		// write dates
		for (Date date: book.getMetadata().getDates()) {
            serializer.setPrefix(PREFIX_OPF, NAMESPACE_OPF);
            serializer.startTag(NAMESPACE_DUBLIN_CORE, DCTags.date);
			if (date.getEvent() != null) {
				serializer.attribute(NAMESPACE_OPF, OPFAttributes.event, date.getEvent().toString());
			}
			serializer.text(date.getValue());
			serializer.endTag(NAMESPACE_DUBLIN_CORE, DCTags.date);
		}

		// write language
		if(StringUtil.isNotBlank(book.getMetadata().getLanguage())) {
			serializer.startTag(NAMESPACE_DUBLIN_CORE, "language");
			serializer.text(book.getMetadata().getLanguage());
			serializer.endTag(NAMESPACE_DUBLIN_CORE, "language");
		}

		// write other properties
		if(book.getMetadata().getOtherProperties() != null) {
			for(Map.Entry<QName, String> mapEntry: book.getMetadata().getOtherProperties().entrySet()) {
				String namespaceURI = mapEntry.getKey().getNamespaceURI();
				serializer.startTag(
						StringUtil.isNotBlank(namespaceURI) ? namespaceURI : NAMESPACE_OPF,
						OPFTags.meta
				);
				serializer.attribute(EpubWriter.EMPTY_NAMESPACE_PREFIX, OPFAttributes.property, mapEntry.getKey().getLocalPart());
				serializer.text(mapEntry.getValue());
				serializer.endTag(
						StringUtil.isNotBlank(namespaceURI) ? namespaceURI : NAMESPACE_OPF,
						OPFTags.meta
				);
				
			}
		}

		// write cover image
		if(book.getCoverImage() != null) { // write the cover image
			serializer.startTag(NAMESPACE_OPF, OPFTags.meta);
			serializer.attribute(EpubWriter.EMPTY_NAMESPACE_PREFIX, OPFAttributes.name, OPFValues.meta_cover);
			serializer.attribute(EpubWriter.EMPTY_NAMESPACE_PREFIX, OPFAttributes.content, book.getCoverImage().getId());
			serializer.endTag(NAMESPACE_OPF, OPFTags.meta);
		}

		// write generator
		serializer.startTag(NAMESPACE_OPF, OPFTags.meta);
		serializer.attribute(EpubWriter.EMPTY_NAMESPACE_PREFIX, OPFAttributes.name, OPFValues.generator);
		serializer.attribute(EpubWriter.EMPTY_NAMESPACE_PREFIX, OPFAttributes.content, Constants.EPUBLIB_GENERATOR_NAME);
		serializer.endTag(NAMESPACE_OPF, OPFTags.meta);
		
		serializer.endTag(NAMESPACE_OPF, OPFTags.metadata);
	}

	private static void writeTitles(List<Title> titles, final XmlSerializer serializer) throws IOException {
		int counter = 0;
		for (Title title : titles) {
			writeTitle(title, serializer, counter++);
		}
	}

	private static void writeTitle(Title title, XmlSerializer serializer, int counter) throws IOException {
		String titleId = DCTags.title + counter;
		serializer.startTag(NAMESPACE_DUBLIN_CORE, DCTags.title);
		serializer.attribute(EpubWriter.EMPTY_NAMESPACE_PREFIX, OPFAttributes.id, titleId);
		serializer.text(title.getValue());
		serializer.endTag(NAMESPACE_DUBLIN_CORE, DCTags.title);
		if(StringUtil.isNotBlank(title.getType())){
			serializer.startTag(NAMESPACE_OPF, OPFTags.meta);
			serializer.attribute(EpubWriter.EMPTY_NAMESPACE_PREFIX, "refines", "#" + titleId);
			serializer.attribute(EpubWriter.EMPTY_NAMESPACE_PREFIX, OPFAttributes.property, OPFAttributes.title_type);
			serializer.text(title.getType());
			serializer.endTag(NAMESPACE_OPF, OPFTags.meta);
		}
	}

	private static boolean isEpub3(Book book) {
		return null != book.getOpfResource() && book.getOpfResource().getVersion().equals("3.0");
	}

	private static void writeAuthorEpub2Syntax(XmlSerializer serializer, Author author, String creator) throws IOException {
        serializer.setPrefix(PREFIX_OPF, NAMESPACE_OPF);
        serializer.startTag(NAMESPACE_DUBLIN_CORE, creator);
		if(null != author.getRelator()) {
            serializer.attribute(NAMESPACE_OPF, OPFAttributes.role, author.getRelator().getCode());
        }
		serializer.attribute(NAMESPACE_OPF, OPFAttributes.file_as, author.getLastname() + ", " + author.getFirstname());
		serializer.text(author.getFirstname() + " " + author.getLastname());
		serializer.endTag(NAMESPACE_DUBLIN_CORE, creator);
	}

	private static void writeAuthorEpub3Syntax(XmlSerializer serializer, Author author, String creator, int countAuthors) throws IOException {
		String authorId = creator + countAuthors;
		serializer.startTag(NAMESPACE_DUBLIN_CORE, creator);
		serializer.attribute(EpubWriter.EMPTY_NAMESPACE_PREFIX, OPFAttributes.id, authorId);
		serializer.text(author.getFirstname() + " " + author.getLastname());
		serializer.endTag(NAMESPACE_DUBLIN_CORE, creator);

		if(!(
                null == author.getScheme()
                        && (null == author.getRelator() || Relator.AUTHOR.getName().equals(author.getRelator().getCode()))
        )){
			serializer.startTag(NAMESPACE_OPF, OPFTags.meta);
			serializer.attribute(EpubWriter.EMPTY_NAMESPACE_PREFIX, "refines", "#" + authorId);
			serializer.attribute(EpubWriter.EMPTY_NAMESPACE_PREFIX, OPFAttributes.property, OPFAttributes.role);
			if(null != author.getScheme()) {
				serializer.attribute(EpubWriter.EMPTY_NAMESPACE_PREFIX, OPFAttributes.scheme, author.getScheme().getName());
			}
			serializer.text(author.getRelator().getCode());
			serializer.endTag(NAMESPACE_OPF, OPFTags.meta);
		}


		serializer.startTag(NAMESPACE_OPF, OPFTags.meta);
		serializer.attribute(EpubWriter.EMPTY_NAMESPACE_PREFIX, "refines", "#" + authorId);
		serializer.attribute(EpubWriter.EMPTY_NAMESPACE_PREFIX, OPFAttributes.property, OPFAttributes.file_as);
		serializer.text(author.getLastname() + ", " + author.getFirstname());
		serializer.endTag(NAMESPACE_OPF, OPFTags.meta);

	}

	private static void writeSimpleMetadataElements(String tagName, List<String> values, XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException {
		for(String value: values) {
			if (StringUtil.isBlank(value)) {
				continue;
			}
			serializer.startTag(NAMESPACE_DUBLIN_CORE, tagName);
			serializer.text(value);
			serializer.endTag(NAMESPACE_DUBLIN_CORE, tagName);
		}
	}

	
	/**
	 * Writes out the complete list of Identifiers to the package document.
	 * The first identifier for which the bookId is true is made the bookId identifier.
	 * If no identifier has bookId == true then the first bookId identifier is written as the primary.
	 */
	private static void writeIdentifiersEpub2(List<Identifier> identifiers, XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException  {
		Identifier bookIdIdentifier = Identifier.getBookIdIdentifier(identifiers);
		if(bookIdIdentifier == null) {
			return;
		}

		serializer.startTag(NAMESPACE_DUBLIN_CORE, DCTags.identifier);
		serializer.attribute(EpubWriter.EMPTY_NAMESPACE_PREFIX, DCAttributes.id, BOOK_ID_ID);
		serializer.attribute(NAMESPACE_OPF, OPFAttributes.scheme, bookIdIdentifier.getScheme().getName());
		serializer.text(bookIdIdentifier.getValue());
		serializer.endTag(NAMESPACE_DUBLIN_CORE, DCTags.identifier);

		for(Identifier identifier: identifiers.subList(1, identifiers.size())) {
			if(identifier == bookIdIdentifier) {
				continue;
			}
			serializer.startTag(NAMESPACE_DUBLIN_CORE, DCTags.identifier);
			if(null != identifier.getScheme() && StringUtil.isNotBlank(identifier.getScheme().getName())) {
                serializer.attribute(NAMESPACE_OPF, "scheme", identifier.getScheme().getName());
            }
			serializer.text(identifier.getValue());
			serializer.endTag(NAMESPACE_DUBLIN_CORE, DCTags.identifier);
		}
	}

	/**
	 * Writes out the complete list of Identifiers to the package document.
	 * The first identifier for which the bookId is true is made the bookId identifier.
	 * If no identifier has bookId == true then the first bookId identifier is written as the primary.
	 */
	private static void writeIdentifiersEpub3(List<Identifier> identifiers, XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException  {
		Identifier bookIdIdentifier = Identifier.getBookIdIdentifier(identifiers);
		if(bookIdIdentifier == null) {
			return;
		}

		writeIdentifier(serializer, bookIdIdentifier, 0);

		int idCount = 1;
		for(Identifier identifier: identifiers.subList(1, identifiers.size())) {
			if(identifier == bookIdIdentifier) {
				continue;
			}
			writeIdentifier(serializer, bookIdIdentifier, idCount++);
		}
	}

	private static void writeIdentifier(XmlSerializer serializer, Identifier bookIdIdentifier, int counter) throws IOException {
		String bookId = (counter > 0) ? BOOK_ID_ID.concat(String.valueOf(counter)) : BOOK_ID_ID;
		serializer.startTag(NAMESPACE_DUBLIN_CORE, DCTags.identifier);
		serializer.attribute(EpubWriter.EMPTY_NAMESPACE_PREFIX, DCAttributes.id, bookId);
		serializer.text(bookIdIdentifier.getValue());
		serializer.endTag(NAMESPACE_DUBLIN_CORE, DCTags.identifier);

		String schemeValue = bookIdIdentifier.getScheme().getValue();
		if(StringUtil.isNotBlank(schemeValue)) {
			serializer.startTag(NAMESPACE_OPF, OPFTags.meta);
			serializer.attribute(EpubWriter.EMPTY_NAMESPACE_PREFIX, OPFAttributes.refines, "#" + bookId);
			serializer.attribute(EpubWriter.EMPTY_NAMESPACE_PREFIX, OPFAttributes.property, OPFAttributes.identifier_type);
			serializer.attribute(EpubWriter.EMPTY_NAMESPACE_PREFIX, OPFAttributes.scheme, bookIdIdentifier.getScheme().getName());
			serializer.text(schemeValue);
			serializer.endTag(NAMESPACE_OPF, OPFTags.meta);
		}
	}

}
