package nl.siegmann.epublib.epub;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import net.sf.jazzlib.ZipEntry;
import net.sf.jazzlib.ZipException;
import net.sf.jazzlib.ZipFile;
import net.sf.jazzlib.ZipInputStream;
import nl.siegmann.epublib.domain.LazyResource;
import nl.siegmann.epublib.domain.MediaType;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Resources;
import nl.siegmann.epublib.service.MediatypeService;
import nl.siegmann.epublib.util.CollectionUtil;
import nl.siegmann.epublib.util.ResourceUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loads Resources from inputStreams, ZipFiles, etc
 * 
 * @author paul
 *
 */
public class ResourcesLoader {
	private static final Logger LOG = LoggerFactory.getLogger(ResourcesLoader.class);

	/**
	 * Loads the entries of the zipFile as resources.
	 * 
	 * The MediaTypes that are in the lazyLoadedTypes will not get their contents loaded, but are stored as references to
	 * entries into the ZipFile and are loaded on demand by the Resource system.
	 * 
	 * @param zipFile
	 * @param defaultHtmlEncoding
	 * @param lazyLoadedTypes
	 * @return
	 * @throws IOException
	 */
	public static Resources loadResources(ZipFile zipFile, String defaultHtmlEncoding,
			List<MediaType> lazyLoadedTypes) throws IOException {		
				
		Resources result = new Resources();
        Enumeration<? extends ZipEntry> entries = zipFile.entries();

        while( entries.hasMoreElements() ) {
            ZipEntry zipEntry = entries.nextElement();

			if(zipEntry == null || zipEntry.isDirectory()) {
				continue;
			}
			
			String href = zipEntry.getName();
			
			Resource resource;
			
			if (shouldLoadLazy(href, lazyLoadedTypes)) {
				resource = new LazyResource(zipFile.getName(), zipEntry.getSize(), href);								
			} else {		
				resource = ResourceUtil.createResource(zipEntry, zipFile.getInputStream(zipEntry));
			}
			
			if(resource.getMediaType() == MediatypeService.XHTML) {
				resource.setInputEncoding(defaultHtmlEncoding);
			}
			result.add(resource);
		}
		
		return result;
	}
	
	/**
	 * Whether the given href will load a mediaType that is in the collection of lazilyLoadedMediaTypes.
	 * 
	 * @param href
	 * @param lazilyLoadedMediaTypes
	 * @return Whether the given href will load a mediaType that is in the collection of lazilyLoadedMediaTypes.
	 */
	private static boolean shouldLoadLazy(String href, Collection<MediaType> lazilyLoadedMediaTypes) {
		if (CollectionUtil.isEmpty(lazilyLoadedMediaTypes)) {
			return false;
		}
		MediaType mediaType = MediatypeService.determineMediaType(href);
		return lazilyLoadedMediaTypes.contains(mediaType);
	}

	/**
	 * Loads all entries from the ZipInputStream as Resources.
	 * 
	 * Loads the contents of all ZipEntries into memory.
	 * Is fast, but may lead to memory problems when reading large books on devices with small amounts of memory.
	 * 
	 * @param zipInputStream
	 * @param defaultHtmlEncoding
	 * @return
	 * @throws IOException
	 */
	public static Resources loadResources(ZipInputStream zipInputStream, String defaultHtmlEncoding) throws IOException {
		Resources result = new Resources();
		ZipEntry zipEntry;
		do {
			// get next valid zipEntry
			zipEntry = getNextZipEntry(zipInputStream);
			if((zipEntry == null) || zipEntry.isDirectory()) {
				continue;
			}
			
			// store resource
			Resource resource = ResourceUtil.createResource(zipEntry, zipInputStream);
			if(resource.getMediaType() == MediatypeService.XHTML) {
				resource.setInputEncoding(defaultHtmlEncoding);
			}
			result.add(resource);
		} while(zipEntry != null);

		return result;
	}

	
	private static ZipEntry getNextZipEntry(ZipInputStream zipInputStream) throws IOException {
		try {
			return zipInputStream.getNextEntry();
		} catch(ZipException e) {
			//see <a href="https://github.com/psiegman/epublib/issues/122">Issue #122 Infinite loop</a>.
			//when reading a file that is not a real zip archive or a zero length file, zipInputStream.getNextEntry()
			//throws an exception and does not advance, so loadResources enters an infinite loop
			LOG.error("Invalid or damaged zip file.", e);
			try { zipInputStream.closeEntry(); } catch (Exception ignored) {}
			throw e;
		}
	}

	/**
	 * Loads all entries from the ZipInputStream as Resources.
	 * 
	 * Loads the contents of all ZipEntries into memory.
	 * Is fast, but may lead to memory problems when reading large books on devices with small amounts of memory.
	 * 
	 * @param zipFile
	 * @param defaultHtmlEncoding
	 * @return
	 * @throws IOException
	 */
    public static Resources loadResources(ZipFile zipFile, String defaultHtmlEncoding) throws IOException {
    	return loadResources(zipFile, defaultHtmlEncoding, Collections.<MediaType>emptyList());
    }

}
