package nl.siegmann.epublib;

import java.io.File;
import java.io.FileOutputStream;

import nl.siegmann.epublib.bookprocessor.XslBookProcessor;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.hhc.ChmParser;

import org.apache.commons.lang.StringUtils;

public class Chm2Epub {

	public static void main(String[] args) throws Exception {
		String inputDir = "";
		String resultFile = "";
		String xslFile = "";
		for(int i = 0; i < args.length; i++) {
			if(args[i].equals("--in")) {
				inputDir = args[++i];
			} else if(args[i].equals("--result")) {
				resultFile = args[++i];
			} else if(args[i].equals("--xsl")) {
				xslFile = args[++i];
			}
		}
		if(StringUtils.isBlank(inputDir) || StringUtils.isBlank(resultFile)) {
			usage();
		}
		EpubWriter epubWriter = new EpubWriter();
		epubWriter.getBookProcessingPipeline().add(new XslBookProcessor(xslFile));
		Book book = ChmParser.parseChm(new File(inputDir));
		epubWriter.write(book, new FileOutputStream(resultFile));
	}

	private static void usage() {
		System.out.println(Chm2Epub.class.getName() + " --in [input directory] --result [resulting epub file] --xsl [html post processing file]");
	}
}
