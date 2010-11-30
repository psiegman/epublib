package nl.siegmann.epublib.viewer;

import java.awt.Image;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.text.html.HTMLDocument;

import nl.siegmann.epublib.browsersupport.Navigator;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.util.StringUtil;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	private Hashtable cache = new Hashtable();
	private Book book;
	private String currentFolder = "";
	private Navigator navigator;
	
	public ImageLoaderCache(Navigator navigator) {
		this.navigator = navigator;
		initBook(navigator.getBook());
	}
	
	public void initBook(Book book) {
		if (book == null) {
			return;
		}
		this.book = book;
		cache.clear();
	}

	public void setContextResource(Resource resource) {
		if (resource == null) {
			return;
		}
		if (StringUtils.isNotBlank(resource.getHref())) {
			int lastSlashPos = resource.getHref().lastIndexOf('/');
			if (lastSlashPos >= 0) {
				this.currentFolder = resource.getHref().substring(0, lastSlashPos + 1);
			}
		}
	}

	public void initImageLoader(HTMLDocument document) {
		try {
			document.setBase(new URL(ImageLoaderCache.IMAGE_URL_PREFIX));
		} catch (MalformedURLException e) {
			log.error(e.getMessage());
		}
		setContextResource(navigator.getCurrentResource());
		document.getDocumentProperties().put("imageCache", this);
	}


	public Object get(Object key) {
		if (book == null) {
			return null;
		}
		Image result = (Image) cache.get(key);
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
			cache.put(key, result);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return result;
	}

	public int size() {
		return cache.size();
	}

	public boolean isEmpty() {
		return cache.isEmpty();
	}

	public int hashCode() {
		return cache.hashCode();
	}

	public Enumeration keys() {
		return cache.keys();
	}

	public Enumeration elements() {
		return cache.elements();
	}

	public boolean equals(Object obj) {
		return cache.equals(obj);
	}

	public Object put(Object key, Object value) {
		return cache.put(key, value);
	}

	public Object remove(Object key) {
		return cache.remove(key);
	}

	public String toString() {
		return cache.toString();
	}
}