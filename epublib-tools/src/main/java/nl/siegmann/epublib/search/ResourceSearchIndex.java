package nl.siegmann.epublib.search;

import nl.siegmann.epublib.domain.Resource;

/**
 * The search index for a single resource.
 * 
 * @author paul.siegmann
 *
 */
// package
class ResourceSearchIndex {
	private String content;
	private Resource resource;

	public ResourceSearchIndex(Resource resource, String searchContent) {
		this.resource = resource;
		this.content = searchContent;
	}

	public String getContent() {
		return content;
	}

	public Resource getResource() {
		return resource;
	}

}