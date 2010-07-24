package nl.siegmann.epublib.domain;

import java.util.ArrayList;
import java.util.List;

public class Guide {

	public interface Types {
		String COVER = "cover";
		String TITLE_PAGE = "title-page";
		String TOC = "toc";
		String TEXT = "text";
		String PREFACE = "preface";
	}

	private List<GuideReference> references = new ArrayList<GuideReference>();
	private Resource coverPage;
	private Resource coverImage;

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
	
	/**
	 * The main image used by the cover page.
	 * 
	 * @return
	 */
	public Resource getCoverImage() {
		return coverImage;
	}
	public void setCoverImage(Resource coverImage) {
		this.coverImage = coverImage;
	}

	public ResourceReference addReference(GuideReference reference) {
		this.references.add(reference);
		return reference;
	}
}
