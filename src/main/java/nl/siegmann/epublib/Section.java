package nl.siegmann.epublib;

public class Section {
	private String id;
	private String name;
	private String href;
	
	public Section(String id, String name, String href) {
		super();
		this.id = id;
		this.name = name;
		this.href = href;
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
	
	
}
