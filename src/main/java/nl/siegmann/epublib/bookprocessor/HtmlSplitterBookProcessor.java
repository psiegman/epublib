package nl.siegmann.epublib.bookprocessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Section;
import nl.siegmann.epublib.epub.EpubWriter;
import nl.siegmann.epublib.service.MediatypeService;

public class HtmlSplitterBookProcessor implements BookProcessor {

	@Override
	public Book processBook(Book book, EpubWriter epubWriter) {
		processSections(book, book.getSpineSections(), BookProcessorUtil.createResourceByHrefMap(book));
		return book;
	}

	private List<Section> processSections(Book book, List<Section> sections, Map<String, Resource> resources) {
		List<Section> result = new ArrayList<Section>(sections.size());
		for(Section section: sections) {
			List<Section> children = processSections(book, section.getChildren(), resources);
			List<Section> foo = splitSection(section, resources);
			if(foo.size() > 1) {
				foo.get(0).setChildren(new ArrayList<Section>());
			}
			foo.get(foo.size() - 1).setChildren(children);
			result.addAll(foo);
		}
		return result;
	}

	private List<Section> splitSection(Section section,
			Map<String, Resource> resources) {
		Resource resource = BookProcessorUtil.getResourceByHref(section.getHref(), resources);
		List<Section> result = Arrays.asList(new Section[] {section});
		if(resource == null || (resource.getMediaType() != MediatypeService.XHTML)) {
			return result;
		}
		List<Resource> splitResources = splitHtml(resource);
		if(splitResources.size() == 1) {
			return result;
		}
		
		// So ok, the resource file is apparently split into several pieces.
		// hmm. now what ?
		
		// TODO Auto-generated method stub
		return null;
	}

	
	List<Resource> splitHtml(Resource resource) {
		return Arrays.asList(new Resource[] {resource});
	}
}
