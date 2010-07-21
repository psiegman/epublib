package nl.siegmann.epublib.bookprocessor;

import junit.framework.TestCase;

public class CoverpageBookProcessorTest extends TestCase {

	public void testCalculateAbsoluteImageHref1() {
		String[] testData = new String[] {
				"/foo/index.html", "bar.html", "/foo/bar.html",
				"/foo/index.html", "../bar.html", "/bar.html",
				"/foo/index.html", "../sub/bar.html", "/sub/bar.html"
		};
		for (int i = 0; i < testData.length; i+= 3) {
			String actualResult = CoverpageBookProcessor.calculateAbsoluteImageHref(testData[i + 1], testData[i]);
			assertEquals(testData[i + 2], actualResult);
		}
	}

	public void testCollapsePathDots() {
		String[] testData = new String[] {
				"/foo/bar.html", "/foo/bar.html",
				"/foo/../bar.html", "/bar.html",
				"/foo//bar.html", "/foo/bar.html",
				"/foo/./bar.html", "/foo/bar.html",
				"/foo/../sub/bar.html", "/sub/bar.html"
		};
		for (int i = 0; i < testData.length; i += 2) {
			String actualResult = CoverpageBookProcessor.collapsePathDots(testData[i]);
			assertEquals(testData[i + 1], actualResult);
		}
	}
}
