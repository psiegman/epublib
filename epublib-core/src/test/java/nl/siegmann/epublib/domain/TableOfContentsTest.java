package nl.siegmann.epublib.domain;

import junit.framework.TestCase;

public class TableOfContentsTest extends TestCase {
	
	public void testCalculateDepth_simple1() {
		TableOfContents tableOfContents = new TableOfContents();
		assertEquals(0, tableOfContents.calculateDepth());
	}
	
	public void testCalculateDepth_simple2() {
		TableOfContents tableOfContents = new TableOfContents();
		tableOfContents.addTOCReference(new TOCReference());
		assertEquals(1, tableOfContents.calculateDepth());
	}

	public void testCalculateDepth_simple3() {
		TableOfContents tableOfContents = new TableOfContents();
		tableOfContents.addTOCReference(new TOCReference());
		TOCReference childTOCReference = tableOfContents.addTOCReference(new TOCReference());
		childTOCReference.addChildSection(new TOCReference());
		tableOfContents.addTOCReference(new TOCReference());
		
		assertEquals(2, tableOfContents.calculateDepth());
	}
	
	public void testAddAtLocation1() {
		Resource resource = new Resource("foo/bar");
		TableOfContents toc = new TableOfContents();
		TOCReference tocReference = toc.addResourceAtLocation(resource, "apple/pear", "/");
		assertNotNull(tocReference);
		assertNotNull(tocReference.getResource());
		assertEquals(2, toc.size());
		assertEquals("pear", tocReference.getTitle());
	}

	public void testAddAtLocation2() {
		Resource resource = new Resource("foo/bar");
		TableOfContents toc = new TableOfContents();
		TOCReference tocReference = toc.addResourceAtLocation(resource, "apple/pear", "/");
		assertNotNull(tocReference);
		assertNotNull(tocReference.getResource());
		assertEquals(2, toc.size());
		assertEquals("pear", tocReference.getTitle());

		TOCReference tocReference2 = toc.addResourceAtLocation(resource, "apple/banana", "/");
		assertNotNull(tocReference2);
		assertNotNull(tocReference2.getResource());
		assertEquals(3, toc.size());
		assertEquals("banana", tocReference2.getTitle());

		TOCReference tocReference3 = toc.addResourceAtLocation(resource, "apple", "/");
		assertNotNull(tocReference3);
		assertNotNull(tocReference.getResource());
		assertEquals(3, toc.size());
		assertEquals("apple", tocReference3.getTitle());
	}
}
