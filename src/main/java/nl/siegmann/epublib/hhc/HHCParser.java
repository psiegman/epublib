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
import org.htmlcleaner.PrettyXmlSerializer;
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
		PrettyXmlSerializer prettyPrinter = new PrettyXmlSerializer(props);
		System.out.println(prettyPrinter.getXmlAsString(node));
		XPath xpath = XPathFactory.newInstance().newXPath();
		Node ulNode = (Node) xpath.evaluate("body/ul", hhcDocument
				.getDocumentElement(), XPathConstants.NODE);
		result.setSections(processUlNode(xpath, chmRootDir, ulNode));
		System.out.println("hi");
		result.setResources(findResources(chmRootDir));
		return result;
	}
	
	private static List<Resource> findResources(File rootDir) throws IOException {
		List<Resource> result = new ArrayList<Resource>();
		Iterator fileIter = FileUtils.iterateFiles(rootDir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
		while(fileIter.hasNext()) {
			File file = (File) fileIter.next();
			System.out.println("file:" + file);
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

	private static List<Section> processUlNode(XPath xpath, File rootDir, Node ulNode) throws XPathExpressionException {
		List<Section> result = new ArrayList<Section>();
		NodeList liNodes = (NodeList) xpath.evaluate("li", ulNode,
				XPathConstants.NODESET);
		for (int i = 0; i < liNodes.getLength(); i++) {
			Node objectNode = (Node) xpath.evaluate("object", liNodes.item(i),
					XPathConstants.NODE);
			String name = ((Element) xpath.evaluate("param[@name = 'Name']",
					objectNode, XPathConstants.NODE)).getAttribute("value");
			String href = ((Element) xpath.evaluate("param[@name = 'Local']",
					objectNode, XPathConstants.NODE)).getAttribute("value");
			// System.out.println(HHCParser.class.getName() + " node:" +
			// nodes.item(i).getNodeName());
			Section section = new Section(href, name, href);
			result.add(section);
			Node childUlNode = (Node) xpath.evaluate("ul", liNodes.item(i), XPathConstants.NODE);
			if (childUlNode != null) {
				section.setChildren(processUlNode(xpath, rootDir, childUlNode));
			}
			System.out.println(HHCParser.class.getName() + " name:" + name
					+ ", href:" + href);
		}
		System.out.println("done with sections");
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
