package nl.siegmann.epublib.epub;

import nl.siegmann.epublib.domain.*;
import nl.siegmann.epublib.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads the package document metadata.
 * <p>
 * In its own separate class because the PackageDocumentReader became a bit large and unwieldy.
 *
 * @author paul
 */
// package
class PackageDocumentMetadataReader extends PackageDocumentBase {

    private static final Logger log = LoggerFactory.getLogger(PackageDocumentMetadataReader.class);

    public static Metadata readMetadata(Document packageDocument) {
        Metadata result = new Metadata();
        Element metadataElement = DOMUtil.getFirstElementByTagNameNS(packageDocument.getDocumentElement(), NAMESPACE_OPF, OPFTags.metadata);
        if (metadataElement == null) {
            log.error("Package does not contain element " + OPFTags.metadata);
            return result;
        }
        result.setTitles(readTitles(metadataElement));
        result.setPublishers(DOMUtil.getElementsTextChild(metadataElement, NAMESPACE_DUBLIN_CORE, DCTags.publisher));
        result.setDescriptions(DOMUtil.getElementsTextChild(metadataElement, NAMESPACE_DUBLIN_CORE, DCTags.description));
        result.setRights(DOMUtil.getElementsTextChild(metadataElement, NAMESPACE_DUBLIN_CORE, DCTags.rights));
        result.setTypes(DOMUtil.getElementsTextChild(metadataElement, NAMESPACE_DUBLIN_CORE, DCTags.type));
        result.setSubjects(DOMUtil.getElementsTextChild(metadataElement, NAMESPACE_DUBLIN_CORE, DCTags.subject));
        result.setIdentifiers(readIdentifiers(metadataElement));
        result.setAuthors(readCreators(metadataElement));
        result.setContributors(readContributors(metadataElement));
        result.setDates(readDates(metadataElement));
        result.setOtherProperties(readOtherProperties(metadataElement));
        result.setMetaAttributes(readMetaProperties(metadataElement));
        Element languageTag = DOMUtil.getFirstElementByTagNameNS(metadataElement, NAMESPACE_DUBLIN_CORE, DCTags.language);
        if (languageTag != null) {
            result.setLanguage(DOMUtil.getTextChildrenContent(languageTag));
        }


        return result;
    }

    private static List<Title> readTitles(Element metadataElement) {
        List<Title> result = new ArrayList<>();
        NodeList titleElements = metadataElement.getOwnerDocument().getElementsByTagNameNS(NAMESPACE_DUBLIN_CORE, DCTags.title);

        for (int j = 0; j < titleElements.getLength(); j++) {
            Node titleElement = titleElements.item(j);
            result.add(
                    new Title(
                            titleElement.getTextContent(),
                            findTitleType(titleElement, metadataElement)
                    )
            );
        }
        return result;
    }

    private static String findTitleType(Node titleElement, Element metadataElement) {
        // Try to find redefine as used in epub 3
        NodeList metaElements = metadataElement.getOwnerDocument().getElementsByTagNameNS("*", "meta");
        for (int j = 0; j < metaElements.getLength(); j++) {
            Node metaElement = metaElements.item(j);
            Node refines = metaElement.getAttributes().getNamedItem(OPFAttributes.refines);
            Node property = metaElement.getAttributes().getNamedItem(OPFAttributes.property);
            if (
                    null != refines
                            && null != property
                            && refines.getNodeValue().equals(
                                    "#" + DOMUtil.getAttribute(
                                            titleElement,
                                            EpubWriter.EMPTY_NAMESPACE_PREFIX,
                                            OPFAttributes.id
                                    )
                    )
            ) {
                return metaElement.getTextContent();
            }
        }
        return null;
    }

    /**
     * consumes meta tags that have a property attribute as defined in the standard. For example:
     * &lt;meta property="rendition:layout"&gt;pre-paginated&lt;/meta&gt;
     *
     * @param metadataElement
     * @return
     */
    private static Map<QName, String> readOtherProperties(Element metadataElement) {
        Map<QName, String> result = new HashMap<QName, String>();

        NodeList metaTags = metadataElement.getElementsByTagName(OPFTags.meta);
        for (int i = 0; i < metaTags.getLength(); i++) {
            Node metaNode = metaTags.item(i);
            Node property = metaNode.getAttributes().getNamedItem(OPFAttributes.property);
            Node refines = metaNode.getAttributes().getNamedItem(OPFAttributes.refines);
            if (property != null && refines == null) {
                String name = property.getNodeValue();
                String value = metaNode.getTextContent();
                result.put(new QName(name), value);
            }
        }

        return result;
    }

    /**
     * consumes meta tags that have a property attribute as defined in the standard. For example:
     * &lt;meta property="rendition:layout"&gt;pre-paginated&lt;/meta&gt;
     *
     * @param metadataElement
     * @return
     */
    private static Map<String, String> readMetaProperties(Element metadataElement) {
        Map<String, String> result = new HashMap<String, String>();

        NodeList metaTags = metadataElement.getElementsByTagName(OPFTags.meta);
        for (int i = 0; i < metaTags.getLength(); i++) {
            Element metaElement = (Element) metaTags.item(i);
            String name = metaElement.getAttribute(OPFAttributes.name);
            String value = metaElement.getAttribute(OPFAttributes.content);
            result.put(name, value);
        }

        return result;
    }

    private static String getBookIdId(Document document) {
        Element packageElement = DOMUtil.getFirstElementByTagNameNS(document.getDocumentElement(), NAMESPACE_OPF, OPFTags.packageTag);
        if (packageElement == null) {
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
        for (int i = 0; i < elements.getLength(); i++) {
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
        for (int i = 0; i < elements.getLength(); i++) {
            Element dateElement = (Element) elements.item(i);
            Date date;
            try {
                date = new Date(DOMUtil.getTextChildrenContent(dateElement), dateElement.getAttributeNS(NAMESPACE_OPF, OPFAttributes.event));
                result.add(date);
            } catch (IllegalArgumentException e) {
                log.error(e.getMessage());
            }
        }
        return result;

    }

    private static Author createAuthor(Element authorElement) {
        String authorString = DOMUtil.getTextChildrenContent(authorElement);
        if (StringUtil.isBlank(authorString)) {
            return null;
        }
        int spacePos = authorString.lastIndexOf(' ');
        Author result;
        if (spacePos < 0) {
            result = new Author(authorString);
        } else {
            result = new Author(authorString.substring(0, spacePos), authorString.substring(spacePos + 1));
        }

        String role = DOMUtil.getAttribute(authorElement, NAMESPACE_OPF, OPFAttributes.role);
        if (StringUtil.isNotBlank(role)) {
            result.setRole(role);
        } else {
            // Try to find redefine as used in epub 3
            NodeList metaElements = authorElement.getOwnerDocument().getElementsByTagNameNS("*", "meta");
            for (int j = 0; j < metaElements.getLength(); j++) {
                Node metaElement = metaElements.item(j);
                Node refines = metaElement.getAttributes().getNamedItem(OPFAttributes.refines);
                Node property = metaElement.getAttributes().getNamedItem(OPFAttributes.property);
                Node schemeNode = metaElement.getAttributes().getNamedItem(OPFAttributes.scheme);
                if (
                        null != refines
                                && null != property
                                && null != schemeNode
                                && refines.getNodeValue().equals("#" + authorElement.getAttribute("id"))
                                && OPFAttributes.role.equals(property.getNodeValue())
                ) {
                    result.setRole(metaElement.getTextContent());
                    result.setScheme(new Scheme(schemeNode.getNodeValue()));
                }
            }
        }
        return result;
    }


    private static List<Identifier> readIdentifiers(Element metadataElement) {
        NodeList identifierElements = metadataElement.getElementsByTagNameNS(NAMESPACE_DUBLIN_CORE, DCTags.identifier);
        if (identifierElements.getLength() == 0) {
            log.error("Package does not contain element " + DCTags.identifier);
            return new ArrayList<Identifier>();
        }
        String bookIdId = getBookIdId(metadataElement.getOwnerDocument());
        List<Identifier> result = new ArrayList<Identifier>(identifierElements.getLength());
        for (int i = 0; i < identifierElements.getLength(); i++) {
            Element identifierElement = (Element) identifierElements.item(i);
            Scheme scheme = new Scheme(identifierElement.getAttributeNS(NAMESPACE_OPF, DCAttributes.scheme));
            if (StringUtil.isBlank(scheme.getName())) {
                //Try to find redefine meta element as used in opf version 3
                NodeList metaElements = identifierElement.getOwnerDocument().getElementsByTagNameNS("*", "meta");
                for (int j = 0; j < metaElements.getLength(); j++) {
                    Node metaElement = metaElements.item(j);
                    Node refines = metaElement.getAttributes().getNamedItem(OPFAttributes.refines);
                    Node property = metaElement.getAttributes().getNamedItem(OPFAttributes.property);
                    Node schemeNode = metaElement.getAttributes().getNamedItem(OPFAttributes.scheme);
                    if (
                            null != refines
                                    && null != property
                                    && null != scheme
                                    && refines.getNodeValue().equals("#" + identifierElement.getAttribute("id"))
                                    && "identifier-type".equals(property.getNodeValue())
                    ) {
                        scheme = new Scheme(schemeNode.getNodeValue(), metaElement.getTextContent());
                    }
                }
            }
            String identifierValue = DOMUtil.getTextChildrenContent(identifierElement);
            if (StringUtil.isBlank(identifierValue)) {
                continue;
            }
            Identifier identifier = new Identifier(scheme, identifierValue);
            if (identifierElement.getAttribute("id").equals(bookIdId)) {
                identifier.setBookId(true);
            }
            result.add(identifier);
        }
        return result;
    }
}
