package nl.siegmann.epublib.epub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;

import nl.siegmann.epublib.domain.Identifier;
import nl.siegmann.epublib.domain.Metadata;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class PackageDocumentMetadataReaderTest {
	
	@Test	
	public void test1() {
		try {
			Document document = EpubProcessorSupport.createDocumentBuilder().parse(PackageDocumentMetadataReader.class.getResourceAsStream("/opf/test2.opf"));
			Metadata metadata = PackageDocumentMetadataReader.readMetadata(document);
			assertEquals(1, metadata.getAuthors().size());
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test	
    public void testReadsLanguage() {
        Metadata metadata = getMetadata("/opf/test_language.opf");
        assertEquals("fi", metadata.getLanguage());
    }

	@Test	
    public void testDefaultsToEnglish() {
        Metadata metadata = getMetadata("/opf/test_default_language.opf");
        assertEquals("en", metadata.getLanguage());
    }

    private Metadata getMetadata(String file) {
        try {
            Document document = EpubProcessorSupport.createDocumentBuilder().parse(PackageDocumentMetadataReader.class.getResourceAsStream(file));

            return PackageDocumentMetadataReader.readMetadata(document);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);

            return null;
        }
    }
    
	@Test	
    public void test2() throws SAXException, IOException {
    	// given
    	String input = "<package version=\"2.0\" xmlns=\"http://www.idpf.org/2007/opf\" unique-identifier=\"BookId\">"
			+ "<metadata xmlns=\"http://www.idpf.org/2007/opf\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:opf=\"http://www.idpf.org/2007/opf\">"
	    	    + "<dc:title>Three Men in a Boat</dc:title>"
	    	    + "<dc:creator opf:role=\"aut\" opf:file-as=\"Jerome, Jerome K.\">Jerome K. Jerome</dc:creator>"
	    	    + "<dc:creator opf:role=\"ill\" opf:file-as=\"Frederics, A.\">A. Frederics</dc:creator>"
	    	    + "<dc:language>en</dc:language>"
	    	    + "<dc:date opf:event=\"publication\">1889</dc:date>"
	    	    + "<dc:date opf:event=\"creation\">2009-05-17</dc:date>"
	    	    + "<dc:identifier opf:scheme=\"URI\" id=\"BookId\">zelda@mobileread.com:2010040720</dc:identifier>"
	    	    + "<dc:contributor opf:role=\"bkp\">zelda pinwheel</dc:contributor>"
	    	    + "<dc:publisher>zelda pinwheel</dc:publisher>"
	    	    + "<dc:rights>Public Domain</dc:rights>"
	    	    + "<dc:type>Text</dc:type>"
	    	    + "<dc:type>Image</dc:type>"
	    	    + "<dc:subject>Travel</dc:subject>"
	    	    + "<dc:subject>Humour</dc:subject>"
	    	    + "<dc:description>Three Men in a Boat (To Say Nothing of the Dog), published in 1889, is a humorous account by Jerome K. Jerome of a boating holiday on the Thames between Kingston and Oxford. The book was initially intended to be a serious travel guide, with accounts of local history along the route, but the humorous elements took over to the point where the serious and somewhat sentimental passages seem a distraction to the comic novel. One of the most praised things about Three Men in a Boat is how undated it appears to modern readers, the jokes seem fresh and witty even today.</dc:description>"
	    	    + "<meta name=\"cover\" content=\"cover_pic\" />"
	    	    + "<meta name=\"calibre:rating\" content=\"8\"/>"
    	    + "</metadata>"
    	   + "</package>";

    	// when
    	Document metadataDocument = EpubProcessorSupport.createDocumentBuilder().parse(new InputSource(new StringReader(input)));
    	Metadata metadata = PackageDocumentMetadataReader.readMetadata(metadataDocument);
    	
    	// then
    	Assert.assertEquals("Three Men in a Boat", metadata.getFirstTitle());

    	// test identifier
    	Assert.assertNotNull(metadata.getIdentifiers());
    	Assert.assertEquals(1, metadata.getIdentifiers().size());
    	Identifier identifier = metadata.getIdentifiers().get(0);
    	Assert.assertEquals("URI", identifier.getScheme());
    	Assert.assertEquals("zelda@mobileread.com:2010040720", identifier.getValue());
    	
    	Assert.assertEquals("8", metadata.getMetaAttribute("calibre:rating"));
    	Assert.assertEquals("cover_pic", metadata.getMetaAttribute("cover"));
    }
}
