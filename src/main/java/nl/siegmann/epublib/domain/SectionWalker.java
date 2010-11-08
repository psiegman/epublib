package nl.siegmann.epublib.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;

import org.apache.commons.lang.StringUtils;


public class SectionWalker {
	
	private Book book;
	private int currentIndex;
	private Collection<SectionChangeListener> eventListeners = new ArrayList<SectionChangeListener>();
	
	public static class SectionChangeEvent extends EventObject {
		private static final long serialVersionUID = -6346750144308952762L;

		private int oldPosition;
		
		public SectionChangeEvent(Object source, int oldPosition) {
			super(source);
			this.oldPosition = oldPosition;
		}
		
		public int getPreviousSectionIndex() {
			return oldPosition;
		}
		
		public int getCurrentSectionIndex() {
			return ((SectionWalker) getSource()).getCurrentIndex();
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
	}

	public interface SectionChangeListener {
		public void sectionChanged(SectionChangeEvent sectionChangeEvent);
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
		SectionChangeEvent sectionChangeEvent = new SectionChangeEvent(this, oldPosition);
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
		if (newIndex < 0 || newIndex >= book.getSpine().size()) {
			return currentIndex;
		}
		int oldIndex = currentIndex;
		currentIndex = newIndex;
		handleEventListeners(oldIndex);
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
