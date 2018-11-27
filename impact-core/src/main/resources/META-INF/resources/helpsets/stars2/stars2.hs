<?xml version="1.0" encoding="ISO-8859-1" ?>

<helpset>
<!-- This version of the STARS2 Help Guide was created for initial STARS2 HELP, based on OHJ rel. 4.2.7 
and OHW rel. 2.0.11 -->

<title>System Help Guide</title>

<maps>
	<mapref location="map.xml"/>
</maps>

<!--
<links>
	<linkref location="link.xml"/>
</links>
-->

<view>
	<label>Contents</label>
	<title>Table of Contents</title>
	<type>oracle.help.navigator.tocNavigator.TOCNavigator</type>
	<data engine="oracle.help.engine.XMLTOCEngine">toc.xml</data>
</view>

<!-- comment out Index tab - not yet implemented (i.e., no appropriate index.xml file)
<view>
   <label>Index</label>
   <title>Keyword Index</title>
   <type>oracle.help.navigator.keywordNavigator.KeywordNavigator</type>
   <data engine="oracle.help.engine.XMLIndexEngine">index.xml</data>
</view>
-->
 
<view>
<!-- <name>OHJ_WIZARD_GENERATED_SEARCH</name> -->
	<label>Search</label>
	<title>Full Text Search</title>
	<type>oracle.help.navigator.searchNavigator.SearchNavigator</type>
	<data engine="oracle.help.engine.SearchEngine">stars2.idx</data>
</view>

<!-- DO WE NEED TO PROVIDE JAVADOC FOR OUR STUFF?
<subhelpset location="javadoc/javadoc.hs"/>
-->

</helpset>


