## Epub Viewer

A simple epub viewer built with java Swing.

### Startup

	java nl.siegmann.epublib.viewer.Viewer

## Fileset2epub

A tool to generate an epub from a windows help / chm file or from a set of html files.

	java nl.siegmann.epublib.Fileset2Epub

Arguments:

	--author [lastname,firstname]
	--cover-image [image to use as cover]
	--input-ecoding [text encoding]  # The encoding of the input html files. If funny characters show
	                                 # up in the result try 'iso-8859-1', 'windows-1252' or 'utf-8'
	                                 # If that doesn't work try to find an appropriate one from
	                                 # this list: http://en.wikipedia.org/wiki/Character_encoding
	--in [input directory]
	--isbn [isbn number]
	--out [output epub file]
	--title [book title]
	--type [input type, can be 'epub', 'chm' or empty]
	--xsl [html post processing file]
