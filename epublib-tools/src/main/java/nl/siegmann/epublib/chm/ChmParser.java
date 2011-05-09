package nl.siegmann.epublib.chm;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.MediaType;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Resources;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.domain.TableOfContents;
import nl.siegmann.epublib.service.MediatypeService;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs.AllFileSelector;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;

/**
 * Reads the files that are extracted from a windows help ('.chm') file and creates a epublib Book out of it.
 * 
 * @author paul
 *
 */
public class ChmParser {

	public static final String DEFAULT_CHM_HTML_INPUT_ENCODING = "windows-1252";
	public static final int MINIMAL_SYSTEM_TITLE_LENGTH = 4;
	
	public static Book parseChm(FileObject chmRootDir) throws XPathExpressionException, IOException, ParserConfigurationException {
		return parseChm(chmRootDir, DEFAULT_CHM_HTML_INPUT_ENCODING);
	}

	public static Book parseChm(FileObject chmRootDir, String htmlEncoding)
			throws IOException, ParserConfigurationException,
			XPathExpressionException {
		Book result = new Book();
		result.getMetadata().addTitle(findTitle(chmRootDir));
		FileObject hhcFileObject = findHhcFileObject(chmRootDir);
		if(hhcFileObject == null) {
			throw new IllegalArgumentException("No index file found in directory " + chmRootDir + ". (Looked for file ending with extension '.hhc'");
		}
		if(htmlEncoding == null) {
			htmlEncoding = DEFAULT_CHM_HTML_INPUT_ENCODING;
		}
		Resources resources = findResources(chmRootDir, htmlEncoding);
		List<TOCReference> tocReferences = HHCParser.parseHhc(hhcFileObject.getContent().getInputStream(), resources);
		result.setTableOfContents(new TableOfContents(tocReferences));
		result.setResources(resources);
		result.generateSpineFromTableOfContents();
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
	protected static String findTitle(FileObject chmRootDir) throws IOException {
		FileObject systemFileObject = chmRootDir.resolveFile("#SYSTEM");
		InputStream in = systemFileObject.getContent().getInputStream();
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
	
	private static FileObject findHhcFileObject(FileObject chmRootDir) throws FileSystemException {
		FileObject[] files = chmRootDir.getChildren();
		for(int i = 0; i < files.length; i++) {
			if("hhc".equalsIgnoreCase(files[i].getName().getExtension())) {
				return files[i];
			}
		}
		return null;
	}
	
	
	@SuppressWarnings("unchecked")
	private static Resources findResources(FileObject rootDir, String defaultEncoding) throws IOException {
		Resources result = new Resources();
		FileObject[] allFiles = rootDir.findFiles(new AllFileSelector());
		for(int i = 0; i < allFiles.length; i++) {
			FileObject file = allFiles[i];
			if (file.getType() == FileType.FOLDER) {
				continue;
			}
			MediaType mediaType = MediatypeService.determineMediaType(file.getName().getBaseName()); 
			if(mediaType == null) {
				continue;
			}
			String href = file.getName().toString().substring(rootDir.getName().toString().length() + 1);
			Resource fileResource = new Resource(null, IOUtils.toByteArray(file.getContent().getInputStream()), href, mediaType);
			if(mediaType == MediatypeService.XHTML) {
				fileResource.setInputEncoding(defaultEncoding);
			}
			result.add(fileResource);
		}
		return result;
	}
}
