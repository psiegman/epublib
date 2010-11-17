package nl.siegmann.epublib.browsersupport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;

import org.apache.commons.lang.StringUtils;


public class Navigator {
	
	private Book book;
	private int currentIndex;
	private Resource currentResource;
	
	private Collection<SectionChangeListener> eventListeners = new ArrayList<SectionChangeListener>();
	
	public static class SectionChangeEvent extends EventObject {
		private static final long serialVersionUID = -6346750144308952762L;
		
		private final Resource oldResource;
		private final int oldPosition;
		private final Navigator sectionWalker;
		
		public SectionChangeEvent(Object source, int oldPosition, Resource oldResource, Navigator sectionWalker) {
			super(source);
			this.oldPosition = oldPosition;
			this.oldResource = oldResource;
			this.sectionWalker = sectionWalker;
		}
	
		public Navigator getSectionWalker() {
			return sectionWalker;
		}
		
		public int getPreviousSectionIndex() {
			return oldPosition;
		}
		
		public int getCurrentSectionIndex() {
			return sectionWalker.getCurrentIndex();
		}
		
		public String getCurrentFragmentId() {
			return "";
		}
		
		public String getPreviousFragmentId() {
			return "";
		}
		
		public boolean isSectionChanged() {
			return getPreviousSectionIndex() != getCurrentSectionIndex();
		}

		public boolean isFragmentChanged() {
			return StringUtils.equals(getPreviousFragmentId(), getCurrentFragmentId());
		}

		public Resource getOldResource() {
			return oldResource;
		}
		
		public Resource getCurrentResource() {
			return sectionWalker.getCurrentResource();
		}
	}

	public interface SectionChangeListener {
		public void sectionChanged(SectionChangeEvent sectionChangeEvent);
	}
	
	public Navigator(Book book) {
		this.book = book;
		this.currentIndex = 0;
		this.currentResource = book.getCoverPage();
	}

	public void handleEventListeners(int oldPosition, Resource oldResource, Object source) {
		if (eventListeners == null || eventListeners.isEmpty()) {
			return;
		}
		if (oldPosition == currentIndex) {
			return;
		}
		SectionChangeEvent sectionChangeEvent = new SectionChangeEvent(source, oldPosition, oldResource, this);
		for (SectionChangeListener sectionChangeListener: eventListeners) {
			sectionChangeListener.sectionChanged(sectionChangeEvent);
		}
	}

	public boolean addSectionChangeEventListener(SectionChangeListener sectionChangeListener) {
		return this.eventListeners.add(sectionChangeListener);
	}

	
	public boolean removeSectionChangeEventListener(SectionChangeListener sectionChangeListener) {
		return this.eventListeners.remove(sectionChangeListener);
	}
	
	public int gotoFirst(Object source) {
		return gotoSection(0, source);
	}

	public int gotoPrevious(Object source) {
		if (currentIndex < 0) {
			return gotoSection(0, source);
		} else {
			return gotoSection(currentIndex - 1, source);
		}
	}

	public boolean hasNext() {
		return (currentIndex < (book.getSpine().size() - 1));
	}
	
	public boolean hasPrevious() {
		return (currentIndex > 0);
	}
	
	public int gotoNext(Object source) {
		if (currentIndex < 0) {
			return gotoSection(0, source);
		} else {
			return gotoSection(currentIndex + 1, source);
		}
	}

	public int gotoResource(String resourceHref, Object source) {
		Resource resource = book.getResources().getByCompleteHref(resourceHref);
		return gotoResource(resource, source);
	}
	
	
	public int gotoResource(Resource resource, Object source) {
		if (resource == null) {
			return -1;
		}
		Resource oldResource = currentResource;
		this.currentResource = resource;

		int oldIndex = currentIndex;
		this.currentIndex = book.getSpine().getResourceIndex(currentResource);
		
		handleEventListeners(oldIndex, oldResource, source);
		
		return currentIndex;
	}
	
	
	public int gotoResourceId(String resourceId, Object source) {
		return gotoSection(book.getSpine().findFirstResourceById(resourceId), source);
	}
	
	
	public int gotoSection(int newIndex, Object source) {
		if (newIndex == currentIndex) {
			return currentIndex;
		}
		if (newIndex < 0 || newIndex >= book.getSpine().size()) {
			return currentIndex;
		}
		int oldIndex = currentIndex;
		Resource oldResource = currentResource;
		currentIndex = newIndex;
		currentResource = book.getSpine().getResource(currentIndex);
		handleEventListeners(oldIndex, oldResource, source);
		return currentIndex;
	}

	public int gotoLast(Object source) {
		return gotoSection(book.getSpine().size() - 1, source);
	}
	
	public int getCurrentIndex() {
		return currentIndex;
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
	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
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
	 * @param currentIndex
	 */
	public int setCurrentResource(Resource currentResource) {
		this.currentIndex = book.getSpine().getResourceIndex(currentResource);
		this.currentResource = currentResource;
		return currentIndex;
	}
}
