package nl.siegmann.epublib.domain;

import java.util.Arrays;
import java.util.Collection;

public class MediaType {
	private String name;
	private String defaultExtension;
	private Collection<String> extensions;
	
	public MediaType(String name, String defaultExtension) {
		this(name, defaultExtension, new String[] {defaultExtension});
	}

	public MediaType(String name, String defaultExtension,
			String[] extensions) {
		this(name, defaultExtension, Arrays.asList(extensions));
	}
	
	
	public MediaType(String name, String defaultExtension,
			Collection<String> extensions) {
		super();
		this.name = name;
		this.defaultExtension = defaultExtension;
		this.extensions = extensions;
	}


	public String getName() {
		return name;
	}


	public String getDefaultExtension() {
		return defaultExtension;
	}


	public Collection<String> getExtensions() {
		return extensions;
	}
	
	public boolean equals(Object otherMediaType) {
		if(! (otherMediaType instanceof MediaType)) {
			return false;
		}
		return name.equals(((MediaType) otherMediaType).getName());
	}
}
