package nl.siegmann.epublib.viewer;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;

import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.SectionWalker;
import nl.siegmann.epublib.domain.SectionWalker.SectionChangeEvent;
import nl.siegmann.epublib.domain.SectionWalker.SectionChangeListener;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Displays a page
 * 
 * @return
 */
public class ChapterPane extends JEditorPane implements SectionChangeListener, HyperlinkListener {

	private static final long serialVersionUID = -5322988066178102320L;

	private static final Logger log = Logger.getLogger(ChapterPane.class);
	private ImageLoaderCache imageLoaderCache;
	private SectionWalker sectionWalker;
	private Resource currentResource;
	
	public ChapterPane(SectionWalker sectionWalker) {
		this.sectionWalker = sectionWalker;
		setEditable(false);
		setContentType("text/html");
		addHyperlinkListener(this);
		imageLoaderCache = initImageLoader();
		sectionWalker.addSectionChangeEventListener(this);
		displayPage(sectionWalker.getCurrentResource());
	}

	public void displayPage(Resource resource) {
		if (resource == null) {
			return;
		}
		currentResource = resource;
		try {
			log.debug("Reading resource " + resource.getHref());
			Reader reader = new InputStreamReader(resource.getInputStream(),
					resource.getInputEncoding());
			imageLoaderCache.setContextResource(resource);
			String pageContent = IOUtils.toString(reader);
			pageContent = stripHtml(pageContent);
			setText(pageContent);
			setCaretPosition(0);
		} catch (Exception e) {
			log.error("When reading resource " + resource.getId() + "("
					+ resource.getHref() + ") :" + e.getMessage(), e);
		}
	}
	
	public void hyperlinkUpdate(HyperlinkEvent event) {
		if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			String resourceHref = calculateTargetHref(event.getURL());
			Resource resource = sectionWalker.getBook().getResources().getByCompleteHref(resourceHref);
			if (resource == null) {
				log.error("Resource with url " + resourceHref + " not found");
			} else {
				sectionWalker.gotoResource(resource);
			}
		}
	}

	
	private String calculateTargetHref(URL clickUrl) {
		String resourceHref = clickUrl.toString();
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
		boolean inXml = false;
		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (inXml) {
				if (c == '>') {
					inXml = false;
				}
			} else if (c == '<' // look for &lt;! or &lt;?
					&& i < input.length() - 1
					&& (input.charAt(i + 1) == '!' || input.charAt(i + 1) == '?')) {
				inXml = true;
			} else {
				result.append(c);
			}
		}
		return result.toString();
	}

	public void sectionChanged(SectionChangeEvent sectionChangeEvent) {
		if (sectionChangeEvent.isSectionChanged()) {
			displayPage(((SectionWalker) sectionChangeEvent.getSource())
					.getCurrentResource());
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
		ImageLoaderCache result = new ImageLoaderCache(sectionWalker.getBook(), cache);
		document.getDocumentProperties().put("imageCache", result);
		return result;
	}
}