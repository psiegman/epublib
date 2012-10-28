package nl.siegmann.epublib.utilities;

import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.events.XMLEvent;

import junit.framework.TestCase;
import nl.siegmann.epublib.Constants;

public class HtmlSplitterTest extends TestCase {

	public void test1() {
		HtmlSplitter htmlSplitter = new HtmlSplitter();
		try {
			String bookResourceName = "/holmes_scandal_bohemia.html";
			Reader input = new InputStreamReader(HtmlSplitterTest.class.getResourceAsStream(bookResourceName), Constants.CHARACTER_ENCODING);
			int maxSize = 3000;
			List<List<XMLEvent>> result = htmlSplitter.splitHtml(input, maxSize);
			XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
			for(int i = 0; i < result.size(); i++) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				XMLEventWriter writer = xmlOutputFactory.createXMLEventWriter(out);
				for(XMLEvent xmlEvent: result.get(i)) {
					writer.add(xmlEvent);
				}
				writer.close();
				byte[] data = out.toByteArray();
				assertTrue(data.length > 0);
				assertTrue(data.length <= maxSize);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
