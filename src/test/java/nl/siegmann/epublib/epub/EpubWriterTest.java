package nl.siegmann.epublib.epub;

import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import junit.framework.TestCase;
import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.InputStreamResource;
import nl.siegmann.epublib.domain.Section;

public class EpubWriterTest extends TestCase {

	public void testBook1() {
		try {
			// Create new Book
			Book book = new Book();

			// Set the title
			book.getMetadata().setTitle("Epublib test book 1");
			
			// Add an Author
			book.getMetadata().addAuthor(new Author("Joe", "Tester"));

			// Set cover image
			book.setCoverImage(new InputStreamResource(this.getClass().getResourceAsStream("/book1/rock_640x480.jpg"), "rock.jpg"));
			
			// Add Chapter 1
			book.addResourceAsSection("Chapter 1", new InputStreamResource(this.getClass().getResourceAsStream("/book1/chapter1.html"), "chapter1.html"));

			// Add css file
			book.addResource(new InputStreamResource(this.getClass().getResourceAsStream("/book1/book1.css"), "book1.css"));

			// Add Chapter 2
			Section chapter2 = book.addResourceAsSection("Chapter 2", new InputStreamResource(this.getClass().getResourceAsStream("/book1/chapter2.html"), "chapter2.html"));
			
			// Add image used by Chapter 2
			book.addResource(new InputStreamResource(this.getClass().getResourceAsStream("/book1/flowers_320x240.jpg"), "flowers.jpg"));

			// Add Chapter2, Section 1
			book.addResourceAsSubSection(chapter2, "Chapter 2 section 1", new InputStreamResource(this.getClass().getResourceAsStream("/book1/chapter2_1.html"), "chapter2_1.html"));

			// Add Chapter 3
			book.addResourceAsSection("Chapter 3", new InputStreamResource(this.getClass().getResourceAsStream("/book1/chapter3.html"), "chapter3.html"));

			// Create EpubWriter
			EpubWriter writer = new EpubWriter();

			// Write the Book as Epub
			writer.write(book, new FileOutputStream("test_book1.epub"));
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
