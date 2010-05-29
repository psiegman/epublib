package nl.siegmann.epublib.epub;

import junit.framework.TestCase;

import org.w3c.dom.Document;

public class PackageDocumentReaderTest extends TestCase {
	
	public void testFindCoverHref_content1() {
		EpubReader epubReader = new EpubReader();
		Document packageDocument;
		try {
			packageDocument = epubReader.getDocumentBuilderFactory().newDocumentBuilder().parse(PackageDocumentReaderTest.class.getResourceAsStream("/opf/test1.opf"));
			String coverHref = PackageDocumentReader.findCoverHref(packageDocument);
			assertEquals("cover.html", coverHref);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
}
