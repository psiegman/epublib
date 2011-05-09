package nl.siegmann.epublib.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringUtil {

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
