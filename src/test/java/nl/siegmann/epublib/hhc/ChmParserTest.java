package nl.siegmann.epublib.hhc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;

import junit.framework.TestCase;
import nl.siegmann.epublib.bookprocessor.XslBookProcessor;
import nl.siegmann.epublib.chm.ChmParser;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubWriter;

public class ChmParserTest extends TestCase {

	public void test1() {
		try {
//			String root = "/home/paul/project/veh/backbase/Backbase_Rich_Portal_4.1/documentation/client/Reference/ref/";
//			String root = "/home/paul/project/private/library/chm/peaa/";dev
//			String root = "/home/paul/download/python_man";
//			String root = "/home/paul/download/blender_man_chm";
			String root = "";
			String result = "";
			String xsl = "";
			root = "/home/paul/project/veh/morello/Morello_5.8/smart_client";
			root = "/home/paul/download/realworld"; result = "/home/paul/realworld.epub";
			root = "/home/paul/download/python_man"; result = "/home/paul/pythonman.epub";
			root = "/home/paul/download/tmp/ccs"; result = "/home/paul/ccs.epub"; xsl = "/home/paul/ccs.xsl";
			EpubWriter epubWriter = new EpubWriter();
			if(! StringUtils.isBlank(xsl)) {
				try {
					epubWriter.getBookProcessingPipeline().add(new XslBookProcessor(xsl));
				} catch (TransformerConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Book book = ChmParser.parseChm(new File(root));
			epubWriter.write(book, new FileOutputStream(result));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	public void test2() {
//		try {
////			String root = "/home/paul/project/veh/backbase/Backbase_Rich_Portal_4.1/documentation/client/Reference/ref/";
//			String root = "/home/paul/project/private/library/chm/peaa/";
////			String root = "/home/paul/download/python_man";
////			String root = "/home/paul/download/blender_man_chm";
//			String title = HHCParser.findTitle(new File(root));
//			System.out.println("title:" + title);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}