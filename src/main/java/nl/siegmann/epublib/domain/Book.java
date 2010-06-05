package nl.siegmann.epublib.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Representation of a Book.
 * 
 * @author paul
 *
 */
public class Book {
	private Resource coverPage;
	private Resource coverImage;
	private Resource ncxResource;
	private Metadata metadata = new Metadata();
	private List<Section> spineSections = new ArrayList<Section>();
	private List<Section> tocSections = new ArrayList<Section>();
	private Map<String, Resource> resources = new HashMap<String, Resource>();

	/**
	 * Adds a section to both the spine and the toc sections.
	 * 
	 * @param section
	 * @return
	 */
	public Section addSection(Section section) {
		spineSections.add(section);
		tocSections.add(section);
		return section;
	}
	
	/**
	 * The resources that make up this book.
	 * Resources can be xhtml pages, images, xml documents, etc.
	 * 
	 * @return
	 */
	public Map<String, Resource> getResources() {
		return resources;
	}
	
	
	public boolean containsResourceByHref(String href) {
		return resources.containsKey(href);
	}
	
	public void setResources(Collection<Resource> resources) {
		resources.clear();
		addResources(resources);
	}
	
	public void addResources(Collection<Resource> resources) {
		for(Resource resource: resources) {
			this.resources.put(resource.getHref(), resource);
		}
	}

	public void setResources(Map<String, Resource> resources) {
		this.resources = new HashMap<String, Resource>(resources);
	}

	/**
	 * Adds a resource to the book and creates both a spine and a toc section to point to it.
	 * 
	 * @param title
	 * @param resource
	 * @return
	 */
	public Section addResourceAsSection(String title, Resource resource) {
		addResource(resource);
		return addSection(new Section(title, resource.getHref()));
	}
	
	/**
	 * Adds the resource to the book and creates a subsection of the given parentSection pointing to the new resource.
	 * 
	 * @param parentSection
	 * @param sectionTitle
	 * @param resource
	 * @return
	 */
	public Section addResourceAsSubSection(Section parentSection, String sectionTitle,
			Resource resource) {
		addResource(resource);
		return parentSection.addChildSection(new Section(sectionTitle, resource.getHref()));
	}
	public Resource addResource(Resource resource) {
		this.resources.put(resource.getHref(), resource);
		return resource;
	}
	
	public Resource getResourceByHref(String href) {
		Resource result = resources.get(href);
		return result;
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
	
	/**
	 * The coverpage of the book.
	 * 
	 * @return
	 */
	public Resource getCoverPage() {
		return coverPage;
	}
	public void setCoverPage(Resource coverPage) {
		this.coverPage = coverPage;
	}
	
	/**
	 * The main image used by the cover page.
	 * 
	 * @return
	 */
	public Resource getCoverImage() {
		return coverImage;
	}
	public void setCoverImage(Resource coverImage) {
		this.coverImage = coverImage;
	}
	
	
	/**
	 * The NCX resource of the Book (contains the table of contents)
	 * 
	 * @return
	 */
	public Resource getNcxResource() {
		return ncxResource;
	}
	public void setNcxResource(Resource ncxResource) {
		this.ncxResource = ncxResource;
	}

	public void setSections(List<Section> sections) {
		setSpineSections(sections);
		setTocSections(sections);
	}
	
	/**
	 * The spine sections are the sections of the book in the order in which the book should be read.
	 * This contrasts with the Table of Contents sections which is an index into the Book's sections.
	 * 
	 * @return
	 */
	public List<Section> getSpineSections() {
		return spineSections;
	}
	
	public void setSpineSections(List<Section> spineSections) {
		this.spineSections = spineSections;
	}
	
	/**
	 * The Book's table of contents.
	 * 
	 * @return
	 */
	public List<Section> getTocSections() {
		return tocSections;
	}
	
	public void setTocSections(List<Section> tocSections) {
		this.tocSections = tocSections;
	}
}