package nl.siegmann.epublib.epub;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathFactory;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.ZipEntryResource;
import nl.siegmann.epublib.service.MediatypeService;
import nl.siegmann.epublib.util.ResourceUtil;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Reads an epub file.
 * 
 * @author paul
 *
 */
public class EpubReader extends EpubProcessor {

	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(EpubReader.class);
	private XPathFactory xpathFactory;
	
	public EpubReader() {
		this.xpathFactory = XPathFactory.newInstance();
	}
	
	public Book readEpub(InputStream in) throws IOException {
		return readEpub(new ZipInputStream(in));
	}
	
	public Book readEpub(ZipInputStream in) throws IOException {
		Book result = new Book();
		Map<String, Resource> resources = readResources(in, Constants.ENCODING);
		handleMimeType(result, resources);
		String packageResourceHref = getPackageResourceHref(result, resources);
		Resource packageResource = processPackageResource(packageResourceHref, result, resources);
		processNcxResource(packageResource, result);
		return result;
	}

	private void processNcxResource(Resource packageResource, Book book) {
		NCXDocument.read(book, this);
	}

	private Resource processPackageResource(String packageResourceHref, Book book, Map<String, Resource> resources) {
		Resource packageResource = resources.remove(packageResourceHref);
		try {
			PackageDocumentReader.read(packageResource, this, book, resources);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return packageResource;
	}

	private String getPackageResourceHref(Book book, Map<String, Resource> resources) {
		String defaultResult = "OEBPS/content.opf";
		String result = defaultResult;

		Resource containerResource = resources.remove("META-INF/container.xml");
		if(containerResource == null) {
			return result;
		}
		try {
			Document document = ResourceUtil.getAsDocument(containerResource, createDocumentBuilder());
			Element rootFileElement = (Element) ((Element) document.getDocumentElement().getElementsByTagName("rootfiles").item(0)).getElementsByTagName("rootfile").item(0);
			result = rootFileElement.getAttribute("full-path");
		} catch (Exception e) {
			log.error(e);
		}
		if(StringUtils.isBlank(result)) {
			result = defaultResult;
		}
		return result;
	}

	private void handleMimeType(Book result, Map<String, Resource> resources) {
		resources.remove("mimetype");
	}

	private Map<String, Resource> readResources(ZipInputStream in, Charset defaultHtmlEncoding) throws IOException {
		Map<String, Resource> result = new HashMap<String, Resource>();
		for(ZipEntry zipEntry = in.getNextEntry(); zipEntry != null; zipEntry = in.getNextEntry()) {
//			System.out.println(zipEntry.getName());
			if(zipEntry.isDirectory()) {
				continue;
			}
			Resource resource = new ZipEntryResource(zipEntry, in);
			if(resource.getMediaType() == MediatypeService.XHTML
					&& resource.getInputEncoding() == null) {
				resource.setInputEncoding(defaultHtmlEncoding);
			}
			result.put(resource.getHref(), resource);
		}
		return result;
	}


	public XPathFactory getXpathFactory() {
		return xpathFactory;
	}
}
