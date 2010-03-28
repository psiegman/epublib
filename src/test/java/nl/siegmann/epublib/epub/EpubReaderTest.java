package nl.siegmann.epublib.epub;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import junit.framework.TestCase;
import nl.siegmann.epublib.domain.Book;

public class EpubReaderTest extends TestCase {
	
	public void test1() {
		EpubReader epubReader = new EpubReader();
		try {
			Book book = epubReader.readEpub(new FileInputStream(new File("/home/paul/ccs.epub")));
			System.out.println("found " + book.getResources().size() + " resources");
			EpubWriter epubWriter = new EpubWriter();
			epubWriter.write(book, new FileOutputStream("/home/paul/ccstest.epub"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
