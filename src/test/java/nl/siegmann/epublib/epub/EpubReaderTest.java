package nl.siegmann.epublib.epub;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.InputStreamResource;

public class EpubReaderTest extends TestCase {

	public void testCover_only_cover() {
		try {
			Book book = new Book();
			
			book.getMetadata().setCoverImage(new InputStreamResource(this.getClass().getResourceAsStream("/book1/test_cover.png"), "cover.png"));

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			(new EpubWriter()).write(book, out);
			byte[] epubData = out.toByteArray();
			Book readBook = new EpubReader().readEpub(new ByteArrayInputStream(epubData));
			assertNotNull(readBook.getMetadata().getCoverPage());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertTrue(false);
		}

	}

	public void testCover_cover_one_section() {
		try {
			Book book = new Book();
			
			book.getMetadata().setCoverImage(new InputStreamResource(this.getClass().getResourceAsStream("/book1/test_cover.png"), "cover.png"));
			book.addResourceAsSection("Introduction", new InputStreamResource(this.getClass().getResourceAsStream("/book1/chapter1.html"), "chapter1.html"));

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			(new EpubWriter()).write(book, out);
			byte[] epubData = out.toByteArray();
			Book readBook = new EpubReader().readEpub(new ByteArrayInputStream(epubData));
			assertNotNull(readBook.getMetadata().getCoverPage());
			assertEquals(1, book.getSpineSections().size());
			assertEquals(1, book.getTocSections().size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertTrue(false);
		}
	}
}
