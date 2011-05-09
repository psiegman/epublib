package nl.siegmann.epublib.browsersupport;

import java.util.EventObject;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Used to tell NavigationEventListener just what kind of navigation action the user just did.
 * 
 * @author paul
 *
 */
public class NavigationEvent extends EventObject {

	private static final long serialVersionUID = -6346750144308952762L;
	
	private Resource oldResource;
	private int oldSpinePos;
	private Navigator navigator;
	private Book oldBook;
	private int oldSectionPos;
	private String oldFragmentId;
	
	public NavigationEvent(Object source) {
		super(source);
	}
	
	public NavigationEvent(Object source, Navigator navigator) {
		super(source);
		this.navigator = navigator;
		this.oldBook = navigator.getBook();
		this.oldFragmentId = navigator.getCurrentFragmentId();
		this.oldSectionPos = navigator.getCurrentSectionPos();
		this.oldResource = navigator.getCurrentResource();
		this.oldSpinePos = navigator.getCurrentSpinePos();
	}

	/**
	 * The previous position within the section.
	 * 
	 * @return
	 */
	public int getOldSectionPos() {
		return oldSectionPos;
	}
	
	public Navigator getNavigator() {
		return navigator;
	}

	public String getOldFragmentId() {
		return oldFragmentId;
	}

	// package
	void setOldFragmentId(String oldFragmentId) {
		this.oldFragmentId = oldFragmentId;
	}

	public Book getOldBook() {
		return oldBook;
	}

	// package
	void setOldPagePos(int oldPagePos) {
		this.oldSectionPos = oldPagePos;
	}

	public int getCurrentSectionPos() {
		return navigator.getCurrentSectionPos();
	}
	
	public int getOldSpinePos() {
		return oldSpinePos;
	}
	
	public int getCurrentSpinePos() {
		return navigator.getCurrentSpinePos();
	}
	
	public String getCurrentFragmentId() {
		return navigator.getCurrentFragmentId();
	}
	
	public boolean isBookChanged() {
		if (oldBook == null) {
			return true;
		}
		return oldBook != navigator.getBook();
	}
	
	public boolean isSpinePosChanged() {
		return getOldSpinePos() != getCurrentSpinePos();
	}

	public boolean isFragmentChanged() {
		return StringUtils.equals(getOldFragmentId(), getCurrentFragmentId());
	}

	public Resource getOldResource() {
		return oldResource;
	}
	
	public Resource getCurrentResource() {
		return navigator.getCurrentResource();
	}
	public void setOldResource(Resource oldResource) {
		this.oldResource = oldResource;
	}
	
	
	public void setOldSpinePos(int oldSpinePos) {
		this.oldSpinePos = oldSpinePos;
	}
	
	
	public void setNavigator(Navigator navigator) {
		this.navigator = navigator;
	}
	
	
	public void setOldBook(Book oldBook) {
		this.oldBook = oldBook;
	}

	public Book getCurrentBook() {
		return getNavigator().getBook();
	}

	public boolean isResourceChanged() {
		return oldResource != getCurrentResource();
	}
	
	public String toString() {
		return new ToStringBuilder(this).
			append("oldSectionPos", oldSectionPos).
			append("oldResource", oldResource).
			append("oldBook", oldBook).
			append("oldFragmentId", oldFragmentId).
			append("oldSpinePos", oldSpinePos).
			append("currentPagePos", getCurrentSectionPos()).
			append("currentResource", getCurrentResource()).
			append("currentBook", getCurrentBook()).
			append("currentFragmentId", getCurrentFragmentId()).
			append("currentSpinePos", getCurrentSpinePos()).
		toString();
	}

	public boolean isSectionPosChanged() {
		return oldSectionPos != getCurrentSectionPos();
	}
}