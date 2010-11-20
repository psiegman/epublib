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

import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Resources;
import nl.siegmann.epublib.domain.SectionResource;
import nl.siegmann.epublib.domain.TOCReference;

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
	
	public static List<TOCReference> parseHhc(InputStream hhcFile, Resources resources) throws IOException, ParserConfigurationException,	XPathExpressionException {
		HtmlCleaner htmlCleaner = new HtmlCleaner();
		CleanerProperties props = htmlCleaner.getProperties();
		TagNode node = htmlCleaner.clean(hhcFile);
		Document hhcDocument = new DomSerializer(props).createDOM(node);
		XPath xpath = XPathFactory.newInstance().newXPath();
		Node ulNode = (Node) xpath.evaluate("body/ul", hhcDocument
				.getDocumentElement(), XPathConstants.NODE);
		List<TOCReference> sections = processUlNode(ulNode, resources);
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
	private static List<TOCReference> processUlNode(Node ulNode, Resources resources) {
		List<TOCReference> result = new ArrayList<TOCReference>();
		NodeList children = ulNode.getChildNodes();
		for(int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if(node.getNodeName().equals("li")) {
				List<TOCReference> section = processLiNode(node, resources);
				result.addAll(section);
			} else if(node.getNodeName().equals("ul")) {
				List<TOCReference> childTOCReferences = processUlNode(node, resources);
				if(result.isEmpty()) {
					result = childTOCReferences;
				} else {
					result.get(result.size() - 1).getChildren().addAll(childTOCReferences);
				}
			}
		}
		return result;
	}

	
	private static List<TOCReference> processLiNode(Node liNode, Resources resources) {
		List<TOCReference> result = new ArrayList<TOCReference>();
		NodeList children = liNode.getChildNodes();
		for(int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if(node.getNodeName().equals("object")) {
				TOCReference section = processObjectNode(node, resources);
				if(section != null) {
					result.add(section);
				}
			} else if(node.getNodeName().equals("ul")) {
				List<TOCReference> childTOCReferences = processUlNode(node, resources);
				if(result.isEmpty()) {
					result = childTOCReferences;
				} else {
					result.get(result.size() - 1).getChildren().addAll(childTOCReferences);
				}
			}
		}
		return result;
	}

	
	/**
	 * Processes a CHM object node into a TOCReference
	 * If the local name is empty then a TOCReference node is made with a null href value.
	 * 
	 * <object type="text/sitemap">
	 * 		<param name="Name" value="My favorite section" />
	 * 		<param name="Local" value="section123.html" />
	 *		<param name="ImageNumber" value="2" />
	 * </object>
	 * 
	 * @param objectNode
	 * 
	 * @return A TOCReference of the object has a non-blank param child with name 'Name' and a non-blank param name 'Local'
	 */
	private static TOCReference processObjectNode(Node objectNode, Resources resources) {
		TOCReference result = null;
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
			Resource resource = resources.getByHref(href);
			if (resource == null) {
				resource = new SectionResource(null, name, href);
				resources.add(resource);
			}
			result = new TOCReference(name, resource);
		}
		return result;
	}
}
