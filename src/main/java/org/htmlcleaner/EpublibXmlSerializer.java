package org.htmlcleaner;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import org.htmlcleaner.BaseToken;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.ContentToken;
import org.htmlcleaner.SpecialEntities;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.Utils;
import org.htmlcleaner.XmlSerializer;

public class EpublibXmlSerializer extends XmlSerializer {
	public EpublibXmlSerializer(CleanerProperties paramCleanerProperties) {
		super(paramCleanerProperties);
	}

	protected void serialize(TagNode paramTagNode, Writer paramWriter)
			throws IOException {
		serializeOpenTag(paramTagNode, paramWriter, false);
		List localList = paramTagNode.getChildren();
		if (isMinimizedTagSyntax(paramTagNode))
			return;
		Iterator localIterator = localList.iterator();
		while (localIterator.hasNext()) {
			Object localObject = localIterator.next();
			if (localObject == null) {
				continue;
			}
			if (localObject instanceof ContentToken) {
				String str = ((ContentToken) localObject).getContent();
				paramWriter.write((dontEscape(paramTagNode)) ? str.replaceAll(
						"]]>", "]]&gt;") : escapeXml(str, this.props, false));
			} else {
				((BaseToken) localObject).serialize(this, paramWriter);
			}
		}
		serializeEndTag(paramTagNode, paramWriter, false);
	}

	public static String escapeXml(String paramString,
			CleanerProperties paramCleanerProperties, boolean paramBoolean) {
		boolean advancedXmlEscape = paramCleanerProperties.isAdvancedXmlEscape();
		boolean recognizeUnicodeChars = paramCleanerProperties.isRecognizeUnicodeChars();
		boolean translateSpecialEntities = paramCleanerProperties.isTranslateSpecialEntities();
		if (paramString != null) {
			int i = paramString.length();
			StringBuilder localStringBuffer = new StringBuilder(i);
			for (int j = 0; j < i; ++j) {
				char c1 = paramString.charAt(j);
				if (c1 == '&') {
					if ((((advancedXmlEscape) || (recognizeUnicodeChars))) && (j < i - 1)
							&& (paramString.charAt(j + 1) == '#')) {
						int k = j + 2;
						String str2 = "";
						while ((k < i)
								&& (((Utils.isHexadecimalDigit(paramString
										.charAt(k)))
										|| (paramString.charAt(k) == 'x') || (paramString
										.charAt(k) == 'X')))) {
							str2 += paramString.charAt(k);
							++k;
						}
						if ((k == i) || (!("".equals(str2)))) {
							try {
								char c2 = (str2.toLowerCase().startsWith("x")) ? (char) Integer
										.parseInt(str2.substring(1), 16)
										: (char) Integer.parseInt(str2);
								if ("&<>'\"".indexOf(c2) < 0) {
									int i1 = ((k < i) && (paramString.charAt(k) == ';')) ? str2
											.length() + 1
											: str2.length();
									localStringBuffer.append("&#" + str2 + ";");
									j += i1 + 1;
								} else {
									j = k;
									localStringBuffer.append("&amp;#" + str2
											+ ";");
								}
							} catch (NumberFormatException localNumberFormatException) {
								j = k;
								localStringBuffer.append("&amp;#" + str2 + ";");
							}
						} else {
							localStringBuffer.append("&amp;");
						}
					} else {
						String str1;
						if (translateSpecialEntities) {
							str1 = paramString.substring(j, j
									+ Math.min(10, i - j));
							int l = str1.indexOf(59);
							if (l > 0) {
								String str3 = str1.substring(1, l);
								Integer localInteger = (Integer) SpecialEntities.entities
										.get(str3);
								if (localInteger != null) {
									int i2 = str3.length();
									if (recognizeUnicodeChars) {
										localStringBuffer
												.append((char) localInteger
														.intValue());
									} else {
										localStringBuffer.append("&#"
												+ localInteger + ";");
									}
									j += i2 + 1;
								}
							}
						} else if (advancedXmlEscape) {
							str1 = paramString.substring(j);
							if (str1.startsWith("&amp;")) {
								localStringBuffer.append((paramBoolean) ? "&"
										: "&amp;");
								j += 4;
							} else if (str1.startsWith("&apos;")) {
								localStringBuffer.append("'");
								j += 5;
							} else if (str1.startsWith("&gt;")) {
								localStringBuffer.append((paramBoolean) ? ">"
										: "&gt;");
								j += 3;
							} else if (str1.startsWith("&lt;")) {
								localStringBuffer.append((paramBoolean) ? "<"
										: "&lt;");
								j += 3;
							} else if (str1.startsWith("&quot;")) {
								localStringBuffer.append((paramBoolean) ? "\""
										: "&quot;");
								j += 5;
							} else {
								localStringBuffer.append((paramBoolean) ? "&"
										: "&amp;");
							}
						} else {
							localStringBuffer.append("&amp;");
						}
					}
				} else if (c1 == '\'') {
					localStringBuffer.append("'");
				} else if (c1 == '>') {
					localStringBuffer.append("&gt;");
				} else if (c1 == '<') {
					localStringBuffer.append("&lt;");
				} else if (c1 == '"') {
					localStringBuffer.append("&quot;");
				} else {
					localStringBuffer.append(c1);
				}
			}
			return localStringBuffer.toString();
		}
		return null;
	}
}