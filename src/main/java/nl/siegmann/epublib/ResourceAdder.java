package nl.siegmann.epublib;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;

import org.ccil.cowan.tagsoup.Parser;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class ResourceAdder {

	private File sourceDir;
	private Collection<Resource> result = new ArrayList<Resource>();
	private SAXTransformerFactory stf = (SAXTransformerFactory) TransformerFactory
			.newInstance();

	private DomCleaner domCleaner;
	
	public ResourceAdder(File sourceDir) {
		this.sourceDir = sourceDir;
	}

	public interface DomCleaner {
		public Document cleanupDocument(Document document);
	}
	
	public static class Resource {

	}

	public void addResources() {
		listFiles(sourceDir);
	}

	public void listFiles(File currentDir) {
		File[] directoryEntries = currentDir.listFiles();
		for (int i = 0; i < directoryEntries.length; i++) {
			File entry = directoryEntries[i];
			if (entry.isDirectory()) {
				listFiles(entry);
			} else if (entry.isFile()) {
				Resource resource = createResource(entry);
				result.add(resource);
			}
		}
	}

	Resource createResource(File file) {

		return new Resource();
	}

	/**
	 * @param urlString
	 *            The URL of the page to retrieve
	 * @return A Node with a well formed XML doc coerced from the page.
	 * @throws Exception
	 *             if something goes wrong. No error handling at all for
	 *             brevity.
	 */
	public Document getHtmlUrlNode(File htmlFile) throws Exception {

		TransformerHandler transformerHandler = stf.newTransformerHandler();

		// This dom result will contain the results of the transformation
		DOMResult domResult = new DOMResult();
		transformerHandler.setResult(domResult);

		Parser tagsoupParser = new Parser();
		tagsoupParser.setContentHandler(transformerHandler);

		// This is where the magic happens to convert HTML to XML
		tagsoupParser.parse(new InputSource(new FileInputStream(htmlFile)));
		return domResult.getNode().getOwnerDocument();
	}
}
