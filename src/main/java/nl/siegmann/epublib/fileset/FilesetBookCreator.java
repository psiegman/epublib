package nl.siegmann.epublib.fileset;


import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.FileObjectResource;
import nl.siegmann.epublib.domain.MediaType;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Section;
import nl.siegmann.epublib.service.MediatypeService;

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
	
	
	public static Book createBookFromDirectory(File rootDirectory) throws IOException {
		return createBookFromDirectory(rootDirectory, Charset.defaultCharset());	
	}
	
	
	public static Book createBookFromDirectory(File rootDirectory, Charset encoding) throws IOException {
		FileObject rootFileObject = VFS.getManager().resolveFile("file:" + rootDirectory.getCanonicalPath());
		return createBookFromDirectory(rootFileObject, encoding);
	}
	
	/**
	 * Recursively adds all files that are allowed to be part of an epub to the Book.
	 * 
	 * @see nl.siegmann.epublib.domain.MediaTypeService
	 * @param rootDirectory
	 * @return
	 * @throws IOException
	 */
	public static Book createBookFromDirectory(FileObject rootDirectory, Charset encoding) throws IOException {
		Book result = new Book();
		List<Section> sections = new ArrayList<Section>();
		List<Resource> resources = new ArrayList<Resource>();
		processDirectory(rootDirectory, rootDirectory, sections, resources, encoding);
		result.getResources().set(resources);
		result.setSections(sections);
		return result;
	}

	private static void processDirectory(FileObject rootDir, FileObject directory, List<Section> sections, List<Resource> resources, Charset inputEncoding) throws IOException {
		FileObject[] files = directory.getChildren();
		Arrays.sort(files, fileComparator);
		for(int i = 0; i < files.length; i++) {
			FileObject file = files[i];
			if(file.getType() == FileType.FOLDER) {
				processSubdirectory(rootDir, file, sections, resources, inputEncoding);
			} else {
				Resource resource = createResource(rootDir, file, inputEncoding);
				if(resource == null) {
					continue;
				}
				resources.add(resource);
				if(MediatypeService.XHTML == resource.getMediaType()) {
					Section section = new Section(file.getName().getBaseName(), resource.getHref());
					sections.add(section);
				}
			}
		}
	}

	private static void processSubdirectory(FileObject rootDir, FileObject file,
			List<Section> sections, List<Resource> resources, Charset inputEncoding)
			throws IOException {
		List<Section> childSections = new ArrayList<Section>();
		processDirectory(rootDir, file, childSections, resources, inputEncoding);
		if(! childSections.isEmpty()) {
			Section section = new Section(file.getName().getBaseName(), calculateHref(rootDir,file));
			section.setChildren(childSections);
			sections.add(section);
		}
	}

	private static Resource createResource(FileObject rootDir, FileObject file, Charset inputEncoding) throws IOException {
		MediaType mediaType = MediatypeService.determineMediaType(file.getName().getBaseName());
		if(mediaType == null) {
			return null;
		}
		String href = calculateHref(rootDir, file);
		Resource result = new FileObjectResource(null, file, href, mediaType);
		result.setInputEncoding(inputEncoding);
		return result;
	}
	
	private static String calculateHref(FileObject rootDir, FileObject currentFile) throws IOException {
		String result = currentFile.getName().toString().substring(rootDir.getName().toString().length() + 1);
		return result;
	}
}
