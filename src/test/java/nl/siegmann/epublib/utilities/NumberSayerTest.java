package nl.siegmann.epublib.utilities;

import junit.framework.TestCase;

public class NumberSayerTest extends TestCase {
	public void test1() {
		Object[] testinput = new Object[] {
			1, "one",
			42, "fourtytwo",
			127, "hundredtwentyseven",
			433, "fourhundredthirtythree"
		};
		for(int i = 0; i < testinput.length; i += 2) {
			assertEquals((String) testinput[i + 1], NumberSayer.getNumberName((Integer) testinput[i])); 
		}
	}
}
