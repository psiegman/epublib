package nl.siegmann.epublib.chm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.FileResource;
import nl.siegmann.epublib.domain.MediaType;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Section;
import nl.siegmann.epublib.service.MediatypeService;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.StringUtils;

/**
 * Reads the files that are extracted from a windows help ('.chm') file and creates a epublib Book out of it.
 * 
 * @author paul
 *
 */
public class ChmParser {

	public static final String DEFAULT_CHM_HTML_INPUT_ENCODING = "Windows-1251";
	public static final int MINIMAL_SYSTEM_TITLE_LENGTH = 4;
	
	public static Book parseChm(File chmRootDir) throws XPathExpressionException, IOException, ParserConfigurationException {
		return parseChm(chmRootDir, DEFAULT_CHM_HTML_INPUT_ENCODING);
	}

	public static Book parseChm(File chmRootDir, String htmlEncoding)
			throws IOException, ParserConfigurationException,
			XPathExpressionException {
		Book result = new Book();
		result.getMetadata().addTitle(findTitle(chmRootDir));
		File hhcFile = findHhcFile(chmRootDir);
		if(hhcFile == null) {
			throw new IllegalArgumentException("No index file found in directory " + chmRootDir.getAbsolutePath() + ". (Looked for file ending with extension '.hhc'");
		}
		if(StringUtils.isBlank(htmlEncoding)) {
			htmlEncoding = DEFAULT_CHM_HTML_INPUT_ENCODING;
		}
		Map<String, Resource> resources = findResources(chmRootDir, htmlEncoding);
		List<Section> sections = HHCParser.parseHhc(new FileInputStream(hhcFile));
		result.setSections(sections);
		result.getResources().set(resources);
		return result;
	}
	

	/**
	 * Finds in the '#SYSTEM' file the 3rd set of characters that have ascii value >= 32 and <= 126 and is more than 3 characters long.
	 * Assumes that that is then the title of the book.
	 * 
	 * @param chmRootDir
	 * @return
	 * @throws IOException
	 */
	protected static String findTitle(File chmRootDir) throws IOException {
		File systemFile = new File(chmRootDir.getAbsolutePath() + File.separatorChar + "#SYSTEM");
		InputStream in = new FileInputStream(systemFile);
		boolean inText = false;
		int lineCounter = 0;
		StringBuilder line = new StringBuilder();
		for(int c = in.read(); c >= 0; c = in.read()) {
			if(c >= 32 && c <= 126) {
				line.append((char) c);
				inText = true;
			} else {
				if(inText) {
					if(line.length() >= 3) {
						lineCounter++;
						if(lineCounter >= MINIMAL_SYSTEM_TITLE_LENGTH) {
							return line.toString();
						}
					}
					line = new StringBuilder();
				}
				inText = false;
			}
		}
		return "<unknown title>";
	}
	
	private static File findHhcFile(File chmRootDir) {
		File[] files = chmRootDir.listFiles();
		for(int i = 0; i < files.length; i++) {
			if(StringUtils.endsWithIgnoreCase(files[i].getName(), ".hhc")) {
				return files[i];
			}
		}
		return null;
	}
	
	

	@SuppressWarnings("unchecked")
	private static Map<String, Resource> findResources(File rootDir, String defaultEncoding) throws IOException {
		Map<String, Resource> result = new LinkedHashMap<String, Resource>();
		Iterator<File> fileIter = FileUtils.iterateFiles(rootDir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
		while(fileIter.hasNext()) {
			File file = fileIter.next();
//			System.out.println("file:" + file);
			if(file.isDirectory()) {
				continue;
			}
			MediaType mediaType = MediatypeService.determineMediaType(file.getName()); 
			if(mediaType == null) {
				continue;
			}
			String href = file.getCanonicalPath().substring(rootDir.getCanonicalPath().length() + 1);
			FileResource fileResource = new FileResource(null, file, href, mediaType);
			if(mediaType == MediatypeService.XHTML) {
				fileResource.setInputEncoding(defaultEncoding);
			}
			result.put(fileResource.getHref(), fileResource);
		}
		return result;
	}
}
