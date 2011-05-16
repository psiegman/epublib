package nl.siegmann.epublib.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The table of contents of the book.
 * The TableOfContents is a tree structure at the root it is a list of TOCReferences, each if which may have as children another list of TOCReferences.
 * 
 * The table of contents is used by epub as a quick index to chapters and sections within chapters.
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
	private static final String DEFAULT_PATH_SEPARATOR = "/";
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
	
	/**
	 * Calls addTOCReferenceAtLocation after splitting the path using the DEFAULT_PATH_SEPARATOR.
	 */
	public TOCReference addResourceAtLocation(Resource resource, String path) {
		return addResourceAtLocation(resource, path, DEFAULT_PATH_SEPARATOR);
	}
	
	/**
	 * Calls addTOCReferenceAtLocation after splitting the path using the given pathSeparator.
	 * 
	 * @param resource
	 * @param path
	 * @param pathSeparator
	 * @return
	 */
	public TOCReference addResourceAtLocation(Resource resource, String path, String pathSeparator) {
		String[] pathElements = path.split(pathSeparator);
		return addTOCReferenceAtLocation(resource, pathElements);
	}
	
	/**
	 * Finds the first TOCReference in the given list that has the same title as the given Title.
	 * 
	 * @param title
	 * @param tocReferences
	 * @return null if not found.
	 */
	private static TOCReference findTocReferenceByTitle(String title, List<TOCReference> tocReferences) {
		for (TOCReference tocReference: tocReferences) {
			if (title.equals(tocReference.getTitle())) {
				return tocReference;
			}
		}
		return null;
	}

	/**
	 * Adds the given Resources to the TableOfContents at the location specified by the pathElements.
	 * 
	 * Example:
	 * Calling this method with a Resource and new String[] {"chapter1", "paragraph1"} will result in the following:
	 * <ul>
	 * <li>a TOCReference with title "chapter1" at the root level.<br/>
	 * If this TOCReference did not yet exist it will have been created and does not point to any resource</li>
	 * <li>A TOCReference that has the title "paragraph1". This TOCReference will be the child of TOCReference "chapter1" and
	 * will point to the given Resource</li>
	 * </ul>
	 *    
	 * @param resource
	 * @param pathElements
	 * @return
	 */
	public TOCReference addTOCReferenceAtLocation(Resource resource, String[] pathElements) {
		if (pathElements == null || pathElements.length == 0) {
			return null;
		}
		TOCReference result = null;
		List<TOCReference> currentTocReferences = this.tocReferences;
		for (int i = 0; i < pathElements.length; i++) {
			String currentTitle = pathElements[i];
			result = findTocReferenceByTitle(currentTitle, currentTocReferences);
			if (result == null) {
				result = new TOCReference(currentTitle, null);
				currentTocReferences.add(result);
			}
			currentTocReferences = result.getChildren();
		}
		result.setResource(resource);
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
