package nl.siegmann.epublib.epub;

import java.util.ArrayList;
import java.util.List;

import nl.siegmann.epublib.util.StringUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * Utility methods for working with the DOM.
 * 
 * @author paul
 *
 */
class DOMUtil {

	
	/**
	 * First tries to get the attribute value by doing an getAttributeNS on the element, if that gets an empty element it does a getAttribute without namespace.
	 */
	public static String getAttribute(Node element, String namespace, String attribute) {
		Node node = element.getAttributes().getNamedItemNS(namespace, attribute);
		if (node == null) {
			node = element.getAttributes().getNamedItem(attribute);
		}
		return node != null ? node.getNodeValue() : "";
	}
	
	/**
	 * Gets all descendant elements of the given parentElement with the given namespace and tag name and returns their text child as a list of String.
	 */
	public static List<String> getElementsTextChild(Element parentElement, String namespace, String tagName) {
		NodeList elements = parentElement.getElementsByTagNameNS(namespace, tagName);
		List<String> result = new ArrayList<>(elements.getLength());
		for(int i = 0; i < elements.getLength(); i++) {
			result.add(getTextChildrenContent((Element) elements.item(i)));
		}
		return result;
	}

	/**
	 * Finds in the current document the first element with the given namespace and elementName and with the given findAttributeName and findAttributeValue.
	 * It then returns the value of the given resultAttributeName.
	 */
	public static String getFindAttributeValue(Document document, String namespace, String elementName, String findAttributeName, String findAttributeValue, String resultAttributeName) {
		NodeList metaTags = document.getElementsByTagNameNS(namespace, elementName);
		for(int i = 0; i < metaTags.getLength(); i++) {
			Element metaElement = (Element) metaTags.item(i);
			if(findAttributeValue.equalsIgnoreCase(metaElement.getAttribute(findAttributeName)) 
				&& StringUtil.isNotBlank(metaElement.getAttribute(resultAttributeName))) {
				return metaElement.getAttribute(resultAttributeName);
			}
		}
		return null;
	}

	/**
	 * Gets the first element that is a child of the parentElement and has the given namespace and tagName
	 */
	public static Element getFirstElementByTagNameNS(Element parentElement, String namespace, String tagName) {
		NodeList nodes = parentElement.getElementsByTagNameNS(namespace, tagName);
		if(nodes.getLength() == 0) {
			return null;
		}
		return (Element) nodes.item(0);
	}

	/**
	 * The contents of all Text nodes that are children of the given parentElement.
	 * The result is trim()-ed.
	 * 
	 * The reason for this more complicated procedure instead of just returning the data of the firstChild is that
	 * when the text is Chinese characters, then on Android each Character is represented in the DOM as
	 * an individual Text node.
	 */
	public static String getTextChildrenContent(Element parentElement) {
		if(parentElement == null) {
			return null;
		}
		StringBuilder result = new StringBuilder();
		NodeList childNodes = parentElement.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			if ((node == null) ||
					(node.getNodeType() != Node.TEXT_NODE)) {
				continue;
			}
			result.append(((Text) node).getData());
		}
		return result.toString().trim();
	}

}
