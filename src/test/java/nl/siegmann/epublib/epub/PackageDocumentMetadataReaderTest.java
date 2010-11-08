package nl.siegmann.epublib.epub;

import nl.siegmann.epublib.domain.Metadata;

import org.w3c.dom.Document;

import junit.framework.TestCase;

public class PackageDocumentMetadataReaderTest extends TestCase {
	
	public void test1() {
		EpubProcessor epubProcessor = new EpubProcessor();
		try {
			Document document = epubProcessor.createDocumentBuilder().parse(PackageDocumentMetadataReader.class.getResourceAsStream("/opf/test2.opf"));
			Metadata metadata = PackageDocumentMetadataReader.readMetadata(document);
			assertEquals(1, metadata.getAuthors().size());
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
}
