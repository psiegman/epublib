package nl.siegmann.epublib.domain;

import org.apache.commons.lang.StringUtils;


public class Reference {
	private String title;
	private Resource resource;
	private String type;
	
	public Reference(Resource resource) {
		this(null, resource);
	}
	
	public Reference(String title, Resource resource) {
		this.title = title;
		this.resource = resource;
	}
	
	public Reference(Resource resource, String type, String title) {
		this.resource = resource;
		this.type = StringUtils.isNotBlank(type) ? type.toLowerCase() : null;
		this.title = title;
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
	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
