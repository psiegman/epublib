package nl.siegmann.epublib.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

public class IOUtilTest {

	@Test
	public void testToByteArray1() {
		byte[] testArray = new byte[Byte.MAX_VALUE - Byte.MIN_VALUE];
		for (int i = Byte.MIN_VALUE; i < Byte.MAX_VALUE; i++) {
			testArray[i - Byte.MIN_VALUE] = (byte) i;
		}
		try {
			byte[] result = IOUtil.toByteArray(new ByteArrayInputStream(testArray));
			assertTrue(Arrays.equals(testArray, result));
		} catch (IOException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testToByteArray2() {
		byte[] testArray = new byte[IOUtil.IO_COPY_BUFFER_SIZE + 1];
		Random random = new Random();
		random.nextBytes(testArray);
		try {
			byte[] result = IOUtil.toByteArray(new ByteArrayInputStream(testArray));
			assertTrue(Arrays.equals(testArray, result));
		} catch (IOException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testCopyInputStream1() {
		byte[] testArray = new byte[(IOUtil.IO_COPY_BUFFER_SIZE * 3) + 10];
		Random random = new Random();
		random.nextBytes(testArray);
		try {
			ByteArrayOutputStream result = new ByteArrayOutputStream();
			int copySize = IOUtil.copy(new ByteArrayInputStream(testArray), result);
			assertTrue(Arrays.equals(testArray, result.toByteArray()));
			assertEquals(testArray.length, copySize);
		} catch (IOException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	@Test
	public void testCalcNrRead() {
		Integer[] testData = new Integer[] {
			// nrRead, totalNrRead, reault
			0, 0, 0,
			1, 1, 2,
			10, Integer.MAX_VALUE - 10, Integer.MAX_VALUE,
			1, Integer.MAX_VALUE - 1, Integer.MAX_VALUE,
			10, Integer.MAX_VALUE - 9, -1
		};
		for (int i = 0; i < testData.length; i += 3) {
			int actualResult = IOUtil.calcNewNrReadSize(testData[i], testData[i + 1]);
			int expectedResult = testData[i + 2];
			assertEquals((i / 3) + " : " + testData[i] + ", " + testData[i + 1], expectedResult, actualResult);
		}
	}
}
