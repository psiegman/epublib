package nl.siegmann.epublib;

import java.text.SimpleDateFormat;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang.StringUtils;

public class PackageDocument {
	public static final String NAMESPACE_OPF = "http://www.idpf.org/2007/opf";
	public static final String NAMESPACE_DUBLIN_CORE = "http://purl.org/dc/elements/1.1/";
	public static final String PREFIX_DUBLIN_CORE = "dc";
	public static final String dateFormat = "yyyy-MM-dd";
	
	public static void write(EpubWriter writeAction, XMLStreamWriter writer, Book book) throws XMLStreamException {
		writer.writeStartDocument(Constants.encoding, "1.0");
		writer.setDefaultNamespace(NAMESPACE_OPF);
		writer.writeStartElement(NAMESPACE_OPF, "package");
		writer.writeNamespace(PREFIX_DUBLIN_CORE, NAMESPACE_DUBLIN_CORE);
//		writer.writeNamespace("ncx", NAMESPACE_NCX);
		writer.writeAttribute("xmlns", NAMESPACE_OPF);
		writer.writeAttribute("version", "2.0");
		writer.writeAttribute("unique-identifier", "BookID");

		writer.writeStartElement(NAMESPACE_OPF, "metadata");
		
		writer.writeStartElement(NAMESPACE_DUBLIN_CORE, "identifier");
		writer.writeAttribute(NAMESPACE_DUBLIN_CORE, "id", "BookdID");
		writer.writeAttribute(NAMESPACE_OPF, "scheme", "UUID");
		writer.writeCharacters(book.getUid());
		writer.writeEndElement(); // dc:identifier

		writer.writeStartElement(NAMESPACE_DUBLIN_CORE, "title");
		writer.writeCharacters(book.getTitle());
		writer.writeEndElement(); // dc:title

		for(Author author: book.getAuthors()) {
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
		writer.writeCharacters((new SimpleDateFormat(dateFormat)).format(book.getDate()));
		writer.writeEndElement(); // dc:date

		if(StringUtils.isNotEmpty(book.getLanguage())) {
			writer.writeStartElement(NAMESPACE_DUBLIN_CORE, "language");
			writer.writeCharacters(book.getLanguage());
			writer.writeEndElement(); // dc:date
		}

		if(StringUtils.isNotEmpty(book.getRights())) {
			writer.writeStartElement(NAMESPACE_DUBLIN_CORE, "rights");
			writer.writeCharacters(book.getRights());
			writer.writeEndElement(); // dc:rights
		}

		writer.writeEndElement(); // dc:metadata

		writer.writeStartElement(NAMESPACE_OPF, "manifest");
		writer.writeStartElement(NAMESPACE_OPF, "item");
		writer.writeAttribute("id", writeAction.getNcxId());;
		writer.writeAttribute("href", writeAction.getNcxHref());
		writer.writeAttribute("media-type", writeAction.getNcxMediaType());
		writer.writeEndElement(); // item
		writer.writeEndElement(); // manifest

		writer.writeStartElement(NAMESPACE_OPF, "spine");
		writer.writeAttribute("toc", writeAction.getNcxId());;
		for(Section section: book.getSections()) {
			writer.writeEmptyElement(NAMESPACE_OPF, "itemref");
			writer.writeAttribute("idref", section.getId());;
		}
		writer.writeEndElement(); // spine

		writer.writeEndElement(); // package
		writer.writeEndDocument();
	}
		/*
 
 def writePackage(book) {
	new File(targetDir + File.separator + contentDir).mkdir()
	def packageWriter = new FileWriter(new File(targetDir + File.separator + contentDir + File.separator + 'content.opf'))
	def markupBuilder = new MarkupBuilder(packageWriter)
	markupBuilder.setDoubleQuotes(true)
	markupBuilder.'package'(xmlns: "http://www.idpf.org/2007/opf",  'unique-identifier': "BookID",  version: "2.0") {
		metadata('xmlns:dc': "http://purl.org/dc/elements/1.1/", 'xmlns:opf': "http://www.idpf.org/2007/opf") {
			'dc:identifier'(id: "BookID", 'opf:scheme': "UUID", book.uid)
			'dc:title' (book.title)
			book.authors.each() { author ->
				'dc:creator' ('opf:role' : "aut", 'opf:file-as': author.lastname + ', ' + author.firstname, author.firstname + ' ' + author.lastname)
			}
			book.subjects.each() { subject ->
				'dc:subject'(subject)
			}
			'dc:date' (book.date.format('yyyy-MM-dd'))
			'dc:language'(book.language)
			if (book.rights) {
				'dc:rights' (book.rights)
			}
		}
		manifest {
			item( id: "ncx", href: "toc.ncx", 'media-type': "application/x-dtbncx+xml")
			copyAndIndexContentFiles(markupBuilder, new File(inputHtmlDir))
		}
		spine (toc: 'ncx') {
			book.sections.each() {
				itemref(idref: it.id)
			}
		}
	}
}

 */
}
