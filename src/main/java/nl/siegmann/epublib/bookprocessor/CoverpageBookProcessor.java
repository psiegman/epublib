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
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubWriter;
import nl.siegmann.epublib.service.MediatypeService;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

/**
 * If the book contains a cover image then this will add a cover page to the book.
 * 
 * FIXME:
 *  only handles the case of a given cover image
 *  will overwrite any "cover.jpg" or "cover.html" that are already there.
 *  
 * @author paul
 *
 */
public class CoverpageBookProcessor implements BookProcessor {

	public static int MAX_COVER_IMAGE_SIZE = 999;
	
	@Override
	public Book processBook(Book book, EpubWriter epubWriter) {
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
					coverImage.setHref(getCoverImageHref(coverImage));
				}
				String coverPageHtml = createCoverpageHtml(book.getMetadata().getTitle(), coverImage.getHref());
				coverPage = new ByteArrayResource("cover", coverPageHtml.getBytes(), "cover.html", MediatypeService.XHTML);
			}
		} else { // coverPage != null
			if(book.getCoverImage() == null) {
				// TODO find the image in the page, make a new resource for it and add it to the book.
			} else { // coverImage != null
				
			}
		}
		
		book.setCoverImage(coverImage);
		book.setCoverPage(coverPage);
		setCoverResourceIds(book);
		return book;
	}

	private String getCoverImageHref(Resource coverImageResource) {
		return "cover" + coverImageResource.getMediaType().getDefaultExtension();
	}
	
	private void setCoverResourceIds(Book book) {
		if(book.getCoverImage() != null) {
			book.getCoverImage().setId("cover-image");
		}
		if(book.getCoverPage() != null) {
			book.getCoverPage().setId("cover");
		}
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
