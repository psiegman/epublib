package nl.siegmann.epublib.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The spine sections are the sections of the book in the order in which the book should be read.
 * This contrasts with the Table of Contents sections which is an index into the Book's sections.
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

	public Resource getTocResource() {
		return tocResource;
	}
}
