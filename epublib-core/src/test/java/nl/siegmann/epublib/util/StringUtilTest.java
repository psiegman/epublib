package nl.siegmann.epublib.util;

import java.io.IOException;

import junit.framework.TestCase;

public class StringUtilTest extends TestCase {

	public void testDefaultIfNull() {
		Object[] testData = new Object[] { null, "", "", "", " ", " ", "foo",
				"foo" };
		for (int i = 0; i < testData.length; i += 2) {
			String actualResult = StringUtil
					.defaultIfNull((String) testData[i]);
			String expectedResult = (String) testData[i + 1];
			assertEquals((i / 2) + " : " + testData[i], expectedResult,
					actualResult);
		}
	}

	public void testDefaultIfNull_with_default() {
		Object[] testData = new Object[] { null, null, null, "", null, "",
				null, "", "", "foo", "", "foo", "", "foo", "", " ", " ", " ",
				null, "foo", "foo", };
		for (int i = 0; i < testData.length; i += 3) {
			String actualResult = StringUtil.defaultIfNull(
					(String) testData[i], (String) testData[i + 1]);
			String expectedResult = (String) testData[i + 2];
			assertEquals(
					(i / 3) + " : " + testData[i] + ", " + testData[i + 1],
					expectedResult, actualResult);
		}
	}

	public void testIsEmpty() {
		Object[] testData = new Object[] { null, true, "", true, " ", false,
				"asdfasfd", false };
		for (int i = 0; i < testData.length; i += 2) {
			boolean actualResult = StringUtil.isEmpty((String) testData[i]);
			boolean expectedResult = (Boolean) testData[i + 1];
			assertEquals(expectedResult, actualResult);
		}
	}

	public void testIsBlank() {
		Object[] testData = new Object[] { null, true, "", true, " ", true,
				"\t\t \n\n", true, "asdfasfd", false };
		for (int i = 0; i < testData.length; i += 2) {
			boolean actualResult = StringUtil.isBlank((String) testData[i]);
			boolean expectedResult = (Boolean) testData[i + 1];
			assertEquals(expectedResult, actualResult);
		}
	}

	public void testIsNotBlank() {
		Object[] testData = new Object[] { null, !true, "", !true, " ", !true,
				"\t\t \n\n", !true, "asdfasfd", !false };
		for (int i = 0; i < testData.length; i += 2) {
			boolean actualResult = StringUtil.isNotBlank((String) testData[i]);
			boolean expectedResult = (Boolean) testData[i + 1];
			assertEquals((i / 2) + " : " + testData[i], expectedResult,
					actualResult);
		}
	}

	public void testEquals() {
		Object[] testData = new Object[] { null, null, true, "", "", true,
				null, "", false, "", null, false, null, "foo", false, "foo",
				null, false, "", "foo", false, "foo", "", false, "foo", "bar",
				false, "foo", "foo", true };
		for (int i = 0; i < testData.length; i += 3) {
			boolean actualResult = StringUtil.equals((String) testData[i],
					(String) testData[i + 1]);
			boolean expectedResult = (Boolean) testData[i + 2];
			assertEquals(
					(i / 3) + " : " + testData[i] + ", " + testData[i + 1],
					expectedResult, actualResult);
		}
	}

	public void testEndWithIgnoreCase() {
		Object[] testData = new Object[] { null, null, true, "", "", true, "",
				"foo", false, "foo", "foo", true, "foo.bar", "bar", true,
				"foo.bar", "barX", false, "foo.barX", "bar", false, "foo",
				"bar", false, "foo.BAR", "bar", true, "foo.bar", "BaR", true };
		for (int i = 0; i < testData.length; i += 3) {
			boolean actualResult = StringUtil.endsWithIgnoreCase(
					(String) testData[i], (String) testData[i + 1]);
			boolean expectedResult = (Boolean) testData[i + 2];
			assertEquals(
					(i / 3) + " : " + testData[i] + ", " + testData[i + 1],
					expectedResult, actualResult);
		}
	}

	public void testSubstringBefore() {
		Object[] testData = new Object[] { "", ' ', "", "", 'X', "", "fox",
				'x', "fo", "foo.bar", 'b', "foo.", "aXbXc", 'X', "a", };
		for (int i = 0; i < testData.length; i += 3) {
			String actualResult = StringUtil.substringBefore(
					(String) testData[i], (Character) testData[i + 1]);
			String expectedResult = (String) testData[i + 2];
			assertEquals(
					(i / 3) + " : " + testData[i] + ", " + testData[i + 1],
					expectedResult, actualResult);
		}
	}

	public void testSubstringBeforeLast() {
		Object[] testData = new Object[] { "", ' ', "", "", 'X', "", "fox",
				'x', "fo", "foo.bar", 'b', "foo.", "aXbXc", 'X', "aXb", };
		for (int i = 0; i < testData.length; i += 3) {
			String actualResult = StringUtil.substringBeforeLast(
					(String) testData[i], (Character) testData[i + 1]);
			String expectedResult = (String) testData[i + 2];
			assertEquals(
					(i / 3) + " : " + testData[i] + ", " + testData[i + 1],
					expectedResult, actualResult);
		}
	}

	public void testSubstringAfter() {
		Object[] testData = new Object[] { "", ' ', "", "", 'X', "", "fox",
				'f', "ox", "foo.bar", 'b', "ar", "aXbXc", 'X', "bXc", };
		for (int i = 0; i < testData.length; i += 3) {
			String actualResult = StringUtil.substringAfter(
					(String) testData[i], (Character) testData[i + 1]);
			String expectedResult = (String) testData[i + 2];
			assertEquals(
					(i / 3) + " : " + testData[i] + ", " + testData[i + 1],
					expectedResult, actualResult);
		}
	}

	public void testSubstringAfterLast() {
		Object[] testData = new Object[] { "", ' ', "", "", 'X', "", "fox",
				'f', "ox", "foo.bar", 'b', "ar", "aXbXc", 'X', "c", };
		for (int i = 0; i < testData.length; i += 3) {
			String actualResult = StringUtil.substringAfterLast(
					(String) testData[i], (Character) testData[i + 1]);
			String expectedResult = (String) testData[i + 2];
			assertEquals(
					(i / 3) + " : " + testData[i] + ", " + testData[i + 1],
					expectedResult, actualResult);
		}
	}

	public void testToString() {
		assertEquals("[name: 'paul']", StringUtil.toString("name", "paul"));
		assertEquals("[name: 'paul', address: 'a street']",
				StringUtil.toString("name", "paul", "address", "a street"));
		assertEquals("[name: <null>]", StringUtil.toString("name", null));
		assertEquals("[name: 'paul', address: <null>]",
				StringUtil.toString("name", "paul", "address"));
	}

	public void testHashCode() {
		assertEquals(2522795, StringUtil.hashCode("isbn", "1234"));
		assertEquals(3499691, StringUtil.hashCode("ISBN", "1234"));
	}

	public void testReplacementForCollapsePathDots() throws IOException {
		// This used to test StringUtil.collapsePathDots(String path).
		// I have left it to confirm that the Apache commons
		// FilenameUtils.normalize
		// is a suitable replacement, but works where for "/a/b/../../c", which
		// the old method did not.
		String[] testData = new String[] { //
			"/foo/bar.html", "/foo/bar.html",
			"/foo/../bar.html", "/bar.html", //
			"/foo/moo/../../bar.html", //
			"/bar.html", "/foo//bar.html", //
			"/foo/bar.html", "/foo/./bar.html", //
			"/foo/bar.html", //
			"/a/b/../../c", "/c", //
			"/foo/../sub/bar.html", "/sub/bar.html" //
		};
		for (int i = 0; i < testData.length; i += 2) {
			String actualResult = StringUtil.collapsePathDots(testData[i]);
			assertEquals(testData[i], testData[i + 1], actualResult);
		}
	}

}
