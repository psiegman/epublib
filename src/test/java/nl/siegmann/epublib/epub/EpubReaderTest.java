package nl.siegmann.epublib.epub;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;

public class EpubReaderTest extends TestCase {
	
	public void testCover_only_cover() {
		try {
			Book book = new Book();
			
			book.setCoverImage(new Resource(this.getClass().getResourceAsStream("/book1/test_cover.png"), "cover.png"));

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
			
			book.setCoverImage(new Resource(this.getClass().getResourceAsStream("/book1/test_cover.png"), "cover.png"));
			book.addSection("Introduction", new Resource(this.getClass().getResourceAsStream("/book1/chapter1.html"), "chapter1.html"));
			book.generateSpineFromTableOfContents();
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			(new EpubWriter()).write(book, out);
			byte[] epubData = out.toByteArray();
			Book readBook = new EpubReader().readEpub(new ByteArrayInputStream(epubData));
			assertNotNull(readBook.getCoverPage());
			assertEquals(1, book.getSpine().size());
			assertEquals(1, book.getTableOfContents().size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertTrue(false);
		}
	}
}
