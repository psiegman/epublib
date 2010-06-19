package nl.siegmann.epublib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import nl.siegmann.epublib.bookprocessor.CoverpageBookProcessor;
import nl.siegmann.epublib.bookprocessor.XslBookProcessor;
import nl.siegmann.epublib.chm.ChmParser;
import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.FileResource;
import nl.siegmann.epublib.domain.Identifier;
import nl.siegmann.epublib.epub.EpubReader;
import nl.siegmann.epublib.epub.EpubWriter;
import nl.siegmann.epublib.fileset.FilesetBookCreator;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

public class Fileset2Epub {

	public static void main(String[] args) throws Exception {
		String inputDir = "";
		String outFile = "";
		String xslFile = "";
		String coverImage = "";
		String title = "";
		List<String> authorNames = new ArrayList<String>();
		String type = "";
		String isbn = "";
		String encoding = Constants.ENCODING.name();

		for(int i = 0; i < args.length; i++) {
			if(args[i].equalsIgnoreCase("--in")) {
				inputDir = args[++i];
			} else if(args[i].equalsIgnoreCase("--out")) {
				outFile = args[++i];
			} else if(args[i].equalsIgnoreCase("--encoding")) {
				encoding = args[++i];
			} else if(args[i].equalsIgnoreCase("--xsl")) {
				xslFile = args[++i];
			} else if(args[i].equalsIgnoreCase("--cover-image")) {
				coverImage = args[++i];
			} else if(args[i].equalsIgnoreCase("--author")) {
				authorNames.add(args[++i]);
			} else if(args[i].equalsIgnoreCase("--title")) {
				title = args[++i];
			} else if(args[i].equalsIgnoreCase("--isbn")) {
				isbn = args[++i];
			} else if(args[i].equalsIgnoreCase("--type")) {
				type = args[++i];
			}
		}
		if(StringUtils.isBlank(inputDir) || StringUtils.isBlank(outFile)) {
			usage();
		}
		EpubWriter epubWriter = new EpubWriter();

		if(! StringUtils.isBlank(xslFile)) {
			epubWriter.getBookProcessingPipeline().add(new XslBookProcessor(xslFile));
		}
		
		Book book;
		if("chm".equals(type)) {
			book = ChmParser.parseChm(new File(inputDir), Charset.forName(encoding));
		} else if ("epub".equals(type)) {
			book = new EpubReader().readEpub(new FileInputStream(inputDir));
		} else {
			book = FilesetBookCreator.createBookFromDirectory(new File(inputDir), Charset.forName(encoding));
		}
		
		if(StringUtils.isNotBlank(coverImage)) {
//			book.getResourceByHref(book.getCoverImage());
			book.setCoverImage(new FileResource(new File(coverImage)));
			epubWriter.getBookProcessingPipeline().add(new CoverpageBookProcessor());
		}
		
		if(StringUtils.isNotBlank(title)) {
			List<String> titles = new ArrayList<String>();
			titles.add(title);
			book.getMetadata().setTitles(titles);
		}
		
		if(StringUtils.isNotBlank(isbn)) {
			book.getMetadata().addIdentifier(new Identifier(Identifier.Scheme.ISBN, isbn));
		}
		
		initAuthors(authorNames, book);
		
		epubWriter.write(book, new FileOutputStream(outFile));
	}

	private static void initAuthors(List<String> authorNames, Book book) {
		if(CollectionUtils.isEmpty(authorNames)) {
			return;
		}
		List<Author> authorObjects = new ArrayList<Author>();
		for(String authorName: authorNames) {
			String[] authorNameParts = authorName.split(",");
			Author authorObject = null;
			if(authorNameParts.length > 1) {
				authorObject = new Author(authorNameParts[1], authorNameParts[0]);
			} else if(authorNameParts.length > 0) {
				authorObject = new Author(authorNameParts[0]);
			}
			authorObjects.add(authorObject);
		}
		book.getMetadata().setAuthors(authorObjects);
	}

	
	private static void usage() {
		System.out.println("usage: " + Fileset2Epub.class.getName() 
				+ "\n  --author [lastname,firstname]"
				+ "\n  --cover-image [image to use as cover]"
				+ "\n  --ecoding [text encoding]  # The encoding of the input html files. If funny characters show"
				+ "\n                             # up in the result try 'iso-8859-1', 'windows-1252' or 'utf-8'"
				+ "\n                             # If that doesn't work try to find an appropriate one from"
				+ "\n                             # this list: http://en.wikipedia.org/wiki/Character_encoding"
				+ "\n  --in [input directory]"
				+ "\n  --isbn [isbn number]"
				+ "\n  --out [output epub file]"
				+ "\n  --title [book title]"
				+ "\n  --type [input type, can be 'epub', 'chm' or empty]"
				+ "\n  --xsl [html post processing file]"
				);
		System.exit(0);
	}
}