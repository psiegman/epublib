package nl.siegmann.epublib.browsersupport;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;

import org.junit.Test;

public class NavigationHistoryTest {

	private static final Resource mockResource = new Resource("mockResource.html");
	
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
		
		public int gotoFirstSpineSection(Object source) {
			throw new UnsupportedOperationException("Method not supported in mock implementation");
		}
		public int gotoPreviousSpineSection(Object source) {
			throw new UnsupportedOperationException("Method not supported in mock implementation");
		}
		public boolean hasNextSpineSection() {
			throw new UnsupportedOperationException("Method not supported in mock implementation");
		}
		public boolean hasPreviousSpineSection() {
			throw new UnsupportedOperationException("Method not supported in mock implementation");
		}
		public int gotoNextSpineSection(Object source) {
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
		public int gotoSpineSection(int newIndex, Object source) {
			throw new UnsupportedOperationException("Method not supported in mock implementation");
		}
		public int gotoLastSpineSection(Object source) {
			throw new UnsupportedOperationException("Method not supported in mock implementation");
		}
		public int getCurrentSpinePos() {
			throw new UnsupportedOperationException("Method not supported in mock implementation");
		}
		public Resource getCurrentResource() {
			return resourcesByHref.values().iterator().next();
		}
		public void setCurrentSpinePos(int currentIndex) {
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

	@Test
	public void test1() {
		MockSectionWalker navigator = new MockSectionWalker(new MockBook()); 
		NavigationHistory browserHistory = new NavigationHistory(navigator);
		
		assertEquals(navigator.getCurrentResource().getHref(), browserHistory.getCurrentHref());
		assertEquals(0, browserHistory.getCurrentPos());
		assertEquals(1, browserHistory.getCurrentSize());

		browserHistory.addLocation(navigator.getMockResource().getHref());
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
	
	@Test
	public void test2() {
		MockSectionWalker navigator = new MockSectionWalker(new MockBook()); 
		NavigationHistory browserHistory = new NavigationHistory(navigator);
		
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
	
	@Test
	public void test3() {
		MockSectionWalker navigator = new MockSectionWalker(new MockBook()); 
		NavigationHistory browserHistory = new NavigationHistory(navigator);
		
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
