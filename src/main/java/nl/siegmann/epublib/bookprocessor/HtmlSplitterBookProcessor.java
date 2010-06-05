package nl.siegmann.epublib.bookprocessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Section;
import nl.siegmann.epublib.epub.EpubWriter;
import nl.siegmann.epublib.service.MediatypeService;

public class HtmlSplitterBookProcessor implements BookProcessor {

	@Override
	public Book processBook(Book book, EpubWriter epubWriter) {
		processSections(book, book.getSpineSections());
		return book;
	}

	private List<Section> processSections(Book book, List<Section> sections) {
		List<Section> result = new ArrayList<Section>(sections.size());
		for(Section section: sections) {
			List<Section> children = processSections(book, section.getChildren());
			List<Section> foo = splitSection(section, book);
			if(foo.size() > 1) {
				foo.get(0).setChildren(new ArrayList<Section>());
			}
			foo.get(foo.size() - 1).setChildren(children);
			result.addAll(foo);
		}
		return result;
	}

	private List<Section> splitSection(Section section, Book book) {
		Resource resource = book.getResourceByHref(section.getHref());
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
