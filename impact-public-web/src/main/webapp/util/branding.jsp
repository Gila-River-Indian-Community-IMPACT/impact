<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>

<f:facet name="branding">
	<af:panelGroup layout="vertical">
		<af:objectImage source="/images/stars2.png" shortDesc="Impact logo" />
		<af:outputText value="Version 10.1 | #{infraDefs.mavenVersion}"
			inlineStyle="font-size:9px" />
	</af:panelGroup>
</f:facet>
<f:facet name="messages">
	<af:messages />
</f:facet>