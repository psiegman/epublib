package nl.siegmann.epublib.util;

import junit.framework.TestCase;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.StringResource;

public class ResourceUtilTest extends TestCase {

	public void testFindTitle() {
		String[] testData = new String[] {
				"<html><title>my title1</title><body><h1>wrong title</h1></body></html>", "my title1",
				"<html><tiTle>my title2</titlE><body><h1>wrong title</h1></body></html>", "my title2",
				"<html><body><h1>my h1 title3</h1></body></html>", "my h1 title3",
				"<html><body><H1>my h1 title4</h1></body></html>", "my h1 title4",
				"<html><body><H1 class=\"main\">my h1 title5</h1></body></html>", "my h1 title5",
				"<html><body><XH1 class=\"main\">wrong title</Xh1><h2>test title 6</h2></body></html>", "test title 6",
		};
		for (int i = 0; i < testData.length; i+= 2) {
			Resource resource = new StringResource(testData[i]);
			String actualTitle = ResourceUtil.findTitleFromXhtml(resource);
			assertEquals(testData[i + 1], actualTitle);
		}
	}
}
