package nl.siegmann.epublib.epub;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.MediaType;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Resources;
import nl.siegmann.epublib.service.MediatypeService;
import nl.siegmann.epublib.util.ResourceUtil;
import nl.siegmann.epublib.util.StringUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Reads an epub file.
 * 
 * @author paul
 *
 */
public class EpubReader {

	private static final Logger log = LoggerFactory.getLogger(EpubReader.class);
	private BookProcessor bookProcessor = BookProcessor.IDENTITY_BOOKPROCESSOR;
	
	public Book readEpub(InputStream in) throws IOException {
		return readEpub(in, Constants.CHARACTER_ENCODING);
	}	
	
	public Book readEpub(ZipInputStream in) throws IOException {
		return readEpub(in, Constants.CHARACTER_ENCODING);
	}

    public Book readEpub(ZipFile zipfile) throws IOException {
        return readEpub(zipfile, Constants.CHARACTER_ENCODING);
    }

	/**
	 * Read epub from inputstream
	 * 
	 * @param in the inputstream from which to read the epub
	 * @param encoding the encoding to use for the html files within the epub
	 * @return
	 * @throws IOException
	 */
	public Book readEpub(InputStream in, String encoding) throws IOException {
		return readEpub(new ZipInputStream(in), encoding);
	}	
	
	/**
	 * Reads this EPUB without loading all resources into memory.
	 * 
	 * @param fileName the file to load
	 * @param encoding the encoding for XHTML files
	 * @param lazyLoadedTypes a list of the MediaType to load lazily
	 * @return
	 * @throws IOException
	 */
	public Book readEpubLazy( String fileName, String encoding, List<MediaType> lazyLoadedTypes ) throws IOException {
		Book result = new Book();
		Resources resources = readLazyResources(fileName, encoding, lazyLoadedTypes);
		handleMimeType(result, resources);
		String packageResourceHref = getPackageResourceHref(resources);
		Resource packageResource = processPackageResource(packageResourceHref, result, resources);
		result.setOpfResource(packageResource);
		Resource ncxResource = processNcxResource(packageResource, result);
		result.setNcxResource(ncxResource);
		result = postProcessBook(result);
		return result;
	}
	

	/**
	 * Reads this EPUB without loading any resources into memory.
	 * 
	 * @param fileName the file to load
	 * @param encoding the encoding for XHTML files
	 * 
	 * @return
	 * @throws IOException
	 */
	public Book readEpubLazy( String fileName, String encoding ) throws IOException {
		return readEpubLazy(fileName, encoding, Arrays.asList(MediatypeService.mediatypes) );
	}
	
	public Book readEpub(ZipInputStream in, String encoding) throws IOException {
        return readEpubResources(readResources(in, encoding));
	}

    public Book readEpub(ZipFile in, String encoding) throws IOException {
        return readEpubResources(readResources(in, encoding));
    }

    public Book readEpubResources(Resources resources) throws IOException{
        Book result = new Book();
        handleMimeType(result, resources);
        String packageResourceHref = getPackageResourceHref(resources);
        Resource packageResource = processPackageResource(packageResourceHref, result, resources);
        result.setOpfResource(packageResource);
        Resource ncxResource = processNcxResource(packageResource, result);
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

	private Resource processNcxResource(Resource packageResource, Book book) {
		return NCXDocument.read(book, this);
	}

	private Resource processPackageResource(String packageResourceHref, Book book, Resources resources) {
		Resource packageResource = resources.remove(packageResourceHref);
		try {
			PackageDocumentReader.read(packageResource, this, book, resources);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
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
			log.error(e.getMessage(), e);
		}
		if(StringUtil.isBlank(result)) {
			result = defaultResult;
		}
		return result;
	}

	private void handleMimeType(Book result, Resources resources) {
		resources.remove("mimetype");
	}
	
	private Resources readLazyResources( String fileName, String defaultHtmlEncoding,
			List<MediaType> lazyLoadedTypes) throws IOException {		
				
		ZipInputStream in = new ZipInputStream(new FileInputStream(fileName));
		
		Resources result = new Resources();
		for(ZipEntry zipEntry = in.getNextEntry(); zipEntry != null; zipEntry = in.getNextEntry()) {
			if(zipEntry.isDirectory()) {
				continue;
			}
			
			String href = zipEntry.getName();
			MediaType mediaType = MediatypeService.determineMediaType(href);			
			
			Resource resource;
			
			if ( lazyLoadedTypes.contains(mediaType) ) {
				resource = new Resource(fileName, zipEntry.getSize(), href);								
			} else {			
				resource = new Resource( in, href );
			}
			
			if(resource.getMediaType() == MediatypeService.XHTML) {
				resource.setInputEncoding(defaultHtmlEncoding);
			}
			result.add(resource);
		}
		
		return result;
	}	

	private Resources readResources(ZipInputStream in, String defaultHtmlEncoding) throws IOException {
		Resources result = new Resources();
		for(ZipEntry zipEntry = in.getNextEntry(); zipEntry != null; zipEntry = in.getNextEntry()) {
			if(zipEntry.isDirectory()) {
				continue;
			}
			Resource resource = ResourceUtil.createResource(zipEntry, in);
			if(resource.getMediaType() == MediatypeService.XHTML) {
				resource.setInputEncoding(defaultHtmlEncoding);
			}
			result.add(resource);
		}
		return result;
	}

    private Resources readResources(ZipFile zipFile, String defaultHtmlEncoding) throws IOException {
        Resources result = new Resources();
        Enumeration<? extends ZipEntry> entries = zipFile.entries();

        while(entries.hasMoreElements()){
            ZipEntry zipEntry = entries.nextElement();
            if(zipEntry != null && !zipEntry.isDirectory()){
                Resource resource = ResourceUtil.createResource(zipEntry, zipFile.getInputStream(zipEntry));
                if(resource.getMediaType() == MediatypeService.XHTML) {
                    resource.setInputEncoding(defaultHtmlEncoding);
                }
                result.add(resource);
            }
        }

        return result;
    }
}
