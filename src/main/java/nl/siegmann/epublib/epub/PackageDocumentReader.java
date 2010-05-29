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
	
	
	public static void read(Resource packageResource, EpubReader epubReader, Book book, Map<String, Resource> resourcesByHref) throws UnsupportedEncodingException, SAXException, IOException, ParserConfigurationException {
		Document packageDocument = ResourceUtil.getAsDocument(packageResource, epubReader.getDocumentBuilderFactory());
		String packageHref = packageResource.getHref();
		resourcesByHref = fixHrefs(packageHref, resourcesByHref);
		String coverHref = readCover(packageDocument, book, resourcesByHref);
		Map<String, Resource> resourcesById = readManifest(packageDocument, packageHref, epubReader, book, resourcesByHref, coverHref);
		readMetadata(packageDocument, epubReader, book);
		List<Section> spineSections = readSpine(packageDocument, epubReader, book, resourcesById);
		book.setSpineSections(spineSections);
	}
	
	
	/**
	 * Strips off the package prefixes up to the href of the packageHref.
	 * Example:
	 * If the packageHref is "OEBPS/content.opf" then a resource href like "OEBPS/foo/bar.html" will be turned into "foo/bar.html"
	 * 
	 * @param packageHref
	 * @param resourcesByHref
	 * @return
	 */
	private static Map<String, Resource> fixHrefs(String packageHref,
			Map<String, Resource> resourcesByHref) {
		int lastSlashPos = packageHref.lastIndexOf('/');
		if(lastSlashPos < 0) {
			return resourcesByHref;
		}
		Map<String, Resource> result = new HashMap<String, Resource>();
		for(Resource resource: resourcesByHref.values()) {
			if(StringUtils.isNotBlank(resource.getHref())
					|| resource.getHref().length() > lastSlashPos) {
				resource.setHref(resource.getHref().substring(lastSlashPos + 1));
			}
			result.put(resource.getHref(), resource);
		}
		return result;
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
			if(resource == Resource.NULL_RESOURCE) {
				continue;
			}
			Section section = new Section(null, resource.getHref());
			result.add(section);
		}
		return result;
	}
	
	/**
	 * Search for the cover page in the meta tags and the guide references
	 * @param packageDocument
	 * @return
	 */
	// package
	static String findCoverHref(Document packageDocument) {
		
		// try and find a meta tag with name = 'cover' and href is not blank
		String result = getFindAttributeValue(packageDocument, NAMESPACE_OPF,
											OPFTags.meta, OPFAttributes.name, OPFValues.meta_cover,
											OPFAttributes.content);

		if(StringUtils.isBlank(result)) {
			// try and find a reference tag with type is 'cover' and reference is not blank
			result = getFindAttributeValue(packageDocument, NAMESPACE_OPF,
											OPFTags.reference, OPFAttributes.type, OPFValues.reference_cover,
											OPFAttributes.href);
		}

		if(StringUtils.isBlank(result)) {
			result = null;
		}
		return result;
	}

	/**
	 * Finds in the current document the first element with the given namespace and elementName and with the given findAttributeName and findAttributeValue.
	 * It then returns the value of the given resultAttributeName.
	 * 
	 * @param document
	 * @param namespace
	 * @param elementName
	 * @param findAttributeName
	 * @param findAttributeValue
	 * @param resultAttributeName
	 * @return
	 */
	private static String getFindAttributeValue(Document document, String namespace, String elementName, String findAttributeName, String findAttributeValue, String resultAttributeName) {
		NodeList metaTags = document.getElementsByTagNameNS(namespace, elementName);
		for(int i = 0; i < metaTags.getLength(); i++) {
			Element metaElement = (Element) metaTags.item(i);
			if(findAttributeValue.equalsIgnoreCase(metaElement.getAttribute(findAttributeName)) 
				&& StringUtils.isNotBlank(metaElement.getAttribute(resultAttributeName))) {
				return metaElement.getAttribute(resultAttributeName);
			}
		}
		return null;
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
	 * Finds the cover resource in the packageDocument and adds it to the book if found.
	 * Keeps the cover resource in the resources map
	 * @param packageDocument
	 * @param book
	 * @param resources
	 * @return
	 */
	private static String readCover(Document packageDocument, Book book, Map<String, Resource> resources) {
		
		String coverHref = findCoverHref(packageDocument);

		if(StringUtils.isNotBlank(coverHref) && resources.containsKey(coverHref)) {
			book.setCoverPage(resources.get(coverHref));
		}
		return coverHref;
	}
	
	
	/**
	 * 
	 * @param packageDocument
	 * @param packageHref
	 * @param epubReader
	 * @param book
	 * @param resourcesByHref
	 * @return a Map with resources, with their id's as key.
	 */
	private static Map<String, Resource> readManifest(Document packageDocument, String packageHref,
			EpubReader epubReader, Book book, Map<String, Resource> resourcesByHref, String coverHref) {
		Element manifestElement = getFirstElementByTagNameNS(packageDocument.getDocumentElement(), NAMESPACE_OPF, "manifest");
		if(manifestElement == null) {
			log.error("Package document does not contain element manifest");
			return Collections.<String, Resource>emptyMap();
		}
		NodeList itemElements = manifestElement.getElementsByTagName("item");
		Map<String, Resource> result = new HashMap<String, Resource>();
		for(int i = 0; i < itemElements.getLength(); i++) {
			Element itemElement = (Element) itemElements.item(i);
			String mediaTypeName = itemElement.getAttribute("media-type");
			String href = itemElement.getAttribute("href");
			String id = itemElement.getAttribute("id");
			Resource resource = resourcesByHref.remove(href);
			if(resource == null) {
				System.err.println("resource not found:" + href);
				continue;
			}
			MediaType mediaType = MediatypeService.getMediaTypeByName(mediaTypeName);
			if(mediaType != null) {
				resource.setMediaType(mediaType);
			}
			if(resource.getMediaType() == MediatypeService.NCX) {
				book.setNcxResource(resource);
			} else if(StringUtils.isNotBlank(coverHref)
				&& coverHref.equals(href)) {
				result.put(id, Resource.NULL_RESOURCE);
			} else {
				book.addResource(resource);
				result.put(id, resource);
			}
		}
		return result;
	}
}