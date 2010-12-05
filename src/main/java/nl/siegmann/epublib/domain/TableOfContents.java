package nl.siegmann.epublib.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The table of contents of the book.
 * 
 * The table of contents is used by epub as a quick index to chapters.
 * It may contain duplicate entries, may decide to point not to certain chapters, etc.
 * 
 * See the spine for the complete list of sections in the order in which they should be read.
 * 
 * @see nl.siegmann.epublib.domain.Spine
 * 
 * @author paul
 *
 */
public class TableOfContents implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3147391239966275152L;
	private List<TOCReference> tocReferences;

	public TableOfContents() {
		this(new ArrayList<TOCReference>());
	}
	
	public TableOfContents(List<TOCReference> tocReferences) {
		this.tocReferences = tocReferences;
	}

	public List<TOCReference> getTocReferences() {
		return tocReferences;
	}

	public void setTocReferences(List<TOCReference> tocReferences) {
		this.tocReferences = tocReferences;
	}
	
	public TOCReference addResourceAtLocation(Resource resource, String path,  String pathSeparator) {
		String[] pathElements = path.split(pathSeparator);
		return addTOCReferenceAtLocation(resource, pathElements, 0, tocReferences);
	}
	
	private static TOCReference findTocReferenceByTitle(String title, List<TOCReference> tocReferences) {
		for (TOCReference tocReference: tocReferences) {
			if (title.equals(tocReference.getTitle())) {
				return tocReference;
			}
		}
		return null;
	}
	
	public static TOCReference addTOCReferenceAtLocation(Resource resource, String[] pathElements, int pathPos, List<TOCReference> tocReferences) {
		String currentTitle = pathElements[pathPos];
		TOCReference currentTocReference = findTocReferenceByTitle(currentTitle, tocReferences);
		if (currentTocReference == null) {
			currentTocReference = new TOCReference(currentTitle, null);
			tocReferences.add(currentTocReference);
		}
		TOCReference result;
		if (pathPos >= (pathElements.length - 1)) {
			currentTocReference.setResource(resource);
			result = currentTocReference;
		} else {
			result = addTOCReferenceAtLocation(resource, pathElements, pathPos + 1, currentTocReference.getChildren());
		}
		return result;
	}
	
	
	public TOCReference addTOCReference(TOCReference tocReference) {
		if (tocReferences == null) {
			tocReferences = new ArrayList<TOCReference>();
		}
		tocReferences.add(tocReference);
		return tocReference;
	}
	
	/**
	 * All unique references (unique by href) in the order in which they are referenced to in the table of contents.
	 * 
	 * @return
	 */
	public List<Resource> getAllUniqueResources() {
		Set<String> uniqueHrefs = new HashSet<String>();
		List<Resource> result = new ArrayList<Resource>();
		getAllUniqueResources(uniqueHrefs, result, tocReferences);
		return result;
	}
	
	
	private static void getAllUniqueResources(Set<String> uniqueHrefs, List<Resource> result, List<TOCReference> tocReferences) {
		for (TOCReference tocReference: tocReferences) {
			Resource resource = tocReference.getResource();
			if (resource != null && ! uniqueHrefs.contains(resource.getHref())) {
				uniqueHrefs.add(resource.getHref());
				result.add(resource);
			}
			getAllUniqueResources(uniqueHrefs, result, tocReference.getChildren());
		}
	}

	/**
	 * The total number of references in this table of contents.
	 * 
	 * @return
	 */
	public int size() {
		return getTotalSize(tocReferences);
	}
	
	private static int getTotalSize(Collection<TOCReference> tocReferences) {
		int result = tocReferences.size();
		for (TOCReference tocReference: tocReferences) {
			result += getTotalSize(tocReference.getChildren());
		}
		return result;
	}
	
	public int calculateDepth() {
		return calculateDepth(tocReferences, 0);
	}

	private int calculateDepth(List<TOCReference> tocReferences, int currentDepth) {
		int maxChildDepth = 0;
		for (TOCReference tocReference: tocReferences) {
			int childDepth = calculateDepth(tocReference.getChildren(), 1);
			if (childDepth > maxChildDepth) {
				maxChildDepth = childDepth;
			}
		}
		return currentDepth + maxChildDepth;
	}
}
