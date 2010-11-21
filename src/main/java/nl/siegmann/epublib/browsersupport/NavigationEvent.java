package nl.siegmann.epublib.browsersupport;

import java.util.EventObject;

import nl.siegmann.epublib.domain.Book;
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
	
	private Resource oldResource;
	private int oldSpinePos;
	private Navigator navigator;
	private Book oldBook;
	private int oldPagePos;
	private String oldFragmentId;
	
	public NavigationEvent(Object source) {
		super(source);
	}
	
	public NavigationEvent(Object source, Navigator navigator) {
		super(source);
		this.navigator = navigator;
		this.oldBook = navigator.getBook();
		this.oldFragmentId = navigator.getCurrentFragmentId();
		this.oldPagePos = navigator.getCurrentPagePos();
		this.oldResource = navigator.getCurrentResource();
		this.oldSpinePos = navigator.getCurrentSpinePos();
	}

	public NavigationEvent(Object source, Book oldBook, int oldPagePos, int oldPosition, Resource oldResource, Navigator navigator) {
		super(source);
		this.oldBook = oldBook;
		this.oldSpinePos = oldPosition;
		this.oldResource = oldResource;
		this.navigator = navigator;
	}

	public int getOldPagePos() {
		return oldPagePos;
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
		this.oldPagePos = oldPagePos;
	}

	public Navigator getSectionWalker() {
		return navigator;
	}
	
	public int getCurrentPagePos() {
		return navigator.getCurrentPagePos();
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
		return oldBook == navigator.getBook();
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
}