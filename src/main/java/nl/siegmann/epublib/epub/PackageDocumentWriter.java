package nl.siegmann.epublib.epub;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.GuideReference;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Spine;
import nl.siegmann.epublib.domain.SpineReference;
import nl.siegmann.epublib.service.MediatypeService;
import nl.siegmann.epublib.utilities.IndentingXMLStreamWriter;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;

/**
 * Writes the opf package document as defined by namespace http://www.idpf.org/2007/opf
 *  
 * @author paul
 *
 */
public class PackageDocumentWriter extends PackageDocumentBase {

	private static final Logger log = LoggerFactory.getLogger(PackageDocumentWriter.class);

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

		PackageDocumentMetadataWriter.writeMetaData(book, writer);

		writeManifest(book, epubWriter, writer);

		writeSpine(book, epubWriter, writer);

		writeGuide(book, epubWriter, writer);
		
		writer.writeEndElement(); // package
		writer.writeEndDocument();
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
		writer.writeStartElement(OPFTags.spine);
		writer.writeAttribute(OPFAttributes.toc, book.getSpine().getTocResource().getId());

		if(book.getCoverPage() != null // there is a cover page
			&&	book.getSpine().findFirstResourceById(book.getCoverPage().getId()) < 0) { // cover page is not already in the spine
			// write the cover html file
			writer.writeEmptyElement("itemref");
			writer.writeAttribute("idref", book.getCoverPage().getId());
			writer.writeAttribute("linear", "no");
		}
		writeSpineItems(book.getSpine(), writer);
		writer.writeEndElement(); // spine
	}

	
	private static void writeManifest(Book book, EpubWriter epubWriter, XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartElement("manifest");

		writer.writeEmptyElement(OPFTags.item);
		writer.writeAttribute(OPFAttributes.id, epubWriter.getNcxId());
		writer.writeAttribute(OPFAttributes.href, epubWriter.getNcxHref());
		writer.writeAttribute(OPFAttributes.media_type, epubWriter.getNcxMediaType());

//		writeCoverResources(book, writer);
		
		for(Resource resource: getAllResourcesSortById(book)) {
			writeItem(book, resource, writer);
		}
		
		writer.writeEndElement(); // manifest
	}

	private static List<Resource> getAllResourcesSortById(Book book) {
		List<Resource> allResources = new ArrayList<Resource>(book.getResources().getAll());
		Collections.sort(allResources, new Comparator<Resource>() {

			@Override
			public int compare(Resource resource1, Resource resource2) {
				return resource1.getId().compareToIgnoreCase(resource2.getId());
			}
		});
		return allResources;
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
				&& book.getSpine().getTocResource() != null)) {
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
		writer.writeEmptyElement(OPFTags.item);
		writer.writeAttribute(OPFAttributes.id, resource.getId());
		writer.writeAttribute(OPFAttributes.href, resource.getHref());
		writer.writeAttribute(OPFAttributes.media_type, resource.getMediaType().getName());
	}

	/**
	 * Writes the cover resource items.
	 * 
	 * @param book
	 * @param writer
	 * @throws XMLStreamException
	 */
	private static void writeCoverResources(Book book, XMLStreamWriter writer) throws XMLStreamException {
		writeItem(book, book.getCoverImage(), writer);
		writeItem(book, book.getCoverPage(), writer);
	}

	/**
	 * List all spine references
	 */
	private static void writeSpineItems(Spine spine, XMLStreamWriter writer) throws XMLStreamException {
		for(SpineReference spineReference: spine.getSpineReferences()) {
			writer.writeEmptyElement(OPFTags.itemref);
			writer.writeAttribute(OPFAttributes.idref, spineReference.getResourceId());
			if (! spineReference.isLinear()) {
				writer.writeAttribute(OPFAttributes.linear, OPFValues.no);
			}
		}
	}

	private static void writeGuide(Book book, EpubWriter epubWriter, XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartElement(OPFTags.guide);
		for (GuideReference reference: book.getGuide().getReferences()) {
			writeGuideReference(reference, writer);
		}
		writer.writeEndElement(); // guide
	}
	
	private static void writeGuideReference(GuideReference reference, XMLStreamWriter writer) throws XMLStreamException {
		if (reference == null) {
			return;
		}
		writer.writeEmptyElement(OPFTags.reference);
		writer.writeAttribute(OPFAttributes.type, reference.getType());
		writer.writeAttribute(OPFAttributes.href, reference.getCompleteHref());
		if (StringUtils.isNotBlank(reference.getTitle())) {
			writer.writeAttribute(OPFAttributes.title, reference.getTitle());
		}
	}
}