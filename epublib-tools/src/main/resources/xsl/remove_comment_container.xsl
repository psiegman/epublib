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

<xsl:template match="xhtml:div[@class = 'comment_container']"/>
<xsl:template match="xhtml:div[@class = 'comment_count']"/>
<xsl:template match="xhtml:div[@class = 'comment']"/>
<xsl:template match="xhtml:div[@class = 'oreilly-header']"/>
<xsl:template match="xhtml:div[@class = 'navheader']"/>
<xsl:template match="xhtml:div[@class = 'navfooter']"/>

</xsl:stylesheet>

