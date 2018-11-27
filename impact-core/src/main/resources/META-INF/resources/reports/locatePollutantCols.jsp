<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<af:column sortProperty="name" sortable="true"
	headerText="EU Name">
	<af:goLink text="#{emissionLine.name}"
		rendered="#{!reportProfile.publicApp}"
		shortDesc="Click to find this row of emissions in the report"
		onclick="setFocus('#{emissionLine.link}', '#{emissionLine.clientID}', '#{emissionLine.showDetailClientID}')" />
	<af:outputText value="#{emissionLine.name}"
		rendered="#{reportProfile.publicApp}">
		</af:outputText>
</af:column>
<af:column headerText="Emissions Reported">
	<af:column formatType="icon"
		sortProperty="fugitiveEmissions" sortable="true"
		headerText="Fugitive Amount">
		<af:outputText value="#{emissionLine.fugitiveEmissions}"/>
	</af:column>
	<af:column formatType="icon"
		sortProperty="stackEmissions"
		sortable="true" headerText="Stack Amount">
		<af:outputText value="#{emissionLine.stackEmissions}"/>
	</af:column>
	<af:column formatType="icon"
		sortProperty="totalEmissionsV" sortable="true"
		headerText="Total">
		<af:outputText value="#{emissionLine.totalEmissions}"/>
	</af:column>
	<af:column sortProperty="emissionsUnitNumerator"
		sortable="true" headerText="Units">
		<af:selectOneChoice value="#{emissionLine.emissionsUnitNumerator}"
			readOnly="true">
			<f:selectItems
				value="#{facilityReference.emissionUnitReportingDefs.items[(empty emissionLine.emissionsUnitNumerator ? '' : emissionLine.emissionsUnitNumerator)]}" />
		</af:selectOneChoice>
	</af:column>
</af:column>
<f:facet name="footer">
	<afh:rowLayout halign="center">
		<af:panelButtonBar>
			<af:commandButton actionListener="#{tableExporter.printTable}"
				onclick="#{tableExporter.onClickScript}" text="Printable view" />
			<af:commandButton actionListener="#{tableExporter.excelTable}"
				onclick="#{tableExporter.onClickScript}" text="Export to excel" />
		</af:panelButtonBar>
	</afh:rowLayout>
</f:facet>
