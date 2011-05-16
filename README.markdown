# epublib
Epublib is a java library for reading/writing/manipulating epub files.

I consists of 2 parts: a core that reads/writes epub and a collection of tools.
The tools contain an epub cleanup tool, a tool to create epubs from html files, a tool to create an epub from an uncompress html file.
It also contains a swing-based epub viewer.
![Epublib viewer](http://www.siegmann.nl/wp-content/uploads/Alice%E2%80%99s-Adventures-in-Wonderland_2011-01-30_18-17-30.png)

The core runs both on android and a standard java environment. The tools run only on a standard java environment.

This means that reading/writing epub files works on Android.

## Command line examples

Set the author of an existing epub
	java -jar epublib-3.0-SNAPSHOT.one-jar.jar --in input.epub --out result.epub --author Tester,Joe

Set the cover image of an existing epub
	java -jar epublib-3.0-SNAPSHOT.one-jar.jar --in input.epub --out result.epub --cover-image my_cover.jpg

## Creating an epub programmatically

	package nl.siegmann.epublib.examples;

	import java.io.FileOutputStream;

	import nl.siegmann.epublib.domain.Author;
	import nl.siegmann.epublib.domain.Book;
	import nl.siegmann.epublib.domain.InputStreamResource;
	import nl.siegmann.epublib.domain.Section;
	import nl.siegmann.epublib.epub.EpubWriter;

	public class Simple1 {
		public static void main(String[] args) {
			try {
				// Create new Book
				Book book = new Book();

				// Set the title
				book.getMetadata().addTitle("Epublib test book 1");

				// Add an Author
				book.getMetadata().addAuthor(new Author("Joe", "Tester"));

				// Set cover image
				book.getMetadata().setCoverImage(new InputStreamResource(Simple1.class.getResourceAsStream("/book1/test_cover.png"), "cover.png"));

				// Add Chapter 1
				book.addResourceAsSection("Introduction", new InputStreamResource(Simple1.class.getResourceAsStream("/book1/chapter1.html"), "chapter1.html"));

				// Add css file
				book.getResources().add(new InputStreamResource(Simple1.class.getResourceAsStream("/book1/book1.css"), "book1.css"));

				// Add Chapter 2
				Section chapter2 = book.addResourceAsSection("Second Chapter", new InputStreamResource(Simple1.class.getResourceAsStream("/book1/chapter2.html"), "chapter2.html"));

				// Add image used by Chapter 2
				book.getResources().add(new InputStreamResource(Simple1.class.getResourceAsStream("/book1/flowers_320x240.jpg"), "flowers.jpg"));

				// Add Chapter2, Section 1
				book.addResourceAsSubSection(chapter2, "Chapter 2, section 1", new InputStreamResource(Simple1.class.getResourceAsStream("/book1/chapter2_1.html"), "chapter2_1.html"));

				// Add Chapter 3
				book.addResourceAsSection("Conclusion", new InputStreamResource(Simple1.class.getResourceAsStream("/book1/chapter3.html"), "chapter3.html"));

				// Create EpubWriter
				EpubWriter epubWriter = new EpubWriter();

				// Write the Book as Epub
				epubWriter.write(book, new FileOutputStream("test1_book1.epub"));
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
