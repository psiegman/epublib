package nl.siegmann.epublib.epub;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Resources;

public class PackageDocumentReaderTest {
	
	@Test
	public void testFindCoverHref_content1() throws SAXException, IOException {
		Document packageDocument;
		packageDocument = EpubProcessorSupport.createDocumentBuilder().parse(PackageDocumentReaderTest.class.getResourceAsStream("/opf/test1.opf"));
		Collection<String> coverHrefs = PackageDocumentReader.findCoverHrefs(packageDocument);
		assertEquals(1, coverHrefs.size());
		assertEquals("cover.html", coverHrefs.iterator().next());
	}
	
	@Test
	public void testFixHrefs_simple_correct() {
		// given
		String packageHref = "OEBPS/content.opf";
		String resourceHref = "OEBPS/foo/bar.html";
		Resources resources = mock(Resources.class);
		Resource resource = mock(Resource.class);
		when(resources.getAll()).thenReturn(Arrays.asList(resource));
		when(resource.getHref()).thenReturn(resourceHref);
		
		// when
		PackageDocumentReader.fixHrefs(packageHref, resources);
		
		// then
		Mockito.verify(resource).setHref("foo/bar.html");
	}
	
	
	@Test
	public void testFixHrefs_invalid_prefix() {
		// given
		String packageHref = "123456789/";
		String resourceHref = "1/2.html";
		Resources resources = mock(Resources.class);
		Resource resource = mock(Resource.class);
		when(resources.getAll()).thenReturn(Arrays.asList(resource));
		when(resource.getHref()).thenReturn(resourceHref);
		
		// when
		PackageDocumentReader.fixHrefs(packageHref, resources);
		
		// then
		Assert.assertTrue(true);
	}
}
