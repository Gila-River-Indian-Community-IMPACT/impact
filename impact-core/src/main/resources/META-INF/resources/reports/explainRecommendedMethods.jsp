<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>

<af:showDetailHeader text="Recommended Methods" size="1"
	disclosed="true">
	<af:outputFormatted styleUsage="instruction"
		value="Methods are arranged by decreasing accuracy. When multiple methods are recommended, it is preferrable to use the method with the highest accuracy." />
	<af:objectSpacer height="10px" />	
	<af:table value="#{reportProfile.explainRecommendedMethods}"
		bandingInterval="1" binding="#{reportProfile.explainRecommendedMethods.table}"
		id="methodsRecommended" banding="row" width="98%" var="method">
		<af:column formatType="text" headerText="Method" noWrap="true" sortable="false"
			sortProperty="methodDsc">
			<af:outputText value="#{method.methodDsc}" />
		</af:column>
		<af:column formatType="text" headerText="Recommended" sortable="false"
			sortProperty="recommended" width="20%">
			<af:selectOneChoice id="recommendedCd" showRequired="true"
				autoSubmit="true" readOnly="true"
				value="#{method.recommended}">
				<f:selectItem itemLabel="Yes" itemValue="true" />
				<f:selectItem itemLabel="No" itemValue="false" />
			</af:selectOneChoice>
		</af:column>
		<af:column formatType="text" headerText="Explanation" sortable="false"
			sortProperty="reason">
			<af:outputText value="#{method.reason}" />
		</af:column>
	</af:table>
</af:showDetailHeader>
