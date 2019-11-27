autoScalaLibrary := false

crossPaths := false

name := "epublib-core"

organization := "nl.siegmann.epublib"

version := "4.0"

publishMavenStyle := true

javacOptions in doc += "-Xdoclint:none"

libraryDependencies +=  "net.sf.kxml" % "kxml2" % "2.3.0" 

libraryDependencies +=  "xmlpull" % "xmlpull" % "1.1.3.4d_b4_min" 

libraryDependencies +=  "org.slf4j" % "slf4j-api" % "1.6.1" 

libraryDependencies +=  "org.slf4j" % "slf4j-simple" % "1.6.1" 

libraryDependencies +=  "junit" % "junit" % "4.10" 


