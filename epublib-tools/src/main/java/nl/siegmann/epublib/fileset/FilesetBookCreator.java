package nl.siegmann.epublib.fileset;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.bookprocessor.DefaultBookProcessorPipeline;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Resources;
import nl.siegmann.epublib.domain.Spine;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.domain.TableOfContents;
import nl.siegmann.epublib.epub.BookProcessor;
import nl.siegmann.epublib.service.MediatypeService;
import nl.siegmann.epublib.util.ResourceUtil;
import nl.siegmann.epublib.util.VFSUtil;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.VFS;

/**
 * Creates a Book from a collection of html and image files.
 * 
 * @author paul
 *
 */
public class FilesetBookCreator {
	
	private static Comparator<FileObject> fileComparator = new Comparator<FileObject>(){
		@Override
		public int compare(FileObject o1, FileObject o2) {
			return o1.getName().getBaseName().compareToIgnoreCase(o2.getName().getBaseName());
		}
	};
	
	private static final BookProcessor bookProcessor = new DefaultBookProcessorPipeline();
	
	public static Book createBookFromDirectory(File rootDirectory) throws IOException {
		return createBookFromDirectory(rootDirectory, Constants.CHARACTER_ENCODING);	
	}
	
	
	public static Book createBookFromDirectory(File rootDirectory, String encoding) throws IOException {
		FileObject rootFileObject = VFS.getManager().resolveFile("file:" + rootDirectory.getCanonicalPath());
		return createBookFromDirectory(rootFileObject, encoding);
	}
	
	public static Book createBookFromDirectory(FileObject rootDirectory) throws IOException {
		return createBookFromDirectory(rootDirectory, Constants.CHARACTER_ENCODING);
	}
	
	/**
	 * Recursively adds all files that are allowed to be part of an epub to the Book.
	 * 
	 * @see nl.siegmann.epublib.domain.MediaTypeService
	 * @param rootDirectory
	 * @return
	 * @throws IOException
	 */
	public static Book createBookFromDirectory(FileObject rootDirectory, String encoding) throws IOException {
		Book result = new Book();
		List<TOCReference> sections = new ArrayList<TOCReference>();
		Resources resources = new Resources();
		processDirectory(rootDirectory, rootDirectory, sections, resources, encoding);
		result.setResources(resources);
		TableOfContents tableOfContents = new TableOfContents(sections);
		result.setTableOfContents(tableOfContents);
		result.setSpine(new Spine(tableOfContents));
		
		result = bookProcessor.processBook(result);
		
		return result;
	}

	private static void processDirectory(FileObject rootDir, FileObject directory, List<TOCReference> sections, Resources resources, String inputEncoding) throws IOException {
		FileObject[] files = directory.getChildren();
		Arrays.sort(files, fileComparator);
		for(int i = 0; i < files.length; i++) {
			FileObject file = files[i];
			if(file.getType() == FileType.FOLDER) {
				processSubdirectory(rootDir, file, sections, resources, inputEncoding);
			} else if (MediatypeService.determineMediaType(file.getName().getBaseName()) == null) {
				continue;
			} else {
				Resource resource = VFSUtil.createResource(rootDir, file, inputEncoding);
				if(resource == null) {
					continue;
				}
				resources.add(resource);
				if(MediatypeService.XHTML == resource.getMediaType()) {
					TOCReference section = new TOCReference(file.getName().getBaseName(), resource);
					sections.add(section);
				}
			}
		}
	}

	private static void processSubdirectory(FileObject rootDir, FileObject file,
			List<TOCReference> sections, Resources resources, String inputEncoding)
			throws IOException {
		List<TOCReference> childTOCReferences = new ArrayList<TOCReference>();
		processDirectory(rootDir, file, childTOCReferences, resources, inputEncoding);
		if(! childTOCReferences.isEmpty()) {
			String sectionName = file.getName().getBaseName();
			Resource sectionResource = ResourceUtil.createResource(sectionName, VFSUtil.calculateHref(rootDir,file));
			resources.add(sectionResource);
			TOCReference section = new TOCReference(sectionName, sectionResource);
			section.setChildren(childTOCReferences);
			sections.add(section);
		}
	}

}
