package nl.siegmann.epublib.fileset;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;
import nl.siegmann.epublib.domain.Book;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.NameScope;
import org.apache.commons.vfs.VFS;

public class FilesetBookCreatorTest extends TestCase {
	
	public void test1() {
		try {
			FileObject dir = createDirWithSourceFiles();
			Book book = FilesetBookCreator.createBookFromDirectory(dir);
			assertEquals(5, book.getSpine().size());
			assertEquals(5, book.getTableOfContents().size());
		} catch(Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	public void test2() {
		try {
			FileObject dir = createDirWithSourceFiles();
			
			// this file should be ignored
			copyInputStreamToFileObject(new ByteArrayInputStream("hi".getBytes()), dir, "foo.nonsense");
			
			Book book = FilesetBookCreator.createBookFromDirectory(dir);
			assertEquals(5, book.getSpine().size());
			assertEquals(5, book.getTableOfContents().size());
		} catch(Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	private FileObject createDirWithSourceFiles() throws IOException {
		FileSystemManager fsManager = VFS.getManager();
		FileObject dir = fsManager.resolveFile("ram://fileset_test_dir");
		dir.createFolder();
		String[] sourceFiles = new String[] {
				"book1.css",
				"chapter1.html",
				"chapter2_1.html",
				"chapter2.html",
				"chapter3.html",
				"cover.html",
				"flowers_320x240.jpg",
				"test_cover.png"
		};
		String testSourcesDir = "/book1";
		for (String filename: sourceFiles) {
			String sourceFileName = testSourcesDir + "/" + filename;
			copyResourceToFileObject(sourceFileName, dir, filename);
		}
		return dir;
	}
	
	private void copyResourceToFileObject(String resourceUrl, FileObject targetDir, String targetFilename) throws IOException {
		InputStream inputStream = this.getClass().getResourceAsStream(resourceUrl);
		copyInputStreamToFileObject(inputStream, targetDir, targetFilename);
	}
	
	private void copyInputStreamToFileObject(InputStream inputStream, FileObject targetDir, String targetFilename) throws IOException {
		FileObject targetFile = targetDir.resolveFile(targetFilename, NameScope.DESCENDENT);
		targetFile.createFile();
		IOUtils.copy(inputStream, targetFile.getContent().getOutputStream());
		targetFile.getContent().close();
	}
}
