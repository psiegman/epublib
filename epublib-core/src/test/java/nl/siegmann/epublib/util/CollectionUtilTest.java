package nl.siegmann.epublib.util;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class CollectionUtilTest {

	@Test
	public void testIsEmpty_null() {
		Assert.assertTrue(CollectionUtil.isEmpty(null));
	}

	@Test
	public void testIsEmpty_empty() {
		Assert.assertTrue(CollectionUtil.isEmpty(new ArrayList<Object>()));
	}

	@Test
	public void testIsEmpty_elements() {
		Assert.assertFalse(CollectionUtil.isEmpty(Arrays.asList("foo")));
	}
}
