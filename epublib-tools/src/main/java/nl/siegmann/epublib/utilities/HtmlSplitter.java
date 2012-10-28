package nl.siegmann.epublib.utilities;

import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

/**
 * Splits up a xhtml document into pieces that are all valid xhtml documents.
 * 
 * @author paul
 *
 */
public class HtmlSplitter {

	private XMLEventFactory xmlEventFactory = XMLEventFactory.newInstance();
	private XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
	private List<XMLEvent> headerElements = new ArrayList<XMLEvent>();
	private List<XMLEvent> footerElements = new ArrayList<XMLEvent>();
	private int footerCloseTagLength;
	private List<XMLEvent> elementStack = new ArrayList<XMLEvent>();
	private StringWriter currentDoc = new StringWriter();
	private List<XMLEvent> currentXmlEvents = new ArrayList<XMLEvent>();
	private XMLEventWriter out;
	private int maxLength = 300000; // 300K, the max length of a chapter of an epub document
	private List<List<XMLEvent>> result = new ArrayList<List<XMLEvent>>();
		
	public List<List<XMLEvent>> splitHtml(Reader reader, int maxLength) throws XMLStreamException {
		XMLEventReader xmlEventReader = XMLInputFactory.newInstance().createXMLEventReader(reader);
		return splitHtml(xmlEventReader, maxLength);
	}
	
	private static int calculateTotalTagStringLength(List<XMLEvent> xmlEvents) {
		int result = 0;
		for(XMLEvent xmlEvent: xmlEvents) {
			result += xmlEvent.toString().length();
		}
		return result;
	}
	
	public List<List<XMLEvent>> splitHtml(XMLEventReader reader, int maxLength) throws XMLStreamException {
		this.headerElements = getHeaderElements(reader);
		this.footerElements = getFooterElements();
		footerCloseTagLength = calculateTotalTagStringLength(footerElements);
		this.maxLength = (int) ((float) maxLength * 0.9);
		currentXmlEvents = new ArrayList<XMLEvent>();
		currentXmlEvents.addAll(headerElements);
		currentXmlEvents.addAll(elementStack);
		out = xmlOutputFactory.createXMLEventWriter(currentDoc);
		for(XMLEvent headerXmlEvent: headerElements) {
			out.add(headerXmlEvent);
		}
		XMLEvent xmlEvent = reader.nextEvent();
		while(! isBodyEndElement(xmlEvent)) {
			processXmlEvent(xmlEvent, result);
			xmlEvent = reader.nextEvent();
		}
		result.add(currentXmlEvents);
		return result;
	}
	
	
	private void closeCurrentDocument() throws XMLStreamException {
		closeAllTags(currentXmlEvents);
		currentXmlEvents.addAll(footerElements);
		result.add(currentXmlEvents);
	}

	private void startNewDocument() throws XMLStreamException {
		currentDoc = new StringWriter();
		out = xmlOutputFactory.createXMLEventWriter(currentDoc);
		for(XMLEvent headerXmlEvent: headerElements) {
			out.add(headerXmlEvent);
		}
		for(XMLEvent stackXmlEvent: elementStack) {
			out.add(stackXmlEvent);
		}
		
		currentXmlEvents = new ArrayList<XMLEvent>();
		currentXmlEvents.addAll(headerElements);
		currentXmlEvents.addAll(elementStack);
	}

	private void processXmlEvent(XMLEvent xmlEvent, List<List<XMLEvent>> docs) throws XMLStreamException {
		out.flush();
		String currentSerializerDoc = currentDoc.toString();
		if((currentSerializerDoc.length() + xmlEvent.toString().length() + footerCloseTagLength) >= maxLength) {
			closeCurrentDocument();
			startNewDocument();
		}
		updateStack(xmlEvent);
		out.add(xmlEvent);
		currentXmlEvents.add(xmlEvent);
	}

	private void closeAllTags(List<XMLEvent> xmlEvents) throws XMLStreamException {
		for(int i = elementStack.size() - 1; i>= 0; i--) {
			XMLEvent xmlEvent = elementStack.get(i);
			XMLEvent xmlEndElementEvent = xmlEventFactory.createEndElement(xmlEvent.asStartElement().getName(), null);
			xmlEvents.add(xmlEndElementEvent);
		}
	}
	
	private void updateStack(XMLEvent xmlEvent) {
		if(xmlEvent.isStartElement()) {
			elementStack.add(xmlEvent);
		} else if(xmlEvent.isEndElement()) {
			XMLEvent lastEvent = elementStack.get(elementStack.size() - 1);
			if(lastEvent.isStartElement() &&
					xmlEvent.asEndElement().getName().equals(lastEvent.asStartElement().getName())) {
				elementStack.remove(elementStack.size() - 1);
			}
		}
	}

	private List<XMLEvent> getHeaderElements(XMLEventReader reader) throws XMLStreamException {
		List<XMLEvent> result = new ArrayList<XMLEvent>();
		XMLEvent event = reader.nextEvent();
		while(event != null && (!isBodyStartElement(event))) {
			result.add(event);
			event = reader.nextEvent();
		}
		
		// add the body start tag to the result
		if(event != null) {
			result.add(event);
		}
		return result;
	}

	private List<XMLEvent> getFooterElements() throws XMLStreamException {
		List<XMLEvent> result = new ArrayList<XMLEvent>();
		result.add(xmlEventFactory.createEndElement("", null, "body"));
		result.add(xmlEventFactory.createEndElement("", null, "html"));
		result.add(xmlEventFactory.createEndDocument());
		return result;
	}

	private static boolean isBodyStartElement(XMLEvent xmlEvent) {
		return xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equals("body");
	}

	private static boolean isBodyEndElement(XMLEvent xmlEvent) {
		return xmlEvent.isEndElement() && xmlEvent.asEndElement().getName().getLocalPart().equals("body");
	}
}
