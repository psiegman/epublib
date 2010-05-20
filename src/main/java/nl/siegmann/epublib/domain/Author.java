package nl.siegmann.epublib.domain;

import org.apache.commons.lang.StringUtils;

/**
 * Represents one of the authors of the book
 * 
 * @author paul
 *
 */
public class Author {
	private String firstname;
	private String lastname;
	
	public Author(String singleName) {
		this("", singleName);
	}
	
	
	public Author(String firstname, String lastname) {
		this.firstname = firstname;
		this.lastname = lastname;
	}
	
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	
	public String toString() {
		return lastname + ", " + firstname;
	}
	public boolean equals(Object authorObject) {
		if(! (authorObject instanceof Author)) {
			return false;
		}
		Author other = (Author) authorObject;
		return StringUtils.equals(firstname, other.firstname)
		 && StringUtils.equals(lastname, other.lastname);
	}
}
