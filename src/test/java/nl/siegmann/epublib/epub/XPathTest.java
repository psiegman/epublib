package nl.siegmann.epublib.epub;

import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XPathTest extends TestCase {
	
	public void test1() {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		try {
			Document document = documentBuilderFactory.newDocumentBuilder().parse(new FileInputStream("/home/paul/ncx.xml"));
			XPathFactory factory = XPathFactory.newInstance();
		    XPath xpath = factory.newXPath();
		    NodeList nodes = (NodeList) xpath.evaluate("ncx/navMap/navPoint", document, XPathConstants.NODESET);
//		    System.out.println("found ncxnode:" + ncxNode.getLocalName());
//		    XPath xpath2 = factory.newXPath();
//		    NodeList nodes = (NodeList) result;
		    for (int i = 0; i < nodes.getLength(); i++) {
		        System.out.println(((Element) nodes.item(i)).getAttribute("id")); 
		        String label = xpath.evaluate("navLabel/text", nodes.item(i));
		        System.out.println("label:" + label);
		    }
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
