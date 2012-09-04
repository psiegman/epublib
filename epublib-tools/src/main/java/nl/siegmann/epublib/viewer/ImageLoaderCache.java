package nl.siegmann.epublib.viewer;

import java.awt.Image;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.text.html.HTMLDocument;

import nl.siegmann.epublib.browsersupport.Navigator;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.util.CollectionUtil;
import org.apache.commons.io.FilenameUtils;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a trick to get the JEditorKit to load its images from the epub file instead of from the given url.
 * 
 * This class is installed as the JEditorPane's image cache.
 * Whenever it is requested an image it will try to load that image from the epub.
 * 
 * Can be shared by multiple documents but can only be <em>used</em> by one document at the time because of the currentFolder issue. 
 * 
 * @author paul
 *
 */
class ImageLoaderCache extends Dictionary<String, Image> {

	public static final String IMAGE_URL_PREFIX = "http:/";

	private static final Logger log = LoggerFactory.getLogger(ImageLoaderCache.class);
	
	private Map<String, Image> cache = new HashMap<String, Image>();
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
		this.currentFolder = "";
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


	private String getResourceHref(String requestUrl) {
		String resourceHref = requestUrl.toString().substring(IMAGE_URL_PREFIX.length());
		resourceHref = currentFolder + resourceHref;
		resourceHref = FilenameUtils.normalize(resourceHref);
		return resourceHref;
	}
	
	/**
	 * Create an Image from the data of the given resource.
	 * 
	 * @param imageResource
	 * @return
	 */
	private Image createImage(Resource imageResource) {
		Image result = null;
		try {
			result = ImageIO.read(imageResource.getInputStream());
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		return result;
	}
	
	public Image get(Object key) {
		if (book == null) {
			return null;
		}
		
		String imageURL = key.toString();

		// see if the image is already in the cache
		Image result = cache.get(imageURL);
		if (result != null) {
			return result;
		}
		
		// get the image resource href
		String resourceHref = getResourceHref(imageURL);
		
		// find the image resource in the book resources
		Resource imageResource = book.getResources().getByHref(resourceHref);
		if (imageResource == null) {
			return result;
		}
		
		// create an image from the resource and add it to the cache
		result = createImage(imageResource);
		if (result != null) {
			cache.put(imageURL.toString(), result);
		}
		
		return result;
	}

	public int size() {
		return cache.size();
	}

	public boolean isEmpty() {
		return cache.isEmpty();
	}

	public Enumeration<String> keys() {
		return CollectionUtil.createEnumerationFromIterator(cache.keySet().iterator());
	}

	public Enumeration<Image> elements() {
		return CollectionUtil.createEnumerationFromIterator(cache.values().iterator());
	}

	public Image put(String key, Image value) {
		return cache.put(key.toString(), (Image) value);
	}

	public Image remove(Object key) {
		return cache.remove(key);
	}

	/**
	 * Clears the image cache.
	 */
	public void clear() {
		cache.clear();
	}
	
	public String toString() {
		return cache.toString();
	}
}