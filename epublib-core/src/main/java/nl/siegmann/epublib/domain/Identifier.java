package nl.siegmann.epublib.domain;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import nl.siegmann.epublib.util.StringUtil;

/**
 * A Book's identifier.
 * 
 * Defaults to a random UUID and scheme "UUID"
 * 
 * @author paul
 *
 */
public class Identifier implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 955949951416391810L;
	
	private boolean bookId = false;
	private Scheme scheme;
	private String value;

	/**
	 * Creates an Identifier with as value a random UUID and scheme "UUID"
	 */
	public Identifier() {
		this(Scheme.UUID, UUID.randomUUID().toString());
	}
	
	
	public Identifier(Scheme scheme, String value) {
		this.scheme = scheme;
		this.value = value;
	}

	public Scheme getScheme() {
		return scheme;
	}

	public void setScheme(Scheme scheme) {
		this.scheme = scheme;
	}

	/**
	 * The first identifier for which the bookId is true is made the bookId identifier.
	 * If no identifier has bookId == true then the first bookId identifier is written as the primary.
	 * 
	 * @param identifiers
	 * @return The first identifier for which the bookId is true is made the bookId identifier.
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
	 * @return whether this is the unique book id.
	 */
	public boolean isBookId() {
		return bookId;
	}

	public int hashCode() {
		return StringUtil.defaultIfNull(scheme.getName()).hashCode() ^ StringUtil.defaultIfNull(value).hashCode();
	}
	
	public boolean equals(Object otherIdentifier) {
		if(! (otherIdentifier instanceof Identifier)) {
			return false;
		}
		return StringUtil.equals(scheme.getName(), ((Identifier) otherIdentifier).scheme.getName())
		&& StringUtil.equals(value, ((Identifier) otherIdentifier).value);
	}
	
	public String toString() {
		if (StringUtil.isBlank(scheme.getName())) {
			return "" + value;
		}
		return "" + scheme + ":" + value;
	}
}
