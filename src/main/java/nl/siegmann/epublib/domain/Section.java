package nl.siegmann.epublib.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * A Section of a book.
 * Represents both an item in the package document and a item in the index.
 * 
 * @author paul
 *
 */
public class Section extends ResourceReference {
	private List<Section> children;
	
	public Section() {
		this(null, null);
	}
	public Section(String name, Resource resource) {
		this(name, resource, new ArrayList<Section>());
	}
	
	public Section(String title, Resource resource, List<Section> children) {
		super(title,resource);
		this.children = children;
	}

	public String getItemId() {
		if (resource != null) {
			return resource.getId();
		}
		return null;
	}
	
	public List<Section> getChildren() {
		return children;
	}

	public Section addChildSection(Section childSection) {
		this.children.add(childSection);
		return childSection;
	}
	
	public void setChildren(List<Section> children) {
		this.children = children;
	}
}
