package nl.siegmann.epublib.epub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;
import org.w3c.dom.Document;

public class PackageDocumentReaderTest {
	
	@Test
	public void testFindCoverHref_content1() {
		EpubReader epubReader = new EpubReader();
		Document packageDocument;
		try {
			packageDocument = EpubProcessorSupport.createDocumentBuilder().parse(PackageDocumentReaderTest.class.getResourceAsStream("/opf/test1.opf"));
			Collection<String> coverHrefs = PackageDocumentReader.findCoverHrefs(packageDocument);
			assertEquals(1, coverHrefs.size());
			assertEquals("cover.html", coverHrefs.iterator().next());
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
}
