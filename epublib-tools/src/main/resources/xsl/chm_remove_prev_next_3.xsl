<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output
	method="xml"
	encoding="UTF-8"
/>
 
<xsl:template match="/xhtml:html/xhtml:body">
	<xhtml:body>
		<xsl:apply-templates select="xhtml:div/xhtml:div/*"/>
	</xhtml:body>
</xsl:template>

<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>

</xsl:stylesheet>

