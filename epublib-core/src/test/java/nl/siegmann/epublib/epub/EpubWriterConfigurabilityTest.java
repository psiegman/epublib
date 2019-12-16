package nl.siegmann.epublib.epub;

import net.sf.jazzlib.ZipEntry;
import net.sf.jazzlib.ZipInputStream;
import nl.siegmann.epublib.domain.Book;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSParser;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;
import static org.junit.Assert.fail;

/**
 *  Unit tests for the configurability of the {@link EpubWriter} using {@link EpubWriterConfiguration}.
 */
public class EpubWriterConfigurabilityTest {

    /**
     * Tests that the behavior of {@link EpubWriter} without configuration uses a default configuration.
     * The default configuration must result in an unmodified behavior of the {@link EpubWriter}.
     */
    @Test
    public void regressionTestDirectoryName() throws IOException, XPathExpressionException {
        Book book = new Book();
        EpubWriter epubWriter = new EpubWriter();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        epubWriter.write(book, out);

        assertZipFileContainsEntries(out.toByteArray(), "mimetype", "META-INF/container.xml", "OEBPS/toc.ncx", "OEBPS/content.opf");
        assertEpubIncludesContainerEntries(out.toByteArray(), "OEBPS/content.opf");
    }

    /**
     * Tests that the behavior of {@link EpubWriter} with a configuration allows changing the content directory name.
     */
    @Test
    public void testConfigureContentDirectoryName() throws IOException, XPathExpressionException {
        Book book = new Book();
        EpubWriter epubWriter = new EpubWriter(new EpubWriterConfiguration().withContentDirectoryName("OPS"));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        epubWriter.write(book, out);

        assertZipFileContainsEntries(out.toByteArray(), "mimetype", "META-INF/container.xml", "OPS/toc.ncx", "OPS/content.opf");
        assertEpubIncludesContainerEntries(out.toByteArray(), "OPS/content.opf");
    }

    private static void assertZipFileContainsEntries(byte[] zipFileData, String... expectedEntries) throws IOException {
        assertZipFileContainsEntries(new ByteArrayInputStream(zipFileData), expectedEntries);
    }

    private static void assertZipFileContainsEntries(InputStream in, String... expectedEntries) throws IOException {
        ZipInputStream zipInputStream = new ZipInputStream(in);
        Set<String> actualNames = new HashSet<>();
        Set<String> expectedNames = setOf(expectedEntries);
        for (ZipEntry zipEntry; (zipEntry = zipInputStream.getNextEntry()) != null; )
            actualNames.add(zipEntry.getName());
        assertContainsAll(expectedNames, actualNames);
    }

    private static <T> void assertContainsAll(Set<T> expected, Set<T> actual) {
        Set<T> missing = new HashSet<>(expected);
        missing.removeAll(actual);
        if (!missing.isEmpty())
            fail("Expected set " + actual + " to contain all elements from set " + actual + " but was missing he following elements: " + missing);
    }

    private static void assertEpubIncludesContainerEntries(byte[] zipFileData, String... expectedContainerEntries) throws IOException, XPathExpressionException {
        assertEpubIncludesContainerEntries(new ByteArrayInputStream(zipFileData), expectedContainerEntries);
    }

    private static void assertEpubIncludesContainerEntries(InputStream in, String... expectedContainerEntries) throws IOException, XPathExpressionException {
        ZipInputStream zipInputStream = new ZipInputStream(in);
        for (ZipEntry zipEntry; (zipEntry = zipInputStream.getNextEntry()) != null; )
            if ("META-INF/container.xml".equals(zipEntry.getName())) {
                assertIncludesContainerEntries(zipInputStream, expectedContainerEntries);
                return;
            }
        fail("Could not find META-INF/container.xml");
    }

    private static void assertIncludesContainerEntries(InputStream zipInputStream, String... expectedContainerEntries) throws XPathExpressionException {
        Document doc = readDocument(zipInputStream);
        XPath xPath = XPathFactory.newInstance().newXPath();
        xPath.setNamespaceContext(new ContainerNamespaceContext());

        NodeList nodeList = (NodeList) xPath.evaluate("/container:container/container:rootfiles/container:rootfile/@full-path", doc, XPathConstants.NODESET);
        Set<String> actualPaths = new HashSet<>();
        for (int i = 0; i < nodeList.getLength(); i++)
            actualPaths.add(nodeList.item(i).getNodeValue());
        assertContainsAll(setOf(expectedContainerEntries), actualPaths);
    }

    private static Document readDocument(InputStream in) {
        DOMImplementationLS domImplementationLS = (DOMImplementationLS) mustGetRegistry().getDOMImplementation("LS");
        LSInput lsInput = domImplementationLS.createLSInput();
        lsInput.setByteStream(in);
        LSParser lsParser = domImplementationLS.createLSParser(DOMImplementationLS.MODE_SYNCHRONOUS, null);
        return lsParser.parse(lsInput);
    }

    private static DOMImplementationRegistry mustGetRegistry() {
        try {
            return DOMImplementationRegistry.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new AssertionError("Could not initialize DOMImplementationRegistry", e);
        }
    }

    @SafeVarargs
    private static <T> Set<T> setOf(T... elements) {
        return unmodifiableSet(new HashSet<>(asList(elements)));
    }
}
