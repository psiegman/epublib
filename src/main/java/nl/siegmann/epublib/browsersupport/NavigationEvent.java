package nl.siegmann.epublib.browsersupport;

import java.util.EventObject;

import nl.siegmann.epublib.domain.Resource;

import org.apache.commons.lang.StringUtils;

/**
 * Used to tell NavigationEventListener just what kind of navigation action the user just did.
 * 
 * @author paul
 *
 */
public class NavigationEvent extends EventObject {
	private static final long serialVersionUID = -6346750144308952762L;
	
	private final Resource oldResource;
	private final int oldSpinePos;
	private final Navigator navigator;
	
	public NavigationEvent(Object source, int oldPosition, Resource oldResource, Navigator navigator) {
		super(source);
		this.oldSpinePos = oldPosition;
		this.oldResource = oldResource;
		this.navigator = navigator;
	}

	public Navigator getSectionWalker() {
		return navigator;
	}
	
	public int getOldSpinePos() {
		return oldSpinePos;
	}
	
	public int getCurrentSpinePos() {
		return navigator.getCurrentSpinePos();
	}
	
	public String getCurrentFragmentId() {
		return "";
	}
	
	public String getPreviousFragmentId() {
		return "";
	}
	
	public boolean isSpinePosChanged() {
		return getOldSpinePos() != getCurrentSpinePos();
	}

	public boolean isFragmentChanged() {
		return StringUtils.equals(getPreviousFragmentId(), getCurrentFragmentId());
	}

	public Resource getOldResource() {
		return oldResource;
	}
	
	public Resource getCurrentResource() {
		return navigator.getCurrentResource();
	}
}