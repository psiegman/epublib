package nl.siegmann.epublib.domain;

public enum ManifestItemRefProperties implements ManifestProperties {
	PAGE_SPREAD_LEFT("page-spread-left"),
	PAGE_SPREAD_RIGHT("page-spread-right");
	
	private String name;
	
	private ManifestItemRefProperties(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
