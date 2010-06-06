package nl.siegmann.epublib.html.htmlcleaner;

import java.io.IOException;

import junit.framework.TestCase;
import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.bookprocessor.HtmlCleanerBookProcessor;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.ByteArrayResource;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubWriter;
import nl.siegmann.epublib.service.MediatypeService;

public class HtmlCleanerBookProcessorTest extends TestCase {

	public void testSimpleDocument() {
		Book book = new Book();
		String testInput = "<html><head><title>test page</title><link foo=\"bar\"></head><body background=\"red\">Hello, world!</html>";
		String expectedResult = "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><title>test page</title><link foo=\"bar\" /></head><body background=\"red\">Hello, world!</body></html>";
		try {
			Resource resource = new ByteArrayResource("test.html", testInput.getBytes(Constants.ENCODING));
			book.getResources().add(resource);
			EpubWriter epubWriter = new EpubWriter();
			HtmlCleanerBookProcessor htmlCleanerBookProcessor = new HtmlCleanerBookProcessor();
			byte[] processedHtml = htmlCleanerBookProcessor.processHtml(resource, book, epubWriter, Constants.ENCODING);
			String actualResult = new String(processedHtml, Constants.ENCODING);
			assertEquals(expectedResult, actualResult);
		} catch (IOException e) {
			assertTrue(e.getMessage(), false);
		}
	}
	
	public void testSimpleDocument2() {
		Book book = new Book();
		String testInput = "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><title>test page</title><link foo=\"bar\" /></head><body background=\"red\">Hello, world!</body></html>";
		try {
			Resource resource = new ByteArrayResource("test.html", testInput.getBytes(Constants.ENCODING));
			book.getResources().add(resource);
			EpubWriter epubWriter = new EpubWriter();
			HtmlCleanerBookProcessor htmlCleanerBookProcessor = new HtmlCleanerBookProcessor();
			byte[] processedHtml = htmlCleanerBookProcessor.processHtml(resource, book, epubWriter, Constants.ENCODING);
			String result = new String(processedHtml, Constants.ENCODING);
			assertEquals(testInput, result);
		} catch (IOException e) {
			assertTrue(e.getMessage(), false);
		}
	}

	public void testSimpleDocument3() {
		Book book = new Book();
		String testInput = "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><title>test page</title></head><body>Hello, world! ß</body></html>";
		try {
			Resource resource = new ByteArrayResource(null, testInput.getBytes(Constants.ENCODING), "test.html", MediatypeService.XHTML, Constants.ENCODING);
			book.getResources().add(resource);
			EpubWriter epubWriter = new EpubWriter();
			HtmlCleanerBookProcessor htmlCleanerBookProcessor = new HtmlCleanerBookProcessor();
			byte[] processedHtml = htmlCleanerBookProcessor.processHtml(resource, book, epubWriter, Constants.ENCODING);
			String result = new String(processedHtml, Constants.ENCODING);
			assertEquals(testInput, result);
		} catch (IOException e) {
			assertTrue(e.getMessage(), false);
		}
	}


	public void XtestSimpleDocument4() {
		Book book = new Book();
		String testInput = "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><title>test page</title><link foo=\"bar\" /></head><body background=\"red\">Hello, world!ß</body></html>";
		try {
			String inputEncoding = "iso-8859-1";
			Resource resource = new ByteArrayResource(null, testInput.getBytes(inputEncoding), "test.html", MediatypeService.XHTML, inputEncoding);
			book.getResources().add(resource);
			EpubWriter epubWriter = new EpubWriter();
			HtmlCleanerBookProcessor htmlCleanerBookProcessor = new HtmlCleanerBookProcessor();
			byte[] processedHtml = htmlCleanerBookProcessor.processHtml(resource, book, epubWriter, Constants.ENCODING);
			String result = new String(processedHtml, Constants.ENCODING);
			assertEquals(testInput, result);
		} catch (IOException e) {
			assertTrue(e.getMessage(), false);
		}
	}
}
