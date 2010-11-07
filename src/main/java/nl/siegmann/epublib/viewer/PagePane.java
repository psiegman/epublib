package nl.siegmann.epublib.viewer;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.JEditorPane;
import javax.swing.text.html.HTMLDocument;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.SectionWalker;
import nl.siegmann.epublib.domain.SectionWalker.SectionChangeEvent;
import nl.siegmann.epublib.domain.SectionWalker.SectionChangeListener;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

/**
 * Displays a page
 *  
 * @return
 */
public class PagePane extends JEditorPane implements SectionChangeListener {

	private static final long serialVersionUID = -5322988066178102320L;

	private static final Logger log = Logger.getLogger(PagePane.class);
	private ImageLoaderCache imageLoaderCache;
	private Book book;
	
	public PagePane(Book book) {
		this.book = book;
		setEditable(false);
		setContentType("text/html");
		imageLoaderCache = initImageLoader();
	}

	public void displayPage(Resource resource) {
		if (resource == null) {
			return;
		}
		try {
			log.debug("Reading resource " + resource.getHref());
			Reader reader = new InputStreamReader(resource.getInputStream(), resource.getInputEncoding());
			String pageContent = IOUtils.toString(reader);
			imageLoaderCache.setContextResource(resource);
			setText(pageContent);
			setCaretPosition(0);
		} catch (Exception e) {
			log.error("When reading resource " + resource.getId() + "(" + resource.getHref() + ") :" + e.getMessage(), e);
		}
	}

	public void sectionChanged(SectionChangeEvent sectionChangeEvent) {
		if (sectionChangeEvent.isSectionChanged()) {
			displayPage(((SectionWalker) sectionChangeEvent.getSource()).getCurrentResource());
		}
	}

	private ImageLoaderCache initImageLoader() {
		HTMLDocument document = (HTMLDocument) getDocument();
		try {
			document.setBase(new URL(ImageLoaderCache.IMAGE_URL_PREFIX));
		} catch (MalformedURLException e) {
			log.error(e);
		}
        Dictionary cache = (Dictionary) document.getProperty("imageCache");
        if (cache == null) {
        	cache = new Hashtable();
        }
        ImageLoaderCache result = new ImageLoaderCache(book, cache);
        document.getDocumentProperties().put("imageCache", result);
        return result;
	}
}