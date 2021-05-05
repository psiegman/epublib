package nl.siegmann.epublib.epub;

import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;

import static java.util.Collections.singleton;
import static javax.xml.XMLConstants.*;

class ContainerNamespaceContext implements NamespaceContext {
    public static final String XMLNS_CONTAINER = "urn:oasis:names:tc:opendocument:xmlns:container";
    private static final String XMLNS_CONTAINER_PREFIX = "container";

    @Override
    public String getNamespaceURI(String prefix) {
        if (prefix == null) throw new IllegalArgumentException();
        switch (prefix) {
        case XMLNS_CONTAINER_PREFIX: return XMLNS_CONTAINER;
        case XML_NS_PREFIX: return XML_NS_URI;
        case XMLNS_ATTRIBUTE: return XMLNS_ATTRIBUTE_NS_URI;
        default: return NULL_NS_URI;
        }
    }

    @Override
    public String getPrefix(String namespaceURI) {
        if (namespaceURI == null) throw new IllegalArgumentException();
        switch (namespaceURI) {
        case "urn:oasis:names:tc:opendocument:xmlns:container": return XMLNS_CONTAINER_PREFIX;
        case XML_NS_URI: return XML_NS_PREFIX;
        case XMLNS_ATTRIBUTE_NS_URI: return XMLNS_ATTRIBUTE;
        default: return null;
        }
    }

    @Override
    public Iterator<String> getPrefixes(String namespaceURI) {
        return singleton(getPrefix(namespaceURI)).iterator();
    }
}
