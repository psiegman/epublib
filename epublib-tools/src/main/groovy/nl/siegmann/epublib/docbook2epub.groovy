/*
 * Copyright 2009 Paul Siegmann <paul@siegmann.nl>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.ccil.cowan.tagsoup.Parser
import groovy.xml.*
import org.apache.commons.io.FileUtils 
import java.util.zip.*
import nl.siegmann.epublib.*
import nl.siegmann.epublib.domain.*

// the directory where the userguide xml files are located:
inputXmlDir = '/home/paul/project/private/gradledoc/foo/gradle-0.8/src/docs/userguide'

// the directory where the generated userguide html files are located
inputHtmlDir = '/home/paul/opt/gradle-0.8/docs/userguide'

// files in the generated userguide html file directory that should not be included in the resulting epub file
excludeFiles = ['userguide_single.html', 'userguide.pdf']

// the filename of the resulting epub file
resultingEpubFile = '/home/paul/gradleuserguide.epub'

// the subjects of this book
subjects = ['gradle']



targetDir = createTmpDir().getAbsolutePath()

fileIdLookup = [:]
contentDir = 'OEBPS'
CONTENTYPE_XHTML = 'application/xhtml+xml'

tagsoupParser = createTagsoupParser()

def createTagsoupParser() {
	def result = new XmlParser(new Parser())
	return result
}

/**
 * Read in the main docbook index file
 */
def getIndexXml() {
	new XmlParser().parse(new File(inputXmlDir + File.separator + 'userguide.xml'))
}


/**
 * Extract metadata from the main docbook file
 */
def processIndexXml(indexXml) {
	def book = new Book()
	book.title = indexXml.bookinfo.title.text() + ' - ' + indexXml.bookinfo.subtitle.text()
	indexXml.bookinfo.author.each() {
		book.authors << new Author(it.firstname.text(), it.surname.text())
	}
	book.rights = indexXml.bookinfo.legalnotice.para.text()
	indexXml.children().each() {
		if (it.name().toString() == '{http://www.w3.org/2001/XInclude}include') {
			def chapterXml = new XmlParser().parse(new File(inputXmlDir + File.separator + it['@href']))
			def chapterId = chapterXml['@id']
			def chapterName = chapterXml.title.text()
			def chapterFile = chapterId + '.html'
			book.sections << new Section(chapterId, chapterName, chapterFile)
			fileIdLookup[chapterFile] = chapterId
		}
	}
	return book
}

/**
 * Write the epub mime type to the 'mimetype' file
 */
def writeMimetype() {
	def mimetype = 'application/epub+zip'
	new File(targetDir + File.separator + 'mimetype').withWriter{ it << mimetype }
}


/**
 * Write the container xml file
 */
def writeContainer() {
	new File(targetDir + File.separator + 'META-INF').mkdir()
	def writer = new FileWriter(targetDir + File.separator + 'META-INF' + File.separator + 'container.xml')
	def markupBuilder = new MarkupBuilder(writer)
	markupBuilder.setDoubleQuotes(true)
	markupBuilder.container(version: '1.0', xmlns: 'urn:oasis:names:tc:opendocument:xmlns:container') {
		rootfiles {
			rootfile('full-path': contentDir + '/content.opf', 'media-type': 'application/oebps-package+xml')
		}
	}
}


/**
 * Get the mimetype using the filename's extension
 */
def getMediaType(filename) {
	def result = ''
	filename = filename.toLowerCase()
	if(filename.endsWith(".html") || filename.endsWith(".htm")) {
		result = CONTENTYPE_XHTML
	} else if(filename.endsWith('.jpg') || filename.endsWith(".jpeg")) {
		result = 'image/jpeg'
	} else if(filename.endsWith('.png')) {
		result = 'image/png'
	} else if(filename.endsWith('.gif')) {
		result = 'image/gif'
	} else if(filename.endsWith('.css')) {
		result = 'text/css'
	}
	return result
}

/**
 * Write the package file
 */
def writePackage(book) {
	new File(targetDir + File.separator + contentDir).mkdir()
	def packageWriter = new FileWriter(new File(targetDir + File.separator + contentDir + File.separator + 'content.opf'))
	def markupBuilder = new MarkupBuilder(packageWriter)
	markupBuilder.setDoubleQuotes(true)
	markupBuilder.'package'(xmlns: "http://www.idpf.org/2007/opf",  'unique-identifier': "BookID",  version: "2.0") {
		metadata('xmlns:dc': "http://purl.org/dc/elements/1.1/", 'xmlns:opf': "http://www.idpf.org/2007/opf") {
			'dc:identifier'(id: "BookID", 'opf:scheme': "UUID", book.uid)
			'dc:title' (book.title)
			book.authors.each() { author ->
				'dc:creator' ('opf:role' : "aut", 'opf:file-as': author.lastname + ', ' + author.firstname, author.firstname + ' ' + author.lastname)
			}
			book.subjects.each() { subject ->
				'dc:subject'(subject)
			}
			'dc:date' (book.date.format('yyyy-MM-dd'))
			'dc:language'(book.language)
			if (book.rights) {
				'dc:rights' (book.rights)
			}
		}
		manifest {
			item( id: "ncx", href: "toc.ncx", 'media-type': "application/x-dtbncx+xml")
			copyAndIndexContentFiles(markupBuilder, new File(inputHtmlDir))
		}
		spine (toc: 'ncx') {
			book.sections.each() {
				itemref(idref: it.id)
			}
		}
	}
}

/**
 * Copy the epub content files from the source to the target dir and add the file as an item to the manifest
 */
def copyAndIndexContentFiles(markupBuilder, startDir) {
	startDir.eachFileRecurse({
		if(it.getName() in excludeFiles) {
			return
		}
		def targetFile = it.path[startDir.path.length()+1..-1]
		if (it.isDirectory()) {
			new File(targetDir + File.separator + contentDir + File.separator + targetFile).mkdir()
		} else {
			def mediaType = getMediaType(targetFile)
			def target = new File(targetDir + File.separator + contentDir + File.separator + targetFile)
			if (mediaType == CONTENTYPE_XHTML) {
				html2XHtml(it, target)
			} else {
				FileUtils.copyFile(it, target)
			}
			markupBuilder.item(id: (fileIdLookup[targetFile] ?: targetFile), href: targetFile,  'media-type': mediaType)
		}
	})
}

/**
 * Read in a html file, convert it to xml using tagsoup and write the result to outputfile
 */
def html2XHtml(inputFile, outputFile) {
	def doc = tagsoupParser.parse(inputFile)
	postProcessHtml(doc)
	def xmlNodePrinter = new XmlNodePrinter(new PrintWriter(outputFile)) 
	xmlNodePrinter.setPreserveWhitespace(true)
	xmlNodePrinter.print(doc)
}


/**
 * Make the html more ebook friendly
 */
def postProcessHtml(htmlDoc) {
	
	// set content encoding to UTF-8
	def node = htmlDoc.head.meta.find{it.'@content' == 'text/html; charset=ISO-8859-1'}
	def UTF8_encoding = 'text/html; charset=UTF-8'
	if(node) {
		node.attributes()['content'] = UTF8_encoding
	} else {
		htmlDoc.head.appendNode('meta', ['content': UTF8_encoding])
	}
	
	// ebooks already have their own navigation
	node = htmlDoc.body.div.find{it.'@class' == 'navheader'}
	if(node) {
		node.parent().remove(node)
	}
	node = htmlDoc.body.div.find{it.'@class' == 'navfooter'}
	if(node) {
		node.parent().remove(node)
	}
}

/**
 * Write the ncx file
 */
def writeNcx(book) {
	new File(targetDir + File.separator + contentDir).mkdir()
	def ncxWriter = new FileWriter(new File(targetDir + File.separator + contentDir + File.separator + 'toc.ncx'))
	def markupBuilder = new MarkupBuilder(ncxWriter)
	markupBuilder.setDoubleQuotes(true)
	//	<!DOCTYPE ncx PUBLIC "-//NISO//DTD ncx 2005-1//EN" "http://www.daisy.org/z3986/2005/ncx-2005-1.dtd">
	markupBuilder.ncx(xmlns: "http://www.daisy.org/z3986/2005/ncx/", version:"2005-1") {
	    head {
	        meta(name: "dtb:uid", content: book.uid)
	        meta(name: "dtb:depth", content: "1")
	        meta(name: "dtb:totalPageCount", content: "0")
	        meta(name: "dtb:maxPageNumber", content: "0")
		}
	   docTitle {
	        text(book.title)
	   }
	   book.authors.each{ author ->
			docAuthor {
				text(author.lastname + ', ' + author.firstname)
			}
	   }
	   navMap {
		   book.sections.eachWithIndex {section, index ->
			   navPoint(id: "navPoint-${index}", playOrder: (index + 1), 'class': 'chapter') {
				   navLabel {
					   text((index + 1) + '. ' + section.name)
				   }
				   content(src: section.href)
			   }
		   }
		}
	}
}


/**
 * Zip up the current directory into the given filename destination parameter
 * copied from http://blog.xebia.com/2008/05/25/powerful-groovy/
 */
File.metaClass.zip = { String destination ->
	def result = new ZipOutputStream(new FileOutputStream(destination))
	result.withStream {zipOutStream->
		delegate.eachFileRecurse { f ->
			if(! f.isDirectory()) {
				def entryName = f.getPath().substring(getPath().length() + 1)
				zipOutStream.putNextEntry(new ZipEntry(entryName))
				new FileInputStream(f).withStream { inStream ->
					zipOutStream << inStream
				}
				zipOutStream.closeEntry()
			}
		}
	}
	return new File(destination)
}


def zipFiles(filename) {
	new File(targetDir).zip(filename)
}

/**
 * Create a new temporary subdirectory in the java temp directory
 */
def createTmpDir() {
	final File temp;
	
	temp = File.createTempFile("temp", Long.toString(System.nanoTime()));
	
	if(!(temp.delete())) {
		throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
	}
	
	if(!(temp.mkdir()))	{
		throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
	}
	
	return temp;
	
}

//println 'tmpdir:' + targetDir

doc = getIndexXml()
def book = processIndexXml(doc)
book.subjects = subjects

writeMimetype()
writeContainer()
writePackage(book)

new File(targetDir + File.separator + contentDir).mkdir()
def ncxFile = new File(targetDir + File.separator + contentDir + File.separator + 'toc.ncx')
NCX.write(book, ncxFile)

//writeNcx(book)

def finalResult = zipFiles(resultingEpubFile)

FileUtils.forceDelete(new File(targetDir))

println 'written epub file to: ' + finalResult.getAbsolutePath()