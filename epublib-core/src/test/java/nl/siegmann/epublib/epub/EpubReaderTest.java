package nl.siegmann.epublib.epub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import nl.siegmann.epublib.domain.OpfResource;
import org.junit.Test;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.service.MediatypeService;

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
		assertNotNull(readBook.getCoverImage());
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
		assertNotNull(readBook.getCoverPage());
		assertEquals(1, readBook.getSpine().size());
		assertEquals(1, readBook.getTableOfContents().size());
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
		assertNotNull(readBook.getCoverPage());
		assertEquals(1, readBook.getSpine().size());
		assertEquals(1, readBook.getTableOfContents().size());
		assertNotNull(readBook.getOpfResource());
		assertNotNull(readBook.getNcxResource());
		assertEquals(MediatypeService.NCX, readBook.getNcxResource()
				.getMediaType());
	}

	@Test
	public void testReadEpub_opf_ncx_version() throws IOException {
		Book book = new Book();

		book.setOpfResource(new OpfResource(new Resource(this.getClass().getResourceAsStream(
				"/opf/test3.opf"), "content.opf")));
		book.getOpfResource().setVersion("3.0");
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
		assertNotNull(readBook.getCoverPage());
		assertEquals(1, readBook.getSpine().size());
		assertEquals(1, readBook.getTableOfContents().size());
		assertNotNull(readBook.getOpfResource());
		assertEquals("3.0", readBook.getOpfResource().getVersion());
		assertNotNull(readBook.getNcxResource());
		assertEquals(MediatypeService.NCX, readBook.getNcxResource()
				.getMediaType());
	}

	@Test
	public void testReadEpub_opf_prefix() throws IOException {
		Book book = new Book();

		book.setOpfResource(new OpfResource(new Resource(this.getClass().getResourceAsStream(
				"/opf/test3.opf"), "content.opf")));
		book.getOpfResource().setVersion("3.0");
		book.getOpfResource().setPrefix("test_prefix");
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
		assertNotNull(readBook.getCoverPage());
		assertEquals(1, readBook.getSpine().size());
		assertEquals(1, readBook.getTableOfContents().size());
		assertNotNull(readBook.getOpfResource());
		assertEquals("test_prefix", readBook.getOpfResource().getPrefix());
		assertNotNull(readBook.getNcxResource());
		assertEquals(MediatypeService.NCX, readBook.getNcxResource()
				.getMediaType());
	}
}
