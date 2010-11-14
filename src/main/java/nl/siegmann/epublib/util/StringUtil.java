package nl.siegmann.epublib.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringUtil {

	/**
	 * Poor mans http decoder, decodes %{digit}{digit} things into their source character.
	 * 
	 * Example: 'abc%20de' =&gt; 'abc de'
	 * 
	 * @param input
	 * @return
	 */
	public static String unescapeHttp(String input) {

		StringBuilder result = new StringBuilder();
		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c == '%') {
				if (i < input.length() - 2) {
					result.append(
							(char) (
									(16 * (input.charAt(++i) - '0'))
									+ (input.charAt(++i) - '0')
							)
					);
				}
			} else {
				result.append(c);
			}
		}
		return result.toString();
	}

	/**
	 * Changes a path containing '..', '.' and empty dirs into a path that doesn't.
	 * X/foo/../Y is changed into 'X/Y', etc.
	 * Does not handle invalid paths like "../".
	 * 
	 * @param path
	 * @return
	 */
	public static String collapsePathDots(String path) {
		String[] stringParts = path.split("/");
		List<String> parts = new ArrayList<String>(Arrays.asList(stringParts));
		for (int i = 0; i < parts.size() - 1; i++) {
			String currentDir = parts.get(i);
			if (currentDir.length() == 0 || currentDir.equals(".")) {
				parts.remove(i);
				i--;
			} else if(currentDir.equals("..")) {
				parts.remove(i - 1);
				parts.remove(i - 1);
				i--;
			}
		}
		StringBuilder result = new StringBuilder();
		if (path.startsWith("/")) {
			result.append('/');
		}
		for (int i = 0; i < parts.size(); i++) {
			result.append(parts.get(i));
			if (i < (parts.size() - 1)) {
				result.append('/');
			}
		}
		return result.toString();
	}

}
