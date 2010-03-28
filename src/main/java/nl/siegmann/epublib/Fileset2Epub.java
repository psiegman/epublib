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
		for(int i = 0; i < args.length; i++) {
			if(args[i].equals("--in")) {
				inputDir = args[++i];
			} else if(args[i].equals("--result")) {
				resultFile = args[++i];
			} else if(args[i].equals("--xsl")) {
				xslFile = args[++i];
			} else if(args[i].equals("--cover-image")) {
				coverImage = args[++i];
			} else if(args[i].equals("--author")) {
				author = args[++i];
			} else if(args[i].equals("--title")) {
				title = args[++i];
			} else if(args[i].equals("--type")) {
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
			book = ChmParser.parseChm(new File(inputDir));
		} else {
			book = FilesetBookCreator.createBookFromDirectory(new File(inputDir));
		}
		
		if(! StringUtils.isBlank(coverImage)) {
			book.setCoverImage(new FileResource(new File(coverImage)));
			epubWriter.getBookProcessingPipeline().add(new CoverpageBookProcessor());
		}
		
		if(! StringUtils.isBlank(title)) {
			book.getMetadata().setTitle(title);
		}

		if(! StringUtils.isBlank(author)) {
			String[] authorNameParts = author.split(",");
			Author authorObject = new Author(authorNameParts[1], authorNameParts[0]);
			book.getMetadata().setAuthors(Arrays.asList(new Author[] {authorObject}));
		}

		epubWriter.write(book, new FileOutputStream(resultFile));
	}

	private static void usage() {
		System.out.println(Fileset2Epub.class.getName() + " --in [input directory] --result [resulting epub file] --xsl [html post processing file] --cover-image [image to use as cover] --type [input type, can be 'chm' or empty]");
		System.exit(0);
	}
}