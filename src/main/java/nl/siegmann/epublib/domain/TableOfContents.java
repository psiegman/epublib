package nl.siegmann.epublib.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TableOfContents {

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

	public TOCReference addTOCReference(TOCReference tocReference) {
		if (tocReferences == null) {
			tocReferences = new ArrayList<TOCReference>();
		}
		tocReferences.add(tocReference);
		return tocReference;
	}
	
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

	public int size() {
		return tocReferences.size();
	}
	
	public int getTotalSize() {
		return getTotalSize(tocReferences);
	}
	
	private static int getTotalSize(Collection<TOCReference> tocReferences) {
		int result = tocReferences.size();
		for (TOCReference tocReference: tocReferences) {
			result += getTotalSize(tocReference.getChildren());
		}
		return result;
	}
}
