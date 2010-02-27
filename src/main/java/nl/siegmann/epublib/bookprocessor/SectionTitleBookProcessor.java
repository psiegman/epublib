package nl.siegmann.epublib.bookprocessor;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Section;
import nl.siegmann.epublib.epub.EpubWriter;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.InputSource;

public class SectionTitleBookProcessor implements BookProcessor {

	@Override
	public Book processBook(Book book, EpubWriter epubWriter) {
		Map<String, Resource> resources = BookProcessorUtil.createResourceByHrefMap(book);
		XPath xpath = createXPathExpression();
		processSections(book.getSections(), resources, xpath);
		return book;
	}

	private void processSections(List<Section> sections, Map<String, Resource> resources, XPath xpath) {
		for(Section section: sections) {
			if(! StringUtils.isBlank(section.getName())) {
				continue;
			}
			try {
				String title = getTitle(section, resources, xpath);
			} catch (XPathExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	private String getTitle(Section section, Map<String, Resource> resources, XPath xpath) throws IOException, XPathExpressionException {
		Resource resource = BookProcessorUtil.getResourceByHref(section.getHref(), resources);
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
