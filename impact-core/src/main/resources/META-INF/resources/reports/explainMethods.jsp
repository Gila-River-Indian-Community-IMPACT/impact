<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>

<af:showDetailHeader text="Methods Explained" size="1" disclosed="false">
	<af:outputFormatted styleUsage="instruction" value="Methods are arranged by decreasing accuracy." />
	<af:objectSpacer height="10px" />	
	<af:table value="#{reportProfile.explainMethods}" bandingInterval="1"
		binding="#{reportProfile.explainMethods.table}" id="methodsExplained"
		banding="row" width="98%" var="method">
		<af:column formatType="text" headerText="Method" noWrap="true" sortable="false"
			sortProperty="description">
			<af:outputText value="#{method.description}" />
		</af:column>
		<af:column formatType="text" headerText="Description" sortable="false"
			sortProperty="explanation">
			<af:outputText value="#{method.explanation}" />
		</af:column>
	</af:table>
</af:showDetailHeader>
