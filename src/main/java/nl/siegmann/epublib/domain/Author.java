package nl.siegmann.epublib.domain;

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
}
