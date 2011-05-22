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
	
	public void testAddResource1() {
		Resource resource = new Resource("foo");
		TableOfContents toc = new TableOfContents();
		TOCReference tocReference = toc.addSection(resource, "apple/pear", "/");
		assertNotNull(tocReference);
		assertNotNull(tocReference.getResource());
		assertEquals(2, toc.size());
		assertEquals("pear", tocReference.getTitle());
	}

	public void testAddResource2() {
		Resource resource = new Resource("foo");
		TableOfContents toc = new TableOfContents();
		TOCReference tocReference = toc.addSection(resource, "apple/pear", "/");
		assertNotNull(tocReference);
		assertNotNull(tocReference.getResource());
		assertEquals(2, toc.size());
		assertEquals("pear", tocReference.getTitle());

		TOCReference tocReference2 = toc.addSection(resource, "apple/banana", "/");
		assertNotNull(tocReference2);
		assertNotNull(tocReference2.getResource());
		assertEquals(3, toc.size());
		assertEquals("banana", tocReference2.getTitle());

		TOCReference tocReference3 = toc.addSection(resource, "apple", "/");
		assertNotNull(tocReference3);
		assertNotNull(tocReference.getResource());
		assertEquals(3, toc.size());
		assertEquals("apple", tocReference3.getTitle());
	}

	public void testAddResource3() {
		Resource resource = new Resource("foo");
		TableOfContents toc = new TableOfContents();
		TOCReference tocReference = toc.addSection(resource, "apple/pear");
		assertNotNull(tocReference);
		assertNotNull(tocReference.getResource());
		assertEquals(1, toc.getTocReferences().size());
		assertEquals(1, toc.getTocReferences().get(0).getChildren().size());
		assertEquals(2, toc.size());
		assertEquals("pear", tocReference.getTitle());
	}

	public void testAddResourceWithIndexes() {
		Resource resource = new Resource("foo");
		TableOfContents toc = new TableOfContents();
		TOCReference tocReference = toc.addSection(resource, new int[] {0, 0}, "Section ", ".");
		
		// check newly created TOCReference
		assertNotNull(tocReference);
		assertNotNull(tocReference.getResource());
		assertEquals("Section 1.1", tocReference.getTitle());
		
		// check table of contents
		assertEquals(1, toc.getTocReferences().size());
		assertEquals(1, toc.getTocReferences().get(0).getChildren().size());
		assertEquals(2, toc.size());
		assertEquals("Section 1", toc.getTocReferences().get(0).getTitle());
		assertEquals("Section 1.1", toc.getTocReferences().get(0).getChildren().get(0).getTitle());
		assertEquals(1, toc.getTocReferences().get(0).getChildren().size());
	}
}
