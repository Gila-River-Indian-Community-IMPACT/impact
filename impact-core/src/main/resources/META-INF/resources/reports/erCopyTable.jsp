<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:table value="#{reportProfile.selectRptList}" bandingInterval="1"
	banding="row" var="reportResults">
	<af:column sortProperty="emissionsInventoryId" sortable="true"
		headerText="Inventory ID" formatType="text">
		<af:commandLink action="#{reportProfile.startReportCopy}">
			<af:outputText value="#{reportResults.emissionsInventoryId}" />
			<t:updateActionListener property="#{reportProfile.selectedReportId}"
				value="#{reportResults.emissionsRptId}" />
		</af:commandLink>
	</af:column>
	<af:column sortProperty="emissionsInventoryModifiedId" sortable="true"
		headerText="Previous Inventory" formatType="text">
		<af:commandLink action="#{reportProfile.startReportCopy}">
			<af:outputText value="#{reportResults.emissionsInventoryModifiedId}" />
			<t:updateActionListener property="#{reportProfile.selectedReportId}"
				value="#{reportResults.reportModified}" />
		</af:commandLink>
	</af:column>
	<af:column sortProperty="fpId" sortable="true" headerText="Facility History ID"
		formatType="text" noWrap="true">
		<af:outputText value="Current (#{reportResults.fpId})"
			rendered="#{reportResults.versionId == -1}" />
		<af:outputText value="#{reportResults.fpId}"
			rendered="#{reportResults.versionId != -1}" />
	</af:column>
	<af:column sortProperty="facilityId" sortable="true"
		headerText="Facility ID" formatType="text" noWrap="true"
		rendered="#{reportSearch.fromFacility == 'false'}">
		<af:outputText value="#{reportResults.facilityId}" />
	</af:column>
	<af:column sortProperty="facilityName" sortable="true"
		headerText="Facility Name" formatType="text"
		rendered="#{reportSearch.fromFacility == 'false'}">
		<af:outputText value="#{reportResults.facilityName}" />
	</af:column>
	<af:column sortProperty="years" sortable="true" headerText="Year"
		formatType="text">
		<af:outputText value="#{reportResults.years}" />
	</af:column>
	<af:column sortProperty="contentTypeCd" sortable="true"
		headerText="Content Type" formatType="text">
		<af:selectOneChoice id="contentType" readOnly="true"
			value="#{reportResults.contentTypeCd}">
			<f:selectItems
				value="#{facilityReference.contentTypeDefs.allSearchItems}" />
		</af:selectOneChoice>
	</af:column>
	<af:column sortProperty="regulatoryRequirementCdsString"
		sortable="true" headerText="Regulatory Requirements" formatType="text">
		<af:outputText value="#{reportResults.regulatoryRequirementCdsString}" />
	</af:column>
	<af:column sortProperty="reportingState" sortable="true"
		headerText="Reporting State" formatType="icon">
		<af:outputText
			value="#{reportResults.reportingStateDesc}" />
	</af:column>
	<%--
	<af:column sortProperty="eisStatusDesc" sortable="true" headerText="EIS State"
		formatType="icon">
		<af:outputText
			value="#{reportResults.eisStatusDesc}" />
	</af:column>
	--%>
	<af:column sortProperty="receivedDate" sortable="true"
		headerText="Received Date" formatType="text">
		<af:selectInputDate readOnly="true" value="#{reportResults.receivedDate}" />
	</af:column>
	<%--
		<af:column sortProperty="lowRange" sortable="true" headerText="Range"
			formatType="text">
			<af:outputText value="#{reportResults.emissionRange}" />
		</af:column>
		<af:column sortProperty="totalEmissionsDigits" sortable="true"
			headerText="Chargeable" formatType="number">
			<af:outputText value="#{reportResults.totalEmissionsDigits}" />
		</af:column>
		--%>
		<af:column sortProperty="totalReportedEmissionsDigits" sortable="true" headerText="Total Emissions Reported (Tons)"
			formatType="number">
			<af:outputText value="#{reportResults.totalReportedEmissionsDigits}" />
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
</af:table>
