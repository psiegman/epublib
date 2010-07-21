package nl.siegmann.epublib.domain;

import org.apache.commons.lang.StringUtils;


public class Reference {
	private String title;
	private Resource resource;
	private String type;
	private String fragmentId;
	
	public Reference(Resource resource) {
		this(null, resource);
	}
	
	public Reference(String title, Resource resource) {
		this.title = title;
		this.resource = resource;
	}
	
	public Reference(Resource resource, String type, String title, String fragmentId) {
		this.resource = resource;
		this.type = StringUtils.isNotBlank(type) ? type.toLowerCase() : null;
		this.title = title;
		this.fragmentId = fragmentId;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Resource getResource() {
		return resource;
	}
	
	/**
	 * If the fragmentId is blank it returns the resource href, otherwise it returns the resource href + '#' + the fragmentId.
	 * 
	 * @return
	 */
	public String getCompleteHref() {
		if (StringUtils.isBlank(fragmentId)) {
			return resource.getHref();
		} else {
			return resource.getHref() + '#' + fragmentId;
		}
	}
	
	
	/**
	 * Besides setting the resource it also sets the fragmentId to null.
	 * 
	 * @param resource
	 */
	public void setResource(Resource resource) {
		setResource(resource, null);
	}

	public void setResource(Resource resource, String fragmentId) {
		this.resource = resource;
		this.fragmentId = fragmentId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
