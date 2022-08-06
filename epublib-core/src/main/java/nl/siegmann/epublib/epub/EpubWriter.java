package nl.siegmann.epublib.epub;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import nl.siegmann.epublib.domain.OpfResource;
import org.xmlpull.v1.XmlSerializer;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.service.MediatypeService;
import nl.siegmann.epublib.util.IOUtil;

/**
 * Generates an epub file. Not thread-safe, single use object.
 * 
 * @author paul
 *
 */
public class EpubWriter {
	
	private final static Logger log = Logger.getLogger(EpubWriter.class.getName()); 
	
	// package
	static final String EMPTY_NAMESPACE_PREFIX = "";
	
	private BookProcessor bookProcessor = BookProcessor.IDENTITY_BOOKPROCESSOR;

	public EpubWriter() {
		this(BookProcessor.IDENTITY_BOOKPROCESSOR);
	}
	
	
	public EpubWriter(BookProcessor bookProcessor) {
		this.bookProcessor = bookProcessor;
	}


	public void write(Book book, OutputStream out) throws IOException {
		book = processBook(book);
		ZipOutputStream resultStream = new ZipOutputStream(out);
		writeMimeType(resultStream);
		writeContainer(resultStream);
		if(
                null == book.getOpfResource()
                || OpfResource.DEFAULT_VERSION.equals(book.getOpfResource().getVersion())
                || !book.getTableOfContents().getTocReferences().isEmpty()
        ) {
            initTOCResource(book);
        }
		writeResources(book, resultStream);
		writePackageDocument(book, resultStream);
		resultStream.close();
	}

	private Book processBook(Book book) {
		if (bookProcessor != null) {
			book = bookProcessor.processBook(book);
		}
		return book;
	}

	private void initTOCResource(Book book) {
		Resource tocResource;
		try {
			tocResource = NCXDocument.createNCXResource(book);
			Resource currentTocResource = book.getSpine().getTocResource();
			if (currentTocResource != null) {
				book.getResources().remove(currentTocResource.getHref());
			}
			book.getSpine().setTocResource(tocResource);
			book.getResources().add(tocResource);
		} catch (Exception e) {
			log.severe("Error writing table of contents: " + e.getClass().getName() + ": " + e.getMessage());
		}
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
			IOUtil.copy(inputStream, resultStream);
			inputStream.close();
		} catch(Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	

	private void writePackageDocument(Book book, ZipOutputStream resultStream) throws IOException {
		resultStream.putNextEntry(new ZipEntry("OEBPS/content.opf"));
		XmlSerializer xmlSerializer = EpubProcessorSupport.createXmlSerializer(resultStream);
		PackageDocumentWriter.write(this, xmlSerializer, book);
		xmlSerializer.flush();
//		String resultAsString = result.toString();
//		resultStream.write(resultAsString.getBytes(Constants.ENCODING));
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

	String getNcxId() {
		return "ncx";
	}
	
	String getNcxHref() {
		return "toc.ncx";
	}

	String getNcxMediaType() {
		return MediatypeService.NCX.getName();
	}

	public BookProcessor getBookProcessor() {
		return bookProcessor;
	}
	
	
	public void setBookProcessor(BookProcessor bookProcessor) {
		this.bookProcessor = bookProcessor;
	}
	
}
