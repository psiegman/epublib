package nl.siegmann.epublib.domain;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Resources {

	private Map<String, Resource> resources = new HashMap<String, Resource>();
	
	public Resource add(Resource resource) {
		this.resources.put(resource.getHref(), resource);
		return resource;
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
		resources.clear();
		addAll(resources);
	}
	
	public void addAll(Collection<Resource> resources) {
		for(Resource resource: resources) {
			this.resources.put(resource.getHref(), resource);
		}
	}

	public void set(Map<String, Resource> resources) {
		this.resources = new HashMap<String, Resource>(resources);
	}
	
	public Resource getByHref(String href) {
		Resource result = resources.get(href);
		return result;
	}

}
