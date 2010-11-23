package nl.siegmann.epublib.browsersupport;

import java.util.ArrayList;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;


/**
 * A helper class for epub browser applications.
 * 
 * It helps moving from one resource to the other, from one resource to the other and keeping other
 * elements of the application up-to-date by calling the NavigationEventListeners.
 * 
 * @author paul
 *
 */
public class Navigator {
	
	private Book book;
	private int currentSpinePos;
	private Resource currentResource;
	private int currentPagePos;
	private String currentFragmentId;
	
	private List<NavigationEventListener> eventListeners = new ArrayList<NavigationEventListener>();
	
	public Navigator() {
		this(null);
	}
	public Navigator(Book book) {
		this.book = book;
		this.currentSpinePos = 0;
		if (book != null) {
			this.currentResource = book.getCoverPage();
		}
		this.currentPagePos = 0;
	}
	
	private void handleEventListeners(NavigationEvent navigationEvent) {
		for (int i = 0; i < eventListeners.size(); i++) {
			NavigationEventListener navigationEventListener = eventListeners.get(i);
			navigationEventListener.navigationPerformed(navigationEvent);
		}
	}
	
	public boolean addNavigationEventListener(NavigationEventListener navigationEventListener) {
		return this.eventListeners.add(navigationEventListener);
	}

	
	public boolean removeNavigationEventListener(NavigationEventListener navigationEventListener) {
		return this.eventListeners.remove(navigationEventListener);
	}
	
	public int gotoFirst(Object source) {
		return gotoSection(0, source);
	}

	public int gotoPrevious(Object source) {
		if (currentSpinePos < 0) {
			return gotoSection(0, source);
		} else {
			return gotoSection(currentSpinePos - 1, source);
		}
	}

	public boolean hasNext() {
		return (currentSpinePos < (book.getSpine().size() - 1));
	}
	
	public boolean hasPrevious() {
		return (currentSpinePos > 0);
	}
	
	public int gotoNext(Object source) {
		if (currentSpinePos < 0) {
			return gotoSection(0, source);
		} else {
			return gotoSection(currentSpinePos + 1, source);
		}
	}

	public int gotoResource(String resourceHref, Object source) {
		Resource resource = book.getResources().getByHref(resourceHref);
		return gotoResource(resource, source);
	}
	
	
	public int gotoResource(Resource resource, Object source) {
		if (resource == null) {
			return -1;
		}
		NavigationEvent navigationEvent = new NavigationEvent(source, this);
		this.currentResource = resource;
		this.currentSpinePos = book.getSpine().getResourceIndex(currentResource);
		this.currentPagePos = 0;
		this.currentFragmentId = null;
		handleEventListeners(navigationEvent);
		
		return currentSpinePos;
	}
	
	public int gotoResourceId(String resourceId, Object source) {
		return gotoSection(book.getSpine().findFirstResourceById(resourceId), source);
	}
	
	
	/**
	 * Go to a specific section.
	 * Illegal spine positions are silently ignored.
	 * 
	 * @param newSpinePos
	 * @param source
	 * @return
	 */
	public int gotoSection(int newSpinePos, Object source) {
		if (newSpinePos == currentSpinePos) {
			return currentSpinePos;
		}
		if (newSpinePos < 0 || newSpinePos >= book.getSpine().size()) {
			return currentSpinePos;
		}
		NavigationEvent navigationEvent = new NavigationEvent(source, this);
		currentSpinePos = newSpinePos;
		currentResource = book.getSpine().getResource(currentSpinePos);
		handleEventListeners(navigationEvent);
		return currentSpinePos;
	}

	public int gotoLast(Object source) {
		return gotoSection(book.getSpine().size() - 1, source);
	}
	
	public void gotoBook(Book book, Object source) {
		NavigationEvent navigationEvent = new NavigationEvent(source, this);
		this.book = book;
		this.currentFragmentId = null;
		this.currentPagePos = 0;
		currentResource = null;
		this.currentSpinePos = book.getSpine().getResourceIndex(currentResource);
		handleEventListeners(navigationEvent);
	}
	

	/**
	 * The current position within the spine.
	 * 
	 * @return something < 0 if the current position is not within the spine.
	 */
	public int getCurrentSpinePos() {
		return currentSpinePos;
	}
	
	public Resource getCurrentResource() {
		return currentResource;
	}

	/**
	 * Sets the current index and resource without calling the eventlisteners.
	 * 
	 * If you want the eventListeners called use gotoSection(index);
	 * 
	 * @param currentIndex
	 */
	public void setCurrentSpinePos(int currentIndex) {
		this.currentSpinePos = currentIndex;
		this.currentResource = book.getSpine().getResource(currentIndex);
	}

	public Book getBook() {
		return book;
	}

	/**
	 * Sets the current index and resource without calling the eventlisteners.
	 * 
	 * If you want the eventListeners called use gotoSection(index);
	 * 
	 * @param currentSpinePos
	 */
	public int setCurrentResource(Resource currentResource) {
		this.currentSpinePos = book.getSpine().getResourceIndex(currentResource);
		this.currentResource = currentResource;
		return currentSpinePos;
	}
	
	public String getCurrentFragmentId() {
		return currentFragmentId;
	}
	
	public int getCurrentPagePos() {
		return currentPagePos;
	}
}
