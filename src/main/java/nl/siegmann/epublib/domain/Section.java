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
public class Section {
	private boolean partOfTableOfContents = true;
	private boolean partOfPageFlow = true;
	private String name;
	private String href;
	private String itemId;
	private List<Section> children;
	
	public Section() {
		this(null, null);
	}
	public Section(String name, String href) {
		this(name, href, new ArrayList<Section>());
	}
	
	public Section(String name, String href, List<Section> children) {
		super();
		this.name = name;
		this.href = href;
		this.children = children;
	}

	public String getItemId() {
		return itemId;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}

	public List<Section> getChildren() {
		return children;
	}

	public void addChildSection(Section childSection) {
		this.children.add(childSection);
	}
	
	public void setChildren(List<Section> children) {
		this.children = children;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public boolean isPartOfTableOfContents() {
		return partOfTableOfContents;
	}

	public void setPartOfTableOfContents(boolean partOfTableOfContents) {
		this.partOfTableOfContents = partOfTableOfContents;
	}

	public boolean isPartOfPageFlow() {
		return partOfPageFlow;
	}

	public void setPartOfPageFlow(boolean partOfPageFlow) {
		this.partOfPageFlow = partOfPageFlow;
	}
}
