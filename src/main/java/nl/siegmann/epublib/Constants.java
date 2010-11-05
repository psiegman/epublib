package nl.siegmann.epublib;

import java.nio.charset.Charset;


public interface Constants {
	Charset ENCODING = Charset.forName("UTF-8");
	String NAMESPACE_XHTML = "http://www.w3.org/1999/xhtml";
	String EPUBLIB_GENERATOR_NAME = "EPUBLib version 1.1";
	String FRAGMENT_SEPARATOR = "#";
	String DEFAULT_TOC_ID = "toc";
}
