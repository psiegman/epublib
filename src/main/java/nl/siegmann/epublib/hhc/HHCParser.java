package nl.siegmann.epublib.hhc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import nl.siegmann.epublib.Book;
import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.FileResource;
import nl.siegmann.epublib.Resource;
import nl.siegmann.epublib.Section;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.StringUtils;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class HHCParser {

	public static Book parseHhc(File hhcFile, File chmRootDir)
			throws IOException, ParserConfigurationException,
			XPathExpressionException {
		Book result = new Book();
		result.setTitle("test book");
		HtmlCleaner htmlCleaner = new HtmlCleaner();
		CleanerProperties props = htmlCleaner.getProperties();
		TagNode node = htmlCleaner.clean(hhcFile);
		Document hhcDocument = new DomSerializer(props).createDOM(node);
//		PrettyXmlSerializer prettyPrinter = new PrettyXmlSerializer(props);
//		System.out.println(prettyPrinter.getXmlAsString(node));
		XPath xpath = XPathFactory.newInstance().newXPath();
		Node ulNode = (Node) xpath.evaluate("body/ul", hhcDocument
				.getDocumentElement(), XPathConstants.NODE);
		result.setSections(processUlNode(ulNode)); // processUlNode(xpath, chmRootDir, ulNode));
		result.setResources(findResources(chmRootDir));
		return result;
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
		if((! (StringUtils.isBlank(name)) && (! StringUtils.isBlank(href)))) {
			result = new Section(href, name, href);
		}
		return result;
	}


	@SuppressWarnings("unchecked")
	private static List<Resource> findResources(File rootDir) throws IOException {
		List<Resource> result = new ArrayList<Resource>();
		Iterator<File> fileIter = FileUtils.iterateFiles(rootDir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
		while(fileIter.hasNext()) {
			File file = fileIter.next();
//			System.out.println("file:" + file);
			if(file.isDirectory()) {
				continue;
			}
			String mediaType = determineMediaType(file.getName());
			if(StringUtils.isBlank(mediaType)) {
				continue;
			}
			String href = file.getCanonicalPath().substring(rootDir.getCanonicalPath().length() + 1);
			result.add(new FileResource(file, href, mediaType));
		}
		return result;
	}

	private static String determineMediaType(String filename) {
		String result = "";
		filename = filename.toLowerCase();
		if (filename.endsWith(".html") || filename.endsWith(".htm")) {
			result = Constants.MediaTypes.xhtml;
		} else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
			result = "image/jpeg";
		} else if (filename.endsWith(".png")) {
			result = "image/png";
		} else if (filename.endsWith(".gif")) {
			result = "image/gif";
		} else if (filename.endsWith(".css")) {
			result = "text/css";
		}
		return result;

	}
}
