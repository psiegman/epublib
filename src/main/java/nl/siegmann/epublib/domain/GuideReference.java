package nl.siegmann.epublib.domain;

import nl.siegmann.epublib.Constants;

import org.apache.commons.lang.StringUtils;


public class GuideReference extends ResourceReference {
	private String type;
	private String fragmentId;
	
	public GuideReference(Resource resource) {
		this(null, resource);
	}
	
	public GuideReference(String title, Resource resource) {
		super(title, resource);
	}
	
	public GuideReference(Resource resource, String type, String title, String fragmentId) {
		super(title, resource);
		this.type = StringUtils.isNotBlank(type) ? type.toLowerCase() : null;
		this.fragmentId = fragmentId;
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
			return resource.getHref() + Constants.FRAGMENT_SEPARATOR + fragmentId;
		}
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
