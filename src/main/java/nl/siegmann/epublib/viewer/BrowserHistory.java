package nl.siegmann.epublib.viewer;

import java.util.ArrayList;
import java.util.List;

import nl.siegmann.epublib.domain.SectionWalker;
import nl.siegmann.epublib.domain.SectionWalker.SectionChangeEvent;
import nl.siegmann.epublib.domain.SectionWalker.SectionChangeListener;

/**
 * A history of locations with the epub.
 * 
 * @author paul.siegmann
 *
 */
public class BrowserHistory implements SectionChangeListener {

	public static final int DEFAULT_MAX_HISTORY_SIZE = 1000;
	
	private static class Location {
		private String href;

		public Location(String href) {
			super();
			this.href = href;
		}

		public void setHref(String href) {
			this.href = href;
		}

		public String getHref() {
			return href;
		}
	}
	
	private List<Location> locations = new ArrayList<Location>();
	private SectionWalker sectionWalker;
	private int currentPos = -1;
	private int currentSize = 0;
	private int maxHistorySize = DEFAULT_MAX_HISTORY_SIZE;
	
	public BrowserHistory(SectionWalker sectionWalker) {
		this.sectionWalker = sectionWalker;
		sectionWalker.addSectionChangeEventListener(this);
		init(sectionWalker);
	}
	
	public int getCurrentPos() {
		return currentPos;
	}


	public int getCurrentSize() {
		return currentSize;
	}
	
	public void init(SectionWalker sectionWalker) {
		this.sectionWalker = sectionWalker;
		locations = new ArrayList<Location>();
		currentPos = 0;
		currentSize = 1;
		locations.add(new Location(sectionWalker.getCurrentResource().getHref()));
	}
	
	/**
	 * Adds the location after the current position.
	 * If the currentposition is not the end of the list then the elements between the current element and the end of the list will be discarded.
	 * Does nothing if the new location matches the current location.
	 * <br/>
	 * If this nr of locations becomes larger then the historySize then the first item(s) will be removed.
	 * 
	 * @param location
	 * @return
	 */
	public void addLocation(Location location) {
		// do nothing if the new location matches the current location
		if ( !(locations.isEmpty()) && 
				location.getHref().equals(locations.get(currentPos).getHref())) {
			return;
		}
		currentPos++;
		if (currentPos != currentSize) {
			locations.set(currentPos, location);
		} else {
			locations.add(location);
			checkHistorySize();
		}
		currentSize = currentPos + 1;
	}
	
	/**
	 * Removes all elements that are too much for the maxHistorySize out of the history.
	 * 
	 */
	private void checkHistorySize() {
		while(locations.size() > maxHistorySize) {
			locations.remove(0);
			currentSize--;
			currentPos--;
		}
	}

	public void addLocation(String href) {
		addLocation(new Location(href));
	}

	private String getLocationHref(int pos) {
		if (pos < 0 || pos >= locations.size()) {
			return null;
		}
		return locations.get(currentPos).getHref();	
	}

	/**
	 * Moves the current positions delta positions.
	 * 
	 * move(-1) to go one position back in history.<br/>
	 * move(1) to go one position forward.<br/>
	 * 
	 * @param delta
	 * 
	 * @return Whether we actually moved. If the requested value is illegal it will return false, true otherwise.
	 */
	public boolean move(int delta) {
		if (((currentPos + delta) < 0)
		|| ((currentPos + delta) >= currentSize)) {
			return false;
		}
		currentPos += delta;
		sectionWalker.gotoResource(getLocationHref(currentPos), this);
		return true;
	}
	
	
	/**
	 * If this is not the source of the sectionChangeEvent then the addLocation will be called with the href of the currentResource in the sectionChangeEvent.
	 */
	@Override
	public void sectionChanged(SectionChangeEvent sectionChangeEvent) {
		if (sectionChangeEvent.getSource() == this) {
			return;
		}
		addLocation(sectionChangeEvent.getCurrentResource().getHref());
	}

	public String getCurrentHref() {
		if (currentPos < 0 || currentPos >= locations.size()) {
			return null;
		}
		return locations.get(currentPos).getHref();
	}

	public void setMaxHistorySize(int maxHistorySize) {
		this.maxHistorySize = maxHistorySize;
	}

	public int getMaxHistorySize() {
		return maxHistorySize;
	}
}
