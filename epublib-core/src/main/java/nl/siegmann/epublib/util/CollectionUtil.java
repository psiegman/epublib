package nl.siegmann.epublib.util;

import java.util.List;

public class CollectionUtil {

	public static <T> T first(List<T> list) {
		if(list == null || list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}
}
