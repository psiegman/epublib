package nl.siegmann.epublib.hhc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;

import junit.framework.TestCase;
import nl.siegmann.epublib.Book;
import nl.siegmann.epublib.EpubWriter;

public class HHCParserTest extends TestCase {

	public void test1() {
		try {
			Book book = HHCParser.parseHhc(new File("/home/paul/project/private/library/chm/peaa/0321127420.hhc"), new File("/home/paul"));
			(new EpubWriter()).write(book, new FileOutputStream("/home/paul/foo"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathExpressionException e) {
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
