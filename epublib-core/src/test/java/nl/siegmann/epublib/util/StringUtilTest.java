package nl.siegmann.epublib.util;

import junit.framework.TestCase;

public class StringUtilTest extends TestCase {

	public void testCollapsePathDots() {
		String[] testData = new String[] {
				"/foo/bar.html", "/foo/bar.html",
				"/foo/../bar.html", "/bar.html",
				"/foo//bar.html", "/foo/bar.html",
				"/foo/./bar.html", "/foo/bar.html",
				"/foo/../sub/bar.html", "/sub/bar.html"
		};
		for (int i = 0; i < testData.length; i += 2) {
			String actualResult = StringUtil.collapsePathDots(testData[i]);
			assertEquals(testData[i + 1], actualResult);
		}
	}

}
