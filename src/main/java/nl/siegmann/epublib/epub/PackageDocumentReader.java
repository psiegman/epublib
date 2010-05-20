package nl.siegmann.epublib.epub;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Identifier;
import nl.siegmann.epublib.domain.MediaType;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Section;
import nl.siegmann.epublib.service.MediatypeService;
import nl.siegmann.epublib.util.ResourceUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 * Reads the opf package document as defined by namespace http://www.idpf.org/2007/opf
 *  
 * @author paul
 *
 */
public class PackageDocumentReader extends PackageDocumentBase {
	
	private static final Logger log = Logger.getLogger(PackageDocumentReader.class);
	
	
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
}