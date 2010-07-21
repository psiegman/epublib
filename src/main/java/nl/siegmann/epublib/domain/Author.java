package nl.siegmann.epublib.domain;

import org.apache.commons.lang.StringUtils;

/**
 * Represents one of the authors of the book
 * 
 * @author paul
 *
 */
public class Author {
	
	public enum Role {
		AUTHOR("aut"), ILLUSTRATOR("ill");
		
		private final String value;

		Role(String v) {
			value = v;
		}

		public static Role fromValue(String v) {
			for (Role c : Role.values()) {
				if (c.value.equals(v)) {
					return c;
				}
			}
			return null;
		}
		
		public String toString() {
			return value;
		}
	};

	private String firstname;
	private String lastname;
	private Role role = Role.AUTHOR;
	
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

	public Role setRole(String roleName) {
		Role result = Role.fromValue(roleName);
		if (result == null) {
			result = Role.AUTHOR;
		}
		this.role = result;
		return result;
	}

	public Role getRole() {
		return role;
	}


	public void setRole(Role role) {
		this.role = role;
	}
}
