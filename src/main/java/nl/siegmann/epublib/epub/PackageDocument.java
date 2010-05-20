package nl.siegmann.epublib.epub;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Identifier;
import nl.siegmann.epublib.domain.MediaType;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Section;
import nl.siegmann.epublib.service.MediatypeService;
import nl.siegmann.epublib.util.ResourceUtil;
import nl.siegmann.epublib.utilities.IndentingXMLStreamWriter;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

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
	
	private static final Logger log = Logger.getLogger(PackageDocument.class);
	
	private interface DCTags {
		String title = "title";
        String creator = "creator";
        String subject = "subject";
        String description = "description";
        String publisher = "publisher";
        String contributor = "contributor";
        String date = "date";
        String type = "type";
        String format = "format";
        String identifier = "identifier";
        String source = "source";
        String language = "language";
        String relation = "relation";
        String coverage = "coverage";
        String rights = "rights";
	}
	
	private interface OPFTags {
		String metadata = "metadata";
		String manifest = "manifest";
		String packageTag = "package";
		String itemref = "itemref";
	}
	
	private interface OPFAttributes {
		String uniqueIdentifier = "unique-identifier";
		String idref = "idref";
	}
	
	private interface DCAttributes {
		String scheme = "scheme";
	}
	
	
	public static void read(Resource packageResource, EpubReader epubReader, Book book, Map<String, Resource> resources) throws UnsupportedEncodingException, SAXException, IOException, ParserConfigurationException {
		Document packageDocument = ResourceUtil.getAsDocument(packageResource, epubReader.getDocumentBuilderFactory());
		String packageHref = packageResource.getHref();
		Map<String, Resource> resourcesById = readManifest(packageDocument, packageHref, epubReader, book, resources);
		readMetadata(packageDocument, epubReader, book);
		List<Section> spineSections = readSpine(packageDocument, epubReader, book, resourcesById);
		book.setSpineSections(spineSections);
	}
	
	private static List<Section> readSpine(Document packageDocument,
			EpubReader epubReader, Book book, Map<String, Resource> resourcesById) {

		NodeList spineNodes = packageDocument.getElementsByTagNameNS(NAMESPACE_OPF, OPFTags.itemref);
		List<Section> result = new ArrayList<Section>(spineNodes.getLength());
		for(int i = 0; i < spineNodes.getLength(); i++) {
			Element spineElement = (Element) spineNodes.item(i);
			String itemref = spineElement.getAttribute(OPFAttributes.idref);
			if(StringUtils.isBlank(itemref)) {
				log.error("itemref with missing or empty idref"); // XXX
				continue;
			}
			Resource resource = resourcesById.get(itemref);
			if(resource == null) {
				log.error("resource with id \'" + itemref + "\' not found");
				continue;
			}
			Section section = new Section(null, resource.getHref());
			result.add(section);
		}
		return result;
	}

	private static Element getFirstElementByTagNameNS(Element parentElement, String namespace, String tagName) {
		NodeList nodes = parentElement.getElementsByTagNameNS(namespace, tagName);
		if(nodes.getLength() == 0) {
			return null;
		}
		return (Element) nodes.item(0);
	}

	private static void readMetadata(Document packageDocument, EpubReader epubReader, Book book) {
		Metadata meta = book.getMetadata();
		Element metadataElement = getFirstElementByTagNameNS(packageDocument.getDocumentElement(), NAMESPACE_OPF, OPFTags.metadata);
		if(metadataElement == null) {
			log.error("Package does not contain element " + OPFTags.metadata);
			return;
		}
		meta.setTitles(getElementsTextChild(metadataElement, NAMESPACE_DUBLIN_CORE, DCTags.title));
		meta.setRights(getElementsTextChild(metadataElement, NAMESPACE_DUBLIN_CORE, DCTags.rights));
		meta.setIdentifiers(readIdentifiers(metadataElement));
		meta.setAuthors(readAuthors(metadataElement));
	}
	
	
	private static List<Author> readAuthors(Element metadataElement) {
		NodeList elements = metadataElement.getElementsByTagNameNS(NAMESPACE_DUBLIN_CORE, DCTags.creator);
		List<Author> result = new ArrayList<Author>(elements.getLength());
		for(int i = 0; i < elements.getLength(); i++) {
			String authorString = getTextChild((Element) elements.item(i));
			result.add(createAuthor(authorString));
		}
		return result;
		
	}

	private static Author createAuthor(String authorString) {
		int spacePos = authorString.lastIndexOf(' ');
		if(spacePos < 0) {
			return new Author(authorString);
		} else {
			return new Author(authorString.substring(0, spacePos), authorString.substring(spacePos + 1));
		}
	}
	
	
	private static List<Identifier> readIdentifiers(Element metadataElement) {
		NodeList identifierElements = metadataElement.getElementsByTagNameNS(NAMESPACE_DUBLIN_CORE, DCTags.identifier);
		if(identifierElements.getLength() == 0) {
			log.error("Package does not contain element " + DCTags.identifier);
			return new ArrayList<Identifier>();
		}
		String bookIdId = getBookIdId(metadataElement.getOwnerDocument());
		List<Identifier> result = new ArrayList<Identifier>(identifierElements.getLength());
		for(int i = 0; i < identifierElements.getLength(); i++) {
			Element identifierElement = (Element) identifierElements.item(i);
			String schemeName = identifierElement.getAttributeNS(NAMESPACE_OPF, DCAttributes.scheme);
			String identifierValue = getTextChild(identifierElement);
			Identifier identifier = new Identifier(schemeName, identifierValue);
			if(identifierElement.getAttribute("id").equals(bookIdId) ) {
				identifier.setBookId(true);
			}
			result.add(identifier);
		}
		return result;
	}

	private static String getBookIdId(Document document) {
		Element packageElement = getFirstElementByTagNameNS(document.getDocumentElement(), NAMESPACE_OPF, OPFTags.packageTag);
		if(packageElement == null) {
			return null;
		}
		String result = packageElement.getAttributeNS(NAMESPACE_OPF, OPFAttributes.uniqueIdentifier);
		return result;
	}
	
	
	private static String getFirstElementTextChild(Element parentElement, String namespace, String tagname) {
		Element element = getFirstElementByTagNameNS(parentElement, namespace, tagname);
		if(element == null) {
			return null;
		}
		return getTextChild(element);
	}

	private static List<String> getElementsTextChild(Element parentElement, String namespace, String tagname) {
		NodeList elements = parentElement.getElementsByTagNameNS(namespace, tagname);
		List<String> result = new ArrayList<String>(elements.getLength());
		for(int i = 0; i < elements.getLength(); i++) {
			result.add(getTextChild((Element) elements.item(i)));
		}
		return result;
	}
	
	private static String getTextChild(Element parentElement) {
		if(parentElement == null) {
			return null;
		}
		Text childContent = (Text) parentElement.getFirstChild();
		if(childContent == null) {
			return null;
		}
		return childContent.getData().trim();
	}

	/**
	 * 
	 * @param packageDocument
	 * @param packageHref
	 * @param epubReader
	 * @param book
	 * @param resources
	 * @return a Map with resources, with their id's as key.
	 */
	private static Map<String, Resource> readManifest(Document packageDocument, String packageHref,
			EpubReader epubReader, Book book, Map<String, Resource> resources) {
		Element manifestElement = getFirstElementByTagNameNS(packageDocument.getDocumentElement(), NAMESPACE_OPF, "manifest");
		if(manifestElement == null) {
			log.error("Package document does not contain element manifest");
			return Collections.<String, Resource>emptyMap();
		}
		NodeList itemElements = manifestElement.getElementsByTagName("item");
		String hrefPrefix = packageHref.substring(0, packageHref.lastIndexOf('/') + 1);
		Map<String, Resource> result = new HashMap<String, Resource>();
		for(int i = 0; i < itemElements.getLength(); i++) {
			Element itemElement = (Element) itemElements.item(i);
			String mediaTypeName = itemElement.getAttribute("media-type");
			String href = itemElement.getAttribute("href");
			String id = itemElement.getAttribute("id");
			href = hrefPrefix + href;
			Resource resource = resources.remove(href);
			if(resource == null) {
				System.err.println("resource not found:" + href);
				continue;
			}
			resource.setHref(resource.getHref().substring(hrefPrefix.length()));
			MediaType mediaType = MediatypeService.getMediaTypeByName(mediaTypeName);
			if(mediaType != null) {
				resource.setMediaType(mediaType);
			}
			if(resource.getMediaType() == MediatypeService.NCX) {
				book.setNcxResource(resource);
			} else {
				book.addResource(resource);
				result.put(id, resource);
			}
		}
		return result;
	}


	public static void write(EpubWriter epubWriter, XMLStreamWriter writer, Book book) throws XMLStreamException {
		writer = new IndentingXMLStreamWriter(writer);
		writer.writeStartDocument(Constants.ENCODING, "1.0");
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

		if(book.getCoverPage() != null) { // write the cover image
			writer.writeEmptyElement("meta");
			writer.writeAttribute("name", "cover");
			writer.writeAttribute("content", book.getCoverPage().getHref());
		}

		writer.writeEmptyElement("meta");
		writer.writeAttribute("name", "generator");
		writer.writeAttribute("content", Constants.EPUBLIB_GENERATOR_NAME);
		
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
		for(Resource resource: book.getResources()) {
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
		writeItem(book, book.getCoverImage(), writer);
		writeItem(book, book.getCoverPage(), writer);
	}

	/**
	 * Recursively list the entire section tree.
	 */
	private static void writeSections(List<Section> sections, XMLStreamWriter writer) throws XMLStreamException {
		for(Section section: sections) {
			if(section.isPartOfPageFlow()) {
				writer.writeEmptyElement(OPFTags.itemref);
				writer.writeAttribute(OPFAttributes.idref, section.getItemId());
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