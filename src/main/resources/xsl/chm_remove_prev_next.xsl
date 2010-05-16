<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output
	method="xml"
	encoding="UTF-8"
/>
 
<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>

<!-- 
<xsl:template match="/html">
	<html xmlns="http://www.w3.org/1999/xhtml">
	    <xsl:apply-templates select="*"/>
	</html>
</xsl:template>
  -->
  
<xsl:template match="xhtml:a[@target = '_parent']">
    <xsl:apply-templates select="*"/>
</xsl:template>

<xsl:template match="xhtml:a[string(@href) = '']">
    <xsl:apply-templates select="*"/>
</xsl:template>

<xsl:template match="@border|@name|@width|@height|@align|@valign|@linktabletoexcel|@bgcolor|@type"/>

<xsl:template match="/xhtml:html/xhtml:head/xhtml:META[@http-equiv='Content-Type']"/>

<xsl:template match="/xhtml:html/xhtml:head/xhtml:meta[@http-equiv='Content-Type']"/>

<xsl:template match="/xhtml:html/xhtml:META[@http-equiv='Content-Type']">
</xsl:template> 

<xsl:template match="/xhtml:html/xhtml:meta[@http-equiv='Content-Type']">
</xsl:template> 

<xsl:template match="/xhtml:html/xhtml:body/xhtml:table">
</xsl:template>

<xsl:template match="/xhtml:html/xhtml:body/xhtml:br">
</xsl:template>

<xsl:template match="/xhtml:html/xhtml:body/xhtml:div[@class = 'chapter']">
    <xsl:apply-templates select="@*|node()"/>
</xsl:template>

<xsl:template match="/xhtml:html/xhtml:body/xhtml:a">
</xsl:template>

</xsl:stylesheet>

