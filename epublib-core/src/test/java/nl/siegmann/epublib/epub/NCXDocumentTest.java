package nl.siegmann.epublib.epub;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.GuideReference;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.service.MediatypeService;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class NCXDocumentTest {

    byte[] ncxData;

    public NCXDocumentTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws IOException {
        ncxData = FileUtils.readFileToByteArray(new File("src/test/resources/toc.xml"));
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of read method, of class NCXDocument.
     */
    @Test
    public void testReadWithNonRootLevelTOC() {
        
        // If the tox.ncx file is not in the root, the hrefs it refers to need to preserve its path.
        Book book = new Book();
        Resource ncxResource = new Resource(ncxData, "xhtml/toc.ncx");
        Resource chapterResource = new Resource("id1", "Hello, world !".getBytes(), "xhtml/chapter1.html", MediatypeService.XHTML);
        book.addResource(chapterResource);
        book.getSpine().addResource(chapterResource);

        book.setNcxResource(ncxResource);
        book.getSpine().setTocResource(ncxResource);

        NCXDocument.read(book, new EpubReader());
        assertEquals("xhtml/chapter1.html", book.getTableOfContents().getTocReferences().get(0).getCompleteHref());
    }
}
