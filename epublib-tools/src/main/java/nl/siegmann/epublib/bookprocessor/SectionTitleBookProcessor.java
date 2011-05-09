package nl.siegmann.epublib.bookprocessor;

import java.io.IOException;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubProcessor;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.InputSource;

public class SectionTitleBookProcessor implements BookProcessor {

	@Override
	public Book processBook(Book book, EpubProcessor epubProcessor) {
		XPath xpath = createXPathExpression();
		processSections(book.getTableOfContents().getTocReferences(), book, xpath);
		return book;
	}

	private void processSections(List<TOCReference> tocReferences, Book book, XPath xpath) {
		for(TOCReference tocReference: tocReferences) {
			if(! StringUtils.isBlank(tocReference.getTitle())) {
				continue;
			}
			try {
				String title = getTitle(tocReference, book, xpath);
				tocReference.setTitle(title);
			} catch (XPathExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	private String getTitle(TOCReference tocReference, Book book, XPath xpath) throws IOException, XPathExpressionException {
		Resource resource = tocReference.getResource();
		if(resource == null) {
			return null;
		}
		InputSource inputSource = new InputSource(resource.getInputStream());
		String title = xpath.evaluate("/html/head/title", inputSource);
		return title;
	}
	
	
	private XPath createXPathExpression() {
		return XPathFactory.newInstance().newXPath();
	}
}
