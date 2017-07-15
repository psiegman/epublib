package nl.siegmann.epublib.epub;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.service.MediatypeService;
import nl.siegmann.epublib.util.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.*;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Generates an epub file. Not thread-safe, single use object.
 * 
 * @author paul
 *
 */
public class EpubWriter3 extends EpubWriter {

	public EpubWriter3() {
		super();
	}

	public EpubWriter3(BookProcessor bookProcessor) {
		super(bookProcessor);
	}

	@Override
	protected void writePackageDocument(Book book, ZipOutputStream resultStream) throws IOException {
		resultStream.putNextEntry(new ZipEntry("OEBPS/content.opf"));
		XmlSerializer xmlSerializer = EpubProcessorSupport.createXmlSerializer(resultStream);
		PackageDocumentWriter3.write(this, xmlSerializer, book);
		xmlSerializer.flush();
	}
	
}