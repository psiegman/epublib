package nl.siegmann.epublib.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * The spine sections are the sections of the book in the order in which the book should be read.
 * 
 * This contrasts with the Table of Contents sections which is an index into the Book's sections.
 *
 * @see nl.siegmann.epublib.domain.TableOfContents
 * 
 * @author paul
 *
 */
public class Spine {

	private Resource tocResource;
	private List<SpineReference> spineReferences;

	public Spine() {
		this(new ArrayList<SpineReference>());
	}
	
	public Spine(TableOfContents tableOfContents) {
		this.spineReferences = createSpineReferences(tableOfContents.getAllUniqueResources());
	}

	public Spine(List<SpineReference> spineReferences) {
		this.spineReferences = spineReferences;
	}

	public static List<SpineReference> createSpineReferences(Collection<Resource> resources) {
		List<SpineReference> result = new ArrayList<SpineReference>(resources.size());
		for (Resource resource: resources) {
			result.add(new SpineReference(resource));
		}
		return result;
	}
	
	public List<SpineReference> getSpineReferences() {
		return spineReferences;
	}
	public void setSpineReferences(List<SpineReference> spineReferences) {
		this.spineReferences = spineReferences;
	}

	
	public Resource getResource(int index) {
		if (index < 0 || index >= spineReferences.size()) {
			return null;
		}
		return spineReferences.get(index).getResource();
	}
	
	
	public int findFirstResourceById(String resourceId) {
		if (StringUtils.isBlank(resourceId)) {
			return -1;
		}
		
		for (int i = 0; i < spineReferences.size(); i++) {
			SpineReference spineReference = spineReferences.get(i);
			if (resourceId.equals(spineReference.getResourceId())) {
				return i;
			}
		}
		return -1;
	}
	
	public SpineReference addSpineReference(SpineReference spineReference) {
		if (spineReferences == null) {
			this.spineReferences = new ArrayList<SpineReference>();
		}
		spineReferences.add(spineReference);
		return spineReference;
	}

	public int size() {
		return spineReferences.size();
	}

	public void setTocResource(Resource tocResource) {
		this.tocResource = tocResource;
	}

	/**
	 * The resource containing the XML for the tableOfContents.
	 * When saving an epub file this resource needs to be in this place.
	 * 
	 * @return
	 */
	public Resource getTocResource() {
		return tocResource;
	}

	public int getResourceIndex(Resource currentResource) {
		int result = -1;
		for (int i = 0; i < spineReferences.size(); i++) {
			if (currentResource.getHref().equals(spineReferences.get(i).getResource().getHref())) {
				result = i;
				break;
			}
		}
		return result;
	}
}
