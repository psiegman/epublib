package nl.siegmann.epublib.viewer;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.browsersupport.NavigationEvent;
import nl.siegmann.epublib.browsersupport.NavigationEventListener;
import nl.siegmann.epublib.browsersupport.Navigator;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Displays a page
 * 
 * @return
 */
public class ContentPane extends JPanel implements NavigationEventListener, HyperlinkListener {

	private static final long serialVersionUID = -5322988066178102320L;

	private static final Logger log = LoggerFactory.getLogger(ContentPane.class);
	private ImageLoaderCache imageLoaderCache;
	private Navigator navigator;
	private Resource currentResource;
	private JEditorPane editorPane;
	private JScrollPane scrollPane;
	private Map<String, Document> documentCache = new HashMap<String, Document>();
	
	public ContentPane(Navigator navigator) {
		super(new GridLayout(1, 0));
		this.scrollPane = (JScrollPane) add(new JScrollPane());
		this.navigator = navigator;
		navigator.addNavigationEventListener(this);
		this.editorPane = createJEditorPane();
		scrollPane.getViewport().add(editorPane);
		initBook(navigator.getBook());
	}

	private JEditorPane createJEditorPane() {
		JEditorPane editorPane = new JEditorPane();
		editorPane.setBackground(Color.white);
		editorPane.setEditable(false);
		HTMLEditorKit htmlKit = new HTMLEditorKit();
//		StyleSheet myStyleSheet = new StyleSheet();
//		String normalTextStyle = "font-size: 12px, font-family: georgia";
//	    myStyleSheet.addRule("body {" + normalTextStyle + "}");
//	    myStyleSheet.addRule("p {" + normalTextStyle + "}");
//	    myStyleSheet.addRule("div {" + normalTextStyle + "}");
//	    htmlKit.setStyleSheet(myStyleSheet);
	    editorPane.setEditorKit(htmlKit);

//		editorPane.setContentType("text/html");
		editorPane.addHyperlinkListener(this);
		editorPane.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent keyEvent) {
				// TODO Auto-generated method stub
				if (keyEvent.getKeyChar() == ' ') {
					ContentPane.this.gotoNextPage();
				}
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		return editorPane;
	}
	
	public void displayPage(Resource resource) {
		if (resource == null) {
			return;
		}
		currentResource = resource;
		try {
			log.debug("Reading resource " + resource.getHref());
//			String pageContent = IOUtils.toString(resource.getReader());
//			pageContent = stripHtml(pageContent);
//			HTMLDocument doc = (HTMLDocument) editorPane.getEditorKit().createDefaultDocument();
//			initImageLoader(doc);
			Document doc = getDocument(resource);
			editorPane.setDocument(doc);
//			editorPane.setText(pageContent);

//			editorPane.setText(pageContent);
			editorPane.setCaretPosition(0);
		} catch (Exception e) {
			log.error("When reading resource " + resource.getId() + "("
					+ resource.getHref() + ") :" + e.getMessage(), e);
		}
	}
	
	public void hyperlinkUpdate(HyperlinkEvent event) {
		if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			String resourceHref = calculateTargetHref(event.getURL());
			Resource resource = navigator.getBook().getResources().getByHref(resourceHref);
			if (resource == null) {
				log.error("Resource with url " + resourceHref + " not found");
			} else {
				navigator.gotoResource(resource, this);
			}
		}
	}

	public void gotoPreviousPage() {
		Point viewPosition = scrollPane.getViewport().getViewPosition();
		if (viewPosition.getY() <= 0) {
			navigator.gotoPrevious(this);
			return;
		}
		int viewportHeight = scrollPane.getViewport().getHeight();
		int newY = (int) viewPosition.getY();
		newY -= viewportHeight;
		newY = Math.max(0, newY - viewportHeight);
		scrollPane.getViewport().setViewPosition(new Point((int) viewPosition.getX(), newY));
	}
	
	public void gotoNextPage() {
		Point viewPosition = scrollPane.getViewport().getViewPosition();
		int viewportHeight = scrollPane.getViewport().getHeight();
		int scrollMax = scrollPane.getVerticalScrollBar().getMaximum();
		if (viewPosition.getY() + viewportHeight >= scrollMax) {
			navigator.gotoNext(this);
			return;
		}
		int newY = ((int) viewPosition.getY()) + viewportHeight;
		scrollPane.getViewport().setViewPosition(new Point((int) viewPosition.getX(), newY));
	}
	
	private String calculateTargetHref(URL clickUrl) {
		String resourceHref = clickUrl.toString();
		try {
			resourceHref = URLDecoder.decode(resourceHref, Constants.ENCODING.name());
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage());
		}
		resourceHref = resourceHref.substring(ImageLoaderCache.IMAGE_URL_PREFIX.length());

		if (currentResource != null && StringUtils.isNotBlank(currentResource.getHref())) {
			int lastSlashPos = currentResource.getHref().lastIndexOf('/');
			if (lastSlashPos >= 0) {
				resourceHref = currentResource.getHref().substring(0, lastSlashPos + 1) + resourceHref;
			}
		}
		return resourceHref;
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

	private void initBook(Book book) {
		if (book == null) {
			return;
		}
		documentCache.clear();
		displayPage(book.getCoverPage());
	}
	
	private Document getDocument(Resource resource) throws IOException, BadLocationException {
		HTMLDocument document = (HTMLDocument) documentCache.get(StringUtils.substringBefore(resource.getHref(), "#"));
		if (document != null) {
			return document;
		}
		String pageContent = IOUtils.toString(resource.getReader());
		pageContent = stripHtml(pageContent);
		document = (HTMLDocument) editorPane.getEditorKit().createDefaultDocument();
	    document.remove(0, document.getLength());
	    Reader r = new StringReader(pageContent);
	    EditorKit kit = editorPane.getEditorKit();
        kit.read(r, document, 0);
		initImageLoader(document);
//		editorPane.setDocument(doc);
//		editorPane.setText(pageContent);
		documentCache.put(StringUtils.substringBefore(resource.getHref(), "#"), document);
		return document;
	}
	
	public void navigationPerformed(NavigationEvent navigationEvent) {
		if (navigationEvent.isResourceChanged()) {
			displayPage(navigationEvent.getSectionWalker().getCurrentResource());
		}
	}

	private void initImageLoader(HTMLDocument document) {
		try {
			document.setBase(new URL(ImageLoaderCache.IMAGE_URL_PREFIX));
		} catch (MalformedURLException e) {
			log.error(e.getMessage());
		}
		Dictionary cache = (Dictionary) document.getProperty("imageCache");
		if (cache == null) {
			cache = new Hashtable();
		}
		if (imageLoaderCache == null) {
			imageLoaderCache = new ImageLoaderCache(navigator, cache);
		}
		imageLoaderCache.setContextResource(navigator.getCurrentResource());
		document.getDocumentProperties().put("imageCache", imageLoaderCache);
	}


}