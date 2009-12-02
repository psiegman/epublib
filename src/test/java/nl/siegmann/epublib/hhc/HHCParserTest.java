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
			String root = "/home/paul/project/veh/backbase/Backbase_Rich_Portal_4.1/documentation/client/Reference/ref/";
			String testHhc =  root + "Reference.hhc";
//			String root = "/home/paul/project/private/library/chm/peaa/";
//			String testHhc =  root + "0321127420.hhc";
			Book book = HHCParser.parseHhc(new File(testHhc), new File(root));
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
