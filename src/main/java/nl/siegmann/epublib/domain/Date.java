package nl.siegmann.epublib.domain;

import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;

import nl.siegmann.epublib.epub.PackageDocumentBase;

/**
 * A Date used by the book's metadata.
 * 
 * Examples: creation-date, modification-date, etc
 * 
 * @author paul
 *
 */
public class Date {
	public enum Event {
		PUBLICATION("publication"),
		MODIFICATION("modification"),
		CREATION("creation");
		
		private final String value;

		Event(String v) {
			value = v;
		}

		public static Event fromValue(String v) {
			for (Event c : Event.values()) {
				if (c.value.equals(v)) {
					return c;
				}
			}
			return null;
		}
		
		public String toString() {
			return value;
		}
	};

	private Event event;
	private String dateString;

	public Date(java.util.Date date) {
		this(date, (Event) null);
	}
	
	public Date(String dateString) {
		this(dateString, (Event) null);
	}
	
	public Date(java.util.Date date, Event event) {
		this((new SimpleDateFormat(PackageDocumentBase.dateFormat)).format(date), event);
	}
	
	public Date(String dateString, Event event) {
		this.dateString = dateString;
		this.event = event;
	}
	
	public Date(java.util.Date date, String event) {
		this((new SimpleDateFormat(PackageDocumentBase.dateFormat)).format(date), event);
	}
	
	public Date(String dateString, String event) {
		this(checkDate(dateString), Event.fromValue(event));
		this.dateString = dateString;
	}

	private static String checkDate(String dateString) {
		if (dateString == null) {
			throw new IllegalArgumentException("Cannot create a date from a blank string");
		}
		return dateString;
	}
	public String getValue() {
		return dateString;
	}
	public Event getEvent() {
		return event;
	}
	
	public void setEvent(Event event) {
		this.event = event;
	}

	public String toString() {
		if (event == null) {
			return dateString;
		}
		return "" + event + ":" + dateString;
	}
}

