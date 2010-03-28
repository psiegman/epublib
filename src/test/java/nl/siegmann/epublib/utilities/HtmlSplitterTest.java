package nl.siegmann.epublib.utilities;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import junit.framework.TestCase;

public class HtmlSplitterTest extends TestCase {

	public void Xtest1() {
		HtmlSplitter htmlSplitter = new HtmlSplitter();
		try {
			Reader input = new FileReader("/home/paul/anathem.xhtml");
			List<List<XMLEvent>> result = htmlSplitter.splitHtml(input, 10000);
			System.out.println(this.getClass().getName() + ": split in " + result.size() + " pieces");
			XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
			for(int i = 0; i < result.size(); i++) {
				File partFile = new File("part_" + i + ".xhtml");
				XMLEventWriter out = xmlOutputFactory.createXMLEventWriter(new FileWriter(partFile));
				for(XMLEvent xmlEvent: result.get(i)) {
					out.add(xmlEvent);
				}
				out.close();
				System.out.println("written to " + partFile.getAbsolutePath());
			}
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
