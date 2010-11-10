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
	private static final int COVERPAGE_NOT_FOUND = -1;
	private static final int COVERPAGE_UNITIALIZED = -2;
	
	private int coverPageIndex = -1;
	
	public List<GuideReference> getReferences() {
		return references;
	}

	public void setReferences(List<GuideReference> references) {
		this.references = references;
		uncheckCoverPage();
	}
	
	private void uncheckCoverPage() {
		coverPageIndex = COVERPAGE_UNITIALIZED;
	}

	public GuideReference getCoverReference() {
		checkCoverPage();
		if (coverPageIndex >= 0) {
			return references.get(coverPageIndex);
		}
		return null;
	}
	
	public int setCoverReference(GuideReference guideReference) {
		if (coverPageIndex >= 0) {
			references.set(coverPageIndex, guideReference);
		} else {
			references.add(0, guideReference);
			coverPageIndex = 0;
		}
		return coverPageIndex;
	}

	private void checkCoverPage() {
		if (coverPageIndex == COVERPAGE_UNITIALIZED) {
			initCoverPage();
		}
	}
	
	
	private void initCoverPage() {
		int result = COVERPAGE_NOT_FOUND;
		for (int i = 0; i < references.size(); i++) {
			GuideReference guideReference = references.get(i);
			if (guideReference.getType().equals(GuideReference.COVER)) {
				result = i;
				break;
			}
		}
		coverPageIndex = result;
	}
	
	/**
	 * The coverpage of the book.
	 * 
	 * @return
	 */
	public Resource getCoverPage() {
		GuideReference guideReference = getCoverReference();
		if (guideReference == null) {
			return null;
		}
		return guideReference.getResource();
	}

	public void setCoverPage(Resource coverPage) {
		GuideReference coverpageGuideReference = new GuideReference(coverPage, GuideReference.COVER, DEFAULT_COVER_TITLE);
		setCoverReference(coverpageGuideReference);
	}
	

	public ResourceReference addReference(GuideReference reference) {
		this.references.add(reference);
		uncheckCoverPage();
		return reference;
	}
}