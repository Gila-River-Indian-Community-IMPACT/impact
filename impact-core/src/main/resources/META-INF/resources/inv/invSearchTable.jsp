<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:table emptyText =" " value="#{invoiceSearch.invoices}" width="98%"
	bandingInterval="1" banding="row" var="invoiceResults"
	rows="#{invoiceSearch.pageLimit}">
	<af:column sortProperty="invoiceId" sortable="true"
		formatType="text" headerText="Invoice ID">
		<af:commandLink action="#{invoiceDetail.submit}">
			<af:outputText value="#{invoiceResults.invoiceId}" />
			<t:updateActionListener property="#{invoiceDetail.invoiceId}"
				value="#{invoiceResults.invoiceId}" />				
		</af:commandLink>
	</af:column>
	<af:column sortProperty="facilityId" sortable="true"
		formatType="text" noWrap="true" headerText="Facility ID"
		rendered="#{invoiceSearch.fromFacility == 'false'}">
		<af:commandLink action="#{facilityProfile.submitProfile}">
			<af:outputText value="#{invoiceResults.facilityId}" />
			<t:updateActionListener property="#{facilityProfile.fpId}"
				value="#{invoiceResults.fpId}" />
		</af:commandLink>
	</af:column>
	<af:column sortProperty="facilityName" sortable="true"
		formatType="text" headerText="Facility Name" 
		rendered="#{invoiceSearch.fromFacility == 'false'}">
		<af:outputText value="#{invoiceResults.facilityName}" />
	</af:column>
	<af:column sortProperty="doLaaCd" sortable="true"
		formatType="text" headerText="District" 
		rendered="#{invoiceSearch.fromFacility == 'false'}">
		<af:selectOneChoice inlineStyle="#{infraDefs.hidden}"
            readOnly="true" value="#{invoiceResults.doLaaCd}">
			<f:selectItems value="#{invoiceSearch.doLaas}" />
		</af:selectOneChoice>
	</af:column>
	<af:column sortProperty="revenueId" sortable="true"
		formatType="number" headerText="Revenue ID">
		<af:outputText value="#{invoiceResults.revenueId}" />
	</af:column>
	<af:column sortProperty="revenueTypeCd" sortable="true"
		formatType="text" headerText="Revenue Type">
		<af:outputText value="#{invoiceResults.revenueTypeCd}" />
	</af:column>
	<af:column sortProperty="emissionsRptId" sortable="true"
		formatType="text" headerText="Report/ Permit ID" noWrap="true">
		<af:commandLink action="#{reportDetail.submit}"
			rendered="#{! empty invoiceResults.emissionsRptId}">
			<af:outputText value="#{invoiceResults.emissionsRptId}" />
			<t:updateActionListener property="#{reportDetail.reportId}"
				value="#{invoiceResults.emissionsRptId}" />
			<t:updateActionListener property="#{reportDetail.emissionsRptCd}"
				value="#{invoiceResults.emissionsRptCd}" />				
			<t:updateActionListener property="#{reportDetail.fromTODOList}"
          		value="false" />
		</af:commandLink>
		<af:commandLink action="#{permitDetail.loadPermit}"
			rendered="#{! empty invoiceResults.permitId}">
			<af:outputText value="#{invoiceResults.permitId}" />
			<t:updateActionListener property="#{permitDetail.permitID}"
				value="#{invoiceResults.permitId}" />
		</af:commandLink>
	</af:column>	
	<af:column sortProperty="invoiceStateDesc" sortable="true"
		formatType="text" headerText="Stars2 Invoice State">
		<af:outputText value="#{invoiceResults.invoiceStateDesc}" />
	</af:column>
	<af:column sortProperty="revenueStateDesc" sortable="true"
		formatType="text" headerText="Revenue State">
		<af:outputText value="#{invoiceResults.revenueStateDesc}"
		rendered="#{invoiceResults.posted}" />
		<af:outputText rendered="#{invoiceResults.revenueStateDesc2 != null && invoiceResults.posted}"
			value="#{invoiceResults.revenueStateDesc2}"
			inlineStyle="color: orange; font-weight: bold;" />
	</af:column>
	<af:column sortProperty="creationDate" sortable="true"
		formatType="text" headerText="Invoice Date">		
		<af:selectInputDate readOnly="true"
        	value="#{invoiceResults.creationDate}"
        	rendered="#{invoiceResults.posted}" />	
	</af:column>
	<af:column sortProperty="dueDate" sortable="true"
		formatType="text" headerText="Due Date">
		<af:selectInputDate readOnly="true"
        	value="#{invoiceResults.dueDate}"
        	rendered="#{invoiceResults.posted}" />
	</af:column>
	<af:column sortProperty="origAmount" sortable="true"
		formatType="number" headerText="Stars2 Amount">
		<af:outputText value="#{invoiceResults.origAmount}">
			<af:convertNumber type="currency" currencySymbol="$" />
		</af:outputText>
	</af:column>
	<af:column sortProperty="originalAmount" sortable="true"
		formatType="number" headerText="Original Amount">
		<af:outputText value="#{invoiceResults.originalAmount}">
			<af:convertNumber type="currency" currencySymbol="$" />
		</af:outputText>
	</af:column>
	<af:column sortProperty="currentAmount" sortable="true"
		formatType="number" headerText="Current Amount">
		<af:outputText value="#{invoiceResults.currentAmount}">
			<af:convertNumber type="currency" currencySymbol="$" />
		</af:outputText>
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

