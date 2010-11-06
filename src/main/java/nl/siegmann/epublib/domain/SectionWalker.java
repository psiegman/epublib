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
		for (SectionChangeListener sectionChangeListener: eventListeners) {
			sectionChangeListener.sectionChanged(this, oldPosition, currentIndex);
		}
	}

	public void addEventListener(SectionChangeListener sectionChangeListener) {
		this.eventListeners.add(sectionChangeListener);
	}
	
	public int gotoFirst() {
		int oldIndex = currentIndex;
		currentIndex = 0;
		handleEventListeners(oldIndex);
		return currentIndex;
	}

	public int gotoPrevious() {
		if (hasPrevious()) {
			currentIndex--;
		}
		handleEventListeners(currentIndex + 1);
		return currentIndex;
	}

	public boolean hasNext() {
		return (currentIndex < (book.getSpine().size() - 1));
	}
	
	public boolean hasPrevious() {
		return (currentIndex > 0);
	}
	
	public int gotoNext() {
		if (hasNext()) {
			currentIndex++;
		}
		handleEventListeners(currentIndex - 1);
		return currentIndex;
	}

	public int gotoResourceId(String resourceId) {
		int newIndex = book.getSpine().findFirstResourceById(resourceId);
		if (newIndex >= 0) {
			int oldIndex = currentIndex;
			currentIndex = newIndex;
			handleEventListeners(oldIndex);
		}
		return currentIndex;
	}
	
	
	public int gotoSection(int sectionIndex) {
		if (sectionIndex >= 0 && sectionIndex < book.getSpine().size()) {
			int oldIndex = currentIndex;
			currentIndex = sectionIndex;
			handleEventListeners(oldIndex);
		}
		return currentIndex;
	}

	public int gotoLast() {
		int oldIndex = currentIndex;
		currentIndex = book.getSpine().size() - 1;
		handleEventListeners(oldIndex);
		return currentIndex;
	}
	
	public int getCurrentIndex() {
		return currentIndex;
	}
	
	public Resource getCurrentResource() {
		return book.getSpine().getSpineReferences().get(currentIndex).getResource();
	}

	public void setCurrentIndex(int currentIndex) {
		int oldIndex = currentIndex;
		this.currentIndex = currentIndex;
		handleEventListeners(oldIndex);
	}

	public Book getBook() {
		return book;
	}
}
