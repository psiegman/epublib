package nl.siegmann.epublib;

import java.util.ArrayList;
import java.util.List;

public class Section {
	private String id;
	private String name;
	private String href;
	private List<Section> children;
	
	public Section(String id, String name, String href) {
		this(id, name, href, new ArrayList<Section>());
	}
	
	public Section(String id, String name, String href, List<Section> children) {
		super();
		this.id = id;
		this.name = name;
		this.href = href;
		this.children = children;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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

	public void setChildren(List<Section> children) {
		this.children = children;
	}	
}
