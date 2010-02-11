package nl.siegmann.epublib.hhc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.FileResource;
import nl.siegmann.epublib.Resource;
import nl.siegmann.epublib.SectionResource;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Section;

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

	public static final String DEFAULT_HTML_INPUT_ENCODING = "Windows-1251";
//	private String htmlInputEncoding = DEFAULT_HTML_ENCODING;
	
	private static class ItemIdGenerator {
		private int itemCounter = 1;
		
		public String getNextItemId() {
			return "item_" + itemCounter++;
		}
	}
	
	public static Book parseHhc(File chmRootDir)
			throws IOException, ParserConfigurationException,
			XPathExpressionException {
		Book result = new Book();
		result.setTitle(findTitle(chmRootDir));
		HtmlCleaner htmlCleaner = new HtmlCleaner();
		CleanerProperties props = htmlCleaner.getProperties();
		File hhcFile = findHhcFile(chmRootDir);
		if(hhcFile == null) {
			throw new IllegalArgumentException("No index file found in directory " + chmRootDir.getAbsolutePath() + ". (Looked for file ending with extension '.hhc'");
		}
		TagNode node = htmlCleaner.clean(hhcFile);
		Document hhcDocument = new DomSerializer(props).createDOM(node);
		XPath xpath = XPathFactory.newInstance().newXPath();
		Node ulNode = (Node) xpath.evaluate("body/ul", hhcDocument
				.getDocumentElement(), XPathConstants.NODE);
		ItemIdGenerator itemIdGenerator = new ItemIdGenerator();
		Map<String, Resource> resources = findResources(itemIdGenerator, chmRootDir);
		List<Section> sections = processUlNode(ulNode);
		matchSectionsAndResources(itemIdGenerator, sections, resources);
		result.setSections(sections);
		result.setResources(resources.values());
		return result;
	}
	
	/**
	 * For every section in the list of sections it finds a resource with a matching href or it creates a new SectionResource and adds it to the sections.
	 * 
	 * @param sectionIdGenerator
	 * @param sections
	 * @param resources
	 */
	private static void matchSectionsAndResources(ItemIdGenerator sectionIdGenerator, List<Section> sections,
			Map<String, Resource> resources) {
		for(Section section: sections) {
			Resource resource = resources.get(section.getHref());
			if(resource == null) {
				resource = createNewSectionResource(sectionIdGenerator, section, resources);
				resources.put(resource.getHref(), resource);
			}
			section.setItemId(resource.getId());
			section.setHref(resource.getHref());
			matchSectionsAndResources(sectionIdGenerator, section.getChildren(), resources);
		}
	}

	private static Resource createNewSectionResource(ItemIdGenerator itemIdGenerator, Section section, Map<String, Resource> resources) {
		String href = calculateSectionResourceHref(section, resources);
		SectionResource result = new SectionResource(itemIdGenerator.getNextItemId(), section.getName(), href);
		return result;
	}
	
	
	private static String calculateSectionResourceHref(Section section,
			Map<String, Resource> resources) {
		String result = section.getName() + ".html";
		if(! resources.containsKey(section.getHref())) {
			return result;
		}
		int i = 1;
		String href = "section_" + i + ".html";
		while(! resources.containsKey(section.getHref())) {
			href = "section_" + (i++) + ".html";
		}
		return href;
	}

	/**
	 * Finds in the '#SYSTEM' file the 3rd set of characters that have ascii value >= 32 and <= 126 and is more than 3 characters long.
	 * 
	 * @param chmRootDir
	 * @return
	 * @throws IOException
	 */
	protected static String findTitle(File chmRootDir) throws IOException {
		File systemFile = new File(chmRootDir.getAbsolutePath() + File.separatorChar + "#SYSTEM");
		InputStream in = new FileInputStream(systemFile);
		boolean inText = false;
		int lineCounter = 0;
		StringBuilder line = new StringBuilder();
		for(int c = in.read(); c >= 0; c = in.read()) {
			if(c >= 32 && c <= 126) {
				line.append((char) c);
				inText = true;
			} else {
				if(inText) {
					if(line.length() >= 3) {
						lineCounter++;
						if(lineCounter >= 3) {
							return line.toString();
						}
					}
					line = new StringBuilder();
				}
				inText = false;
			}
		}
		return "<unknown title>";
	}
	
	private static File findHhcFile(File chmRootDir) {
		File[] files = chmRootDir.listFiles();
		for(int i = 0; i < files.length; i++) {
			if(StringUtils.endsWithIgnoreCase(files[i].getName(), ".hhc")) {
				return files[i];
			}
		}
		return null;
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


	@SuppressWarnings("unchecked")
	private static Map<String, Resource> findResources(ItemIdGenerator itemIdGenerator, File rootDir) throws IOException {
		Map<String, Resource> result = new LinkedHashMap<String, Resource>();
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
			FileResource fileResource = new FileResource(itemIdGenerator.getNextItemId(), file, href, mediaType);
			if(mediaType.equals(Constants.MediaTypes.xhtml)) {
				fileResource.setInputEncoding(DEFAULT_HTML_INPUT_ENCODING);
			}
			result.put(fileResource.getHref(), fileResource);
		}
		return result;
	}

	/**
	 * Determines the files mediatype based on its file extension.
	 * 
	 * @param filename
	 * @return
	 */
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
