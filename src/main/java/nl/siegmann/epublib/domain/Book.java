package nl.siegmann.epublib.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class Book {
	private Resource coverPage;
	private Resource coverImage;
	private Resource ncxResource;
	private Metadata metadata = new Metadata();
	private List<String> subjects = new ArrayList<String>();
	private List<Section> sections = new ArrayList<Section>();
	private Collection<Resource> resources = new ArrayList<Resource>();

	
	public List<Section> getSections() {
		return sections;
	}
	public void setSections(List<Section> sections) {
		this.sections = sections;
	}
	public List<String> getSubjects() {
		return subjects;
	}
	public void setSubjects(List<String> subjects) {
		this.subjects = subjects;
	}
	public Collection<Resource> getResources() {
		return resources;
	}
	public void setResources(Collection<Resource> resources) {
		this.resources = new ArrayList<Resource>(resources);
	}
	public void addResource(Resource resource) {
		this.resources.add(resource);
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
	
}
