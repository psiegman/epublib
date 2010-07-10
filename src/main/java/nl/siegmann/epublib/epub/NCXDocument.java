package nl.siegmann.epublib.epub;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.ByteArrayResource;
import nl.siegmann.epublib.domain.Identifier;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Section;
import nl.siegmann.epublib.service.MediatypeService;
import nl.siegmann.epublib.util.CollectionUtil;
import nl.siegmann.epublib.util.ResourceUtil;
import nl.siegmann.epublib.utilities.IndentingXMLStreamWriter;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Writes the ncx document as defined by namespace http://www.daisy.org/z3986/2005/ncx/
 * 
 * @author paul
 *
 */
public class NCXDocument {
	
	public static final String NAMESPACE_NCX = "http://www.daisy.org/z3986/2005/ncx/";
	public static final String PREFIX_NCX = "ncx";
	public static final String NCX_ITEM_ID = "ncx";
	public static final String NCX_HREF = "toc.ncx";
	
	private static Logger log = Logger.getLogger(NCXDocument.class);

	private interface NCXTags {
		String meta = "meta";
	}

	// package
	@SuppressWarnings("serial")
	static final NamespaceContext NCX_DOC_NAMESPACE_CONTEXT = new NamespaceContext() {

		private final Map<String, List<String>> prefixes = new HashMap<String, List<String>>();
		
		{
			prefixes.put(NAMESPACE_NCX, new ArrayList<String>() {{ add(PREFIX_NCX);}});
		}
		
		@Override
		public String getNamespaceURI(String prefix) {
			if(PREFIX_NCX.equals(prefix)) {
				return NAMESPACE_NCX;
			}
			return null;
		}

		@Override
		public String getPrefix(String namespace) {
			if(NAMESPACE_NCX.equals(namespace)) {
				return PREFIX_NCX;
			}
			return null;
		}

		@Override
		public Iterator<String> getPrefixes(String namespace) {
			List<String> prefixList = prefixes.get(namespace);
			if(prefixList == null) {
				return Collections.<String>emptyList().iterator();
			}
			return prefixList.iterator();
		}
		
	};
	
	
	public static void read(Book book, EpubReader epubReader) {
		if(book.getNcxResource() == null) {
			return;
		}
		try {
			Resource ncxResource = book.getNcxResource();
			if(ncxResource == null) {
				return;
			}
			Document ncxDocument = ResourceUtil.getAsDocument(ncxResource, epubReader.createDocumentBuilder());
			XPath xPath = epubReader.getXpathFactory().newXPath();
			xPath.setNamespaceContext(NCX_DOC_NAMESPACE_CONTEXT);
		    NodeList navmapNodes = (NodeList) xPath.evaluate(PREFIX_NCX + ":ncx/" + PREFIX_NCX + ":navMap/" + PREFIX_NCX + ":navPoint", ncxDocument, XPathConstants.NODESET);
			List<Section> sections = readSections(navmapNodes, xPath);
			book.setTocSections(sections);
		} catch (Exception e) {
			log.error(e);
		}
	}

	private static List<Section> readSections(NodeList navpoints, XPath xPath) throws XPathExpressionException {
		if(navpoints == null) {
			return new ArrayList<Section>();
		}
		List<Section> result = new ArrayList<Section>(navpoints.getLength());
		for(int i = 0; i < navpoints.getLength(); i++) {
			Section childSection = readSection((Element) navpoints.item(i), xPath);
			result.add(childSection);
		}
		return result;
	}

	private static Section readSection(Element navpointElement, XPath xPath) throws XPathExpressionException {
		String name = xPath.evaluate(PREFIX_NCX + ":navLabel/" + PREFIX_NCX + ":text", navpointElement);
		String href = xPath.evaluate(PREFIX_NCX + ":content/@src", navpointElement);
		Section result = new Section(name, href);
		NodeList childNavpoints = (NodeList) xPath.evaluate("" + PREFIX_NCX + ":navPoint", navpointElement, XPathConstants.NODESET);
		result.setChildren(readSections(childNavpoints, xPath));
		return result;
	}

	public static void write(EpubWriter epubWriter, Book book, ZipOutputStream resultStream) throws IOException, XMLStreamException, FactoryConfigurationError {
		resultStream.putNextEntry(new ZipEntry("OEBPS/toc.ncx"));
		XMLStreamWriter out = epubWriter.createXMLStreamWriter(resultStream);
		write(out, book);
		out.flush();
	}
	

	public static Resource createNCXResource(EpubWriter epubWriter, Book book) throws XMLStreamException, FactoryConfigurationError {
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		XMLStreamWriter out = epubWriter.createXMLStreamWriter(data);
		write(out, book);
		Resource resource = new ByteArrayResource(NCX_ITEM_ID, data.toByteArray(), NCX_HREF, MediatypeService.NCX);
		return resource;
	}
	
	
	public static void write(XMLStreamWriter writer, Book book) throws XMLStreamException {
		writer = new IndentingXMLStreamWriter(writer);
		writer.writeStartDocument(Constants.ENCODING.name(), "1.0");
		writer.setDefaultNamespace(NAMESPACE_NCX);
		writer.writeStartElement(NAMESPACE_NCX, "ncx");
//		writer.writeNamespace("ncx", NAMESPACE_NCX);
		writer.writeAttribute("xmlns", NAMESPACE_NCX);
		writer.writeAttribute("version", "2005-1");
		writer.writeStartElement(NAMESPACE_NCX, "head");

		for(Identifier identifier: book.getMetadata().getIdentifiers()) {
			writer.writeEmptyElement(NAMESPACE_NCX, NCXTags.meta);
			writer.writeAttribute("name", "dtb:" + identifier.getScheme());
			writer.writeAttribute("content", identifier.getValue());
		}
		
		writer.writeEmptyElement(NAMESPACE_NCX, NCXTags.meta);
		writer.writeAttribute("name", "dtb:generator");
		writer.writeAttribute("content", Constants.EPUBLIB_GENERATOR_NAME);

		writer.writeEmptyElement(NAMESPACE_NCX, NCXTags.meta);
		writer.writeAttribute("name", "dtb:depth");
		writer.writeAttribute("content", "1");

		writer.writeEmptyElement(NAMESPACE_NCX, NCXTags.meta);
		writer.writeAttribute("name", "dtb:totalPageCount");
		writer.writeAttribute("content", "0");

		writer.writeEmptyElement(NAMESPACE_NCX, NCXTags.meta);
		writer.writeAttribute("name", "dtb:maxPageNumber");
		writer.writeAttribute("content", "0");

		writer.writeEndElement();
		
		writer.writeStartElement(NAMESPACE_NCX, "docTitle");
		writer.writeStartElement(NAMESPACE_NCX, "text");
		// write the first title
		writer.writeCharacters(StringUtils.defaultString(CollectionUtil.first(book.getMetadata().getTitles())));
		writer.writeEndElement(); // text
		writer.writeEndElement(); // docTitle
		
		for(Author author: book.getMetadata().getAuthors()) {
			writer.writeStartElement(NAMESPACE_NCX, "docAuthor");
			writer.writeStartElement(NAMESPACE_NCX, "text");
			writer.writeCharacters(author.getLastname() + ", " + author.getFirstname());
			writer.writeEndElement();
			writer.writeEndElement();
		}
		
		writer.writeStartElement(NAMESPACE_NCX, "navMap");
		writeNavPoints(book.getTocSections(), 1, writer);
		writer.writeEndElement();
		writer.writeEndElement();
		writer.writeEndDocument();
	}


	private static int writeNavPoints(List<Section> sections, int playOrder,
			XMLStreamWriter writer) throws XMLStreamException {
		for(Section section: sections) {
			writeNavPointStart(section, playOrder, writer);
			playOrder++;
			if(! section.getChildren().isEmpty()) {
				playOrder = writeNavPoints(section.getChildren(), playOrder, writer);
			}
			writeNavPointEnd(section, writer);
		}
		return playOrder;
	}


	private static void writeNavPointStart(Section section, int playOrder, XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartElement(NAMESPACE_NCX, "navPoint");
		writer.writeAttribute("id", "navPoint-" + playOrder);
		writer.writeAttribute("playOrder", String.valueOf(playOrder));
		writer.writeAttribute("class", "chapter");
		writer.writeStartElement(NAMESPACE_NCX, "navLabel");
		writer.writeStartElement(NAMESPACE_NCX, "text");
		writer.writeCharacters(section.getName());
		writer.writeEndElement(); // text
		writer.writeEndElement(); // navLabel
		writer.writeEmptyElement(NAMESPACE_NCX, "content");
		writer.writeAttribute("src", section.getHref());
	}

	private static void writeNavPointEnd(Section section, XMLStreamWriter writer) throws XMLStreamException {
		writer.writeEndElement(); // navPoint
	}


}
