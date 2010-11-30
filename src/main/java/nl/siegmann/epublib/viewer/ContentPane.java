package nl.siegmann.epublib.viewer;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
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
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.html.HTML;
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
public class ContentPane extends JPanel implements NavigationEventListener,
		HyperlinkListener {

	private static final long serialVersionUID = -5322988066178102320L;

	private static final Logger log = LoggerFactory
			.getLogger(ContentPane.class);
	private ImageLoaderCache imageLoaderCache;
	private Navigator navigator;
	private Resource currentResource;
	private JEditorPane editorPane;
	private JScrollPane scrollPane;
	private Map<String, HTMLDocument> documentCache = new HashMap<String, HTMLDocument>();

	public ContentPane(Navigator navigator) {
		super(new GridLayout(1, 0));
		this.scrollPane = (JScrollPane) add(new JScrollPane());
		this.scrollPane.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					Point viewPosition = scrollPane.getViewport().getViewPosition();
					int newY = (int) (viewPosition.getY() + 10);
					scrollPane.getViewport().setViewPosition(new Point((int) viewPosition.getX(), newY));
				}
			}
		});
		this.scrollPane.addMouseWheelListener(new MouseWheelListener() {
			
			private boolean gotoNextPage = false;
			private boolean gotoPreviousPage = false;

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
			    int notches = e.getWheelRotation();
//			    if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
//			    	System.out.println(this.getClass().getName() + "    Scroll type: WHEEL_UNIT_SCROLL");
//			    	System.out.println(this.getClass().getName() + "    Scroll amount: " + e.getScrollAmount() + " unit increments per notch");
//			    	System.out.println(this.getClass().getName() + "    Units to scroll: " + e.getUnitsToScroll() + " unit increments");
//			    	System.out.println(this.getClass().getName() + "    Vertical unit increment: " + scrollPane.getVerticalScrollBar().getUnitIncrement(1) + " pixels");
//			    } else { //scroll type == MouseWheelEvent.WHEEL_BLOCK_SCROLL
//			    	System.out.println(this.getClass().getName() + "    Scroll type: WHEEL_BLOCK_SCROLL");
//			    	System.out.println(this.getClass().getName() + "    Vertical block increment: " + scrollPane.getVerticalScrollBar().getBlockIncrement(1) + " pixels");
//			    }
			    int increment = scrollPane.getVerticalScrollBar().getUnitIncrement(1);
			    if (notches < 0) {
					Point viewPosition = scrollPane.getViewport().getViewPosition();
					if (viewPosition.getY() - increment < 0) {
						if (gotoPreviousPage) {
							gotoPreviousPage = false;
							ContentPane.this.navigator.gotoPrevious(-1, ContentPane.this);
						} else {
							gotoPreviousPage = true;
							scrollPane.getViewport().setViewPosition(new Point((int) viewPosition.getX(), 0));
						}
					}
			    } else {
			    	// only move to the next page if we are exactly at the bottom of the current page
			    	Point viewPosition = scrollPane.getViewport().getViewPosition();
					int viewportHeight = scrollPane.getViewport().getHeight();
					int scrollMax = scrollPane.getVerticalScrollBar().getMaximum();
					if (viewPosition.getY() + viewportHeight + increment > scrollMax) {
//						System.out.println(this.getClass() + ": viewY" + viewPosition.getY() + ", viewheight:" + viewportHeight + ", increment:" + increment + ", scrollmax:" + scrollMax + ", gotonext:" + gotoNextPage);
						if (gotoNextPage) {
							gotoNextPage = false;
							ContentPane.this.navigator.gotoNext(ContentPane.this);
						} else {
							gotoNextPage = true;
							int newY = scrollMax - viewportHeight;
							scrollPane.getViewport().setViewPosition(new Point((int) viewPosition.getX(), newY));
						}
					}
			    }
			  }
		});
		this.navigator = navigator;
		navigator.addNavigationEventListener(this);
		this.editorPane = createJEditorPane();
		scrollPane.getViewport().add(editorPane);
		initBook(navigator.getBook());
	}
	
	
	/**
	 * Whether the given searchString matches any of the possibleValues.
	 * 
	 * @param searchString
	 * @param possibleValues
	 * @return
	 */
	private static boolean matchesAny(String searchString, String... possibleValues) {
		for (int i = 0; i < possibleValues.length; i++) {
			String attributeValue = possibleValues[i];
			if (StringUtils.isNotBlank(attributeValue) && (attributeValue.equals(searchString))) {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Scrolls the editorPane to the startOffset of the current element in the elementIterator
	 * 
	 * @param requestFragmentId
	 * @param attributeValue
	 * @param editorPane
	 * @param elementIterator
	 * 
	 * @return whether it was a match and we jumped there.
	 */
	private static void scrollToElement(JEditorPane editorPane, HTMLDocument.Iterator elementIterator) {
		try {
			Rectangle rectangle = editorPane.modelToView(elementIterator.getStartOffset());
			if (rectangle == null) {
				return;
			}
			// the view is visible, scroll it to the
			// center of the current visible area.
			Rectangle visibleRectangle = editorPane.getVisibleRect();
			// r.y -= (vis.height / 2);
			rectangle.height = visibleRectangle.height;
			editorPane.scrollRectToVisible(rectangle);
		} catch (BadLocationException e) {
			log.error(e.getMessage());
		}
	}
	
	
	/**
	 * Scrolls the editorPane to the first anchor element whose id or name matches the given fragmentId.
	 * 
	 * @param fragmentId
	 */
	private void scrollToNamedAnchor(String fragmentId) {
		HTMLDocument doc = (HTMLDocument) editorPane.getDocument();
		for (HTMLDocument.Iterator iter = doc.getIterator(HTML.Tag.A); iter.isValid(); iter.next()) {
			AttributeSet attributes = iter.getAttributes();
			if (matchesAny(fragmentId, (String) attributes.getAttribute(HTML.Attribute.NAME),
					(String) attributes.getAttribute(HTML.Attribute.ID))) {
				scrollToElement(editorPane, iter);
				break;
			}
		}
	}

	private JEditorPane createJEditorPane() {
		JEditorPane editorPane = new JEditorPane();
		editorPane.setBackground(Color.white);
		editorPane.setEditable(false);
		HTMLEditorKit htmlKit = new HTMLEditorKit();
		// StyleSheet myStyleSheet = new StyleSheet();
		// String normalTextStyle = "font-size: 12px, font-family: georgia";
		// myStyleSheet.addRule("body {" + normalTextStyle + "}");
		// myStyleSheet.addRule("p {" + normalTextStyle + "}");
		// myStyleSheet.addRule("div {" + normalTextStyle + "}");
		// htmlKit.setStyleSheet(myStyleSheet);
		editorPane.setEditorKit(htmlKit);
		editorPane.addHyperlinkListener(this);
		editorPane.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent keyEvent) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyPressed(KeyEvent keyEvent) {
				if (keyEvent.getKeyCode() == KeyEvent.VK_RIGHT) {
					navigator.gotoNext(ContentPane.this);
				} else if (keyEvent.getKeyCode() == KeyEvent.VK_LEFT) {
					navigator.gotoPrevious(ContentPane.this);
//				} else if (keyEvent.getKeyCode() == KeyEvent.VK_UP) {
//					ContentPane.this.gotoPreviousPage();
				} else if (keyEvent.getKeyCode() == KeyEvent.VK_SPACE) {
//					|| (keyEvent.getKeyCode() == KeyEvent.VK_DOWN)) {
					ContentPane.this.gotoNextPage();
				}
			}
		});
		return editorPane;
	}

	public void displayPage(Resource resource) {
		displayPage(resource, 0);
	}

	public void displayPage(Resource resource, int pagePos) {
		if (resource == null) {
			return;
		}
		currentResource = resource;
		try {
			Document doc = getDocument(resource);
			editorPane.setDocument(doc);
			if (pagePos < 0) {
				editorPane.setCaretPosition(editorPane.getDocument().getLength());
			} else {
				editorPane.setCaretPosition(pagePos);
			}
			if (pagePos == 0) {
				scrollPane.getViewport().setViewPosition(new Point(0, 0));
			} else if (pagePos < 0) {
				int viewportHeight = scrollPane.getViewport().getHeight();
				int scrollMax = scrollPane.getVerticalScrollBar().getMaximum();
				scrollPane.getViewport().setViewPosition(new Point(0, scrollMax - viewportHeight));
			}
		} catch (Exception e) {
			log.error("When reading resource " + resource.getId() + "("
					+ resource.getHref() + ") :" + e.getMessage(), e);
		}
	}

	public void hyperlinkUpdate(HyperlinkEvent event) {
		if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			String resourceHref = calculateTargetHref(event.getURL());
			Resource resource = navigator.getBook().getResources()
					.getByHref(resourceHref);
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
		scrollPane.getViewport().setViewPosition(
				new Point((int) viewPosition.getX(), newY));
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
		scrollPane.getViewport().setViewPosition(
				new Point((int) viewPosition.getX(), newY));
	}

	private String calculateTargetHref(URL clickUrl) {
		String resourceHref = clickUrl.toString();
		try {
			resourceHref = URLDecoder.decode(resourceHref,
					Constants.ENCODING.name());
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage());
		}
		resourceHref = resourceHref.substring(ImageLoaderCache.IMAGE_URL_PREFIX
				.length());

		if (currentResource != null
				&& StringUtils.isNotBlank(currentResource.getHref())) {
			int lastSlashPos = currentResource.getHref().lastIndexOf('/');
			if (lastSlashPos >= 0) {
				resourceHref = currentResource.getHref().substring(0,
						lastSlashPos + 1)
						+ resourceHref;
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
		fillDocumentCache(book);
	}

	private HTMLDocument getDocument(Resource resource) throws IOException,
			BadLocationException {
		HTMLDocument document = documentCache.get(resource.getHref());
		if (document != null) {
			return document;
		}
		
		document = createDocument(resource);
		initImageLoader(document);
		documentCache.put(resource.getHref(), document);
		return document;
	}


	private HTMLDocument createDocument(Resource resource) throws IOException, BadLocationException {
		HTMLDocument document = (HTMLDocument) editorPane.getEditorKit().createDefaultDocument();
		String pageContent = IOUtils.toString(resource.getReader());
		pageContent = stripHtml(pageContent);
		document.remove(0, document.getLength());
		Reader contentReader = new StringReader(pageContent);
		EditorKit kit = editorPane.getEditorKit();
		kit.read(contentReader, document, 0);
		return document;
	}
	
	
	private void fillDocumentCache(Book book) {
		if (book == null) {
			return;
		}
		for (Resource resource: book.getResources().getAll()) {
			HTMLDocument document;
			try {
				document = createDocument(resource);
				documentCache.put(resource.getHref(), document);
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
	}

	
	public void navigationPerformed(NavigationEvent navigationEvent) {
		if (navigationEvent.isResourceChanged()) {
			displayPage(navigationEvent.getCurrentResource(),
					navigationEvent.getCurrentPagePos());
		} else if (navigationEvent.isPagePosChanged()) {
			editorPane.setCaretPosition(navigationEvent.getCurrentPagePos());
		}
		if (StringUtils.isNotBlank(navigationEvent.getCurrentFragmentId())) {
			scrollToNamedAnchor(navigationEvent.getCurrentFragmentId());
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