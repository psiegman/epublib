package nl.siegmann.epublib.domain;

public class ResourceReference {

	protected Resource resource;

	public ResourceReference(Resource resource) {
		this.resource = resource;
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


	/**
	 * The id of the reference referred to.
	 * 
	 * null of the reference is null or has a null id itself.
	 * 
	 * @return
	 */
	public String getResourceId() {
		if (resource != null) {
			return resource.getId();
		}
		return null;
	}
}
