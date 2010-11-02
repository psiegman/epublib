package nl.siegmann.epublib.bookprocessor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubWriter;

import org.apache.log4j.Logger;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.EpublibXmlSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XmlSerializer;
import org.htmlcleaner.TagNode.ITagNodeCondition;

/**
 * Cleans up regular html into xhtml. Uses HtmlCleaner to do this.
 * 
 * @author paul
 * 
 */
public class HtmlCleanerBookProcessor extends HtmlBookProcessor implements
		BookProcessor {

	@SuppressWarnings("unused")
	private final static Logger log = Logger
			.getLogger(HtmlCleanerBookProcessor.class);

	private HtmlCleaner htmlCleaner;
	private XmlSerializer newXmlSerializer;
	private boolean addXmlNamespace = true;
	private boolean setCharsetMetaTag = true;

	public HtmlCleanerBookProcessor() {
		this.htmlCleaner = createHtmlCleaner();
		this.newXmlSerializer = new EpublibXmlSerializer(htmlCleaner
				.getProperties());
	}

	private static HtmlCleaner createHtmlCleaner() {
		HtmlCleaner result = new HtmlCleaner();
		CleanerProperties cleanerProperties = result.getProperties();
		cleanerProperties.setOmitXmlDeclaration(true);
		cleanerProperties.setRecognizeUnicodeChars(true);
		cleanerProperties.setTranslateSpecialEntities(false);
		cleanerProperties.setIgnoreQuestAndExclam(true);
		return result;
	}

	@SuppressWarnings("unchecked")
	public byte[] processHtml(Resource resource, Book book,
			EpubWriter epubWriter, Charset outputEncoding) throws IOException {
		Charset inputEncoding = resource.getInputEncoding();
		if (inputEncoding == null) {
			inputEncoding = Constants.ENCODING;
		}
		Reader reader = new InputStreamReader(resource.getInputStream(),
				inputEncoding);
		TagNode node = htmlCleaner.clean(reader);
		node.removeAttribute("xmlns:xml");
		setCharsetMeta(node, outputEncoding);
		if (isAddXmlNamespace()) {
			node.getAttributes().put("xmlns", Constants.NAMESPACE_XHTML);
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		newXmlSerializer.writeXmlToStream(node, out, outputEncoding.name());
		return out.toByteArray();
	}

	// <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
	private void setCharsetMeta(TagNode rootNode, Charset charset) {
		List<TagNode> metaContentTypeTags = getElementList(rootNode,
			new TagNode.ITagNodeCondition() {
				@Override
				public boolean satisfy(TagNode tagNode) {
					return tagNode.getName().equalsIgnoreCase("meta")
						&& "Content-Type".equalsIgnoreCase(tagNode.getAttributeByName("http-equiv"));
				}
			},
			true
		);
		for(TagNode metaTag: metaContentTypeTags) {
			metaTag.addAttribute("content", "text/html; charset=" + charset.name());
		}
	}
	
	
	private List<TagNode> getElementList(TagNode tagNode, ITagNodeCondition paramITagNodeCondition,
			boolean paramBoolean) {
		List<TagNode> result = new ArrayList<TagNode>();
		if (paramITagNodeCondition == null) {
			return result;
		}
		for (int i = 0; i < tagNode.getChildren().size(); ++i) {
			Object localObject = tagNode.getChildren().get(i);
			if (!(localObject instanceof TagNode)) {
				continue;
			}
			TagNode localTagNode = (TagNode) localObject;
			if (paramITagNodeCondition.satisfy(localTagNode)) {
				result.add(localTagNode);
			}
			if (!(paramBoolean)) {
				continue;
			}
			List<TagNode> localList = getElementList(localTagNode,
					paramITagNodeCondition, paramBoolean);
			if ((localList == null) || (localList.size() <= 0)) {
				continue;
			}
			result.addAll(localList);
		}
		return result;
	}

	public void setAddXmlNamespace(boolean addXmlNamespace) {
		this.addXmlNamespace = addXmlNamespace;
	}

	public boolean isAddXmlNamespace() {
		return addXmlNamespace;
	}

	public boolean isSetCharsetMetaTag() {
		return setCharsetMetaTag;
	}
	
	public void setSetCharsetMetaTag(boolean setCharsetMetaTag) {
		this.setCharsetMetaTag = setCharsetMetaTag;
	}
}

