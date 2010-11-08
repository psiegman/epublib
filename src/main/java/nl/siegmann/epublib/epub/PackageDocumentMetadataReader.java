package nl.siegmann.epublib.epub;

import java.util.ArrayList;
import java.util.List;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Date;
import nl.siegmann.epublib.domain.Identifier;
import nl.siegmann.epublib.domain.Metadata;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Reads the package document metadata.
 * 
 * In its own separate class because the PackageDocumentReader became a bit large and unwieldy.
 * 
 * @author paul
 *
 */
// package
class PackageDocumentMetadataReader extends PackageDocumentBase {

	private static final Logger log = Logger.getLogger(PackageDocumentMetadataReader.class);

	public static Metadata readMetadata(Document packageDocument) {
		Metadata result = new Metadata();
		Element metadataElement = DOMUtil.getFirstElementByTagNameNS(packageDocument.getDocumentElement(), NAMESPACE_OPF, OPFTags.metadata);
		if(metadataElement == null) {
			log.error("Package does not contain element " + OPFTags.metadata);
			return result;
		}
		result.setTitles(DOMUtil.getElementsTextChild(metadataElement, NAMESPACE_DUBLIN_CORE, DCTags.title));
		result.setPublishers(DOMUtil.getElementsTextChild(metadataElement, NAMESPACE_DUBLIN_CORE, DCTags.publisher));
		result.setDescriptions(DOMUtil.getElementsTextChild(metadataElement, NAMESPACE_DUBLIN_CORE, DCTags.description));
		result.setRights(DOMUtil.getElementsTextChild(metadataElement, NAMESPACE_DUBLIN_CORE, DCTags.rights));
		result.setTypes(DOMUtil.getElementsTextChild(metadataElement, NAMESPACE_DUBLIN_CORE, DCTags.type));
		result.setSubjects(DOMUtil.getElementsTextChild(metadataElement, NAMESPACE_DUBLIN_CORE, DCTags.subject));
		result.setIdentifiers(readIdentifiers(metadataElement));
		result.setAuthors(readCreators(metadataElement));
		result.setContributors(readContributors(metadataElement));
		result.setDates(readDates(metadataElement));
		return result;
	}
	

	private static String getBookIdId(Document document) {
		Element packageElement = DOMUtil.getFirstElementByTagNameNS(document.getDocumentElement(), NAMESPACE_OPF, OPFTags.packageTag);
		if(packageElement == null) {
			return null;
		}
		String result = packageElement.getAttributeNS(NAMESPACE_OPF, OPFAttributes.uniqueIdentifier);
		return result;
	}
	
	private static List<Author> readCreators(Element metadataElement) {
		return readAuthors(DCTags.creator, metadataElement);
	}
	
	private static List<Author> readContributors(Element metadataElement) {
		return readAuthors(DCTags.contributor, metadataElement);
	}
	
	private static List<Author> readAuthors(String authorTag, Element metadataElement) {
		NodeList elements = metadataElement.getElementsByTagNameNS(NAMESPACE_DUBLIN_CORE, authorTag);
		List<Author> result = new ArrayList<Author>(elements.getLength());
		for(int i = 0; i < elements.getLength(); i++) {
			Element authorElement = (Element) elements.item(i);
			Author author = createAuthor(authorElement);
			if (author != null) {
				result.add(author);
			}
		}
		return result;
		
	}

	private static List<Date> readDates(Element metadataElement) {
		NodeList elements = metadataElement.getElementsByTagNameNS(NAMESPACE_DUBLIN_CORE, DCTags.date);
		List<Date> result = new ArrayList<Date>(elements.getLength());
		for(int i = 0; i < elements.getLength(); i++) {
			Element dateElement = (Element) elements.item(i);
			Date date;
			try {
				date = new Date(DOMUtil.getTextChild(dateElement), dateElement.getAttributeNS(NAMESPACE_OPF, OPFAttributes.event));
				result.add(date);
			} catch(IllegalArgumentException e) {
				log.error(e);
			}
		}
		return result;
		
	}

	private static Author createAuthor(Element authorElement) {
		String authorString = DOMUtil.getTextChild(authorElement);
		if (StringUtils.isBlank(authorString)) {
			return null;
		}
		int spacePos = authorString.lastIndexOf(' ');
		Author result;
		if(spacePos < 0) {
			result = new Author(authorString);
		} else {
			result = new Author(authorString.substring(0, spacePos), authorString.substring(spacePos + 1));
		}
		result.setRole(authorElement.getAttributeNS(NAMESPACE_OPF, OPFAttributes.role));
		return result;
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
			String identifierValue = DOMUtil.getTextChild(identifierElement);
			Identifier identifier = new Identifier(schemeName, identifierValue);
			if(identifierElement.getAttribute("id").equals(bookIdId) ) {
				identifier.setBookId(true);
			}
			result.add(identifier);
		}
		return result;
	}

}
