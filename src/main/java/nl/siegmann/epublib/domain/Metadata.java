package nl.siegmann.epublib.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

public class Metadata {

	public static final String DEFAULT_LANGUAGE = "en";
	private List<Author> authors = new ArrayList<Author>();
	private Date date = new Date();
	private String language = DEFAULT_LANGUAGE;
	private Map<QName, String> otherProperties = new HashMap<QName, String>();
	private String rights = "";
	private String title = "";
	private Identifier identifier = new Identifier();

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
}
