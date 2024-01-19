package nl.siegmann.epublib.epub;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.domain.TableOfContents;
import nl.siegmann.epublib.util.ResourceUtil;
import nl.siegmann.epublib.util.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static nl.siegmann.epublib.Constants.NAMESPACE_XHTML;

public class NavDocument {
    private static final Logger log = Logger.getLogger(NavDocument.class.getName());

    private static final String NAV_TAG = "nav";
    private static final String A_HREF_TAG = "a";
    private static final String HREF_ATTR = "href";

    public static void read(Book book) {
        Resource navResource = book.getNavResource();
        try {
            Document navDocument = ResourceUtil.getAsDocument(navResource);
            Element navMapElement = DOMUtil.getFirstElementByTagNameNS(navDocument.getDocumentElement(), NAMESPACE_XHTML, NAV_TAG);
            if (null != navMapElement) {
                NodeList nodes = navMapElement.getElementsByTagNameNS(NAMESPACE_XHTML, A_HREF_TAG);
                if(nodes.getLength() > 0) {
                    List<TOCReference> tocReferences = readTOCReferences(nodes, book);
                    TableOfContents tableOfContents = new TableOfContents(tocReferences);
                    book.setTableOfContents(tableOfContents);
                }
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }
    
    private static List<TOCReference> readTOCReferences(NodeList navPoints, Book book) {
        if(navPoints == null) {
            return new ArrayList<>();
        }
        List<TOCReference> result = new ArrayList<>(navPoints.getLength());
        for(int i = 0; i < navPoints.getLength(); i++) {
            Node node = navPoints.item(i);
            if (node.getNodeType() != Document.ELEMENT_NODE) {
                continue;
            }
            if (! (node.getLocalName().equals(A_HREF_TAG))) {
                continue;
            }
            TOCReference tocReference = readTOCReference((Element) node, book);
            result.add(tocReference);
        }
        return result;
    }

    static TOCReference readTOCReference(Element navPoint, Book book) {
        String label = DOMUtil.getTextChildrenContent(navPoint);

        String navResourceHref = book.getNavResource().getHref();
        String navResourceRoot = StringUtil.substringBeforeLast(navResourceHref, '/');
        if (navResourceRoot.length() == navResourceHref.length()) {
            navResourceRoot = "";
        } else {
            navResourceRoot = navResourceRoot + "/";
        }
        String reference = StringUtil.collapsePathDots(navResourceRoot + readNavReference(navPoint));
        String href = StringUtil.substringBefore(reference, Constants.FRAGMENT_SEPARATOR_CHAR);
        String fragmentId = StringUtil.substringAfter(reference, Constants.FRAGMENT_SEPARATOR_CHAR);
        Resource resource = book.getResources().getByHref(href);
        if (resource == null) {
            log.severe("Resource with href " + href + " in NCX document not found");
        }
        return new TOCReference(label, resource, fragmentId);
    }
    
    private static String readNavReference(Element navPoint) {
        String result = DOMUtil.getAttribute(navPoint, NAMESPACE_XHTML, HREF_ATTR);
        try {
            result = URLDecoder.decode(result, Constants.CHARACTER_ENCODING);
        } catch (UnsupportedEncodingException e) {
            log.severe(e.getMessage());
        }
        return result;
    }
}
