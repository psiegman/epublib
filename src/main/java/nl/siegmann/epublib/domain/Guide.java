package nl.siegmann.epublib.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * The guide is a selection of special pages of the book.
 * Examples of these are the cover, list if illustrations, etc.
 * 
 * It is an optional part of an epub, and support for the various types of references varies by reader.
 * 
 * The only part of this that is heavily used is the cover page.
 * 
 * @author paul
 *
 */
public class Guide {

	private List<GuideReference> references = new ArrayList<GuideReference>();
	private Resource coverPage;

	public List<GuideReference> getReferences() {
		return references;
	}

	public void setReferences(List<GuideReference> references) {
		this.references = references;
	}
	
	/**
	 * The coverpage of the book.
	 * 
	 * @return
	 */
	public Resource getCoverPage() {
		return coverPage;
	}
	public void setCoverPage(Resource coverPage) {
		this.coverPage = coverPage;
	}
	

	public ResourceReference addReference(GuideReference reference) {
		this.references.add(reference);
		return reference;
	}
}
