package nl.siegmann.epublib.epub;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Guide;
import nl.siegmann.epublib.domain.GuideReference;
import nl.siegmann.epublib.domain.MediaType;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Resources;
import nl.siegmann.epublib.domain.Spine;
import nl.siegmann.epublib.domain.SpineReference;
import nl.siegmann.epublib.service.MediatypeService;
import nl.siegmann.epublib.util.ResourceUtil;
import nl.siegmann.epublib.util.StringUtil;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Reads the opf package document as defined by namespace http://www.idpf.org/2007/opf
 *  
 * @author paul
 *
 */
public class PackageDocumentReader extends PackageDocumentBase {
	
	private static final Logger log = LoggerFactory.getLogger(PackageDocumentReader.class);
	private static final String[] POSSIBLE_NCX_ITEM_IDS = new String[] {"toc", "ncx"};
	
	
	public static void read(Resource packageResource, EpubReader epubReader, Book book, Map<String, Resource> resourcesByHref) throws UnsupportedEncodingException, SAXException, IOException, ParserConfigurationException {
		Document packageDocument = ResourceUtil.getAsDocument(packageResource, epubReader);
		String packageHref = packageResource.getHref();
		resourcesByHref = fixHrefs(packageHref, resourcesByHref);
		readGuide(packageDocument, epubReader, book, resourcesByHref);
		book.setResources(readManifest(packageDocument, packageHref, epubReader, resourcesByHref));
		readCover(packageDocument, book);
		book.setMetadata(PackageDocumentMetadataReader.readMetadata(packageDocument, book.getResources()));
		book.setSpine(readSpine(packageDocument, epubReader, book.getResources()));
		
		// if we did not find a cover page then we make the first page of the book the cover page
		if (book.getCoverPage() == null && book.getSpine().size() > 0) {
			book.setCoverPage(book.getSpine().getResource(0));
		}
	}
	
	/**
	 * Reads the manifest containing the resource ids, hrefs and mediatypes.
	 *  
	 * @param packageDocument
	 * @param packageHref
	 * @param epubReader
	 * @param book
	 * @param resourcesByHref
	 * @return a Map with resources, with their id's as key.
	 */
	private static Resources readManifest(Document packageDocument, String packageHref,
			EpubReader epubReader, Map<String, Resource> resourcesByHref) {
		Element manifestElement = DOMUtil.getFirstElementByTagNameNS(packageDocument.getDocumentElement(), NAMESPACE_OPF, OPFTags.manifest);
		Resources result = new Resources();
		if(manifestElement == null) {
			log.error("Package document does not contain element " + OPFTags.manifest);
			return result;
		}
		NodeList itemElements = manifestElement.getElementsByTagNameNS(NAMESPACE_OPF, OPFTags.item);
		for(int i = 0; i < itemElements.getLength(); i++) {
			Element itemElement = (Element) itemElements.item(i);
			String id = DOMUtil.getAttribute(itemElement, NAMESPACE_OPF, OPFAttributes.id);
			String href = DOMUtil.getAttribute(itemElement, NAMESPACE_OPF, OPFAttributes.href);
			href = StringUtil.unescapeHttp(href);
			String mediaTypeName = DOMUtil.getAttribute(itemElement, NAMESPACE_OPF, OPFAttributes.media_type);
			Resource resource = resourcesByHref.remove(href);
			if(resource == null) {
				log.error("resource with href '" + href + "' not found");
				continue;
			}
			resource.setId(id);
			MediaType mediaType = MediatypeService.getMediaTypeByName(mediaTypeName);
			if(mediaType != null) {
				resource.setMediaType(mediaType);
			}
			result.add(resource);
		}
		return result;
	}	

	
	
	
	/**
	 * Reads the book's guide.
	 * Here some more attempts are made at finding the cover page.
	 * 
	 * @param packageDocument
	 * @param epubReader
	 * @param book
	 * @param resourcesByHref
	 */
	private static void readGuide(Document packageDocument,
			EpubReader epubReader, Book book, Map<String, Resource> resourcesByHref) {
		Element guideElement = DOMUtil.getFirstElementByTagNameNS(packageDocument.getDocumentElement(), NAMESPACE_OPF, OPFTags.guide);
		if(guideElement == null) {
			return;
		}
		Guide guide = book.getGuide();
		NodeList guideReferences = guideElement.getElementsByTagNameNS(NAMESPACE_OPF, OPFTags.reference);
		for (int i = 0; i < guideReferences.getLength(); i++) {
			Element referenceElement = (Element) guideReferences.item(i);
			String resourceHref = DOMUtil.getAttribute(referenceElement, NAMESPACE_OPF, OPFAttributes.href);
			if (StringUtils.isBlank(resourceHref)) {
				continue;
			}
			Resource resource = resourcesByHref.get(StringUtils.substringBefore(resourceHref, Constants.FRAGMENT_SEPARATOR));
			if (resource == null) {
				log.error("Guide is referencing resource with href " + resourceHref + " which could not be found");
				continue;
			}
			String type = DOMUtil.getAttribute(referenceElement, NAMESPACE_OPF, OPFAttributes.type);
			if (StringUtils.isBlank(type)) {
				log.error("Guide is referencing resource with href " + resourceHref + " which is missing the 'type' attribute");
				continue;
			}
			String title = DOMUtil.getAttribute(referenceElement, NAMESPACE_OPF, OPFAttributes.title);
			if (GuideReference.COVER.equalsIgnoreCase(type)) {
				continue; // cover is handled elsewhere
			}
			GuideReference reference = new GuideReference(resource, type, title, StringUtils.substringAfter(resourceHref, Constants.FRAGMENT_SEPARATOR));
			guide.addReference(reference);
		}
	}


	/**
	 * Strips off the package prefixes up to the href of the packageHref.
	 * 
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

	/**
	 * Reads the document's spine, containing all sections in reading order.
	 * 
	 * @param packageDocument
	 * @param epubReader
	 * @param book
	 * @param resourcesById
	 * @return
	 */
	private static Spine readSpine(Document packageDocument, EpubReader epubReader, Resources resources) {
		
		Element spineElement = DOMUtil.getFirstElementByTagNameNS(packageDocument.getDocumentElement(), NAMESPACE_OPF, OPFTags.spine);
		if (spineElement == null) {
			log.error("Element " + OPFTags.spine + " not found in package document, generating one automatically");
			return generateSpineFromResources(resources);
		}
		Spine result = new Spine();
		result.setTocResource(findTableOfContentsResource(spineElement, resources));
		NodeList spineNodes = packageDocument.getElementsByTagNameNS(NAMESPACE_OPF, OPFTags.itemref);
		List<SpineReference> spineReferences = new ArrayList<SpineReference>(spineNodes.getLength());
		for(int i = 0; i < spineNodes.getLength(); i++) {
			Element spineItem = (Element) spineNodes.item(i);
			String itemref = DOMUtil.getAttribute(spineItem, NAMESPACE_OPF, OPFAttributes.idref);
			if(StringUtils.isBlank(itemref)) {
				log.error("itemref with missing or empty idref"); // XXX
				continue;
			}
			Resource resource = resources.getByIdOrHref(itemref);
			if(resource == null) {
				log.error("resource with id \'" + itemref + "\' not found");
				continue;
			}
			if(resource == Resource.NULL_RESOURCE) {
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
	 * @param resources
	 * @return
	 */
	private static Spine generateSpineFromResources(Resources resources) {
		Spine result = new Spine();
		List<String> resourceHrefs = new ArrayList<String>();
		resourceHrefs.addAll(resources.getAllHrefs());
		Collections.sort(resourceHrefs, String.CASE_INSENSITIVE_ORDER);
		for (String resourceHref: resourceHrefs) {
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
	 * 
	 * Here we try several ways of finding this table of contents resource.
	 * We try the given attribute value, some often-used ones and finally look through all resources for the first resource with the table of contents mimetype.
	 * 
	 * @param spineElement
	 * @param resourcesById
	 * @return
	 */
	private static Resource findTableOfContentsResource(Element spineElement, Resources resources) {
		String tocResourceId = DOMUtil.getAttribute(spineElement, NAMESPACE_OPF, OPFAttributes.toc);
		Resource tocResource = null;
		if (! StringUtils.isBlank(tocResourceId)) {
			tocResource = resources.getByIdOrHref(tocResourceId);
		}
		
		if (tocResource != null) {
			return tocResource;
		}
		
		for (int i = 0; i < POSSIBLE_NCX_ITEM_IDS.length; i++) {
			tocResource = resources.getByIdOrHref(POSSIBLE_NCX_ITEM_IDS[i]);
			if (tocResource != null) {
				return tocResource;
			}
			tocResource = resources.getByIdOrHref(POSSIBLE_NCX_ITEM_IDS[i].toUpperCase());
			if (tocResource != null) {
				return tocResource;
			}
		}
		
		// get the first resource with the NCX mediatype
		tocResource = resources.findFirstResourceByMediaType(MediatypeService.NCX);

		if (tocResource == null) {
			log.error("Could not find table of contents resource. Tried resource with id '" + tocResourceId + "', " + Constants.DEFAULT_TOC_ID + ", " + Constants.DEFAULT_TOC_ID.toUpperCase() + " and any NCX resource.");
		}
		return tocResource;
	}


	/**
	 * Find all resources that have something to do with the coverpage and the cover image.
	 * Search the meta tags and the guide references
	 * 
	 * @param packageDocument
	 * @return
	 */
	// package
	static Set<String> findCoverHrefs(Document packageDocument) {
		
		Set<String> result = new HashSet<String>();
		
		// try and find a meta tag with name = 'cover' and a non-blank id
		String coverResourceId = DOMUtil.getFindAttributeValue(packageDocument, NAMESPACE_OPF,
											OPFTags.meta, OPFAttributes.name, OPFValues.meta_cover,
											OPFAttributes.content);

		if (StringUtils.isNotBlank(coverResourceId)) {
			String coverHref = DOMUtil.getFindAttributeValue(packageDocument, NAMESPACE_OPF,
					OPFTags.item, OPFAttributes.id, coverResourceId,
					OPFAttributes.href);
			if (StringUtils.isNotBlank(coverHref)) {
				result.add(coverHref);
			} else {
				result.add(coverResourceId); // maybe there was a cover href put in the cover id attribute
			}
		}
		// try and find a reference tag with type is 'cover' and reference is not blank
		String coverHref = DOMUtil.getFindAttributeValue(packageDocument, NAMESPACE_OPF,
											OPFTags.reference, OPFAttributes.type, OPFValues.reference_cover,
											OPFAttributes.href);
		if (StringUtils.isNotBlank(coverHref)) {
			result.add(coverHref);
		}
		return result;
	}

	/**
	 * Finds the cover resource in the packageDocument and adds it to the book if found.
	 * Keeps the cover resource in the resources map
	 * @param packageDocument
	 * @param book
	 * @param resources
	 * @return
	 */
	private static void readCover(Document packageDocument, Book book) {
		
		Collection<String> coverHrefs = findCoverHrefs(packageDocument);
		for (String coverHref: coverHrefs) {
			Resource resource = book.getResources().getByHref(coverHref);
			if (resource == null) {
				log.error("Cover resource " + coverHref + " not found");
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