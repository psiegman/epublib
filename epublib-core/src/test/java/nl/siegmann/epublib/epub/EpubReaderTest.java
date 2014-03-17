package nl.siegmann.epublib.epub;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.service.MediatypeService;

import org.junit.Assert;
import org.junit.Test;

public class EpubReaderTest {

	@Test
	public void testCover_only_cover() throws IOException {
		Book book = new Book();

		book.setCoverImage(new Resource(this.getClass().getResourceAsStream(
				"/book1/cover.png"), "cover.png"));

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		(new EpubWriter()).write(book, out);
		byte[] epubData = out.toByteArray();
		Book readBook = new EpubReader().readEpub(new ByteArrayInputStream(
				epubData));
		Assert.assertNotNull(readBook.getCoverImage());
	}

	@Test
	public void testCover_cover_one_section() throws IOException {
		Book book = new Book();

		book.setCoverImage(new Resource(this.getClass().getResourceAsStream(
				"/book1/cover.png"), "cover.png"));
		book.addSection("Introduction", new Resource(this.getClass()
				.getResourceAsStream("/book1/chapter1.html"), "chapter1.html"));
		book.generateSpineFromTableOfContents();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		(new EpubWriter()).write(book, out);
		byte[] epubData = out.toByteArray();
		Book readBook = new EpubReader().readEpub(new ByteArrayInputStream(
				epubData));
		Assert.assertNotNull(readBook.getCoverPage());
		Assert.assertEquals(1, readBook.getSpine().size());
		Assert.assertEquals(1, readBook.getTableOfContents().size());
	}

	@Test
	public void testReadEpub_opf_ncx_docs() throws IOException {
		Book book = new Book();

		book.setCoverImage(new Resource(this.getClass().getResourceAsStream(
				"/book1/cover.png"), "cover.png"));
		book.addSection("Introduction", new Resource(this.getClass()
				.getResourceAsStream("/book1/chapter1.html"), "chapter1.html"));
		book.generateSpineFromTableOfContents();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		(new EpubWriter()).write(book, out);
		byte[] epubData = out.toByteArray();
		Book readBook = new EpubReader().readEpub(new ByteArrayInputStream(
				epubData));
		Assert.assertNotNull(readBook.getCoverPage());
		Assert.assertEquals(1, readBook.getSpine().size());
		Assert.assertEquals(1, readBook.getTableOfContents().size());
		Assert.assertNotNull(readBook.getOpfResource());
		Assert.assertNotNull(readBook.getNcxResource());
		Assert.assertEquals(MediatypeService.NCX, readBook.getNcxResource()
				.getMediaType());
	}
}
