package nl.siegmann.epublib.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 * A Book's collection of Metadata.
 * In the future it should contain all Dublin Core attributes, for now it contains a set of often-used ones.
 * 
 * @author paul
 *
 */
public class Metadata {

	public static final String DEFAULT_LANGUAGE = "en";
	private List<Author> authors = new ArrayList<Author>();
	private Date date = new Date();
	private String language = DEFAULT_LANGUAGE;
	private Map<QName, String> otherProperties = new HashMap<QName, String>();
	private String rights = "";
	private String title = "";
	private Identifier identifier = new Identifier();
	private List<String> subjects = new ArrayList<String>();

	/*
	 * 
	 
	 Contributor  	An entity responsible for making contributions to the content of the resource
Coverage 	The extent or scope of the content of the resource
Creator 	An entity primarily responsible for making the content of the resource
Format 	The physical or digital manifestation of the resource
Date 	A date of an event in the lifecycle of the resource
Description 	An account of the content of the resource
Identifier 	An unambiguous reference to the resource within a given context
Language 	A language of the intellectual content of the resource
Publisher 	An entity responsible for making the resource available
Relation 	A reference to a related resource
Rights 	Information about rights held in and over the resource
Source 	A Reference to a resource from which the present resource is derived
Subject 	A topic of the content of the resource
Title 	A name given to the resource
Type 	The nature or genre of the content of the resource
	 
	 
	 */
	
	
	/**
	 * Metadata properties not hard-coded like the author, title, etc.
	 * 
	 * @return
	 */
	public Map<QName, String> getOtherProperties() {
		return otherProperties;
	}
	public void setOtherProperties(Map<QName, String> otherProperties) {
		this.otherProperties = otherProperties;
	}
	public List<Author> getAuthors() {
		return authors;
	}
	public void setAuthors(List<Author> authors) {
		this.authors = authors;
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
	public String getRights() {
		return rights;
	}
	public void setRights(String rights) {
		this.rights = rights;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Identifier getIdentifier() {
		return identifier;
	}
	public void setIdentifier(Identifier identifier) {
		this.identifier = identifier;
	}
	public List<String> getSubjects() {
		return subjects;
	}
	public void setSubjects(List<String> subjects) {
		this.subjects = subjects;
	}
}
