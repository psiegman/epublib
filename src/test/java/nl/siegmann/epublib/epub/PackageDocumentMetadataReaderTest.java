package nl.siegmann.epublib.epub;

import junit.framework.TestCase;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.domain.Resources;

import org.w3c.dom.Document;

public class PackageDocumentMetadataReaderTest extends TestCase {
	
	public void test1() {
		EpubProcessor epubProcessor = new EpubProcessor();
		try {
			Document document = epubProcessor.createDocumentBuilder().parse(PackageDocumentMetadataReader.class.getResourceAsStream("/opf/test2.opf"));
			Resources resources = new Resources();
			Metadata metadata = PackageDocumentMetadataReader.readMetadata(document, resources);
			assertEquals(1, metadata.getAuthors().size());
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
}
