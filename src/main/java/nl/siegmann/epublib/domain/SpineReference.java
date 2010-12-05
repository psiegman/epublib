package nl.siegmann.epublib.domain;

import java.io.Serializable;


/**
 * A Section of a book.
 * Represents both an item in the package document and a item in the index.
 * 
 * @author paul
 *
 */
public class SpineReference extends ResourceReference implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7921609197351510248L;
	private boolean linear = true;
	
	public SpineReference(Resource resource) {
		this(resource, true);
	}
	
	
	public SpineReference(Resource resource, boolean linear) {
		super(resource);
		this.linear = linear;
	}

	/**
	 * Linear denotes whether the section is Primary or Auxiliary.
	 * Usually the cover page has linear set to false and all the other sections
	 * have it set to true.
	 * 
	 * It's an optional property that readers may also ignore.
	 * 
	 * <blockquote>primary or auxiliary is useful for Reading Systems which
	 * opt to present auxiliary content differently than primary content.
	 * For example, a Reading System might opt to render auxiliary content in
	 * a popup window apart from the main window which presents the primary
	 * content. (For an example of the types of content that may be considered
	 * auxiliary, refer to the example below and the subsequent discussion.)</blockquote>
	 * @see http://www.idpf.org/2007/opf/OPF_2.0_final_spec.html#TOC2.4
	 * 
	 * @return
	 */
	public boolean isLinear() {
		return linear;
	}

	public void setLinear(boolean linear) {
		this.linear = linear;
	}

}
