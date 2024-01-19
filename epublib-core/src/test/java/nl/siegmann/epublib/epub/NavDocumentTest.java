package nl.siegmann.epublib.epub;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.domain.TableOfContents;
import nl.siegmann.epublib.service.MediatypeService;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class NavDocumentTest {
    String navDataWithResourcesInSubFolder = "<?xml version='1.0' encoding='utf-8'?>\n" +
        "<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:epub=\"http://www.idpf.org/2007/ops\" lang=\"en\" xml:lang=\"en\">\n" +
        "<body>\n" +
        "<nav epub:type=\"toc\">\n" +
        "    <ol>\n" +
        "        <li><a href=\"text/part0000.html\">Title Page</a></li>\n" +
        "        <li><a href=\"text/part0001.html#UGI0-c67f0a5a7d524f06bbddb81b8d1876f5\">Copyright</a></li>\n" +
        "        <li><a href=\"text/part0002.html\">Introduction</a></li>\n" +
        "    </ol>\n" +
        "</nav>\n" +
        "</body>\n" +
        "</html>";

    String navDataWithResourcesInSameFolder = "<?xml version='1.0' encoding='utf-8'?>\n" +
        "<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:epub=\"http://www.idpf.org/2007/ops\" lang=\"en\" xml:lang=\"en\">\n" +
        "<body>\n" +
        "<nav epub:type=\"toc\">\n" +
        "    <ol>\n" +
        "        <li><a href=\"part0000.html\">Title Page</a></li>\n" +
        "        <li><a href=\"part0001.html#UGI0-c67f0a5a7d524f06bbddb81b8d1876f5\">Copyright</a></li>\n" +
        "        <li><a href=\"part0002.html\">Introduction</a></li>\n" +
        "    </ol>\n" +
        "</nav>\n" +
        "</body>\n" +
        "</html>";

    private void addResource(Book book, String filename) {
        Resource chapterResource = new Resource("id1", "Hello, world !".getBytes(), filename, MediatypeService.XHTML);
        book.addResource(chapterResource);
        book.getSpine().addResource(chapterResource);
    }

    @Test
    public void testReadWithNonRootLevelNav() {
        Book book = new Book();
        Resource navResource = new Resource(navDataWithResourcesInSubFolder.getBytes(StandardCharsets.UTF_8), "oebps/nav.xhtml");
        book.setNavResource(navResource);

        addResource(book, "oebps/text/part0000.html");
        addResource(book, "oebps/text/part0001.html");
        addResource(book, "oebps/text/part0002.html");

        NavDocument.read(book);

        TableOfContents tableOfContents = book.getTableOfContents();
        Assert.assertEquals(3, tableOfContents.size());

        List<TOCReference> tocReferences = book.getTableOfContents().getTocReferences();
        
        Assert.assertEquals("oebps/text/part0000.html", tocReferences.get(0).getCompleteHref());
        Assert.assertEquals("Title Page", tocReferences.get(0).getTitle());

        Assert.assertEquals("oebps/text/part0001.html#UGI0-c67f0a5a7d524f06bbddb81b8d1876f5", tocReferences.get(1).getCompleteHref());
        Assert.assertEquals("Copyright", tocReferences.get(1).getTitle());

        Assert.assertEquals("oebps/text/part0002.html", tocReferences.get(2).getCompleteHref());
        Assert.assertEquals("Introduction", tocReferences.get(2).getTitle());
    }

    @Test
    public void testReadWithRootLevelNav_AndResourcesInSubFolder() {
        Book book = new Book();
        Resource navResource = new Resource(navDataWithResourcesInSubFolder.getBytes(StandardCharsets.UTF_8), "nav.xhtml");
        book.setNavResource(navResource);

        addResource(book, "text/part0000.html");
        addResource(book, "text/part0001.html");
        addResource(book, "text/part0002.html");

        NavDocument.read(book);

        TableOfContents tableOfContents = book.getTableOfContents();
        Assert.assertEquals(3, tableOfContents.size());

        List<TOCReference> tocReferences = book.getTableOfContents().getTocReferences();
        
        Assert.assertEquals("text/part0000.html", tocReferences.get(0).getCompleteHref());
        Assert.assertEquals("Title Page", tocReferences.get(0).getTitle());

        Assert.assertEquals("text/part0001.html#UGI0-c67f0a5a7d524f06bbddb81b8d1876f5", tocReferences.get(1).getCompleteHref());
        Assert.assertEquals("Copyright", tocReferences.get(1).getTitle());

        Assert.assertEquals("text/part0002.html", tocReferences.get(2).getCompleteHref());
        Assert.assertEquals("Introduction", tocReferences.get(2).getTitle());
    }

    @Test
    public void testReadWithRootLevelNav_AndResourcesInSameFolder() {
        Book book = new Book();
        Resource navResource = new Resource(navDataWithResourcesInSameFolder.getBytes(StandardCharsets.UTF_8), "nav.xhtml");
        book.setNavResource(navResource);

        addResource(book, "part0000.html");
        addResource(book, "part0001.html");
        addResource(book, "part0002.html");

        NavDocument.read(book);

        TableOfContents tableOfContents = book.getTableOfContents();
        Assert.assertEquals(3, tableOfContents.size());

        List<TOCReference> tocReferences = book.getTableOfContents().getTocReferences();
        
        Assert.assertEquals("part0000.html", tocReferences.get(0).getCompleteHref());
        Assert.assertEquals("Title Page", tocReferences.get(0).getTitle());

        Assert.assertEquals("part0001.html#UGI0-c67f0a5a7d524f06bbddb81b8d1876f5", tocReferences.get(1).getCompleteHref());
        Assert.assertEquals("Copyright", tocReferences.get(1).getTitle());

        Assert.assertEquals("part0002.html", tocReferences.get(2).getCompleteHref());
        Assert.assertEquals("Introduction", tocReferences.get(2).getTitle());
    }
}
