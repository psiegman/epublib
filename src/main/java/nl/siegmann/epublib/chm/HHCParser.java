package nl.siegmann.epublib.chm;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import nl.siegmann.epublib.domain.Section;

import org.apache.commons.lang.StringUtils;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Parses the windows help index (.hhc) file.
 * 
 * @author paul
 *
 */
public class HHCParser {

	public static final String DEFAULT_HTML_INPUT_ENCODING = "Windows-1251";
	
	public static List<Section> parseHhc(InputStream hhcFile) throws IOException, ParserConfigurationException,	XPathExpressionException {
		HtmlCleaner htmlCleaner = new HtmlCleaner();
		CleanerProperties props = htmlCleaner.getProperties();
		TagNode node = htmlCleaner.clean(hhcFile);
		Document hhcDocument = new DomSerializer(props).createDOM(node);
		XPath xpath = XPathFactory.newInstance().newXPath();
		Node ulNode = (Node) xpath.evaluate("body/ul", hhcDocument
				.getDocumentElement(), XPathConstants.NODE);
		List<Section> sections = processUlNode(ulNode);
		return sections;
	}
	
	/*
	 * Sometimes the structure is:
	 * <li> <!-- parent element -->
	 * 	<object> ... </object>
	 *  <ul> ... </ul> <!-- child elements -->
	 * </li>
	 * 
	 * And sometimes:
	 * <li> <!-- parent element -->
	 * 	<object> ... </object>
	 * </li>
	 * <ul> ... </ul> <!-- child elements -->
	 */
	private static List<Section> processUlNode(Node ulNode) {
		List<Section> result = new ArrayList<Section>();
		NodeList children = ulNode.getChildNodes();
		for(int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if(node.getNodeName().equals("li")) {
				List<Section> section = processLiNode(node);
				result.addAll(section);
			} else if(node.getNodeName().equals("ul")) {
				List<Section> childSections = processUlNode(node);
				if(result.isEmpty()) {
					result = childSections;
				} else {
					result.get(result.size() - 1).getChildren().addAll(childSections);
				}
			}
		}
		return result;
	}

	
	private static List<Section> processLiNode(Node liNode) {
		List<Section> result = new ArrayList<Section>();
		NodeList children = liNode.getChildNodes();
		for(int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if(node.getNodeName().equals("object")) {
				Section section = processObjectNode(node);
				if(section != null) {
					result.add(section);
				}
			} else if(node.getNodeName().equals("ul")) {
				List<Section> childSections = processUlNode(node);
				if(result.isEmpty()) {
					result = childSections;
				} else {
					result.get(result.size() - 1).getChildren().addAll(childSections);
				}
			}
		}
		return result;
	}

	
	/**
	 * Processes a CHM object node into a Section
	 * If the local name is empty then a Section node is made with a null href value.
	 * 
	 * <object type="text/sitemap">
	 * 		<param name="Name" value="My favorite section" />
	 * 		<param name="Local" value="section123.html" />
	 *		<param name="ImageNumber" value="2" />
	 * </object>
	 * 
	 * @param objectNode
	 * 
	 * @return A Section of the object has a non-blank param child with name 'Name' and a non-blank param name 'Local'
	 */
	private static Section processObjectNode(Node objectNode) {
		Section result = null;
		NodeList children = objectNode.getChildNodes();
		String name = null;
		String href = null;
		for(int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if(node.getNodeName().equals("param")) {
				String paramName = ((Element) node).getAttribute("name");
				if("Name".equals(paramName)) {
					name = ((Element) node).getAttribute("value");
				} else if("Local".equals(paramName)) {
					href = ((Element) node).getAttribute("value");
				}
			}
		}
		if((! StringUtils.isBlank(href)) && href.startsWith("http://")) {
			return result;
		}
		if(! StringUtils.isBlank(name)) {
			result = new Section(name, href);
		}
		return result;
	}
}
