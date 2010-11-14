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
}
