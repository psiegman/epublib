package nl.siegmann.epublib.epub;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.bookprocessor.BookProcessor;
import nl.siegmann.epublib.bookprocessor.CoverpageBookProcessor;
import nl.siegmann.epublib.bookprocessor.FixIdentifierBookProcessor;
import nl.siegmann.epublib.bookprocessor.HtmlCleanerBookProcessor;
import nl.siegmann.epublib.bookprocessor.SectionHrefSanityCheckBookProcessor;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.service.MediatypeService;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;

/**
 * Generates an epub file. Not thread-safe, single use object.
 * 
 * @author paul
 *
 */
public class EpubWriter extends EpubProcessor {
	
	private final static Logger log = LoggerFactory.getLogger(EpubWriter.class); 
	
	private HtmlProcessor htmlProcessor;
	private List<BookProcessor> bookProcessingPipeline;
	private MediatypeService mediatypeService = new MediatypeService();
	private XMLOutputFactory xmlOutputFactory;

	public EpubWriter() {
		this(createDefaultBookProcessingPipeline());
	}
	
	public EpubWriter(List<BookProcessor> bookProcessingPipeline) {
		this.bookProcessingPipeline = bookProcessingPipeline;
		this.xmlOutputFactory = createXMLOutputFactory();
	}
	
	private static XMLOutputFactory createXMLOutputFactory() {
		XMLOutputFactory result = XMLOutputFactory.newInstance();
//		result.setProperty(name, value)
		return result;
	}
	
	
	private static List<BookProcessor> createDefaultBookProcessingPipeline() {
		List<BookProcessor> result = new ArrayList<BookProcessor>();
		result.addAll(Arrays.asList(new BookProcessor[] {
			new SectionHrefSanityCheckBookProcessor(),
			new HtmlCleanerBookProcessor(),
			new CoverpageBookProcessor(),
			new FixIdentifierBookProcessor()
		}));
		return result;
	}
	
	
	public void write(Book book, OutputStream out) throws IOException, XMLStreamException, FactoryConfigurationError {
		book = processBook(book);
		ZipOutputStream resultStream = new ZipOutputStream(out);
		writeMimeType(resultStream);
		writeContainer(resultStream);
		initTOCResource(book);
		writeResources(book, resultStream);
		writePackageDocument(book, resultStream);
		resultStream.close();
	}

	private void initTOCResource(Book book) throws XMLStreamException, FactoryConfigurationError {
		Resource tocResource = NCXDocument.createNCXResource(this, book);
		Resource currentTocResource = book.getSpine().getTocResource();
		if (currentTocResource != null) {
			book.getResources().remove(currentTocResource.getHref());
		}
		book.getSpine().setTocResource(tocResource);
		book.getResources().add(tocResource);
	}
	
	private Book processBook(Book book) {
		for(BookProcessor bookProcessor: bookProcessingPipeline) {
			book = bookProcessor.processBook(book, this);
		}
		return book;
	}


	private void writeResources(Book book, ZipOutputStream resultStream) throws IOException {
		for(Resource resource: book.getResources().getAll()) {
			writeResource(resource, resultStream);
		}
	}

	/**
	 * Writes the resource to the resultStream.
	 * 
	 * @param resource
	 * @param resultStream
	 * @throws IOException
	 */
	private void writeResource(Resource resource, ZipOutputStream resultStream)
			throws IOException {
		if(resource == null) {
			return;
		}
		try {
			resultStream.putNextEntry(new ZipEntry("OEBPS/" + resource.getHref()));
			InputStream inputStream = resource.getInputStream();
			IOUtils.copy(inputStream, resultStream);
			inputStream.close();
		} catch(Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	

	private void writePackageDocument(Book book, ZipOutputStream resultStream) throws XMLStreamException, IOException {
		resultStream.putNextEntry(new ZipEntry("OEBPS/content.opf"));
		XMLStreamWriter xmlStreamWriter = createXMLStreamWriter(resultStream);
		PackageDocumentWriter.write(this, xmlStreamWriter, book);
		xmlStreamWriter.flush();
	}

	/**
	 * Writes the META-INF/container.xml file.
	 * 
	 * @param resultStream
	 * @throws IOException
	 */
	private void writeContainer(ZipOutputStream resultStream) throws IOException {
		resultStream.putNextEntry(new ZipEntry("META-INF/container.xml"));
		Writer out = new OutputStreamWriter(resultStream);
		out.write("<?xml version=\"1.0\"?>\n");
		out.write("<container version=\"1.0\" xmlns=\"urn:oasis:names:tc:opendocument:xmlns:container\">\n");
		out.write("\t<rootfiles>\n");
		out.write("\t\t<rootfile full-path=\"OEBPS/content.opf\" media-type=\"application/oebps-package+xml\"/>\n");
		out.write("\t</rootfiles>\n");
		out.write("</container>");
		out.flush();
	}

	/**
	 * Stores the mimetype as an uncompressed file in the ZipOutputStream.
	 * 
	 * @param resultStream
	 * @throws IOException
	 */
	private void writeMimeType(ZipOutputStream resultStream) throws IOException {
		ZipEntry mimetypeZipEntry = new ZipEntry("mimetype");
		mimetypeZipEntry.setMethod(ZipEntry.STORED);
		byte[] mimetypeBytes = MediatypeService.EPUB.getName().getBytes();
		mimetypeZipEntry.setSize(mimetypeBytes.length);
		mimetypeZipEntry.setCrc(calculateCrc(mimetypeBytes));
		resultStream.putNextEntry(mimetypeZipEntry);
		resultStream.write(mimetypeBytes);
	}

	private long calculateCrc(byte[] data) {
		CRC32 crc = new CRC32();
		crc.update(data);
		return crc.getValue();
	}
	
	XMLEventFactory createXMLEventFactory() {
		return XMLEventFactory.newInstance();
	}
	
	XMLStreamWriter createXMLStreamWriter(OutputStream out) throws XMLStreamException {
		return xmlOutputFactory.createXMLStreamWriter(out, Constants.ENCODING.name());
	}

	String getNcxId() {
		return "ncx";
	}
	
	String getNcxHref() {
		return "toc.ncx";
	}

	String getNcxMediaType() {
		return "application/x-dtbncx+xml";
	}

	public HtmlProcessor getHtmlProcessor() {
		return htmlProcessor;
	}


	public void setHtmlProcessor(HtmlProcessor htmlProcessor) {
		this.htmlProcessor = htmlProcessor;
	}


	public List<BookProcessor> getBookProcessingPipeline() {
		return bookProcessingPipeline;
	}


	public void setBookProcessingPipeline(List<BookProcessor> bookProcessingPipeline) {
		this.bookProcessingPipeline = bookProcessingPipeline;
	}


	public MediatypeService getMediatypeService() {
		return mediatypeService;
	}
}
