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

	public static final String DEFAULT_COVER_TITLE = GuideReference.COVER;
	
	private List<GuideReference> references = new ArrayList<GuideReference>();
	private GuideReference coverPage;
	
	public List<GuideReference> getReferences() {
		return references;
	}

	public void setReferences(List<GuideReference> references) {
		this.references = references;
	}
	
	public GuideReference getCoverReference() {
		return coverPage;
	}
	
	public void setCoverReference(GuideReference guideReference) {
		this.coverPage = guideReference;
	}

	/**
	 * The coverpage of the book.
	 * 
	 * @return
	 */
	public Resource getCoverPage() {
		if (coverPage == null) {
			return null;
		}
		return coverPage.getResource();
	}

	public void setCoverPage(Resource coverPage) {
		this.coverPage = new GuideReference(coverPage, GuideReference.COVER, DEFAULT_COVER_TITLE);
	}
	

	public ResourceReference addReference(GuideReference reference) {
		this.references.add(reference);
		return reference;
	}
}
