package nl.siegmann.epublib.epub;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.domain.*;
import nl.siegmann.epublib.service.MediatypeService;
import nl.siegmann.epublib.util.ResourceUtil;
import nl.siegmann.epublib.util.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.logging.Logger;

/**
 * Reads the opf package document as defined by namespace http://www.idpf.org/2007/opf
 *
 * @author paul
 */
public class PackageDocumentReader extends PackageDocumentBase {

    private static final Logger log = Logger.getLogger(PackageDocumentReader.class.getName());
    private static final String[] POSSIBLE_NCX_ITEM_IDS = new String[]{"toc", "ncx", "ncxtoc"};


    public static void read(OpfResource packageResource, Book book, Resources resources) throws SAXException, IOException, ParserConfigurationException {
        Document packageDocument = ResourceUtil.getAsDocument(packageResource);
        String packageHref = packageResource.getHref();
        resources = fixHrefs(packageHref, resources);
        if (null != packageDocument) {
            packageResource.setVersion(
                    getOpfVersion(packageDocument)
            );
            packageResource.setPrefix(
                    getOpfPrefix(packageDocument)
            );
        }
        readGuide(packageDocument, book, resources);

        // Books sometimes use non-identifier ids. We map these here to legal ones
        Map<String, String> idMapping = new HashMap<>();

        resources = readManifest(packageDocument, resources, idMapping);
        book.setResources(resources);
        book.setNavResource(resources.getNavResource());
        readCover(packageDocument, book);
        book.setMetadata(PackageDocumentMetadataReader.readMetadata(packageDocument));
        book.setSpine(readSpine(packageDocument, book.getResources(), idMapping));

        // if we did not find a cover page then we make the first page of the book the cover page
        if (book.getCoverPage() == null && book.getSpine().size() > 0) {
            book.setCoverPage(book.getSpine().getResource(0));
        }
    }

    private static String getOpfVersion(Document packageDocument) {
        NodeList packageNodes = packageDocument.getElementsByTagNameNS("*", "package");
        if (packageNodes.getLength() <= 0) return null;
        Node packageNode = packageNodes.item(0);
        if (!packageNode.hasAttributes()) return null;
        Node versionNode = packageNode.getAttributes().getNamedItem("version");
        if (null == versionNode) return null;
        return versionNode.getNodeValue();
    }

    private static String getOpfPrefix(Document packageDocument) {
        NodeList packageNodes = packageDocument.getElementsByTagNameNS("*", "package");
        if (packageNodes.getLength() <= 0) return null;
        Node packageNode = packageNodes.item(0);
        if (!packageNode.hasAttributes()) return null;
        Node prefixNode = packageNode.getAttributes().getNamedItem("prefix");
        if (null == prefixNode) return null;
        return prefixNode.getNodeValue();
    }

//	private static Resource readCoverImage(Element metadataElement, Resources resources) {
//		String coverResourceId = DOMUtil.getFindAttributeValue(metadataElement.getOwnerDocument(), NAMESPACE_OPF, OPFTags.meta, OPFAttributes.name, OPFValues.meta_cover, OPFAttributes.content);
//		if (StringUtil.isBlank(coverResourceId)) {
//			return null;
//		}
//		Resource coverResource = resources.getByIdOrHref(coverResourceId);
//		return coverResource;
//	}


    /**
     * Reads the manifest containing the resource ids, hrefs and mediatypes.
     * 
     * @return a Map with resources, with their id's as key.
     */
    private static Resources readManifest(Document packageDocument, Resources resources, Map<String, String> idMapping) {
        Element manifestElement = DOMUtil.getFirstElementByTagNameNS(packageDocument.getDocumentElement(), NAMESPACE_OPF, OPFTags.manifest);
        Resources result = new Resources();
        if (manifestElement == null) {
            log.severe("Package document does not contain element " + OPFTags.manifest);
            return result;
        }
        NodeList itemElements = manifestElement.getElementsByTagNameNS(NAMESPACE_OPF, OPFTags.item);
        for (int i = 0; i < itemElements.getLength(); i++) {
            Element itemElement = (Element) itemElements.item(i);
            String id = DOMUtil.getAttribute(itemElement, NAMESPACE_OPF, OPFAttributes.id);
            String href = DOMUtil.getAttribute(itemElement, NAMESPACE_OPF, OPFAttributes.href);
            String property = DOMUtil.getAttribute(itemElement, NAMESPACE_OPF, OPFAttributes.properties);
            try {
                href = URLDecoder.decode(href, Constants.CHARACTER_ENCODING);
            } catch (UnsupportedEncodingException e) {
                log.severe(e.getMessage());
            }
            String mediaTypeName = DOMUtil.getAttribute(itemElement, NAMESPACE_OPF, OPFAttributes.media_type);
            Resource resource = resources.remove(href);
            if (resource == null) {
                log.severe("resource with href '" + href + "' not found");
                continue;
            }
            resource.setId(id);
            if (StringUtil.equals(property, OPFValues.nav)) {
                resource.setNav(true);
                result.setNavResource(resource);
            } else {
                resource.setNav(false);
            }
            resource.setContainingSvg(StringUtil.equals(property, OPFValues.svg));
            resource.setScripted(StringUtil.equals(property, OPFValues.scripted));
            MediaType mediaType = MediatypeService.getMediaTypeByName(mediaTypeName);
            if (mediaType != null) {
                resource.setMediaType(mediaType);
            }
            result.add(resource);
            idMapping.put(id, resource.getId());
        }
        return result;
    }


    /**
     * Reads the book's guide.
     * Here some more attempts are made at finding the cover page.
     */
    private static void readGuide(Document packageDocument, Book book, Resources resources) {
        Element guideElement = DOMUtil.getFirstElementByTagNameNS(packageDocument.getDocumentElement(), NAMESPACE_OPF, OPFTags.guide);
        if (guideElement == null) {
            return;
        }
        Guide guide = book.getGuide();
        NodeList guideReferences = guideElement.getElementsByTagNameNS(NAMESPACE_OPF, OPFTags.reference);
        for (int i = 0; i < guideReferences.getLength(); i++) {
            Element referenceElement = (Element) guideReferences.item(i);
            String resourceHref = DOMUtil.getAttribute(referenceElement, NAMESPACE_OPF, OPFAttributes.href);
            if (StringUtil.isBlank(resourceHref)) {
                continue;
            }
            Resource resource = resources.getByHref(StringUtil.substringBefore(resourceHref, Constants.FRAGMENT_SEPARATOR_CHAR));
            if (resource == null) {
                log.severe("Guide is referencing resource with href " + resourceHref + " which could not be found");
                continue;
            }
            String type = DOMUtil.getAttribute(referenceElement, NAMESPACE_OPF, OPFAttributes.type);
            if (StringUtil.isBlank(type)) {
                log.severe("Guide is referencing resource with href " + resourceHref + " which is missing the 'type' attribute");
                continue;
            }
            String title = DOMUtil.getAttribute(referenceElement, NAMESPACE_OPF, OPFAttributes.title);
            if (GuideReference.COVER.equalsIgnoreCase(type)) {
                continue; // cover is handled elsewhere
            }
            GuideReference reference = new GuideReference(resource, type, title, StringUtil.substringAfter(resourceHref, Constants.FRAGMENT_SEPARATOR_CHAR));
            guide.addReference(reference);
        }
    }


    /**
     * Strips off the package prefixes up to the href of the packageHref.
     * <p>
     * Example:
     * If the packageHref is "OEBPS/content.opf" then a resource href like "OEBPS/foo/bar.html" will be turned into "foo/bar.html"
     *
     * @return The stripped package href
     */
    static Resources fixHrefs(String packageHref,
                              Resources resourcesByHref) {
        int lastSlashPos = packageHref.lastIndexOf('/');
        if (lastSlashPos < 0) {
            return resourcesByHref;
        }
        Resources result = new Resources();
        for (Resource resource : resourcesByHref.getAll()) {
            if (StringUtil.isNotBlank(resource.getHref())
                    && resource.getHref().length() > lastSlashPos) {
                resource.setHref(resource.getHref().substring(lastSlashPos + 1));
            }
            result.add(resource);
        }
        return result;
    }

    /**
     * Reads the document's spine, containing all sections in reading order.
     *
     * @return the document's spine, containing all sections in reading order.
     */
    private static Spine readSpine(Document packageDocument, Resources resources, Map<String, String> idMapping) {

        Element spineElement = DOMUtil.getFirstElementByTagNameNS(packageDocument.getDocumentElement(), NAMESPACE_OPF, OPFTags.spine);
        if (spineElement == null) {
            log.severe("Element " + OPFTags.spine + " not found in package document, generating one automatically");
            return generateSpineFromResources(resources);
        }
        Spine result = new Spine();
        String tocResourceId = DOMUtil.getAttribute(spineElement, NAMESPACE_OPF, OPFAttributes.toc);
        result.setTocResource(findTableOfContentsResource(tocResourceId, resources));
        NodeList spineNodes = packageDocument.getElementsByTagNameNS(NAMESPACE_OPF, OPFTags.itemref);
        List<SpineReference> spineReferences = new ArrayList<>(spineNodes.getLength());
        for (int i = 0; i < spineNodes.getLength(); i++) {
            Element spineItem = (Element) spineNodes.item(i);
            String itemref = DOMUtil.getAttribute(spineItem, NAMESPACE_OPF, OPFAttributes.idref);
            if (StringUtil.isBlank(itemref)) {
                log.severe("itemref with missing or empty idref"); // XXX
                continue;
            }
            String id = idMapping.get(itemref);
            if (id == null) {
                id = itemref;
            }
            Resource resource = resources.getByIdOrHref(id);
            if (resource == null) {
                log.severe("resource with id '" + id + "' not found");
                continue;
            }

            SpineReference spineReference = new SpineReference(resource);
            if (OPFValues.no.equalsIgnoreCase(DOMUtil.getAttribute(spineItem, NAMESPACE_OPF, OPFAttributes.linear))) {
                spineReference.setLinear(false);
            }
            spineReferences.add(spineReference);
        }
        result.setSpineReferences(spineReferences);
        return result;
    }

    /**
     * Creates a spine out of all resources in the resources.
     * The generated spine consists of all XHTML pages in order of their href.
     *
     * @return a spine created out of all resources in the resources.
     */
    private static Spine generateSpineFromResources(Resources resources) {
        Spine result = new Spine();
        List<String> resourceHrefs = new ArrayList<>(resources.getAllHrefs());
        Collections.sort(resourceHrefs, String.CASE_INSENSITIVE_ORDER);
        for (String resourceHref : resourceHrefs) {
            Resource resource = resources.getByHref(resourceHref);
            if (resource.getMediaType() == MediatypeService.NCX) {
                result.setTocResource(resource);
            } else if (resource.getMediaType() == MediatypeService.XHTML) {
                result.addSpineReference(new SpineReference(resource));
            }
        }
        return result;
    }


    /**
     * The spine tag should contain a 'toc' attribute with as value the resource id of the table of contents resource.
     * <p>
     * Here we try several ways of finding this table of contents resource.
     * We try the given attribute value, some often-used ones and finally look through all resources for the first resource with the table of contents mimetype.
     *
     * @return the Resource containing the table of contents
     */
    static Resource findTableOfContentsResource(String tocResourceId, Resources resources) {
        Resource tocResource = null;
        if (StringUtil.isNotBlank(tocResourceId)) {
            tocResource = resources.getByIdOrHref(tocResourceId);
        }

        if (tocResource != null) {
            return tocResource;
        }

        // get the first resource with the NCX mediatype
        tocResource = resources.findFirstResourceByMediaType(MediatypeService.NCX);

        if (tocResource == null) {
            for (String possibleNcxItemId : POSSIBLE_NCX_ITEM_IDS) {
                tocResource = resources.getByIdOrHref(possibleNcxItemId);
                if (tocResource != null) {
                    break;
                }
                tocResource = resources.getByIdOrHref(possibleNcxItemId.toUpperCase());
                if (tocResource != null) {
                    break;
                }
            }
        }

        if (tocResource == null) {
            log.severe("Could not find table of contents resource. Tried resource with id '" + tocResourceId + "', " + Constants.DEFAULT_TOC_ID + ", " + Constants.DEFAULT_TOC_ID.toUpperCase() + " and any NCX resource.");
        }
        return tocResource;
    }


    /**
     * Find all resources that have something to do with the coverpage and the cover image.
     * Search the meta tags and the guide references
     *
     * @return all resources that have something to do with the coverpage and the cover image.
     */
    // package
    static Set<String> findCoverHrefs(Document packageDocument) {

        Set<String> result = new HashSet<>();

        // try and find a meta tag with name = 'cover' and a non-blank id
        String coverResourceId = DOMUtil.getFindAttributeValue(packageDocument, NAMESPACE_OPF,
                OPFTags.meta, OPFAttributes.name, OPFValues.meta_cover,
                OPFAttributes.content);

        if (StringUtil.isNotBlank(coverResourceId)) {
            String coverHref = DOMUtil.getFindAttributeValue(packageDocument, NAMESPACE_OPF,
                    OPFTags.item, OPFAttributes.id, coverResourceId,
                    OPFAttributes.href);
            if (StringUtil.isNotBlank(coverHref)) {
                result.add(coverHref);
            } else {
                result.add(coverResourceId); // maybe there was a cover href put in the cover id attribute
            }
        }
        // try and find a reference tag with type is 'cover' and reference is not blank
        String coverHref = DOMUtil.getFindAttributeValue(packageDocument, NAMESPACE_OPF,
                OPFTags.reference, OPFAttributes.type, OPFValues.reference_cover,
                OPFAttributes.href);
        if (StringUtil.isNotBlank(coverHref)) {
            result.add(coverHref);
        }
        return result;
    }

    /**
     * Finds the cover resource in the packageDocument and adds it to the book if found.
     * Keeps the cover resource in the resources map
     */
    private static void readCover(Document packageDocument, Book book) {

        Collection<String> coverHrefs = findCoverHrefs(packageDocument);
        for (String coverHref : coverHrefs) {
            Resource resource = book.getResources().getByHref(coverHref);
            if (resource == null) {
                log.severe("Cover resource " + coverHref + " not found");
                continue;
            }
            if (resource.getMediaType() == MediatypeService.XHTML) {
                book.setCoverPage(resource);
            } else if (MediatypeService.isBitmapImage(resource.getMediaType())) {
                book.setCoverImage(resource);
            }
        }
    }


}
