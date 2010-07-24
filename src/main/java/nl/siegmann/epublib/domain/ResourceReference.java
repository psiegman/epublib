package nl.siegmann.epublib.domain;

public class ResourceReference {

	protected String title;
	protected Resource resource;

	public ResourceReference(String title, Resource resource) {
		this.title = title;
		this.resource = resource;
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
	 * Besides setting the resource it also sets the fragmentId to null.
	 * 
	 * @param resource
	 */
	public void setResource(Resource resource) {
		this.resource = resource;
	}
}
