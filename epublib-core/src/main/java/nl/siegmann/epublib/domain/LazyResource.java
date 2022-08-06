package nl.siegmann.epublib.domain;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import nl.siegmann.epublib.service.MediatypeService;
import nl.siegmann.epublib.util.IOUtil;

/**
 * A Resource that loads its data only on-demand.
 * This way larger books can fit into memory and can be opened faster.
 * 
 */
public class LazyResource extends Resource {
	
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 5089400472352002866L;
	private String filename;
	private long cachedSize;
	
	private static final Logger LOG = Logger.getLogger(LazyResource.class.getName());
	
	/**
	 * Creates a Lazy resource, by not actually loading the data for this entry.
	 * 
	 * The data will be loaded on the first call to getData()
	 * 
	 * @param filename the file name for the epub we're created from.
	 * @param size the size of this resource.
	 * @param href The resource's href within the epub.
	 */
	public LazyResource(String filename, long size, String href) {
		super( null, null, href, MediatypeService.determineMediaType(href));
		this.filename = filename;
		this.cachedSize = size;
	}

    /**
     * Creates a Resource that tries to load the data, but falls back to lazy loading.
     *
     * If the size of the resource is known ahead of time we can use that to allocate
     * a matching byte[]. If this succeeds we can safely load the data.
     *
     * If it fails we leave the data null for now and it will be lazy-loaded when
     * it is accessed.
     *
     * @param in
     * @param fileName
     * @param length
     * @param href
     * @throws IOException
     */
    public LazyResource(InputStream in, String filename, int length, String href) throws IOException {
        super(null, IOUtil.toByteArray(in, length), href, MediatypeService.determineMediaType(href));
        this.filename = filename;
        this.cachedSize = length;
    }
	
	/**
	 * Gets the contents of the Resource as an InputStream.
	 * 
	 * @return The contents of the Resource.
	 * 
	 * @throws IOException
	 */
	public InputStream getInputStream() throws IOException {
		if (isInitialized()) {
			return new ByteArrayInputStream(getData());
		} else {
			return getResourceStream();
		}
	}
	
	/**
	 * Initializes the resource by loading its data into memory.
	 * 
	 * @throws IOException
	 */
	public void initialize() throws IOException {
		getData();
	}

	/**
	 * The contents of the resource as a byte[]
	 * 
	 * If this resource was lazy-loaded and the data was not yet loaded, 
	 * it will be loaded into memory at this point.
	 *  This included opening the zip file, so expect a first load to be slow.
	 * 
	 * @return The contents of the resource
	 */
	public byte[] getData() throws IOException {
		
		if ( data == null ) {
			if (LOG.isLoggable(Level.FINE)) {
				LOG.fine("Initializing lazy resource " + filename + "#" + this.getHref());
			}
			
			InputStream in = getResourceStream();
			byte[] readData = IOUtil.toByteArray(in, (int) this.cachedSize);
			if ( readData == null ) {
			    throw new IOException("Could not load the contents of entry " + this.getHref() + " from epub file " + filename);
			} else {
			    this.data = readData;
			}
			
			in.close();
		}

		return data;
	}

	
	private InputStream getResourceStream() throws FileNotFoundException,
			IOException {
		ZipFile zipFile = new ZipFile(filename);
		ZipEntry zipEntry = zipFile.getEntry(originalHref);
		if (zipEntry == null) {
			zipFile.close();
			throw new IllegalStateException("Cannot find entry " + originalHref + " in epub file " + filename);
		}
		return new ResourceInputStream(zipFile.getInputStream(zipEntry), zipFile);
	}
	
	/**
	 * Tells this resource to release its cached data.
	 * 
	 * If this resource was not lazy-loaded, this is a no-op.
	 */
	public void close() {
		if ( this.filename != null ) {
			this.data = null;
		}
	}
	
	/**
	 * Returns if the data for this resource has been loaded into memory.
	 * 
	 * @return true if data was loaded.
	 */
	public boolean isInitialized() {
		return data != null;
	}

	/**
	 * Returns the size of this resource in bytes.
	 * 
	 * @return the size.
	 */
	public long getSize() {
		if ( data != null ) {
			return data.length;
		}
		
		return cachedSize;
	}
}
