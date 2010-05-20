package nl.siegmann.epublib.epub;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import junit.framework.TestCase;
import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Identifier;
import nl.siegmann.epublib.domain.InputStreamResource;
import nl.siegmann.epublib.domain.Section;
import nl.siegmann.epublib.util.CollectionUtil;

public class EpubWriterTest extends TestCase {

	public void testBook1() {
		try {
			// Create new Book
			Book book = new Book();

			// Set the title
			book.getMetadata().addTitle("Epublib test book 1");
			
			// Set the title
			book.getMetadata().addTitle("A simple test");
			
			// Add an Author
			book.getMetadata().addAuthor(new Author("Joe", "Tester"));

			// Set cover image
			book.setCoverImage(new InputStreamResource(this.getClass().getResourceAsStream("/book1/test_cover.png"), "cover.png"));
			
			// Add Chapter 1
			book.addResourceAsSection("Introduction", new InputStreamResource(this.getClass().getResourceAsStream("/book1/chapter1.html"), "chapter1.html"));

			// Add css file
			book.addResource(new InputStreamResource(this.getClass().getResourceAsStream("/book1/book1.css"), "book1.css"));

			// Add Chapter 2
			Section chapter2 = book.addResourceAsSection("Second Chapter", new InputStreamResource(this.getClass().getResourceAsStream("/book1/chapter2.html"), "chapter2.html"));
			
			// Add image used by Chapter 2
			book.addResource(new InputStreamResource(this.getClass().getResourceAsStream("/book1/flowers_320x240.jpg"), "flowers.jpg"));

			// Add Chapter2, Section 1
			book.addResourceAsSubSection(chapter2, "Chapter 2, section 1", new InputStreamResource(this.getClass().getResourceAsStream("/book1/chapter2_1.html"), "chapter2_1.html"));

			// Add Chapter 3
			book.addResourceAsSection("Conclusion", new InputStreamResource(this.getClass().getResourceAsStream("/book1/chapter3.html"), "chapter3.html"));

			// Create EpubWriter
			EpubWriter epubWriter = new EpubWriter();

			// Write the Book as Epub
			epubWriter.write(book, new FileOutputStream("test1_book1.epub"));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void testBook2() {
		try {
			Book book = new Book();

			book.getMetadata().addTitle("Epublib test book 1");
			book.getMetadata().addTitle("test2");
			
			String isbn = "987654321";
			book.getMetadata().addIdentifier(new Identifier(Identifier.Scheme.ISBN, isbn));
			Author author = new Author("Joe", "Tester");
			book.getMetadata().addAuthor(author);
			book.setCoverImage(new InputStreamResource(this.getClass().getResourceAsStream("/book1/test_cover.png"), "cover.png"));
			book.addResourceAsSection("Chapter 1", new InputStreamResource(this.getClass().getResourceAsStream("/book1/chapter1.html"), "chapter1.html"));
			book.addResource(new InputStreamResource(this.getClass().getResourceAsStream("/book1/book1.css"), "book1.css"));
			Section chapter2 = book.addResourceAsSection("Second chapter", new InputStreamResource(this.getClass().getResourceAsStream("/book1/chapter2.html"), "chapter2.html"));
			book.addResource(new InputStreamResource(this.getClass().getResourceAsStream("/book1/flowers_320x240.jpg"), "flowers.jpg"));
			book.addResourceAsSubSection(chapter2, "Chapter 2 section 1", new InputStreamResource(this.getClass().getResourceAsStream("/book1/chapter2_1.html"), "chapter2_1.html"));
			book.addResourceAsSection("Chapter 3", new InputStreamResource(this.getClass().getResourceAsStream("/book1/chapter3.html"), "chapter3.html"));
			EpubWriter writer = new EpubWriter();

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			writer.write(book, out);
			byte[] epubData = out.toByteArray();
			new FileOutputStream("test2_book1.epub").write(epubData);

			assertNotNull(epubData);
			assertTrue(epubData.length > 0);
			
			Book readBook = new EpubReader().readEpub(new ByteArrayInputStream(epubData));
			assertEquals(book.getMetadata().getTitles(), readBook.getMetadata().getTitles());
			assertEquals(Identifier.Scheme.ISBN, CollectionUtil.first(readBook.getMetadata().getIdentifiers()).getScheme());
			assertEquals(isbn, CollectionUtil.first(readBook.getMetadata().getIdentifiers()).getValue());
			assertEquals(author, CollectionUtil.first(readBook.getMetadata().getAuthors()));
			ByteArrayOutputStream out2 = new ByteArrayOutputStream();
			writer.write(readBook, out2);
			byte[] epubData2 = out2.toByteArray();

			new FileOutputStream("test2_book2.epub").write(epubData2);
//			assertTrue(Arrays.equals(epubData, epubData2));
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
