package nl.siegmann.epublib.epub;

import junit.framework.TestCase;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.domain.Resources;

import org.w3c.dom.Document;

public class PackageDocumentMetadataReaderTest extends TestCase {
	
	public void test1() {
		EpubProcessorSupport epubProcessor = new EpubProcessorSupport();
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

    public void testReadsLanguage() {
        Metadata metadata = getMetadata("/opf/test_language.opf");
        assertEquals("fi", metadata.getLanguage());
    }

    public void testDefaultsToEnglish() {
        Metadata metadata = getMetadata("/opf/test_default_language.opf");
        assertEquals("en", metadata.getLanguage());
    }

    private Metadata getMetadata(String file) {
        EpubProcessorSupport epubProcessor = new EpubProcessorSupport();
        try {
            Document document = EpubProcessorSupport.createDocumentBuilder().parse(PackageDocumentMetadataReader.class.getResourceAsStream(file));
            Resources resources = new Resources();

            return PackageDocumentMetadataReader.readMetadata(document, resources);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);

            return null;
        }
    }
}
