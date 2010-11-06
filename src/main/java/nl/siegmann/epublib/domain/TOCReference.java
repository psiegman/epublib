package nl.siegmann.epublib.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * An item in the Table of Contents.
 * 
 * @see nl.siegmann.epublib.domain.TableOfContents
 * 
 * @author paul
 *
 */
public class TOCReference extends TitledResourceReference {

	private List<TOCReference> children;
	
	public TOCReference() {
		this(null, null, null);
	}
	
	public TOCReference(String name, Resource resource) {
		this(name, resource, null);
	}
	
	public TOCReference(String name, Resource resource, String fragmentId) {
		this(name, resource, fragmentId, new ArrayList<TOCReference>());
	}
	
	public TOCReference(String title, Resource resource, String fragmentId, List<TOCReference> children) {
		super(resource, title, fragmentId);
		this.children = children;
	}

	public List<TOCReference> getChildren() {
		return children;
	}

	public TOCReference addChildSection(TOCReference childSection) {
		this.children.add(childSection);
		return childSection;
	}
	
	public void setChildren(List<TOCReference> children) {
		this.children = children;
	}
}
