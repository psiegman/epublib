package nl.siegmann.epublib.epub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.GuideReference;
import nl.siegmann.epublib.domain.Identifier;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.util.CollectionUtil;

import org.junit.Assert;
import org.junit.Test;

public class EpubWriterTest {

	@Test
	public void testBook1() throws IOException {
		// create test book
		Book book = createTestBook();
		
		// write book to byte[]
		byte[] bookData = writeBookToByteArray(book);
			FileOutputStream fileOutputStream = new FileOutputStream("foo.zip");
			fileOutputStream.write(bookData);
			fileOutputStream.flush();
			fileOutputStream.close();
		Assert.assertNotNull(bookData);
		Assert.assertTrue(bookData.length > 0);
		
		// read book from byte[]
		Book readBook = new EpubReader().readEpub(new ByteArrayInputStream(bookData));
		
		// assert book values are correct
		Assert.assertEquals(book.getMetadata().getTitles(), readBook.getMetadata().getTitles());
		Assert.assertEquals(Identifier.Scheme.ISBN, CollectionUtil.first(readBook.getMetadata().getIdentifiers()).getScheme());
		Assert.assertEquals(CollectionUtil.first(book.getMetadata().getIdentifiers()).getValue(), CollectionUtil.first(readBook.getMetadata().getIdentifiers()).getValue());
		Assert.assertEquals(CollectionUtil.first(book.getMetadata().getAuthors()), CollectionUtil.first(readBook.getMetadata().getAuthors()));
		Assert.assertEquals(1, readBook.getGuide().getGuideReferencesByType(GuideReference.COVER).size());
		Assert.assertEquals(5, readBook.getSpine().size());
		Assert.assertNotNull(book.getCoverPage());
		Assert.assertNotNull(book.getCoverImage());
		Assert.assertEquals(4, readBook.getTableOfContents().size());
			
	}
	
	/**
	 * Test for a very old bug where epublib would throw a NullPointerException when writing a book with a cover that has no id.
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * 
	 */
	public void testWritingBookWithCoverWithNullId() throws FileNotFoundException, IOException {
		Book book = new Book();
	    book.getMetadata().addTitle("Epub test book 1");
	    book.getMetadata().addAuthor(new Author("Joe", "Tester"));
	    InputStream is = this.getClass().getResourceAsStream("/book1/cover.png");
	    book.setCoverImage(new Resource(is, "cover.png"));
	    // Add Chapter 1
	    InputStream is1 = this.getClass().getResourceAsStream("/book1/chapter1.html");
	    book.addSection("Introduction", new Resource(is1, "chapter1.html"));
	
	    EpubWriter epubWriter = new EpubWriter();
	    epubWriter.write(book, new FileOutputStream("test1_book1.epub"));
	}
	
	private Book createTestBook() throws IOException {
		Book book = new Book();
		
		book.getMetadata().addTitle("Epublib test book 1");
		book.getMetadata().addTitle("test2");
		
		book.getMetadata().addIdentifier(new Identifier(Identifier.Scheme.ISBN, "987654321"));
		book.getMetadata().addAuthor(new Author("Joe", "Tester"));
		book.setCoverPage(new Resource(this.getClass().getResourceAsStream("/book1/cover.html"), "cover.html"));
		book.setCoverImage(new Resource(this.getClass().getResourceAsStream("/book1/cover.png"), "cover.png"));
		book.addSection("Chapter 1", new Resource(this.getClass().getResourceAsStream("/book1/chapter1.html"), "chapter1.html"));
		book.addResource(new Resource(this.getClass().getResourceAsStream("/book1/book1.css"), "book1.css"));
		TOCReference chapter2 = book.addSection("Second chapter", new Resource(this.getClass().getResourceAsStream("/book1/chapter2.html"), "chapter2.html"));
		book.addResource(new Resource(this.getClass().getResourceAsStream("/book1/flowers_320x240.jpg"), "flowers.jpg"));
		book.addSection(chapter2, "Chapter 2 section 1", new Resource(this.getClass().getResourceAsStream("/book1/chapter2_1.html"), "chapter2_1.html"));
		book.addSection("Chapter 3", new Resource(this.getClass().getResourceAsStream("/book1/chapter3.html"), "chapter3.html"));
		return book;
	}
	

	private byte[] writeBookToByteArray(Book book) throws IOException {
		EpubWriter epubWriter = new EpubWriter();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		epubWriter.write(book, out);
		return out.toByteArray();
	}
//
//	  public static void writeEpub(BookDTO dto) throws IOException{
//	        Book book = new Book();
//	       
//	        Resource coverImg = new Resource(new FileInputStream(ResourceBundle.getBundle("info.pxdev.pfi.webclient.resources.Config").getString("COVER_DIR")+dto.getCoverFileName()),dto.getCoverFileName());
//	               
//	        book.getMetadata().addTitle(dto.getTitle());
//	       
//	        if(dto.getIdentifier().getType().getName().equals("ISBN"))
//	            book.getMetadata().addIdentifier(new Identifier(Identifier.Scheme.ISBN, dto.getIdentifier().getIdentifier()));
//	        else
//	            book.getMetadata().addIdentifier(new Identifier(Identifier.Scheme.UUID, dto.getIdentifier().getIdentifier()));
//	       
//	        book.getMetadata().addAuthor(new Author(dto.getCreator().getName(), dto.getCreator().getLastName()));
//	        book.getMetadata().addPublisher(dto.getPublisher());
//	        book.getMetadata().addDate(new Date(dto.getLastModified()));
//	        book.getMetadata().addDescription(dto.getDescription());
//	        book.getMetadata().addType("TEXT");
//	        book.getMetadata().setLanguage(dto.getLanguage());
//	        book.getMetadata().setCoverImage(coverImg);
//	        book.getMetadata().setFormat(MediatypeService.EPUB.getName());
//	       
//	        for(BookSubCategoryDTO subject : dto.getSubjects()){
//	            book.getMetadata().getSubjects().add(subject.getName());   
//	        }
//	        for(BookContributorDTO contrib : dto.getContributors()){
//	            Author contributor = new Author(contrib.getName(), contrib.getLastName());
//	            contributor.setRelator(Relator.byCode(contrib.getType().getShortName()));
//	            book.getMetadata().addContributor(contributor);
//	        }
//	       
//	       
//	        book.setCoverImage(coverImg);
//	        for(BookChapterDTO chapter : dto.getChapters()){
//	            Resource aux = new Resource(HTMLGenerator.generateChapterHtmlStream(dto,chapter), "chapter"+chapter.getNumber()+".html");
//	            book.addSection(chapter.getTitle(), aux );
//	        }
//	       
//	        EpubWriter writer = new EpubWriter();
//	        FileOutputStream output = new FileOutputStream(ResourceBundle.getBundle("info.pxdev.pfi.webclient.resources.Config").getString("HTML_CHAPTERS")+dto.getId_book()+"\\test.epub");
//	       
//	        try {
//	            writer.write(book, output);
//	        } catch (XMLStreamException e) {
//	            // TODO Auto-generated catch block
//	            e.printStackTrace();
//	        } catch (FactoryConfigurationError e) {
//	            // TODO Auto-generated catch block
//	            e.printStackTrace();
//	        }
//	    }
}
