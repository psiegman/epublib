package nl.siegmann.epublib.domain;


/**
 * A Section of a book.
 * Represents both an item in the package document and a item in the index.
 * 
 * @author paul
 *
 */
public class SpineReference extends ResourceReference {
	
	private boolean linear = true;
	
	public SpineReference(Resource resource) {
		this(resource, true);
	}
	
	
	public SpineReference(Resource resource, boolean linear) {
		super(resource);
		this.linear = linear;
	}

	public boolean isLinear() {
		return linear;
	}

	public void setLinear(boolean linear) {
		this.linear = linear;
	}

}
