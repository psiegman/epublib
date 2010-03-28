package nl.siegmann.epublib.domain;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;

public class ZipEntryResource extends ByteArrayResource implements Resource {

	public ZipEntryResource(ZipEntry zipEntry, ZipInputStream zipInputStream) throws IOException {
		super(zipEntry.getName(), IOUtils.toByteArray(zipInputStream));
	}
}
