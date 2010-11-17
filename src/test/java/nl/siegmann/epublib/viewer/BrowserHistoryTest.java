package nl.siegmann.epublib.viewer;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import nl.siegmann.epublib.browsersupport.Navigator;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.PlaceholderResource;
import nl.siegmann.epublib.domain.Resource;

public class BrowserHistoryTest extends TestCase {

	private static final Resource mockResource = new PlaceholderResource("mockResource");
	
	private static class MockBook extends Book {
		public Resource getCoverPage() {
			return mockResource;
		}
	}
	
	
	private static class MockSectionWalker extends Navigator {
		
		private Map<String, Resource> resourcesByHref = new HashMap<String, Resource>();

		public MockSectionWalker(Book book) {
			super(book);
			resourcesByHref.put(mockResource.getHref(), mockResource);
		}
		
		public int gotoFirst(Object source) {
			throw new UnsupportedOperationException("Method not supported in mock implementation");
		}
		public int gotoPrevious(Object source) {
			throw new UnsupportedOperationException("Method not supported in mock implementation");
		}
		public boolean hasNext() {
			throw new UnsupportedOperationException("Method not supported in mock implementation");
		}
		public boolean hasPrevious() {
			throw new UnsupportedOperationException("Method not supported in mock implementation");
		}
		public int gotoNext(Object source) {
			throw new UnsupportedOperationException("Method not supported in mock implementation");
		}
		public int gotoResource(String resourceHref, Object source) {
			return -1;
		}
	
		public int gotoResource(Resource resource, Object source) {
			return -1;
		}
		public boolean equals(Object obj) {
			throw new UnsupportedOperationException("Method not supported in mock implementation");
		}

		public int gotoResourceId(String resourceId, Object source) {
			throw new UnsupportedOperationException("Method not supported in mock implementation");
		}
		public int gotoSection(int newIndex, Object source) {
			throw new UnsupportedOperationException("Method not supported in mock implementation");
		}
		public int gotoLast(Object source) {
			throw new UnsupportedOperationException("Method not supported in mock implementation");
		}
		public int getCurrentIndex() {
			throw new UnsupportedOperationException("Method not supported in mock implementation");
		}
		public Resource getCurrentResource() {
			return resourcesByHref.values().iterator().next();
		}
		public void setCurrentIndex(int currentIndex) {
			throw new UnsupportedOperationException("Method not supported in mock implementation");
		}
		public Book getBook() {
			throw new UnsupportedOperationException("Method not supported in mock implementation");
		}
		public int setCurrentResource(Resource currentResource) {
			throw new UnsupportedOperationException("Method not supported in mock implementation");
		}
		public String toString() {
			throw new UnsupportedOperationException("Method not supported in mock implementation");
		}

		public Resource getMockResource() {
			return mockResource;
		}
	}
	
	public void test1() {
		MockSectionWalker sectionWalker = new MockSectionWalker(new MockBook()); 
		BrowserHistory browserHistory = new BrowserHistory(sectionWalker);
		
		assertEquals(sectionWalker.getCurrentResource().getHref(), browserHistory.getCurrentHref());
		assertEquals(0, browserHistory.getCurrentPos());
		assertEquals(1, browserHistory.getCurrentSize());

		browserHistory.addLocation(sectionWalker.getMockResource().getHref());
		assertEquals(0, browserHistory.getCurrentPos());
		assertEquals(1, browserHistory.getCurrentSize());

		browserHistory.addLocation("bar");
		assertEquals(1, browserHistory.getCurrentPos());
		assertEquals(2, browserHistory.getCurrentSize());

		browserHistory.addLocation("bar");
		assertEquals(1, browserHistory.getCurrentPos());
		assertEquals(2, browserHistory.getCurrentSize());

		browserHistory.move(1);
		assertEquals(1, browserHistory.getCurrentPos());
		assertEquals(2, browserHistory.getCurrentSize());

		browserHistory.addLocation("bar");
		assertEquals(1, browserHistory.getCurrentPos());
		assertEquals(2, browserHistory.getCurrentSize());

		browserHistory.move(-1);
		assertEquals(0, browserHistory.getCurrentPos());
		assertEquals(2, browserHistory.getCurrentSize());

		browserHistory.move(0);
		assertEquals(0, browserHistory.getCurrentPos());
		assertEquals(2, browserHistory.getCurrentSize());

		browserHistory.move(-1);
		assertEquals(0, browserHistory.getCurrentPos());
		assertEquals(2, browserHistory.getCurrentSize());

		browserHistory.move(1);
		assertEquals(1, browserHistory.getCurrentPos());
		assertEquals(2, browserHistory.getCurrentSize());

		browserHistory.move(1);
		assertEquals(1, browserHistory.getCurrentPos());
		assertEquals(2, browserHistory.getCurrentSize());
	}
	
	
	public void test2() {
		MockSectionWalker sectionWalker = new MockSectionWalker(new MockBook()); 
		BrowserHistory browserHistory = new BrowserHistory(sectionWalker);
		
		assertEquals(0, browserHistory.getCurrentPos());
		assertEquals(1, browserHistory.getCurrentSize());

		browserHistory.addLocation("green");
		assertEquals(1, browserHistory.getCurrentPos());
		assertEquals(2, browserHistory.getCurrentSize());

		browserHistory.addLocation("blue");
		assertEquals(2, browserHistory.getCurrentPos());
		assertEquals(3, browserHistory.getCurrentSize());

		browserHistory.addLocation("yellow");
		assertEquals(3, browserHistory.getCurrentPos());
		assertEquals(4, browserHistory.getCurrentSize());

		browserHistory.addLocation("orange");
		assertEquals(4, browserHistory.getCurrentPos());
		assertEquals(5, browserHistory.getCurrentSize());

		browserHistory.move(-1);
		assertEquals(3, browserHistory.getCurrentPos());
		assertEquals(5, browserHistory.getCurrentSize());

		browserHistory.move(-1);
		assertEquals(2, browserHistory.getCurrentPos());
		assertEquals(5, browserHistory.getCurrentSize());

		browserHistory.addLocation("taupe");
		assertEquals(3, browserHistory.getCurrentPos());
		assertEquals(4, browserHistory.getCurrentSize());

	}
	
	public void test3() {
		MockSectionWalker sectionWalker = new MockSectionWalker(new MockBook()); 
		BrowserHistory browserHistory = new BrowserHistory(sectionWalker);
		
		assertEquals(0, browserHistory.getCurrentPos());
		assertEquals(1, browserHistory.getCurrentSize());

		browserHistory.addLocation("red");
		browserHistory.addLocation("green");
		browserHistory.addLocation("blue");
		
		assertEquals(3, browserHistory.getCurrentPos());
		assertEquals(4, browserHistory.getCurrentSize());

		browserHistory.move(-1);
		assertEquals(2, browserHistory.getCurrentPos());
		assertEquals(4, browserHistory.getCurrentSize());
		
		browserHistory.move(-1);
		assertEquals(1, browserHistory.getCurrentPos());
		assertEquals(4, browserHistory.getCurrentSize());

		browserHistory.move(-1);
		assertEquals(0, browserHistory.getCurrentPos());
		assertEquals(4, browserHistory.getCurrentSize());

		browserHistory.move(-1);
		assertEquals(0, browserHistory.getCurrentPos());
		assertEquals(4, browserHistory.getCurrentSize());

		browserHistory.addLocation("taupe");
		assertEquals(1, browserHistory.getCurrentPos());
		assertEquals(2, browserHistory.getCurrentSize());
	}
}
