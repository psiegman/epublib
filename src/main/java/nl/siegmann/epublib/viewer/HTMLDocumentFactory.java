package nl.siegmann.epublib.viewer;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.swing.text.BadLocationException;
import javax.swing.text.EditorKit;
import javax.swing.text.html.HTMLDocument;

import nl.siegmann.epublib.browsersupport.Navigator;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.service.MediatypeService;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates swing HTML documents from resources.
 * 
 * Between books the init(Book) function needs to be called in order for images to appear correctly.
 * 
 * @author paul.siegmann
 *
 */
public class HTMLDocumentFactory {
	
	private static final Logger log = LoggerFactory.getLogger(HTMLDocumentFactory.class);
	
	private ImageLoaderCache imageLoaderCache;
	private Map<String, HTMLDocument> documentCache = new HashMap<String, HTMLDocument>();
	private EditorKit editorKit;

	public HTMLDocumentFactory(Navigator navigator, EditorKit editorKit) {
		this.editorKit = editorKit;
		this.imageLoaderCache = new ImageLoaderCache(navigator);
		init(navigator.getBook());
	}

	public void init(Book book) {
		if (book == null) {
			return;
		}
		imageLoaderCache.initBook(book);
		fillDocumentCache(book);
	}

	public HTMLDocument getDocument(Resource resource) {
		HTMLDocument document = documentCache.get(resource.getHref());
		if (document == null) {
			document = createDocument(resource);
			if (document != null) {
				documentCache.put(resource.getHref(), document);
			}
		}
		if (document != null) {
			imageLoaderCache.initImageLoader(document);
		}
		return document;
	}

	private String stripHtml(String input) {
		String result = removeControlTags(input);
		result = result.replaceAll(
				"<meta\\s+[^>]*http-equiv=\"Content-Type\"[^>]*>", "");
		return result;
	}

	/**
	 * Quick and dirty stripper of all &lt;?...&gt; and &lt;!...&gt; tags as
	 * these confuse the html viewer.
	 * 
	 * @param input
	 * @return
	 */
	private static String removeControlTags(String input) {
		StringBuilder result = new StringBuilder();
		boolean inControlTag = false;
		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (inControlTag) {
				if (c == '>') {
					inControlTag = false;
				}
			} else if (c == '<' // look for &lt;! or &lt;?
					&& i < input.length() - 1
					&& (input.charAt(i + 1) == '!' || input.charAt(i + 1) == '?')) {
				inControlTag = true;
			} else {
				result.append(c);
			}
		}
		return result.toString();
	}

	private HTMLDocument createDocument(Resource resource) {
		HTMLDocument result = null;
		if (resource.getMediaType() != MediatypeService.XHTML) {
			return result;
		}
		try {
			HTMLDocument document = (HTMLDocument) editorKit.createDefaultDocument();
			String pageContent = IOUtils.toString(resource.getReader());
			pageContent = stripHtml(pageContent);
			document.remove(0, document.getLength());
			Reader contentReader = new StringReader(pageContent);
			editorKit.read(contentReader, document, 0);
			result = document;
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return result;
	}
	
	
	private void fillDocumentCache(Book book) {
		if (book == null) {
			return;
		}
		documentCache.clear();
		for (Resource resource: book.getResources().getAll()) {
			HTMLDocument document;
			document = createDocument(resource);
			if (document != null) {
				documentCache.put(resource.getHref(), document);
			}
		}
	}



}
