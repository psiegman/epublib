package nl.siegmann.epublib.domain;

import nl.siegmann.epublib.Constants;

import org.apache.commons.lang.StringUtils;


/**
 * These are references to elements of the book's guide.
 * The guide points to resources in the book by function ('cover', 'index', 'acknowledgements', etc).
 * It is an optional part of an epub, and support for the various types of references varies by reader.
 * The only part of this that is heavily used is the cover page.
 * 
 * @author paul
 *
 */
public class GuideReference extends ResourceReference {
	
	public static String COVER = "cover"; //  	 the book cover(s), jacket information, etc.
	public static String TITLE_PAGE = "title-page"; // 	page with possibly title, author, publisher, and other metadata
	public static String TOC = "toc"; // 	table of contents
	public static String INDEX = "index"; // 	back-of-book style index
	public static String GLOSSARY = "glossary"; 	
	public static String ACKNOWLEDGEMENTS = "acknowledgements"; 	
	public static String BIBLIOGRAPHY = "bibliography"; 	
	public static String COLOPHON = "colophon";
	public static String COPYRIGHT_PAGE = "copyright-page"; 	
	public static String DEDICATION = "dedication"; 	
	public static String EPIGRAPH = "epigraph"; 	
	public static String FOREWORD = "foreword"; 	
	public static String LOI = "loi"; // 	list of illustrations
	public static String LOT = "lot"; // 	list of tables
	public static String NOTES = "notes"; 	
	public static String PREFACE = "preface"; 	
	public static String TEXT = "text"; // 	First "real" page of content (e.g. "Chapter 1") 
	
	private String type;
	private String fragmentId;
	
	public GuideReference(Resource resource) {
		this(null, resource);
	}
	
	public GuideReference(String title, Resource resource) {
		super(title, resource);
	}
	
	public GuideReference(Resource resource, String type, String title, String fragmentId) {
		super(title, resource);
		this.type = StringUtils.isNotBlank(type) ? type.toLowerCase() : null;
		this.fragmentId = fragmentId;
	}

	/**
	 * If the fragmentId is blank it returns the resource href, otherwise it returns the resource href + '#' + the fragmentId.
	 * 
	 * @return
	 */
	public String getCompleteHref() {
		if (StringUtils.isBlank(fragmentId)) {
			return resource.getHref();
		} else {
			return resource.getHref() + Constants.FRAGMENT_SEPARATOR + fragmentId;
		}
	}
	
	
	public void setResource(Resource resource, String fragmentId) {
		this.resource = resource;
		this.fragmentId = fragmentId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
