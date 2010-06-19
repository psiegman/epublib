package nl.siegmann.epublib.epub;

import java.nio.charset.Charset;

import junit.framework.TestCase;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.fileset.FilesetBookCreator;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.NameScope;
import org.apache.commons.vfs.VFS;

public class FilesetBookCreatorTest extends TestCase {

	public void test1() {
		try {
			FileSystemManager fsManager = VFS.getManager();
			FileObject dir = fsManager.resolveFile("ram://test-dir");
			dir.createFolder();
			FileObject chapter1 = dir.resolveFile("chapter1.html", NameScope.CHILD);
			chapter1.createFile();
			IOUtils.copy(this.getClass().getResourceAsStream("/book1/chapter1.html"), chapter1.getContent().getOutputStream());
			Book bookFromDirectory = FilesetBookCreator.createBookFromDirectory(dir, Charset.forName("UTF-8"));
			assertEquals(1, bookFromDirectory.getResources().size());
			assertEquals(1, bookFromDirectory.getSpineSections().size());
			assertEquals(1, bookFromDirectory.getTocSections().size());
		} catch(Exception e) {
			assertTrue(false);
		}
	}
}
