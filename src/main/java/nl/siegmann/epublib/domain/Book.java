package nl.siegmann.epublib.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.namespace.QName;


public class Book {
	private String title = "";
    private String rights = "";
	private String uid = UUID.randomUUID().toString();
	private List<Author> authors = new ArrayList<Author>();
	private List<String> subjects = new ArrayList<String>();
	private Map<QName, String> metadataProperties = new HashMap<QName, String>();
	private Date date = new Date();
	private String language = "";
	
	private List<Section> sections = new ArrayList<Section>();
	private Collection<Resource> resources = new ArrayList<Resource>();

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<Section> getSections() {
		return sections;
	}
	public void setSections(List<Section> sections) {
		this.sections = sections;
	}
	public String getRights() {
		return rights;
	}
	public void setRights(String rights) {
		this.rights = rights;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public List<Author> getAuthors() {
		return authors;
	}
	public void setAuthors(List<Author> authors) {
		this.authors = authors;
	}
	public List<String> getSubjects() {
		return subjects;
	}
	public void setSubjects(List<String> subjects) {
		this.subjects = subjects;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public Collection<Resource> getResources() {
		return resources;
	}
	public void setResources(Collection<Resource> resources) {
		this.resources = resources;
	}
	
	/**
	 * Metadata properties not hard-coded like the author, title, etc.
	 * 
	 * @return
	 */
	public Map<QName, String> getMetadataProperties() {
		return metadataProperties;
	}
	public void setMetadataProperties(Map<QName, String> metadataProperties) {
		this.metadataProperties = metadataProperties;
	}
}
