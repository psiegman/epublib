package nl.siegmann.epublib.domain;

import nl.siegmann.epublib.service.MediatypeService;

import org.junit.Assert;
import org.junit.Test;

public class ResourcesTest {
	
	@Test
	public void testGetResourcesByMediaType1() {
		Resources resources = new Resources();
		resources.add(new Resource("foo".getBytes(), MediatypeService.XHTML));
		resources.add(new Resource("bar".getBytes(), MediatypeService.XHTML));
		Assert.assertEquals(0, resources.getResourcesByMediaType(MediatypeService.PNG).size());
		Assert.assertEquals(2, resources.getResourcesByMediaType(MediatypeService.XHTML).size());
		Assert.assertEquals(2, resources.getResourcesByMediaTypes(new MediaType[] {MediatypeService.XHTML}).size());
	}

	@Test
	public void testGetResourcesByMediaType2() {
		Resources resources = new Resources();
		resources.add(new Resource("foo".getBytes(), MediatypeService.XHTML));
		resources.add(new Resource("bar".getBytes(), MediatypeService.PNG));
		resources.add(new Resource("baz".getBytes(), MediatypeService.PNG));
		Assert.assertEquals(2, resources.getResourcesByMediaType(MediatypeService.PNG).size());
		Assert.assertEquals(1, resources.getResourcesByMediaType(MediatypeService.XHTML).size());
		Assert.assertEquals(1, resources.getResourcesByMediaTypes(new MediaType[] {MediatypeService.XHTML}).size());
		Assert.assertEquals(3, resources.getResourcesByMediaTypes(new MediaType[] {MediatypeService.XHTML, MediatypeService.PNG}).size());
		Assert.assertEquals(3, resources.getResourcesByMediaTypes(new MediaType[] {MediatypeService.CSS, MediatypeService.XHTML, MediatypeService.PNG}).size());
	}
}
