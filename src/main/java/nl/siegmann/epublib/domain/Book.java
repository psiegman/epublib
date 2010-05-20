package nl.siegmann.epublib.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


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
	private Collection<Resource> resources = new ArrayList<Resource>();

	public Section addSection(Section section) {
		spineSections.add(section);
		tocSections.add(section);
		return section;
	}
	
	public Collection<Resource> getResources() {
		return resources;
	}
	public void setResources(Collection<Resource> resources) {
		this.resources = new ArrayList<Resource>(resources);
	}

	public Section addResourceAsSection(String title, Resource resource) {
		addResource(resource);
		return addSection(new Section(title, resource.getHref()));
	}
	
	public Section addResourceAsSubSection(Section parentSection, String sectionTitle,
			Resource resource) {
		addResource(resource);
		return parentSection.addChildSection(new Section(sectionTitle, resource.getHref()));
	}
	public Resource addResource(Resource resource) {
		this.resources.add(resource);
		return resource;
	}
	
	public Resource getResourceByHref(String href) {
		for(Resource resource: resources) {
			if(href.equals(resource.getHref())) {
				return resource;
			}
		}
		return null;
	}
	public Metadata getMetadata() {
		return metadata;
	}
	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}
	public Resource getCoverPage() {
		return coverPage;
	}
	public void setCoverPage(Resource coverPage) {
		this.coverPage = coverPage;
	}
	public Resource getCoverImage() {
		return coverImage;
	}
	public void setCoverImage(Resource coverImage) {
		this.coverImage = coverImage;
	}
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
	
	public List<Section> getSpineSections() {
		return spineSections;
	}
	
	public void setSpineSections(List<Section> spineSections) {
		this.spineSections = spineSections;
	}
	
	public List<Section> getTocSections() {
		return tocSections;
	}
	
	public void setTocSections(List<Section> tocSections) {
		this.tocSections = tocSections;
	}
}