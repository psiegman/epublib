package nl.siegmann.epublib.epub;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.domain.*;
import nl.siegmann.epublib.service.MediatypeService;
import nl.siegmann.epublib.util.StringUtil;
import org.xmlpull.v1.XmlSerializer;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import static nl.siegmann.epublib.domain.OpfResource.DEFAULT_VERSION;


/**
 * Writes the opf package document as defined by namespace http://www.idpf.org/2007/opf
 *
 * @author paul
 */
public class PackageDocumentWriter extends PackageDocumentBase {

    private static final Logger log = Logger.getLogger(PackageDocumentWriter.class.getName());

    public static void write(EpubWriter epubWriter, XmlSerializer serializer, Book book) throws IOException {
        try {
            serializer.startDocument(Constants.CHARACTER_ENCODING, false);
            serializer.setPrefix(EpubWriter.EMPTY_NAMESPACE_PREFIX, NAMESPACE_OPF);
//			serializer.setPrefix(PREFIX_DUBLIN_CORE, NAMESPACE_DUBLIN_CORE);
            serializer.startTag(NAMESPACE_OPF, OPFTags.packageTag);
            String version = DEFAULT_VERSION;
            if (null != book.getOpfResource()) {
                version = book.getOpfResource().getVersion();
            }
            serializer.attribute(
                    EpubWriter.EMPTY_NAMESPACE_PREFIX,
                    OPFAttributes.version, StringUtil.isNotBlank(version) ? version : DEFAULT_VERSION
            );
            if (null != book.getOpfResource() && StringUtil.isNotBlank(book.getOpfResource().getPrefix())) {
                serializer.attribute(
                        EpubWriter.EMPTY_NAMESPACE_PREFIX,
                        OPFAttributes.prefix, book.getOpfResource().getPrefix()
                );
            }
            serializer.attribute(EpubWriter.EMPTY_NAMESPACE_PREFIX, OPFAttributes.uniqueIdentifier, BOOK_ID_ID);

            PackageDocumentMetadataWriter.writeMetaData(book, serializer);

            writeManifest(book, epubWriter, serializer);
            writeSpine(book, epubWriter, serializer);
            writeGuide(book, epubWriter, serializer);

            serializer.endTag(NAMESPACE_OPF, OPFTags.packageTag);
            serializer.endDocument();
            serializer.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /**
     * Writes the package's spine.
     *
     * @param book
     * @param epubWriter
     * @param serializer
     * @throws IOException
     * @throws IllegalStateException
     * @throws IllegalArgumentException
     * @throws XMLStreamException
     */
    private static void writeSpine(Book book, EpubWriter epubWriter, XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException {
        serializer.startTag(NAMESPACE_OPF, OPFTags.spine);
        if (null != book.getSpine().getTocResource()) {
            serializer.attribute(EpubWriter.EMPTY_NAMESPACE_PREFIX, OPFAttributes.toc, book.getSpine().getTocResource().getId());
        }
        if (book.getCoverPage() != null // there is a cover page
                && book.getSpine().findFirstResourceById(book.getCoverPage().getId()) < 0) { // cover page is not already in the spine
            // write the cover html file
            serializer.startTag(NAMESPACE_OPF, OPFTags.itemref);
            serializer.attribute(EpubWriter.EMPTY_NAMESPACE_PREFIX, OPFAttributes.idref, book.getCoverPage().getId());
            serializer.attribute(EpubWriter.EMPTY_NAMESPACE_PREFIX, OPFAttributes.linear, "no");
            serializer.endTag(NAMESPACE_OPF, OPFTags.itemref);
        }
        writeSpineItems(book.getSpine(), serializer);
        serializer.endTag(NAMESPACE_OPF, OPFTags.spine);

    }


    private static void writeManifest(Book book, EpubWriter epubWriter, XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException {
        serializer.startTag(NAMESPACE_OPF, OPFTags.manifest);

        if (null != book.getSpine().getTocResource()) {
            serializer.startTag(NAMESPACE_OPF, OPFTags.item);
            serializer.attribute(EpubWriter.EMPTY_NAMESPACE_PREFIX, OPFAttributes.id, epubWriter.getNcxId());
            serializer.attribute(EpubWriter.EMPTY_NAMESPACE_PREFIX, OPFAttributes.href, epubWriter.getNcxHref());
            serializer.attribute(EpubWriter.EMPTY_NAMESPACE_PREFIX, OPFAttributes.media_type, epubWriter.getNcxMediaType());
            serializer.endTag(NAMESPACE_OPF, OPFTags.item);
        }

//		writeCoverResources(book, serializer);

        for (Resource resource : getAllResourcesSortById(book)) {
            writeItem(book, resource, serializer);
        }

        serializer.endTag(NAMESPACE_OPF, OPFTags.manifest);
    }

    private static List<Resource> getAllResourcesSortById(Book book) {
        List<Resource> allResources = new ArrayList<Resource>(book.getResources().getAll());
        Collections.sort(allResources, new Comparator<Resource>() {

            @Override
            public int compare(Resource resource1, Resource resource2) {
                return resource1.getId().compareToIgnoreCase(resource2.getId());
            }
        });
        return allResources;
    }

    /**
     * Writes a resources as an item element
     *
     * @param resource
     * @param serializer
     * @throws IOException
     * @throws IllegalStateException
     * @throws IllegalArgumentException
     * @throws XMLStreamException
     */
    private static void writeItem(Book book, Resource resource, XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException {
        if (resource == null ||
                (resource.getMediaType() == MediatypeService.NCX
                        && book.getSpine().getTocResource() != null)) {
            return;
        }
        if (StringUtil.isBlank(resource.getId())) {
            log.severe("resource id must not be empty (href: " + resource.getHref() + ", mediatype:" + resource.getMediaType() + ")");
            return;
        }
        if (StringUtil.isBlank(resource.getHref())) {
            log.severe("resource href must not be empty (id: " + resource.getId() + ", mediatype:" + resource.getMediaType() + ")");
            return;
        }
        if (resource.getMediaType() == null) {
            log.severe("resource mediatype must not be empty (id: " + resource.getId() + ", href:" + resource.getHref() + ")");
            return;
        }
        serializer.startTag(NAMESPACE_OPF, OPFTags.item);
        serializer.attribute(EpubWriter.EMPTY_NAMESPACE_PREFIX, OPFAttributes.id, resource.getId());
        serializer.attribute(EpubWriter.EMPTY_NAMESPACE_PREFIX, OPFAttributes.href, resource.getHref());
        serializer.attribute(EpubWriter.EMPTY_NAMESPACE_PREFIX, OPFAttributes.media_type, resource.getMediaType().getName());
        if (resource.isNav()) {
            serializer.attribute(EpubWriter.EMPTY_NAMESPACE_PREFIX, OPFAttributes.properties, OPFValues.nav);
        }
        if (resource.isContainingSvg()) {
            serializer.attribute(EpubWriter.EMPTY_NAMESPACE_PREFIX, OPFAttributes.properties, OPFValues.svg);
        }
        if (resource.isScripted()) {
            serializer.attribute(EpubWriter.EMPTY_NAMESPACE_PREFIX, OPFAttributes.properties, OPFValues.scripted);
        }
        serializer.endTag(NAMESPACE_OPF, OPFTags.item);
    }

    /**
     * List all spine references
     *
     * @throws IOException
     * @throws IllegalStateException
     * @throws IllegalArgumentException
     */
    private static void writeSpineItems(Spine spine, XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException {
        for (SpineReference spineReference : spine.getSpineReferences()) {
            serializer.startTag(NAMESPACE_OPF, OPFTags.itemref);
            serializer.attribute(EpubWriter.EMPTY_NAMESPACE_PREFIX, OPFAttributes.idref, spineReference.getResourceId());
            if (!spineReference.isLinear()) {
                serializer.attribute(EpubWriter.EMPTY_NAMESPACE_PREFIX, OPFAttributes.linear, OPFValues.no);
            }
            serializer.endTag(NAMESPACE_OPF, OPFTags.itemref);
        }
    }

    private static void writeGuide(Book book, EpubWriter epubWriter, XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException {
        serializer.startTag(NAMESPACE_OPF, OPFTags.guide);
        ensureCoverPageGuideReferenceWritten(book.getGuide(), epubWriter, serializer);
        for (GuideReference reference : book.getGuide().getReferences()) {
            writeGuideReference(reference, serializer);
        }
        serializer.endTag(NAMESPACE_OPF, OPFTags.guide);
    }

    private static void ensureCoverPageGuideReferenceWritten(Guide guide,
                                                             EpubWriter epubWriter, XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException {
        if (!(guide.getGuideReferencesByType(GuideReference.COVER).isEmpty())) {
            return;
        }
        Resource coverPage = guide.getCoverPage();
        if (coverPage != null) {
            writeGuideReference(new GuideReference(guide.getCoverPage(), GuideReference.COVER, GuideReference.COVER), serializer);
        }
    }


    private static void writeGuideReference(GuideReference reference, XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException {
        if (reference == null) {
            return;
        }
        serializer.startTag(NAMESPACE_OPF, OPFTags.reference);
        serializer.attribute(EpubWriter.EMPTY_NAMESPACE_PREFIX, OPFAttributes.type, reference.getType());
        serializer.attribute(EpubWriter.EMPTY_NAMESPACE_PREFIX, OPFAttributes.href, reference.getCompleteHref());
        if (StringUtil.isNotBlank(reference.getTitle())) {
            serializer.attribute(EpubWriter.EMPTY_NAMESPACE_PREFIX, OPFAttributes.title, reference.getTitle());
        }
        serializer.endTag(NAMESPACE_OPF, OPFTags.reference);
    }
}
