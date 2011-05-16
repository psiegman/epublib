package nl.siegmann.epublib.domain;

import junit.framework.TestCase;
import nl.siegmann.epublib.service.MediatypeService;

public class ResourcesTest extends TestCase {
	
	public void testGetResourcesByMediaType1() {
		Resources resources = new Resources();
		resources.add(new Resource("foo".getBytes(), MediatypeService.XHTML));
		resources.add(new Resource("bar".getBytes(), MediatypeService.XHTML));
		assertEquals(0, resources.getResourcesByMediaType(MediatypeService.PNG).size());
		assertEquals(2, resources.getResourcesByMediaType(MediatypeService.XHTML).size());
		assertEquals(2, resources.getResourcesByMediaTypes(new MediaType[] {MediatypeService.XHTML}).size());
	}

	public void testGetResourcesByMediaType2() {
		Resources resources = new Resources();
		resources.add(new Resource("foo".getBytes(), MediatypeService.XHTML));
		resources.add(new Resource("bar".getBytes(), MediatypeService.PNG));
		resources.add(new Resource("baz".getBytes(), MediatypeService.PNG));
		assertEquals(2, resources.getResourcesByMediaType(MediatypeService.PNG).size());
		assertEquals(1, resources.getResourcesByMediaType(MediatypeService.XHTML).size());
		assertEquals(1, resources.getResourcesByMediaTypes(new MediaType[] {MediatypeService.XHTML}).size());
		assertEquals(3, resources.getResourcesByMediaTypes(new MediaType[] {MediatypeService.XHTML, MediatypeService.PNG}).size());
		assertEquals(3, resources.getResourcesByMediaTypes(new MediaType[] {MediatypeService.CSS, MediatypeService.XHTML, MediatypeService.PNG}).size());
	}
}
