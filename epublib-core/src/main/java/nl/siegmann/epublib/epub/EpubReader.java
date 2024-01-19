package nl.siegmann.epublib.epub;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jazzlib.ZipFile;
import net.sf.jazzlib.ZipInputStream;
import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.domain.*;
import nl.siegmann.epublib.service.MediatypeService;
import nl.siegmann.epublib.util.ResourceUtil;
import nl.siegmann.epublib.util.StringUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Reads an epub file.
 * 
 * @author paul
 *
 */
public class EpubReader {

	private static final Logger log = Logger.getLogger(EpubReader.class.getName());
	private final BookProcessor bookProcessor = BookProcessor.IDENTITY_BOOKPROCESSOR;
	
	public Book readEpub(InputStream in) throws IOException {
		return readEpub(in, Constants.CHARACTER_ENCODING);
	}	
	
	@SuppressWarnings("unused")
	public Book readEpub(ZipInputStream in) throws IOException {
		return readEpub(in, Constants.CHARACTER_ENCODING);
	}

    @SuppressWarnings("unused")
	public Book readEpub(ZipFile zipfile) throws IOException {
        return readEpub(zipfile, Constants.CHARACTER_ENCODING);
    }

	/**
	 * Read epub from inputstream
	 * 
	 * @param in the inputstream from which to read the epub
	 * @param encoding the encoding to use for the html files within the epub
	 * @return the Book as read from the inputstream
	 */
	public Book readEpub(InputStream in, String encoding) throws IOException {
		return readEpub(new ZipInputStream(in), encoding);
	}	
	
	

	/**
	 * Reads this EPUB without loading any resources into memory.
	 * 
	 * @param zipFile the file to load
	 * @param encoding the encoding for XHTML files
	 * 
	 * @return this Book without loading all resources into memory.
	 */
	@SuppressWarnings("unused")
	public Book readEpubLazy(ZipFile zipFile, String encoding ) throws IOException {
		return readEpubLazy(zipFile, encoding, Arrays.asList(MediatypeService.mediatypes) );
	}
	
	public Book readEpub(ZipInputStream in, String encoding) throws IOException {
        return readEpub(ResourcesLoader.loadResources(in, encoding));
	}

    public Book readEpub(ZipFile in, String encoding) throws IOException {
        return readEpub(ResourcesLoader.loadResources(in, encoding));
    }

    /**
	 * Reads this EPUB without loading all resources into memory.
	 * 
	 * @param zipFile the file to load
	 * @param encoding the encoding for XHTML files
	 * @param lazyLoadedTypes a list of the MediaType to load lazily
	 * @return this Book without loading all resources into memory.
	 */
	public Book readEpubLazy(ZipFile zipFile, String encoding, List<MediaType> lazyLoadedTypes ) throws IOException {
		Resources resources = ResourcesLoader.loadResources(zipFile, encoding, lazyLoadedTypes);
		return readEpub(resources);
	}
	
    public Book readEpub(Resources resources) throws IOException{
        return readEpub(resources, new Book());
    }
    
    public Book readEpub(Resources resources, Book result) throws IOException{
    	if (result == null) {
    		result = new Book();
    	}
    	handleMimeType(resources);
    	String packageResourceHref = getPackageResourceHref(resources);
    	OpfResource packageResource = processPackageResource(packageResourceHref, result, resources);
    	result.setOpfResource(packageResource);
    	Resource ncxResource = processNcxResource(result);
		if (ncxResource == null) {
			NavDocument.read(result);
		}
    	result.setNcxResource(ncxResource);
    	result = postProcessBook(result);
    	return result;
    }
    
    
	private Book postProcessBook(Book book) {
		if (bookProcessor != null) {
			book = bookProcessor.processBook(book);
		}
		return book;
	}

	private Resource processNcxResource(Book book) {
		return NCXDocument.read(book, this);
	}

	private OpfResource processPackageResource(String packageResourceHref, Book book, Resources resources) throws IOException {
		OpfResource packageResource = new OpfResource(
				resources.remove(packageResourceHref)
		);
		try {
			PackageDocumentReader.read(packageResource, book, resources);
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
		return packageResource;
	}

	private String getPackageResourceHref(Resources resources) {
		String defaultResult = "OEBPS/content.opf";
		String result = defaultResult;

		Resource containerResource = resources.remove("META-INF/container.xml");
		if(containerResource == null) {
			return result;
		}
		try {
			Document document = ResourceUtil.getAsDocument(containerResource);
			Element rootFileElement = (Element) ((Element) document.getDocumentElement().getElementsByTagName("rootfiles").item(0)).getElementsByTagName("rootfile").item(0);
			result = rootFileElement.getAttribute("full-path");
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
		if(StringUtil.isBlank(result)) {
			result = defaultResult;
		}
		return result;
	}

	private void handleMimeType(Resources resources) {
		resources.remove("mimetype");
	}
}
