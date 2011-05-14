package nl.siegmann.epublib.epub;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Identifier;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.domain.TableOfContents;
import nl.siegmann.epublib.service.MediatypeService;
import nl.siegmann.epublib.util.ResourceUtil;
import nl.siegmann.epublib.util.StringUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;

/**
 * Writes the ncx document as defined by namespace http://www.daisy.org/z3986/2005/ncx/
 * 
 * @author paul
 *
 */
public class NCXDocument {
	
	public static final String NAMESPACE_NCX = "http://www.daisy.org/z3986/2005/ncx/";
	public static final String PREFIX_NCX = "ncx";
	public static final String NCX_ITEM_ID = "ncx";
	public static final String DEFAULT_NCX_HREF = "toc.ncx";
	public static final String PREFIX_DTB = "dtb";
	
	private static final Logger log = LoggerFactory.getLogger(NCXDocument.class);

	private interface NCXTags {
		String ncx = "ncx";
		String meta = "meta";
		String navPoint = "navPoint";
		String navMap = "navMap";
		String navLabel = "navLabel";
		String content = "content";
		String text = "text";
		String docTitle = "docTitle";
		String docAuthor = "docAuthor";
		String head = "head";
	}
	
	private interface NCXAttributes {
		String src = "src";
		String name = "name";
		String content = "content";
		String id = "id";
		String playOrder = "playOrder";
		String clazz = "class";
		String version = "version";
	}

	private interface NCXAttributeValues {

		String chapter = "chapter";
		String version = "2005-1";
		
	}
	
	public static Resource read(Book book, EpubReader epubReader) {
		Resource result = null;
		if(book.getSpine().getTocResource() == null) {
			log.error("Book does not contain a table of contents file");
			return result;
		}
		try {
			result = book.getSpine().getTocResource();
			if(result == null) {
				return result;
			}
			Document ncxDocument = ResourceUtil.getAsDocument(result);
			Element navMapElement = DOMUtil.getFirstElementByTagNameNS(ncxDocument.getDocumentElement(), NAMESPACE_NCX, NCXTags.navMap);
			TableOfContents tableOfContents = new TableOfContents(readTOCReferences(navMapElement.getChildNodes(), book));
			book.setTableOfContents(tableOfContents);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return result;
	}
	
	private static List<TOCReference> readTOCReferences(NodeList navpoints, Book book) {
		if(navpoints == null) {
			return new ArrayList<TOCReference>();
		}
		List<TOCReference> result = new ArrayList<TOCReference>(navpoints.getLength());
		for(int i = 0; i < navpoints.getLength(); i++) {
			Node node = navpoints.item(i);
			if (node.getNodeType() != Document.ELEMENT_NODE) {
				continue;
			}
			if (! (node.getLocalName().equals(NCXTags.navPoint))) {
				continue;
			}
			TOCReference tocReference = readTOCReference((Element) node, book);
			result.add(tocReference);
		}
		return result;
	}

	private static TOCReference readTOCReference(Element navpointElement, Book book) {
		String label = readNavLabel(navpointElement);
		String reference = readNavReference(navpointElement);
		String href = StringUtil.substringBefore(reference, Constants.FRAGMENT_SEPARATOR_CHAR);
		String fragmentId = StringUtil.substringAfter(reference, Constants.FRAGMENT_SEPARATOR_CHAR);
		Resource resource = book.getResources().getByHref(href);
		if (resource == null) {
			log.error("Resource with href " + href + " in NCX document not found");
		}
		TOCReference result = new TOCReference(label, resource, fragmentId);
		readTOCReferences(navpointElement.getChildNodes(), book);
		result.setChildren(readTOCReferences(navpointElement.getChildNodes(), book));
		return result;
	}

	
	private static String readNavReference(Element navpointElement) {
		Element contentElement = DOMUtil.getFirstElementByTagNameNS(navpointElement, NAMESPACE_NCX, NCXTags.content);
		String result = DOMUtil.getAttribute(contentElement, NAMESPACE_NCX, NCXAttributes.src);
		try {
			result = URLDecoder.decode(result, Constants.ENCODING);
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage());
		}
		return result;
	}

	private static String readNavLabel(Element navpointElement) {
		Element navLabel = DOMUtil.getFirstElementByTagNameNS(navpointElement, NAMESPACE_NCX, NCXTags.navLabel);
		return DOMUtil.getTextChild(DOMUtil.getFirstElementByTagNameNS(navLabel, NAMESPACE_NCX, NCXTags.text));
	}

	
	public static void write(EpubWriter epubWriter, Book book, ZipOutputStream resultStream) throws IOException, XMLStreamException, FactoryConfigurationError {
		resultStream.putNextEntry(new ZipEntry(book.getSpine().getTocResource().getHref()));
		XmlSerializer out = epubWriter.createXmlSerializer(resultStream);
		write(out, book);
		out.flush();
	}
	

	/**
	 * Generates a resource containing an xml document containing the table of contents of the book in ncx format.
	 * 
	 * @param epubWriter
	 * @param book
	 * @return
	 * @
	 * @throws FactoryConfigurationError
	 * @throws IOException 
	 * @throws IllegalStateException 
	 * @throws IllegalArgumentException 
	 */
	public static Resource createNCXResource(EpubWriter epubWriter, Book book) throws IllegalArgumentException, IllegalStateException, IOException {
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		XmlSerializer out = epubWriter.createXmlSerializer(data);
		write(out, book);
		Resource resource = new Resource(NCX_ITEM_ID, data.toByteArray(), DEFAULT_NCX_HREF, MediatypeService.NCX);
		return resource;
	}
	
	
	public static void write(XmlSerializer serializer, Book book) throws IllegalArgumentException, IllegalStateException, IOException {
		serializer.startDocument(Constants.ENCODING, false);
		serializer.setPrefix(EpubWriter.EMPTY_NAMESPACE_PREFIX, NAMESPACE_NCX);
		serializer.startTag(NAMESPACE_NCX, NCXTags.ncx);
//		serializer.writeNamespace("ncx", NAMESPACE_NCX);
//		serializer.attribute("xmlns", NAMESPACE_NCX);
		serializer.attribute(EpubWriter.EMPTY_NAMESPACE_PREFIX, NCXAttributes.version, NCXAttributeValues.version);
		serializer.startTag(NAMESPACE_NCX, NCXTags.head);

		for(Identifier identifier: book.getMetadata().getIdentifiers()) {
			writeMetaElement(identifier.getScheme(), identifier.getValue(), serializer);
		}
		
		writeMetaElement("generator", Constants.EPUBLIB_GENERATOR_NAME, serializer);
		writeMetaElement("depth", String.valueOf(book.getTableOfContents().calculateDepth()), serializer);
		writeMetaElement("totalPageCount", "0", serializer);
		writeMetaElement("maxPageNumber", "0", serializer);

		serializer.endTag(NAMESPACE_NCX, "head");
		
		serializer.startTag(NAMESPACE_NCX, NCXTags.docTitle);
		serializer.startTag(NAMESPACE_NCX, NCXTags.text);
		// write the first title
		serializer.text(StringUtil.defaultIfNull(book.getTitle()));
		serializer.endTag(NAMESPACE_NCX, NCXTags.text);
		serializer.endTag(NAMESPACE_NCX, NCXTags.docTitle);
		
		for(Author author: book.getMetadata().getAuthors()) {
			serializer.startTag(NAMESPACE_NCX, NCXTags.docAuthor);
			serializer.startTag(NAMESPACE_NCX, NCXTags.text);
			serializer.text(author.getLastname() + ", " + author.getFirstname());
			serializer.endTag(NAMESPACE_NCX, NCXTags.text);
			serializer.endTag(NAMESPACE_NCX, NCXTags.docAuthor);
		}
		
		serializer.startTag(NAMESPACE_NCX, NCXTags.navMap);
		writeNavPoints(book.getTableOfContents().getTocReferences(), 1, serializer);
		serializer.endTag(NAMESPACE_NCX, NCXTags.navMap);
		
		serializer.endTag(NAMESPACE_NCX, "ncx");
		serializer.endDocument();
	}


	private static void writeMetaElement(String dtbName, String content, XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException  {
		serializer.startTag(NAMESPACE_NCX, NCXTags.meta);
		serializer.attribute(EpubWriter.EMPTY_NAMESPACE_PREFIX, NCXAttributes.name, PREFIX_DTB + ":" + dtbName);
		serializer.attribute(EpubWriter.EMPTY_NAMESPACE_PREFIX, NCXAttributes.content, content);
		serializer.endTag(NAMESPACE_NCX, NCXTags.meta);
	}
	
	private static int writeNavPoints(List<TOCReference> tocReferences, int playOrder,
			XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException  {
		for(TOCReference tocReference: tocReferences) {
			writeNavPointStart(tocReference, playOrder, serializer);
			playOrder++;
			if(! tocReference.getChildren().isEmpty()) {
				playOrder = writeNavPoints(tocReference.getChildren(), playOrder, serializer);
			}
			writeNavPointEnd(tocReference, serializer);
		}
		return playOrder;
	}


	private static void writeNavPointStart(TOCReference tocReference, int playOrder, XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException  {
		serializer.startTag(NAMESPACE_NCX, NCXTags.navPoint);
		serializer.attribute(EpubWriter.EMPTY_NAMESPACE_PREFIX, NCXAttributes.id, "navPoint-" + playOrder);
		serializer.attribute(EpubWriter.EMPTY_NAMESPACE_PREFIX, NCXAttributes.playOrder, String.valueOf(playOrder));
		serializer.attribute(EpubWriter.EMPTY_NAMESPACE_PREFIX, NCXAttributes.clazz, NCXAttributeValues.chapter);
		serializer.startTag(NAMESPACE_NCX, NCXTags.navLabel);
		serializer.startTag(NAMESPACE_NCX, NCXTags.text);
		serializer.text(tocReference.getTitle());
		serializer.endTag(NAMESPACE_NCX, NCXTags.text);
		serializer.endTag(NAMESPACE_NCX, NCXTags.navLabel);
		serializer.startTag(NAMESPACE_NCX, NCXTags.content);
		serializer.attribute(EpubWriter.EMPTY_NAMESPACE_PREFIX, NCXAttributes.src, tocReference.getCompleteHref());
		serializer.endTag(NAMESPACE_NCX, NCXTags.content);
	}

	private static void writeNavPointEnd(TOCReference tocReference, XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException  {
		serializer.endTag(NAMESPACE_NCX, NCXTags.navPoint);
	}
}
