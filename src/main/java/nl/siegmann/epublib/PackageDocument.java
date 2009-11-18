package nl.siegmann.epublib;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class PackageDocument {
	public static final String NAMESPACE_NCX = "http://www.idpf.org/2007/opf";
	
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
