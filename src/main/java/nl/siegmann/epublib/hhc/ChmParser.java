package nl.siegmann.epublib.hhc;

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

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.FileResource;
import nl.siegmann.epublib.Resource;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Section;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.StringUtils;

public class ChmParser {

	public static final String DEFAULT_HTML_INPUT_ENCODING = "Windows-1251";
//	private String htmlInputEncoding = DEFAULT_HTML_ENCODING;
	
	private static class ItemIdGenerator {
		private int itemCounter = 1;
		
		public String getNextItemId() {
			return "item_" + itemCounter++;
		}
	}
	
	public static Book parseChm(File chmRootDir)
			throws IOException, ParserConfigurationException,
			XPathExpressionException {
		Book result = new Book();
		result.setTitle(findTitle(chmRootDir));
		File hhcFile = findHhcFile(chmRootDir);
		if(hhcFile == null) {
			throw new IllegalArgumentException("No index file found in directory " + chmRootDir.getAbsolutePath() + ". (Looked for file ending with extension '.hhc'");
		}
		ItemIdGenerator itemIdGenerator = new ItemIdGenerator();
		Map<String, Resource> resources = findResources(itemIdGenerator, chmRootDir);
		List<Section> sections = HHCParser.parseHhc(new FileInputStream(hhcFile));
		result.setSections(sections);
		result.setResources(resources.values());
		return result;
	}
	

	/**
	 * Finds in the '#SYSTEM' file the 3rd set of characters that have ascii value >= 32 and <= 126 and is more than 3 characters long.
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
						if(lineCounter >= 3) {
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
	private static Map<String, Resource> findResources(ItemIdGenerator itemIdGenerator, File rootDir) throws IOException {
		Map<String, Resource> result = new LinkedHashMap<String, Resource>();
		Iterator<File> fileIter = FileUtils.iterateFiles(rootDir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
		while(fileIter.hasNext()) {
			File file = fileIter.next();
//			System.out.println("file:" + file);
			if(file.isDirectory()) {
				continue;
			}
			String mediaType = determineMediaType(file.getName());
			if(StringUtils.isBlank(mediaType)) {
				continue;
			}
			String href = file.getCanonicalPath().substring(rootDir.getCanonicalPath().length() + 1);
			FileResource fileResource = new FileResource(itemIdGenerator.getNextItemId(), file, href, mediaType);
			if(mediaType.equals(Constants.MediaTypes.xhtml)) {
				fileResource.setInputEncoding(DEFAULT_HTML_INPUT_ENCODING);
			}
			result.put(fileResource.getHref(), fileResource);
		}
		return result;
	}

	/**
	 * Determines the files mediatype based on its file extension.
	 * 
	 * @param filename
	 * @return
	 */
	private static String determineMediaType(String filename) {
		String result = "";
		filename = filename.toLowerCase();
		if (filename.endsWith(".html") || filename.endsWith(".htm")) {
			result = Constants.MediaTypes.xhtml;
		} else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
			result = "image/jpeg";
		} else if (filename.endsWith(".png")) {
			result = "image/png";
		} else if (filename.endsWith(".gif")) {
			result = "image/gif";
		} else if (filename.endsWith(".css")) {
			result = "text/css";
		}
		return result;
	}
}
