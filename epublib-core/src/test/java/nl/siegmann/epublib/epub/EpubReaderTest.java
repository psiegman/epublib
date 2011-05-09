package nl.siegmann.epublib.epub;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.service.MediatypeService;

public class EpubReaderTest extends TestCase {
	
	public void testCover_only_cover() {
		try {
			Book book = new Book();
			
			book.setCoverImage(new Resource(this.getClass().getResourceAsStream("/book1/cover.png"), "cover.png"));

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			(new EpubWriter()).write(book, out);
			byte[] epubData = out.toByteArray();
			Book readBook = new EpubReader().readEpub(new ByteArrayInputStream(epubData));
			assertNotNull(readBook.getCoverImage());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertTrue(false);
		}

	}

	public void testCover_cover_one_section() {
		try {
			Book book = new Book();
			
			book.setCoverImage(new Resource(this.getClass().getResourceAsStream("/book1/cover.png"), "cover.png"));
			book.addSection("Introduction", new Resource(this.getClass().getResourceAsStream("/book1/chapter1.html"), "chapter1.html"));
			book.generateSpineFromTableOfContents();
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			(new EpubWriter()).write(book, out);
			byte[] epubData = out.toByteArray();
			Book readBook = new EpubReader().readEpub(new ByteArrayInputStream(epubData));
			assertNotNull(readBook.getCoverPage());
			assertEquals(1, readBook.getSpine().size());
			assertEquals(1, readBook.getTableOfContents().size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertTrue(false);
		}
	}

	public void testReadEpub_opf_ncx_docs() {
		try {
			Book book = new Book();
			
			book.setCoverImage(new Resource(this.getClass().getResourceAsStream("/book1/cover.png"), "cover.png"));
			book.addSection("Introduction", new Resource(this.getClass().getResourceAsStream("/book1/chapter1.html"), "chapter1.html"));
			book.generateSpineFromTableOfContents();
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			(new EpubWriter()).write(book, out);
			byte[] epubData = out.toByteArray();
			Book readBook = new EpubReader().readEpub(new ByteArrayInputStream(epubData));
			assertNotNull(readBook.getCoverPage());
			assertEquals(1, readBook.getSpine().size());
			assertEquals(1, readBook.getTableOfContents().size());
			assertNotNull(readBook.getOpfResource());
			assertNotNull(readBook.getNcxResource());
			assertEquals(MediatypeService.NCX, readBook.getNcxResource().getMediaType());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertTrue(false);
		}
	}
}
