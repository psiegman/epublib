package nl.siegmann.epublib.epub;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sf.jazzlib.ZipFile;
import net.sf.jazzlib.ZipInputStream;
import nl.siegmann.epublib.domain.LazyResource;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Resources;
import nl.siegmann.epublib.service.MediatypeService;
import nl.siegmann.epublib.util.IOUtil;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ResourcesLoaderTest {

	private static final String encoding = "UTF-8";
	private static String testBookFilename;

	@BeforeClass
	public static void setUpClass() throws IOException {
 	   File testbook = File.createTempFile("testbook", ".epub"); 
 	   OutputStream out = new FileOutputStream(testbook);
 	   IOUtil.copy(ResourcesLoaderTest.class.getResourceAsStream("/testbook1.epub"), out);
 	   out.close();

 	   ResourcesLoaderTest.testBookFilename = testbook.getAbsolutePath();
	}
	
	@AfterClass
	public static void tearDownClass() {
		new File(testBookFilename).delete();
	}
	
	/**
	 * Loads the Resource from an InputStream
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testLoadResources_InputStream() throws FileNotFoundException, IOException {
		// given
		InputStream inputStream = new FileInputStream(new File(testBookFilename));
		
		// when
		Resources resources = ResourcesLoader.loadResources(inputStream, encoding);
		
		// then
		verifyResources(resources);
	}
	
	/**
	 * Loads the Resources from a ZipInputStream
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testLoadResources_ZipInputStream() throws FileNotFoundException, IOException {
		// given
		ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(new File(testBookFilename)));
		
		// when
		Resources resources = ResourcesLoader.loadResources(zipInputStream, encoding);
		
		// then
		verifyResources(resources);
	}

	/**
	 * Loads the Resources from a ZipFile
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testLoadResources_ZipFile() throws FileNotFoundException, IOException {
		// given
		ZipFile zipFile = new ZipFile(testBookFilename);
		
		// when
		Resources resources = ResourcesLoader.loadResources(zipFile, encoding);
		
		// then
		verifyResources(resources);
	}

	/**
	 * Loads all Resources lazily from a ZipFile
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testLoadResources_ZipFile_lazy_all() throws FileNotFoundException, IOException {
		// given
		ZipFile zipFile = new ZipFile(testBookFilename);
		
		// when
		Resources resources = ResourcesLoader.loadResources(zipFile, encoding, Arrays.asList(MediatypeService.mediatypes));
		
		// then
		verifyResources(resources);
		Assert.assertEquals(Resource.class, resources.getById("container").getClass());
		Assert.assertEquals(LazyResource.class, resources.getById("book1").getClass());
	}

	/**
	 * Loads the Resources from a ZipFile, some of them lazily.
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testLoadResources_ZipFile_partial_lazy() throws FileNotFoundException, IOException {
		// given
		ZipFile zipFile = new ZipFile(testBookFilename);
		
		// when
		Resources resources = ResourcesLoader.loadResources(zipFile, encoding, Arrays.asList(MediatypeService.CSS));
		
		// then
		verifyResources(resources);
		Assert.assertEquals(Resource.class, resources.getById("container").getClass());
		Assert.assertEquals(LazyResource.class, resources.getById("book1").getClass());
		Assert.assertEquals(Resource.class, resources.getById("chapter1").getClass());
	}

	private void verifyResources(Resources resources) throws IOException {
		Assert.assertNotNull(resources);
		Assert.assertEquals(12, resources.getAll().size());
		List<String> allHrefs = new ArrayList<String>(resources.getAllHrefs());
		Collections.sort(allHrefs);
		
		Resource resource;
		byte[] expectedData;
		
		// container
		resource = resources.getByHref(allHrefs.get(0));
		Assert.assertEquals("container", resource.getId());
		Assert.assertEquals("META-INF/container.xml", resource.getHref());
		Assert.assertNull(resource.getMediaType());
		Assert.assertEquals(230, resource.getData().length);
		
		// book1.css
		resource = resources.getByHref(allHrefs.get(1));
		Assert.assertEquals("book1", resource.getId());
		Assert.assertEquals("OEBPS/book1.css", resource.getHref());
		Assert.assertEquals(MediatypeService.CSS, resource.getMediaType());
		Assert.assertEquals(65, resource.getData().length);
		expectedData = IOUtil.toByteArray(this.getClass().getResourceAsStream("/book1/book1.css"));
		Assert.assertTrue(Arrays.equals(expectedData, resource.getData()));
		
		
		// chapter1
		resource = resources.getByHref(allHrefs.get(2));
		Assert.assertEquals("chapter1", resource.getId());
		Assert.assertEquals("OEBPS/chapter1.html", resource.getHref());
		Assert.assertEquals(MediatypeService.XHTML, resource.getMediaType());
		Assert.assertEquals(247, resource.getData().length);
		expectedData = IOUtil.toByteArray(this.getClass().getResourceAsStream("/book1/chapter1.html"));
		Assert.assertTrue(Arrays.equals(expectedData, resource.getData()));
	}
}
