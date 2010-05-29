package nl.siegmann.epublib.domain;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

/**
 * A Book's identifier.
 * Defaults to a random UUID and scheme "UUID"
 * 
 * @author paul
 *
 */
public class Identifier {
	
	public interface Scheme {
		String UUID = "UUID";
		String ISBN = "ISBN";
		String URL = "URL";
	}
	
	private boolean bookId = false;
	private String scheme;
	private String value;

	/**
	 * Creates an Identifier with as value a random UUID and scheme "UUID"
	 */
	public Identifier() {
		this(Scheme.UUID, UUID.randomUUID().toString());
	}
	
	
	public Identifier(String scheme, String value) {
		this.scheme = scheme;
		this.value = value;
	}

	/**
	 * The first identifier for which the bookId is true is made the bookId identifier.
	 * If no identifier has bookId == true then the first bookId identifier is written as the primary.
	 * 
	 * @param identifiers
	 * @return
	 */
	public static Identifier getBookIdIdentifier(List<Identifier> identifiers) {
		if(identifiers == null || identifiers.isEmpty()) {
			return null;
		}
		
		Identifier result = null;
		for(Identifier identifier: identifiers) {
			if(identifier.isBookId()) {
				result = identifier;
				break;
			}
		}
		
		if(result == null) {
			result = identifiers.get(0);
		}
		
		return result;
	}
	
	public String getScheme() {
		return scheme;
	}
	public void setScheme(String scheme) {
		this.scheme = scheme;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}


	public void setBookId(boolean bookId) {
		this.bookId = bookId;
	}


	/**
	 * This bookId property allows the book creator to add multiple ids and tell the epubwriter which one to write out as the bookId.
	 *  
	 * The Dublin Core metadata spec allows multiple identifiers for a Book.
	 * The epub spec requires exactly one identifier to be marked as the book id.
	 * 
	 * @return
	 */
	public boolean isBookId() {
		return bookId;
	}

	public boolean equals(Object otherIdentifier) {
		if(! (otherIdentifier instanceof Identifier)) {
			return false;
		}
		return StringUtils.equals(scheme, ((Identifier) otherIdentifier).scheme)
		&& StringUtils.equals(value, ((Identifier) otherIdentifier).value);
	}
}
