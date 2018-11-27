<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:table value="#{reportSearch.reports}" bandingInterval="1"
	width="98%" banding="row" var="reportResults">
	<af:column sortProperty="emissionsInventoryId" sortable="true"
		headerText="Inventory ID" formatType="text" width="68px">
		<af:commandLink action="#{reportDetail.submit}">
			<af:outputText value="#{reportResults.emissionsInventoryId}" />
			<t:updateActionListener property="#{reportDetail.reportId}"
				value="#{reportResults.emissionsRptId}" />
			<t:updateActionListener property="#{reportDetail.fromTODOList}"
				value="false" />
			<t:updateActionListener property="#{menuItem_TVDetail.disabled}"
				value="false" />
		</af:commandLink>
	</af:column>
	
	<af:column sortProperty="emissionsInventoryModifiedId" sortable="true"
		headerText="Previous Inventory" formatType="text" width="68px">
		<af:commandLink action="#{reportDetail.submit}">
			<af:outputText value="#{reportResults.emissionsInventoryModifiedId}" />
			<t:updateActionListener property="#{reportDetail.reportId}"
				value="#{reportResults.reportModified}" />
			<t:updateActionListener property="#{reportDetail.fromTODOList}"
				value="false" />
			<t:updateActionListener property="#{menuItem_TVDetail.disabled}"
				value="false" />
		</af:commandLink>
	</af:column>
	
	<af:column sortProperty="fpId" sortable="true"
		headerText="Facility History ID" formatType="text" noWrap="true" width="70px">
		<af:commandLink action="#{facilityProfile.submitProfile}"
			rendered="#{reportResults.versionId == -1}">
			<af:outputText value="Current (#{reportResults.fpId})" />
			<t:updateActionListener property="#{facilityProfile.fpId}"
				value="#{reportResults.fpId}" />
		</af:commandLink>
		<af:commandLink action="#{facilityProfile.submitProfile}"
			rendered="#{reportResults.versionId != -1}">
			<af:outputText value="#{reportResults.fpId}" />
			<t:updateActionListener property="#{facilityProfile.fpId}"
				value="#{reportResults.fpId}" />
		</af:commandLink>
	</af:column>
	
	<af:column sortProperty="facilityId" sortable="true"
		headerText="Facility ID" formatType="text" noWrap="true"
		rendered="#{reportSearch.fromFacility == 'false'}">
		<af:commandLink action="#{facilityProfile.submitProfileById}"
			text="#{reportResults.facilityId}">
			<t:updateActionListener property="#{facilityProfile.facilityId}"
				value="#{reportResults.facilityId}" />
			<t:updateActionListener property="#{menuItem_facProfile.disabled}"
				value="false" />
		</af:commandLink>
	</af:column>
	
	<af:column sortProperty="facilityName" sortable="true"
		headerText="Facility Name" formatType="text"
		rendered="#{reportSearch.fromFacility == 'false'}">
		<af:outputText value="#{reportResults.facilityName}" />
	</af:column>
	
	<af:column sortProperty="cmpId" sortable="true" formatType="text"
		headerText="Company ID"
		rendered="#{reportSearch.fromFacility == 'false'}">
		<af:commandLink action="#{companyProfile.submitProfile}"
			text="#{reportResults.cmpId}">
			<t:updateActionListener property="#{companyProfile.cmpId}"
				value="#{reportResults.cmpId}" />
			<t:updateActionListener
				property="#{menuItem_companyProfile.disabled}" value="false" />
		</af:commandLink>
	</af:column>
	
	<af:column sortProperty="companyName" sortable="true" formatType="text"
		headerText="Company Name" noWrap="true"
		rendered="#{reportSearch.fromFacility == 'false'}">
		<af:outputText value="#{reportResults.companyName}" />
	</af:column>
	
	<af:column sortProperty="dolaaCd" sortable="true" formatType="text"
		headerText="District" noWrap="true" rendered="#{infraDefs.districtVisible}">
		<af:selectOneChoice id="doLaa" readOnly="true" 
			value="#{reportResults.dolaaCd}">
			<f:selectItems value="#{reportSearch.doLaas}" />
		</af:selectOneChoice>
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
	<af:column sortProperty="regulatoryRequirementCdsString" sortable="true"
		headerText="Regulatory Requirements" formatType="text">
		<af:outputText
			value="#{reportResults.regulatoryRequirementCdsString}" />
	</af:column>
	
	<af:column sortProperty="reportingState" sortable="true"
		headerText="Reporting State" formatType="text">
		<af:outputText value="#{reportResults.reportingStateDesc}" />
	</af:column>
	
	<af:column sortProperty="receivedDate" sortable="true"
		headerText="Received Date" formatType="text" width="65px">
		<af:selectInputDate label="Received Date" readOnly="true"
			value="#{reportResults.receivedDate}" />
	</af:column>
	
	<af:column sortProperty="invoiceAmount" sortable="true"
		headerText="Invoice Amount" formatType="text" width="75px">
		<af:outputText value="#{reportResults.invoiceAmount}">
			<af:convertNumber type='currency'
				locale="en-US" minFractionDigits="2" />
		</af:outputText>
	</af:column>
	
	<af:column sortProperty="invoiceDate" sortable="true"
		headerText="Invoice Date" formatType="text" width="70px">
		<af:selectInputDate label="Invoice Date" readOnly="true"
			value="#{reportResults.invoiceDate}" />
	</af:column>
	
	<af:column sortProperty="paymentReceivedDate" sortable="true"
		headerText="Payment Received Date" formatType="text" width="67px">
		<af:selectInputDate label="Payment Received Date" readOnly="true"
			value="#{reportResults.paymentReceivedDate}" />
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

