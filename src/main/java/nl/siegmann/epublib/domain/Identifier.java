package nl.siegmann.epublib.domain;

import java.util.UUID;

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
	
	private String scheme;
	private String value;

	/**
	 * Creates an Identifier with as value a random UUID and scheme "UUID"
	 */
	public Identifier() {
		this(UUID.randomUUID().toString(), Scheme.UUID);
	}
	
	
	public Identifier(String scheme, String value) {
		this.scheme = scheme;
		this.value = value;
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
}
