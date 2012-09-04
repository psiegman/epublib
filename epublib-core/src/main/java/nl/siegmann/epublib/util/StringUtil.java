package nl.siegmann.epublib.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Various String utility functions.
 * 
 * Most of the functions herein are re-implementations of the ones in apache commons StringUtils.
 * The reason for re-implementing this is that the functions are fairly simple and using my own implementation saves the inclusion of a 200Kb jar file.
 * 
 * @author paul.siegmann
 *
 */
public class StringUtil {

	/**
	 * Whether the String is not null, not zero-length and does not contain of only whitespace.
	 * 
	 * @param text
	 * @return
	 */
	public static boolean isNotBlank(String text) {
		return ! isBlank(text);
	}
	
	/**
	 * Whether the String is null, zero-length and does contain only whitespace.
	 */
	public static boolean isBlank(String text) {
		if (isEmpty(text)) {
			return true;
		}
		for (int i = 0; i < text.length(); i++) {
			if (! Character.isWhitespace(text.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Whether the given string is null or zero-length.
	 * 
	 * @param text
	 * @return
	 */
	public static boolean isEmpty(String text) {
		return (text == null) || (text.length() == 0);
	}
	
	/**
	 * Whether the given source string ends with the given suffix, ignoring case.
	 * 
	 * @param source
	 * @param suffix
	 * @return
	 */
	public static boolean endsWithIgnoreCase(String source, String suffix) {
		if (isEmpty(suffix)) {
			return true;
		}
		if (isEmpty(source)) {
			return false;
		}
		if (suffix.length() > source.length()) {
			return false;
		}
		return source.substring(source.length() - suffix.length()).toLowerCase().endsWith(suffix.toLowerCase());
	}
	
		/**
	 * If the given text is null return "", the original text otherwise.
	 * 
	 * @param text
	 * @return
	 */
	public static String defaultIfNull(String text) {
		return defaultIfNull(text, "");
	}

	/**
	 * If the given text is null return "", the given defaultValue otherwise.
	 * 
	 * @param text
	 * @param defaultValue
	 * @return
	 */
	public static String defaultIfNull(String text, String defaultValue) {
		if (text == null) {
			return defaultValue;
		}
		return text;
	}

	/**
	 * Null-safe string comparator
	 * 
	 * @param text1
	 * @param text2
	 * @return
	 */
	public static boolean equals(String text1, String text2) {
		if (text1 == null) {
			return (text2 == null);
		}
		return text1.equals(text2);
	}

	/**
	 * Pretty toString printer.
	 * 
	 * @param keyValues
	 * @return
	 */
	public static String toString(Object ... keyValues) {
		StringBuilder result = new StringBuilder();
		result.append('[');
		for (int i = 0; i < keyValues.length; i += 2) {
			if (i > 0) {
				result.append(", ");
			}
			result.append(keyValues[i]);
			result.append(": ");
			Object value = null;
			if ((i + 1) < keyValues.length) {
				value = keyValues[i + 1];
			}
			if (value == null) {
				result.append("<null>");
			} else {
				result.append('\'');
				result.append(value);
				result.append('\'');
			}
		}
		result.append(']');
		return result.toString();
	}

	public static int hashCode(String ... values) {
		int result = 31;
		for (int i = 0; i < values.length; i++) {
			result ^= String.valueOf(values[i]).hashCode();
		}
		return result;
	}

	/**
	 * Gives the substring of the given text before the given separator.
	 * 
	 * If the text does not contain the given separator then the given text is returned.
	 * 
	 * @param text
	 * @param separator
	 * @return
	 */
	public static String substringBefore(String text, char separator) {
		if (isEmpty(text)) {
			return text;
		}
		int sepPos = text.indexOf(separator);
		if (sepPos < 0) {
			return text;
		}
		return text.substring(0, sepPos);
	}

	/**
	 * Gives the substring of the given text before the last occurrence of the given separator.
	 * 
	 * If the text does not contain the given separator then the given text is returned.
	 * 
	 * @param text
	 * @param separator
	 * @return
	 */
	public static String substringBeforeLast(String text, char separator) {
		if (isEmpty(text)) {
			return text;
		}
		int cPos = text.lastIndexOf(separator);
		if (cPos < 0) {
			return text;
		}
		return text.substring(0, cPos);
	}

	/**
	 * Gives the substring of the given text after the last occurrence of the given separator.
	 * 
	 * If the text does not contain the given separator then "" is returned.
	 * 
	 * @param text
	 * @param separator
	 * @return
	 */
	public static String substringAfterLast(String text, char separator) {
		if (isEmpty(text)) {
			return text;
		}
		int cPos = text.lastIndexOf(separator);
		if (cPos < 0) {
			return "";
		}
		return text.substring(cPos + 1);
	}

	/**
	 * Gives the substring of the given text after the given separator.
	 * 
	 * If the text does not contain the given separator then "" is returned.
	 * 
	 * @param text
	 * @param separator
	 * @return
	 */
	public static String substringAfter(String text, char c) {
		if (isEmpty(text)) {
			return text;
		}
		int cPos = text.indexOf(c);
		if (cPos < 0) {
			return "";
		}
		return text.substring(cPos + 1);
	}
}
