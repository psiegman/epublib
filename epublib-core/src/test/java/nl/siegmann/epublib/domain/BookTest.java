package nl.siegmann.epublib.domain;

import nl.siegmann.epublib.service.MediatypeService;

import org.junit.Assert;
import org.junit.Test;

public class BookTest {

	@Test
	public void testGetContents1() {
		Book book = new Book();
		Resource resource1 = new Resource("id1", "Hello, world !".getBytes(), "chapter1.html", MediatypeService.XHTML);
		book.getSpine().addResource(resource1);
		book.getTableOfContents().addSection(resource1, "My first chapter");
		Assert.assertEquals(1, book.getContents().size());
	}

	@Test
	public void testGetContents2() {
		Book book = new Book();
		Resource resource1 = new Resource("id1", "Hello, world !".getBytes(), "chapter1.html", MediatypeService.XHTML);
		book.getSpine().addResource(resource1);
		Resource resource2 = new Resource("id1", "Hello, world !".getBytes(), "chapter2.html", MediatypeService.XHTML);
		book.getTableOfContents().addSection(resource2, "My first chapter");
		Assert.assertEquals(2, book.getContents().size());
	}

	@Test
	public void testGetContents3() {
		Book book = new Book();
		Resource resource1 = new Resource("id1", "Hello, world !".getBytes(), "chapter1.html", MediatypeService.XHTML);
		book.getSpine().addResource(resource1);
		Resource resource2 = new Resource("id1", "Hello, world !".getBytes(), "chapter2.html", MediatypeService.XHTML);
		book.getTableOfContents().addSection(resource2, "My first chapter");
		book.getGuide().addReference(new GuideReference(resource2, GuideReference.FOREWORD, "The Foreword"));
		Assert.assertEquals(2, book.getContents().size());
	}

	@Test
	public void testGetContents4() {
		Book book = new Book();
		
		Resource resource1 = new Resource("id1", "Hello, world !".getBytes(), "chapter1.html", MediatypeService.XHTML);
		book.getSpine().addResource(resource1);
		
		Resource resource2 = new Resource("id1", "Hello, world !".getBytes(), "chapter2.html", MediatypeService.XHTML);
		book.getTableOfContents().addSection(resource2, "My first chapter");

		Resource resource3 = new Resource("id1", "Hello, world !".getBytes(), "foreword.html", MediatypeService.XHTML);
		book.getGuide().addReference(new GuideReference(resource3, GuideReference.FOREWORD, "The Foreword"));

		Assert.assertEquals(3, book.getContents().size());
	}
}
