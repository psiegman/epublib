package nl.siegmann.epublib.domain;



/**
 * Representation of a Book.
 * 
 * @author paul
 *
 */
public class Book {
	
	private Resources resources = new Resources();
	private Metadata metadata = new Metadata();
	private Spine spine = new Spine();
	private TableOfContents tableOfContents = new TableOfContents();
	private Guide guide = new Guide();
	
	
	public SectionWalker createSectionWalker() {
		return new SectionWalker(this);
	}
	
	/**
	 * Adds the resource to the table of contents of the book as a child section of the given parentSection
	 * 
	 * @param parentSection
	 * @param sectionTitle
	 * @param resource
	 * @return
	 */
	public TOCReference addSection(TOCReference parentSection, String sectionTitle,
			Resource resource) {
		getResources().add(resource);
		if (spine.findFirstResourceById(resource.getId()) < 0)  {
			spine.addSpineReference(new SpineReference(resource));
		}
		return parentSection.addChildSection(new TOCReference(sectionTitle, resource));
	}

	public void generateSpineFromTableOfContents() {
		Spine spine = new Spine(tableOfContents);
		
		// in case the tocResource was already found and assigned
		spine.setTocResource(this.spine.getTocResource());
		
		this.spine = spine;
	}
	
	/**
	 * Adds a resource to the book's set of resources, table of contents and if there is no resource with the id in the spine also adds it to the spine.
	 * 
	 * @param title
	 * @param resource
	 * @return
	 */
	public TOCReference addSection(String title, Resource resource) {
		getResources().add(resource);
		TOCReference tocReference = tableOfContents.addTOCReference(new TOCReference(title, resource));
		if (spine.findFirstResourceById(resource.getId()) < 0)  {
			spine.addSpineReference(new SpineReference(resource));
		}
		return tocReference;
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


	public Resource addResource(Resource resource) {
		return resources.add(resource);
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
	
	/**
	 * The book's cover page.
	 * An XHTML document containing a link to the cover image.
	 * 
	 * @return
	 */
	public Resource getCoverPage() {
		Resource coverPage = guide.getCoverPage();
		if (coverPage == null) {
			coverPage = spine.getResource(0);
		}
		return coverPage;
	}
	
	
	public void setCoverPage(Resource coverPage) {
		if (! resources.containsByHref(coverPage.getHref())) {
			resources.add(coverPage);
		}
		guide.setCoverPage(coverPage);
	}
	
	/**
	 * Gets the first non-blank title from the book's metadata.
	 * 
	 * @return
	 */
	public String getTitle() {
		return getMetadata().getFirstTitle();
	}
	
	
	/**
	 * The book's cover image.
	 * 
	 * @return
	 */
	public Resource getCoverImage() {
		return metadata.getCoverImage();
	}

	public void setCoverImage(Resource coverImage) {
		if (! resources.containsByHref(coverImage.getHref())) {
			resources.add(coverImage);
		}
		metadata.setCoverImage(coverImage);
	}
	
	public Guide getGuide() {
		return guide;
	}

}