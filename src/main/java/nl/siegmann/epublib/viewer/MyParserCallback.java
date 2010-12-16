package nl.siegmann.epublib.viewer;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.smartcardio.ATR;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;

class MyParserCallback extends ParserCallback {
	private ParserCallback parserCallback;
	private List<String> stylesheetHrefs = new ArrayList<String>();
	
	public MyParserCallback(ParserCallback parserCallback) {
		this.parserCallback = parserCallback;
	}

	public List<String> getStylesheetHrefs() {
		return stylesheetHrefs;
	}
	
	public void setStylesheetHrefs(List<String> stylesheetHrefs) {
		this.stylesheetHrefs = stylesheetHrefs;
	}
	
	private boolean isStylesheetLink(Tag tag, MutableAttributeSet attributes) {
		return ((tag == Tag.LINK)
				&& (attributes.containsAttribute(HTML.Attribute.REL, "stylesheet"))
				&& (attributes.containsAttribute(HTML.Attribute.TYPE, "text/css")));
	}
	
	
	private void handleStylesheet(Tag tag, MutableAttributeSet attributes) {
		if (isStylesheetLink(tag, attributes)) {
			stylesheetHrefs.add(attributes.getAttribute(HTML.Attribute.HREF).toString());
		}
	}
	
	public int hashCode() {
		return parserCallback.hashCode();
	}

	public boolean equals(Object obj) {
		return parserCallback.equals(obj);
	}

	public String toString() {
		return parserCallback.toString();
	}

	public void flush() throws BadLocationException {
		parserCallback.flush();
	}

	public void handleText(char[] data, int pos) {
		parserCallback.handleText(data, pos);
	}

	public void handleComment(char[] data, int pos) {
		parserCallback.handleComment(data, pos);
	}

	public void handleStartTag(Tag t, MutableAttributeSet a, int pos) {
		handleStylesheet(t, a);
		parserCallback.handleStartTag(t, a, pos);
	}

	public void handleEndTag(Tag t, int pos) {
		parserCallback.handleEndTag(t, pos);
	}

	public void handleSimpleTag(Tag t, MutableAttributeSet a, int pos) {
		handleStylesheet(t, a);
		parserCallback.handleSimpleTag(t, a, pos);
	}

	public void handleError(String errorMsg, int pos) {
		parserCallback.handleError(errorMsg, pos);
	}

	public void handleEndOfLineString(String eol) {
		parserCallback.handleEndOfLineString(eol);
	}
}