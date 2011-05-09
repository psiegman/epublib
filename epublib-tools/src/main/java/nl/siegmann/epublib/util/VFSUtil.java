package nl.siegmann.epublib.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.VFS;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;

/**
 * Utitilies for making working with apache commons VFS easier.
 * 
 * @author paul
 *
 */
public class VFSUtil {
	
	private static final Logger log = LoggerFactory.getLogger(VFSUtil.class);

	/**
	 * First tries to load the inputLocation via VFS; if that doesn't work it tries to load it as a local File
	 * @param inputLocation
	 * @return
	 * @throws FileSystemException
	 */
	public static FileObject resolveFileObject(String inputLocation) throws FileSystemException {
		FileObject result = null;
		try {
			result = VFS.getManager().resolveFile(inputLocation);
		} catch (Exception e) {
			try {
				result = VFS.getManager().resolveFile(new File("."), inputLocation);
			} catch (Exception e1) {
				log.error(e.getMessage(), e);
				log.error(e1.getMessage(), e);
			}
		}
		return result;
	}

	
	/**
	 * First tries to load the inputLocation via VFS; if that doesn't work it tries to load it as a local File
	 * 
	 * @param inputLocation
	 * @return
	 * @throws FileSystemException
	 */
	public static InputStream resolveInputStream(String inputLocation) throws FileSystemException {
		InputStream result = null;
		try {
			result = VFS.getManager().resolveFile(inputLocation).getContent().getInputStream();
		} catch (Exception e) {
			try {
				result = new FileInputStream(inputLocation);
			} catch (FileNotFoundException e1) {
				log.error(e.getMessage(), e);
				log.error(e1.getMessage(), e);
			}
		}
		return result;
	}
}
