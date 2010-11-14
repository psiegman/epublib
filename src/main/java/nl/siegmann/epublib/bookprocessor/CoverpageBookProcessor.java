package nl.siegmann.epublib.bookprocessor;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.ByteArrayResource;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Resources;
import nl.siegmann.epublib.epub.EpubWriter;
import nl.siegmann.epublib.service.MediatypeService;
import nl.siegmann.epublib.util.CollectionUtil;
import nl.siegmann.epublib.util.ResourceUtil;
import nl.siegmann.epublib.util.StringUtil;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * If the book contains a cover image then this will add a cover page to the book.
 * If the book contains a cover html page it will set that page's first image as the book's cover image.
 * 
 * FIXME:
 *  will overwrite any "cover.jpg" or "cover.html" that are already there.
 *  
 * @author paul
 *
 */
public class CoverpageBookProcessor implements BookProcessor {

	public static int MAX_COVER_IMAGE_SIZE = 999;
	private static final Logger LOG = Logger.getLogger(CoverpageBookProcessor.class);
	public static final String DEFAULT_COVER_PAGE_ID = "cover";
	public static final String DEFAULT_COVER_PAGE_HREF = "cover.html";
	public static final String DEFAULT_COVER_IMAGE_ID = "cover-image";
	public static final String DEFAULT_COVER_IMAGE_HREF = "images/cover.png";
	
	@Override
	public Book processBook(Book book, EpubWriter epubWriter) {
		Metadata metadata = book.getMetadata();
		if(book.getCoverPage() == null && book.getCoverImage() == null) {
			return book;
		}
		Resource coverPage = book.getCoverPage();
		Resource coverImage = book.getCoverImage();
		if(coverPage == null) {
			if(coverImage == null) {
				// give up
			} else { // coverImage != null
				if(StringUtils.isBlank(coverImage.getHref())) {
					coverImage.setHref(getCoverImageHref(coverImage, book));
				}
				String coverPageHtml = createCoverpageHtml(CollectionUtil.first(metadata.getTitles()), coverImage.getHref());
				coverPage = new ByteArrayResource(null, coverPageHtml.getBytes(), getCoverPageHref(book), MediatypeService.XHTML);
				fixCoverResourceId(book, coverPage, DEFAULT_COVER_PAGE_ID);
			}
		} else { // coverPage != null
			if(book.getCoverImage() == null) {
				coverImage = getFirstImageSource(epubWriter, coverPage, book.getResources());
				book.setCoverImage(coverImage);
				if (coverImage != null) {
					book.getResources().remove(coverImage.getHref());
				}
			} else { // coverImage != null
				
			}
		}
		
		book.setCoverImage(coverImage);
		book.setCoverPage(coverPage);
		setCoverResourceIds(book);
		return book;
	}

//	private String getCoverImageHref(Resource coverImageResource) {
//		return "cover" + coverImageResource.getMediaType().getDefaultExtension();
//	}
	
	private void setCoverResourceIds(Book book) {
		if(book.getCoverImage() != null) {
			fixCoverResourceId(book, book.getCoverImage(), DEFAULT_COVER_IMAGE_ID);
		}
		if(book.getCoverPage() != null) {
			fixCoverResourceId(book, book.getCoverPage(), DEFAULT_COVER_PAGE_ID);
		}
	}

	
	private void fixCoverResourceId(Book book, Resource resource, String defaultId) {
		if (StringUtils.isBlank(resource.getId())) {
			resource.setId(defaultId);
		}
		book.getResources().fixResourceId(resource);
	}
	
	private String getCoverPageHref(Book book) {
		return DEFAULT_COVER_PAGE_HREF;
	}
	
	
	private String getCoverImageHref(Resource imageResource, Book book) {
		return DEFAULT_COVER_IMAGE_HREF;
	}
	
	private Resource getFirstImageSource(EpubWriter epubWriter, Resource titlePageResource, Resources resources) {
		try {
			Document titlePageDocument = ResourceUtil.getAsDocument(titlePageResource, epubWriter.createDocumentBuilder());
			NodeList imageElements = titlePageDocument.getElementsByTagName("img");
			for (int i = 0; i < imageElements.getLength(); i++) {
				String relativeImageHref = ((Element) imageElements.item(i)).getAttribute("src");
				String absoluteImageHref = calculateAbsoluteImageHref(relativeImageHref, titlePageResource.getHref());
				Resource imageResource = resources.getByHref(absoluteImageHref);
				if (imageResource != null) {
					return imageResource;
				}
			}
		} catch (Exception e) {
			LOG.error(e);
		}
		return null;
	}
	
	
	
	// package
	static String calculateAbsoluteImageHref(String relativeImageHref,
			String baseHref) {
		if (relativeImageHref.startsWith("/")) {
			return relativeImageHref;
		}
		String result = StringUtil.collapsePathDots(baseHref.substring(0, baseHref.lastIndexOf('/') + 1) + relativeImageHref);
		return result;
	}

	private String createCoverpageHtml(String title, String imageHref) {
	       return "" +
	       		"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
	       		"<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
	       		"\t<head>\n" +
	       		"\t\t<title>Cover</title>\n" +
	       		"\t\t<style type=\"text/css\"> img { max-width: 100%; } </style>\n" +
	       		"\t</head>\n" +
	       		"\t<body>\n" +
	       		"\t\t<div id=\"cover-image\">\n" +
	       		"\t\t\t<img src=\"" + StringEscapeUtils.escapeHtml(imageHref) + "\" alt=\"" + StringEscapeUtils.escapeHtml(title) + "\"/>\n" +
	       		"\t\t</div>\n" +
	       		"\t</body>\n" +
	       		"</html>\n";
	}
	
    private Dimension calculateResizeSize(BufferedImage image) {
        Dimension result;
        if (image.getWidth() > image.getHeight()) {
            result = new Dimension(MAX_COVER_IMAGE_SIZE, (int) (((double) MAX_COVER_IMAGE_SIZE / (double) image.getWidth()) * (double) image.getHeight()));
        } else {
            result = new Dimension((int) (((double) MAX_COVER_IMAGE_SIZE / (double) image.getHeight()) * (double) image.getWidth()), MAX_COVER_IMAGE_SIZE);
        }
        return result;
    }


    @SuppressWarnings("unused")
	private byte[] createThumbnail(byte[] imageData) throws IOException {
        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(imageData));
        Dimension thumbDimension = calculateResizeSize(originalImage);
        BufferedImage thumbnailImage = createResizedCopy(originalImage, (int) thumbDimension.getWidth(), (int) thumbDimension.getHeight(), false);
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        ImageIO.write(thumbnailImage, "png", result);
        return result.toByteArray();

    }

    private BufferedImage createResizedCopy(java.awt.Image originalImage, int scaledWidth, int scaledHeight, boolean preserveAlpha) {
        int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, imageType);
        Graphics2D g = scaledBI.createGraphics();
        if (preserveAlpha) {
            g.setComposite(AlphaComposite.Src);
        }
        g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
        g.dispose();
        return scaledBI;
    }
}
