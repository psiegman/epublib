package nl.siegmann.epublib.domain;

import java.util.ArrayList;
import java.util.Collection;

public class SectionWalker {
	
	private Book book;
	private int currentIndex;
	private Collection<SectionChangeListener> eventListeners = new ArrayList<SectionChangeListener>();
	
	public interface SectionChangeListener {
		public void sectionChanged(SectionWalker sectionWalker, int oldPosition, int newPosition);
	}
	
	public SectionWalker(Book book) {
		this.book = book;
	}

	public void handleEventListeners(int oldPosition) {
		if (eventListeners == null || eventListeners.isEmpty()) {
			return;
		}
		if (oldPosition == currentIndex) {
			return;
		}
		for (SectionChangeListener sectionChangeListener: eventListeners) {
			sectionChangeListener.sectionChanged(this, oldPosition, currentIndex);
		}
	}

	public boolean addSectionChangeEventListener(SectionChangeListener sectionChangeListener) {
		return this.eventListeners.add(sectionChangeListener);
	}

	
	public boolean removeSectionChangeEventListener(SectionChangeListener sectionChangeListener) {
		return this.eventListeners.remove(sectionChangeListener);
	}
	
	public int gotoFirst() {
		return gotoSection(0);
	}

	public int gotoPrevious() {
		return gotoSection(currentIndex - 1);
	}

	public boolean hasNext() {
		return (currentIndex < (book.getSpine().size() - 1));
	}
	
	public boolean hasPrevious() {
		return (currentIndex > 0);
	}
	
	public int gotoNext() {
		return gotoSection(currentIndex + 1);
	}

	public int gotoResource(Resource resource) {
		if (resource == null) {
			return currentIndex;
		}
		
		return gotoResourceId(resource.getId());
	}
	
	
	public int gotoResourceId(String resourceId) {
		return gotoSection(book.getSpine().findFirstResourceById(resourceId));
	}
	
	
	public int gotoSection(int newIndex) {
		if (newIndex == currentIndex) {
			return currentIndex;
		}
		if (newIndex >= 0 && newIndex < book.getSpine().size()) {
			int oldIndex = currentIndex;
			currentIndex = newIndex;
			handleEventListeners(oldIndex);
		}
		return currentIndex;
	}

	public int gotoLast() {
		return gotoSection(book.getSpine().size() - 1);
	}
	
	public int getCurrentIndex() {
		return currentIndex;
	}
	
	public Resource getCurrentResource() {
		return book.getSpine().getSpineReferences().get(currentIndex).getResource();
	}

	/**
	 * Sets the current index without calling the eventlisteners.
	 * 
	 * If you want the eventListeners called use gotoSection(index);
	 * 
	 * @param currentIndex
	 */
	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}

	public Book getBook() {
		return book;
	}
}
