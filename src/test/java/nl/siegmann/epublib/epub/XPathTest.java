package nl.siegmann.epublib.epub;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import junit.framework.TestCase;
import nl.siegmann.epublib.utilities.HtmlSplitterTest;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XPathTest extends TestCase {
	
	public void test1() {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(true);
		try {
			InputStream input = HtmlSplitterTest.class.getResourceAsStream("/toc.xml");
			Document ncxDocument = documentBuilderFactory.newDocumentBuilder().parse(input);
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			xpath.setNamespaceContext(NCXDocument.NCX_DOC_NAMESPACE_CONTEXT);
			final String PREFIX_NCX = "ncx";
		    NodeList navmapNodes = (NodeList) xpath.evaluate("/" + PREFIX_NCX + ":ncx/" + PREFIX_NCX + ":navMap/" + PREFIX_NCX + ":navPoint", ncxDocument, XPathConstants.NODESET);
		    assertEquals(3, navmapNodes.getLength());
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
