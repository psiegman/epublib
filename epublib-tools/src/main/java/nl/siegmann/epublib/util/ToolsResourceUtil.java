package nl.siegmann.epublib.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.domain.MediaType;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubProcessorSupport;
import nl.siegmann.epublib.service.MediatypeService;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Various resource utility methods
 * 
 * @author paul
 *
 */
public class ToolsResourceUtil {
	
	private static Logger log = LoggerFactory.getLogger(ToolsResourceUtil.class);

	
	public static String getTitle(Resource resource) {
		if (resource == null) {
			return "";
		}
		if (resource.getMediaType() != MediatypeService.XHTML) {
			return resource.getHref();
		}
		String title = findTitleFromXhtml(resource);
		if (title == null) {
			title = "";
		}
		return title;
	}

	

	
	/**
	 * Retrieves whatever it finds between <title>...</title> or <h1-7>...</h1-7>.
	 * The first match is returned, even if it is a blank string.
	 * If it finds nothing null is returned.
	 * @param resource
	 * @return
	 */
	public static String findTitleFromXhtml(Resource resource) {
		if (resource == null) {
			return "";
		}
		if (resource.getTitle() != null) {
			return resource.getTitle();
		}
		Pattern h_tag = Pattern.compile("^h\\d\\s*", Pattern.CASE_INSENSITIVE);
		String title = null;
		try {
			Reader content = resource.getReader();
			Scanner scanner = new Scanner(content);
			scanner.useDelimiter("<");
			while(scanner.hasNext()) {
				String text = scanner.next();
				int closePos = text.indexOf('>');
				String tag = text.substring(0, closePos);
				if (tag.equalsIgnoreCase("title")
					|| h_tag.matcher(tag).find()) {

					title = text.substring(closePos + 1).trim();
					title = StringEscapeUtils.unescapeHtml(title);
					break;
				}
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		resource.setTitle(title);
		return title;
	}
}
