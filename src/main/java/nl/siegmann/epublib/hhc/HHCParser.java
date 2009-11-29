package nl.siegmann.epublib.hhc;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import nl.siegmann.epublib.Book;
import nl.siegmann.epublib.Section;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class HHCParser {
	
	public static Book parseHhc(File hhcFile, File chmRootDir) throws IOException, ParserConfigurationException, XPathExpressionException {
		Book result = new Book();
		result.setTitle("test book");
		HtmlCleaner htmlCleaner = new HtmlCleaner();
		CleanerProperties props = htmlCleaner.getProperties(); 
		TagNode node = htmlCleaner.clean(hhcFile);
		Document hhcDocument = new DomSerializer(props).createDOM(node);
		PrettyXmlSerializer prettyPrinter = new PrettyXmlSerializer(props);
		System.out.println(prettyPrinter.getXmlAsString(node));
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList spineLiNodes = (NodeList) xpath.evaluate("body/ul/li", hhcDocument.getDocumentElement(), XPathConstants.NODESET);
		for(int i = 0; i < spineLiNodes.getLength(); i++) {
			Node objectNode = (Node) xpath.evaluate("object", spineLiNodes.item(i), XPathConstants.NODE);
			String name = ((Element) xpath.evaluate("param[@name = 'Name']", objectNode, XPathConstants.NODE)).getAttribute("value");
			String href= ((Element) xpath.evaluate("param[@name = 'Local']", objectNode, XPathConstants.NODE)).getAttribute("value");
//			System.out.println(HHCParser.class.getName() + " node:" + nodes.item(i).getNodeName());
			Section section = new Section(href, name, href);
			result.getSections().add(section);
			System.out.println(HHCParser.class.getName() + " name:" + name + ", href:" + href);
		}
		return result;
	}	
}
