package nl.siegmann.epublib.epub;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

// package
class DOMUtil {

	public static List<String> getElementsTextChild(Element parentElement, String namespace, String tagname) {
		NodeList elements = parentElement.getElementsByTagNameNS(namespace, tagname);
		List<String> result = new ArrayList<String>(elements.getLength());
		for(int i = 0; i < elements.getLength(); i++) {
			result.add(getTextChild((Element) elements.item(i)));
		}
		return result;
	}

	/**
	 * Finds in the current document the first element with the given namespace and elementName and with the given findAttributeName and findAttributeValue.
	 * It then returns the value of the given resultAttributeName.
	 * 
	 * @param document
	 * @param namespace
	 * @param elementName
	 * @param findAttributeName
	 * @param findAttributeValue
	 * @param resultAttributeName
	 * @return
	 */
	public static String getFindAttributeValue(Document document, String namespace, String elementName, String findAttributeName, String findAttributeValue, String resultAttributeName) {
		NodeList metaTags = document.getElementsByTagNameNS(namespace, elementName);
		for(int i = 0; i < metaTags.getLength(); i++) {
			Element metaElement = (Element) metaTags.item(i);
			if(findAttributeValue.equalsIgnoreCase(metaElement.getAttribute(findAttributeName)) 
				&& StringUtils.isNotBlank(metaElement.getAttribute(resultAttributeName))) {
				return metaElement.getAttribute(resultAttributeName);
			}
		}
		return null;
	}

	/**
	 * Gets the first element that is a child of the parentElement and has the given namespace and tagName
	 * 
	 * @param parentElement
	 * @param namespace
	 * @param tagName
	 * @return
	 */
	public static Element getFirstElementByTagNameNS(Element parentElement, String namespace, String tagName) {
		NodeList nodes = parentElement.getElementsByTagNameNS(namespace, tagName);
		if(nodes.getLength() == 0) {
			return null;
		}
		return (Element) nodes.item(0);
	}

	public static String getTextChild(Element parentElement) {
		if(parentElement == null) {
			return null;
		}
		Text childContent = (Text) parentElement.getFirstChild();
		if(childContent == null) {
			return null;
		}
		return childContent.getData().trim();
	}

}
