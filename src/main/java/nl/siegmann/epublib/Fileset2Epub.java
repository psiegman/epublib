package nl.siegmann.epublib;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;

import nl.siegmann.epublib.bookprocessor.CoverpageBookProcessor;
import nl.siegmann.epublib.bookprocessor.XslBookProcessor;
import nl.siegmann.epublib.chm.ChmParser;
import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.FileResource;
import nl.siegmann.epublib.domain.Identifier;
import nl.siegmann.epublib.epub.EpubWriter;
import nl.siegmann.epublib.fileset.FilesetBookCreator;

import org.apache.commons.lang.StringUtils;

public class Fileset2Epub {

	public static void main(String[] args) throws Exception {
		String inputDir = "";
		String resultFile = "";
		String xslFile = "";
		String coverImage = "";
		String title = "";
		String author = "";
		String type = "";
		String isbn = "";
		String encoding = "";

		for(int i = 0; i < args.length; i++) {
			if(args[i].equalsIgnoreCase("--in")) {
				inputDir = args[++i];
			} else if(args[i].equalsIgnoreCase("--result")) {
				resultFile = args[++i];
			} else if(args[i].equalsIgnoreCase("--encoding")) {
				encoding = args[++i];
			} else if(args[i].equalsIgnoreCase("--xsl")) {
				xslFile = args[++i];
			} else if(args[i].equalsIgnoreCase("--cover-image")) {
				coverImage = args[++i];
			} else if(args[i].equalsIgnoreCase("--author")) {
				author = args[++i];
			} else if(args[i].equalsIgnoreCase("--title")) {
				title = args[++i];
			} else if(args[i].equalsIgnoreCase("--isbn")) {
				isbn = args[++i];
			} else if(args[i].equalsIgnoreCase("--type")) {
				type = args[++i];
			}
		}
		if(StringUtils.isBlank(inputDir) || StringUtils.isBlank(resultFile)) {
			usage();
		}
		EpubWriter epubWriter = new EpubWriter();

		if(! StringUtils.isBlank(xslFile)) {
			epubWriter.getBookProcessingPipeline().add(new XslBookProcessor(xslFile));
		}
		
		Book book;
		if("chm".equals(type)) {
			book = ChmParser.parseChm(new File(inputDir), encoding);
		} else {
			book = FilesetBookCreator.createBookFromDirectory(new File(inputDir));
		}
		
		if(StringUtils.isNotBlank(coverImage)) {
			book.setCoverImage(new FileResource(new File(coverImage)));
			epubWriter.getBookProcessingPipeline().add(new CoverpageBookProcessor());
		}
		
		if(StringUtils.isNotBlank(title)) {
			book.getMetadata().addTitle(title);
		}
		
		if(StringUtils.isNotBlank(isbn)) {
			book.getMetadata().addIdentifier(new Identifier(Identifier.Scheme.ISBN, isbn));
		}
		
		if(StringUtils.isNotBlank(author)) {
			String[] authorNameParts = author.split(",");
			Author authorObject = null;
			if(authorNameParts.length > 1) {
				authorObject = new Author(authorNameParts[1], authorNameParts[0]);
			} else if(authorNameParts.length > 0) {
				authorObject = new Author(authorNameParts[0]);
			}
			
			if(authorObject != null) {
				book.getMetadata().setAuthors(Arrays.asList(new Author[] {authorObject}));
			}
		}
		epubWriter.write(book, new FileOutputStream(resultFile));
	}

	private static void usage() {
		System.out.println(Fileset2Epub.class.getName() + " --in [input directory] --title [book title] --author [lastname,firstname] --isbn [isbn number] --result [resulting epub file] --xsl [html post processing file] --cover-image [image to use as cover] --type [input type, can be 'chm' or empty]");
		System.exit(0);
	}
}