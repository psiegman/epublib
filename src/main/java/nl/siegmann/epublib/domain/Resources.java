package nl.siegmann.epublib.domain;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.service.MediatypeService;

import org.apache.commons.lang.StringUtils;

/**
 * All the resources that make up the book.
 * XHTML files, images and epub xml documents must be here.
 * 
 * @author paul
 *
 */
public class Resources {

	public static final String IMAGE_PREFIX = "image_";
	public static final String ITEM_PREFIX = "item_";
	
	private Map<String, Resource> resources = new HashMap<String, Resource>();
	
	/**
	 * Adds a resource to the resources.
	 * 
	 * Fixes the resources id and href if necessary.
	 * 
	 * @param resource
	 * @return
	 */
	public Resource add(Resource resource) {
		fixResourceHref(resource);
		fixResourceId(resource);
		this.resources.put(resource.getHref(), resource);
		return resource;
	}

	public void fixResourceId(Resource resource) {
		String  resourceId = resource.getId();
		
		// first try and create a unique id based on the resource's href
		if (StringUtils.isBlank(resource.getId())) {
			resourceId = StringUtils.substringBeforeLast(resource.getHref(), ".");
			resourceId = StringUtils.substringAfterLast(resourceId, "/");
		}
		
		resourceId = makeValidId(resourceId, resource);
		
		// check if the id is unique. if not: create one from scratch
		if (StringUtils.isBlank(resourceId) || containsId(resourceId)) {
			resourceId = createUniqueResourceId(resource);
		}
		resource.setId(resourceId);
	}

	/**
	 * Check if the id is a valid identifier. if not: prepend with valid identifier
	 * 
	 * @param resource
	 * @return
	 */
	private String makeValidId(String resourceId, Resource resource) {
		if (! StringUtils.isBlank(resourceId) && ! Character.isJavaIdentifierStart(resourceId.charAt(0))) {
			resourceId = getResourceItemPrefix(resource) + resourceId;
		}
		return resourceId;
	}
	
	private String getResourceItemPrefix(Resource resource) {
		String result;
		if (MediatypeService.isBitmapImage(resource.getMediaType())) {
			result = IMAGE_PREFIX;
		} else {
			result = ITEM_PREFIX;
		}
		return result;
	}
	
	
	private String createUniqueResourceId(Resource resource) {
		int counter = 1;
		String prefix = getResourceItemPrefix(resource);
		String result = prefix + counter;
		while (containsId(result)) {
			result = prefix + (++ counter);
		}
		return result;
	}

	public boolean containsId(String id) {
		if (StringUtils.isBlank(id)) {
			return false;
		}
		for (Resource resource: resources.values()) {
			if (id.equals(resource.getId())) {
				return true;
			}
		}
		return false;
	}
	

	public Resource getById(String id) {
		if (StringUtils.isBlank(id)) {
			return null;
		}
		for (Resource resource: resources.values()) {
			if (id.equals(resource.getId())) {
				return resource;
			}
		}
		return null;
	}
	
	public Resource remove(String href) {
		return resources.remove(href);
	}
	
	private void fixResourceHref(Resource resource) {
		if(! StringUtils.isBlank(resource.getHref())
				&& ! resources.containsKey(resource.getHref())) {
			return;
		}
		if(StringUtils.isBlank(resource.getHref())) {
			if(resource.getMediaType() == null) {
				throw new IllegalArgumentException("Resource must have either a MediaType or a href");
			}
			int i = 1;
			String href = createHref(resource.getMediaType(), i);
			while(resources.containsKey(href)) {
				href = createHref(resource.getMediaType(), (++i));
			}
			resource.setHref(href);
		}
	}
	
	private String createHref(MediaType mediaType, int counter) {
		if(MediatypeService.isBitmapImage(mediaType)) {
			return "image_" + counter + mediaType.getDefaultExtension();
		} else {
			return "item_" + counter + mediaType.getDefaultExtension();
		}
	}
	
	
	public boolean isEmpty() {
		return resources.isEmpty();
	}
	
	public int size() {
		return resources.size();
	}
	
	/**
	 * The resources that make up this book.
	 * Resources can be xhtml pages, images, xml documents, etc.
	 * 
	 * @return
	 */
	public Map<String, Resource> getResourceMap() {
		return resources;
	}
	
	public Collection<Resource> getAll() {
		return resources.values();
	}
	
	
	public boolean containsByHref(String href) {
		if (StringUtils.isBlank(href)) {
			return false;
		}
		return resources.containsKey(StringUtils.substringBefore(href, Constants.FRAGMENT_SEPARATOR));
	}
	
	public void set(Collection<Resource> resources) {
		this.resources.clear();
		addAll(resources);
	}
	
	public void addAll(Collection<Resource> resources) {
		for(Resource resource: resources) {
			fixResourceHref(resource);
			this.resources.put(resource.getHref(), resource);
		}
	}

	public void set(Map<String, Resource> resources) {
		this.resources = new HashMap<String, Resource>(resources);
	}
	
	
	/**
	 * Gets the resource with the given href.
	 * If the given href contains a fragmentId then that fragment id will be ignored.
	 * 
	 * @param href
	 * @return null if not found.
	 */
	public Resource getByHref(String href) {
		if (StringUtils.isBlank(href)) {
			return null;
		}
		href = StringUtils.substringBefore(href, Constants.FRAGMENT_SEPARATOR);
		Resource result = resources.get(href);
		return result;
	}
	
	/**
	 * Gets the first resource (random order) with the give mediatype.
	 * 
	 * Useful for looking up the table of contents as it's supposed to be the only resource with NCX mediatype.
	 * 
	 * @param mediaType
	 * @return
	 */
	public Resource findFirstResourceByMediaType(MediaType mediaType) {
		return findFirstResourceByMediaType(resources.values(), mediaType);
	}
	
	/**
	 * Gets the first resource (random order) with the give mediatype.
	 * 
	 * Useful for looking up the table of contents as it's supposed to be the only resource with NCX mediatype.
	 * 
	 * @param mediaType
	 * @return
	 */
	public static Resource findFirstResourceByMediaType(Collection<Resource> resources, MediaType mediaType) {
		for (Resource resource: resources) {
			if (resource.getMediaType() == mediaType) {
				return resource;
			}
		}
		return null;
	}
}
