package nl.siegmann.epublib.viewer;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;

import javax.imageio.ImageIO;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.util.StringUtil;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;

/**
 * This class is installed as the JEditorPane's image cache.
 * Whenever it is requested an image it will try to load that image from the epub.
 * 
 * It's a trick to get the JEditorKit to load its images from the epub file instead of from the given url.
 * 
 * @author paul
 *
 */
class ImageLoaderCache extends Dictionary {

	public static final String IMAGE_URL_PREFIX = "http:/";

	private static final Logger log = LoggerFactory.getLogger(ImageLoaderCache.class);
	
	private Dictionary dictionary;
	private Book book;
	private String currentFolder = "";
	private Resource contextResource;
	
	public ImageLoaderCache(Book book, Dictionary dictionary) {
		this.book = book;
		this.dictionary = dictionary;
	}
	
	public void setContextResource(Resource resource) {
		if (resource == null) {
			return;
		}
		if (StringUtils.isNotBlank(resource.getHref())) {
			int lastSlashPos = resource.getHref().lastIndexOf('/');
			if (lastSlashPos >= 0) {
				setCurrentFolder(resource.getHref().substring(0, lastSlashPos + 1));
			}
		}
	}

	public Object get(Object key) {
		Image result = (Image) dictionary.get(key);
		if (result != null) {
			return result;
		}
		String resourceHref = ((URL) key).toString().substring(IMAGE_URL_PREFIX.length());
		resourceHref = currentFolder + resourceHref;
		resourceHref = StringUtil.collapsePathDots(resourceHref);
		Resource imageResource = book.getResources().getByHref(resourceHref);
		if (imageResource == null) {
			return null;
		}
		try {
			result = ImageIO.read(imageResource.getInputStream());
			dictionary.put(key, result);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return result;
	}

	public int size() {
		return dictionary.size();
	}

	public boolean isEmpty() {
		return dictionary.isEmpty();
	}

	public int hashCode() {
		return dictionary.hashCode();
	}

	public Enumeration keys() {
		return dictionary.keys();
	}

	public Enumeration elements() {
		return dictionary.elements();
	}

	public boolean equals(Object obj) {
		return dictionary.equals(obj);
	}

	public Object put(Object key, Object value) {
		return dictionary.put(key, value);
	}

	public Object remove(Object key) {
		return dictionary.remove(key);
	}

	public String toString() {
		return dictionary.toString();
	}

	public String getCurrentFolder() {
		return currentFolder;
	}

	public void setCurrentFolder(String currentFolder) {
		this.currentFolder = currentFolder;
	}
	
}