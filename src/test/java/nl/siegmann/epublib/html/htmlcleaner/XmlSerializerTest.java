package nl.siegmann.epublib.html.htmlcleaner;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import junit.framework.TestCase;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;

public class XmlSerializerTest extends TestCase {

	public void testSimpleDocument() {
		HtmlCleaner htmlCleaner = new HtmlCleaner();
		CleanerProperties props = htmlCleaner.getProperties();
		String testInput = "<html><head><title>test page</title><link foo=\"bar\"></head><body background=\"red\">Hello, world!</html>";
		try {
			StringWriter out = new StringWriter();
			TagNode rootNode = htmlCleaner.clean(new StringReader(testInput));
			XmlSerializer testSerializer = new XmlSerializer(props);
			testSerializer.serialize(rootNode, XMLOutputFactory.newInstance().createXMLStreamWriter(out));
			System.out.println("result:" + out.toString());
//			PrettyXmlSerializer ser2 = new PrettyXmlSerializer(props);
//			System.out.println(ser2.getXmlAsString(rootNode));
		} catch (IOException e) {
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

	public void testSimpleDocument2() {
		HtmlCleaner htmlCleaner = new HtmlCleaner();
		CleanerProperties props = htmlCleaner.getProperties();
		try {
			StringWriter out = new StringWriter();
			TagNode rootNode = htmlCleaner.clean(new File("/home/paul/project/private/library/customercentricselling/input/7101final/LiB0004.html"));
			XmlSerializer testSerializer = new XmlSerializer(props);
			testSerializer.serialize(rootNode, XMLOutputFactory.newInstance().createXMLStreamWriter(out));
			System.out.println("result:" + out.toString());
			PrettyXmlSerializer ser2 = new PrettyXmlSerializer(props);
			System.out.println(ser2.getXmlAsString(rootNode));
		} catch (IOException e) {
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
