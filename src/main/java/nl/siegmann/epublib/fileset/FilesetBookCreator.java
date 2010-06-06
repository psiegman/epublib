package nl.siegmann.epublib.fileset;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.FileResource;
import nl.siegmann.epublib.domain.MediaType;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Section;
import nl.siegmann.epublib.service.MediatypeService;

/**
 * Creates a Book from a collection of html and image files.
 * 
 * @author paul
 *
 */
public class FilesetBookCreator {
	
	private static Comparator<File> fileComparator = new Comparator<File>(){
		@Override
		public int compare(File o1, File o2) {
			return o1.getName().compareToIgnoreCase(o2.getName());
		}
	};
	
	/**
	 * Recursively adds all files that are allowed to be part of an epub to the Book.
	 * 
	 * @see nl.siegmann.epublib.domain.MediaTypeService
	 * @param rootDirectory
	 * @return
	 * @throws IOException
	 */
	public static Book createBookFromDirectory(File rootDirectory) throws IOException {
		Book result = new Book();
		List<Section> sections = new ArrayList<Section>();
		List<Resource> resources = new ArrayList<Resource>();
		processDirectory(rootDirectory, rootDirectory, sections, resources);
		result.getResources().set(resources);
		result.setSections(sections);
		return result;
	}

	private static void processDirectory(File rootDir, File directory, List<Section> sections, List<Resource> resources) throws IOException {
		File[] files = directory.listFiles();
		Arrays.sort(files, fileComparator);
		for(int i = 0; i < files.length; i++) {
			File file = files[i];
			if(file.isDirectory()) {
				processSubdirectory(rootDir, file, sections, resources);
			} else {
				Resource resource = createResource(rootDir, file);
				if(resource != null) {
					resources.add(resource);
					if(MediatypeService.XHTML == resource.getMediaType()) {
						Section section = new Section(file.getName(), resource.getHref());
						sections.add(section);
					}
				}
			}
		}
	}

	private static void processSubdirectory(File rootDir, File file,
			List<Section> sections, List<Resource> resources)
			throws IOException {
		List<Section> childSections = new ArrayList<Section>();
		processDirectory(rootDir, file, childSections, resources);
		if(! childSections.isEmpty()) {
			Section section = new Section(file.getName(), calculateHref(rootDir,file));
			section.setChildren(childSections);
			sections.add(section);
		}
	}

	
	private static Resource createResource(File rootDir, File file) throws IOException {
		MediaType mediaType = MediatypeService.determineMediaType(file.getName());
		if(mediaType == null) {
			return null;
		}
		String href = calculateHref(rootDir, file);
		Resource result = new FileResource(null, file, href, mediaType);
		return result;
	}
	
	private static String calculateHref(File rootDir, File currentFile) throws IOException {
		return currentFile.getCanonicalPath().substring(rootDir.getCanonicalPath().length() + 1);
	}
}
