package nl.siegmann.epublib.domain;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import nl.siegmann.epublib.service.MediatypeService;

import org.apache.commons.lang.StringUtils;

public class Resources {

	private Map<String, Resource> resources = new HashMap<String, Resource>();
	
	public Resource add(Resource resource) {
		fixHref(resource);
		this.resources.put(resource.getHref(), resource);
		return resource;
	}

	public Resource remove(String href) {
		return resources.remove(href);
	}
	
	private void fixHref(Resource resource) {
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
		return resources.containsKey(href);
	}
	
	public void set(Collection<Resource> resources) {
		this.resources.clear();
		addAll(resources);
	}
	
	public void addAll(Collection<Resource> resources) {
		for(Resource resource: resources) {
			fixHref(resource);
			this.resources.put(resource.getHref(), resource);
		}
	}

	public void set(Map<String, Resource> resources) {
		this.resources = new HashMap<String, Resource>(resources);
	}
	
	public Resource getByHref(String href) {
		href = StringUtils.substringBefore(href, "#");
		Resource result = resources.get(href);
		return result;
	}
}
