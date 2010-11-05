package nl.siegmann.epublib.domain;



/**
 * Representation of a Book.
 * 
 * @author paul
 *
 */
public class Book {
	private Metadata metadata = new Metadata();
	private Spine spine = new Spine();
	private TableOfContents tableOfContents = new TableOfContents();
	private Resources resources = new Resources();
	
	
	/**
	 * Adds the resource to the table of contents of the book as a child section of the given parentSection
	 * 
	 * @param parentSection
	 * @param sectionTitle
	 * @param resource
	 * @return
	 */
	public TOCReference addToTableOfContents(TOCReference parentSection, String sectionTitle,
			Resource resource) {
		getResources().add(resource);
		return parentSection.addChildSection(new TOCReference(sectionTitle, resource));
	}

	public void generateSpineFromTableOfContents() {
		Spine spine = new Spine(tableOfContents);
		
		// in case the tocResource was already found and assigned
		spine.setTocResource(this.spine.getTocResource());
		
		this.spine = spine;
	}
	
	/**
	 * Adds a resource to the book and creates both a spine and a toc section to point to it.
	 * 
	 * @param title
	 * @param resource
	 * @return
	 */
	public TOCReference addToTableOfContents(String title, Resource resource) {
		getResources().add(resource);
		return tableOfContents.addTOCReference(new TOCReference(title, resource));
	}
	
	
	/**
	 * The Book's metadata (titles, authors, etc)
	 * 
	 * @return
	 */
	public Metadata getMetadata() {
		return metadata;
	}
	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}
	

	public void setResources(Resources resources) {
		this.resources = resources;
	}


	public Resources getResources() {
		return resources;
	}


	public Spine getSpine() {
		return spine;
	}


	public void setSpine(Spine spine) {
		this.spine = spine;
	}


	public TableOfContents getTableOfContents() {
		return tableOfContents;
	}


	public void setTableOfContents(TableOfContents tableOfContents) {
		this.tableOfContents = tableOfContents;
	}
}