package nl.siegmann.epublib.search;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import junit.framework.TestCase;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.service.MediatypeService;

public class SearchIndexTest extends TestCase {

	public void testDoSearch1() {
		try {
			Book testBook = new Book();
			testBook.addSection("chapter1", new Resource(new StringReader("a"), "chapter1.html"));
			testBook.addSection("chapter2", new Resource(new StringReader("<title>ab</title>"), "chapter2.html"));
			testBook.addSection("chapter3", new Resource(new StringReader("ba"), "chapter3.html"));
			testBook.addSection("chapter4", new Resource(new StringReader("aa"), "chapter4.html"));
			SearchIndex searchIndex = new SearchIndex(testBook);
			SearchResults searchResults = searchIndex.doSearch("a");
			assertFalse(searchResults.isEmpty());
			assertEquals(5, searchResults.size());
			assertEquals(0, searchResults.getHits().get(0).getPagePos());
			assertEquals(0, searchResults.getHits().get(1).getPagePos());
			assertEquals(1, searchResults.getHits().get(2).getPagePos());
			assertEquals(0, searchResults.getHits().get(3).getPagePos());
			assertEquals(1, searchResults.getHits().get(4).getPagePos());
		} catch (IOException e) {
			assertTrue(e.getMessage(), false);
		}
	}

	public void testInContent() {
		Object[] testData = new Object[] {
				"a", "a", new Integer[] {0},
				"a", "aa", new Integer[] {0,1},
				"a", "a   \n\t\t\ta", new Integer[] {0,2},
				"a", "ä", new Integer[] {0},
				"a", "A", new Integer[] {0},
				"u", "&uuml;", new Integer[] {0},
				"a", "b", new Integer[] {},
				"XXX", "<html><title>my title1</title><body><h1>wrong title</h1></body></html>", new Integer[] {},
				"title", "<html><title>my title1</title><body><h1>wrong title</h1></body></html>", new Integer[] {3, 15}
		};
		for (int i = 0; i < testData.length; i+= 3) {
			Resource resource = new Resource(((String) testData[i + 1]).getBytes(), MediatypeService.XHTML);
			String content = SearchIndex.getSearchContent(new StringReader((String) testData[i + 1]));
			String searchTerm = (String) testData[i];
			Integer[] expectedResult = (Integer[]) testData[i + 2];
			List<SearchResult> actualResult = SearchIndex.doSearch(searchTerm, content, resource);
			assertEquals("test " + ((i / 3) + 1), expectedResult.length, actualResult.size());
			for (int j = 0; j < expectedResult.length; j++) {
				SearchResult searchResult = actualResult.get(j);
				assertEquals("test " + (i / 3) + ", match " + j, expectedResult[j].intValue(), searchResult.getPagePos());
			}
		}
	}

	public void testCleanText() {
		String[] testData = new String[] {
				"", "",
				" ", "",
				"a", "a",
				"A", "a",
				"a b", "a b",
				"a  b", "a b",
				"a\tb", "a b",
				"a\nb", "a b",
				"a\n\t\r  \n\tb", "a b",
				"ä", "a",
				"", ""
		};
		for (int i = 0; i < testData.length; i+= 2) {
			String actualText = SearchIndex.cleanText(testData[i]);
			assertEquals(testData[i + 1], actualText);
		}
	}
}
