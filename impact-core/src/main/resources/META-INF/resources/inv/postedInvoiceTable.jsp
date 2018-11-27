<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:table value="#{postedInvoices}" var="invoice" rows="25" width="760">
	<af:column headerText="Facility ID" sortable="true"
		sortProperty="facilityId" formatType="text">
		<af:panelHorizontal valign="middle" halign="left">
			<af:inputText readOnly="true" value="#{invoice.facilityId}"
				inlineStyle="white-space: nowrap;" />
		</af:panelHorizontal>
	</af:column>
	<af:column headerText="Facility Name" sortable="true"
		sortProperty="facilityName">
		<af:panelHorizontal valign="middle" halign="left">
			<af:inputText readOnly="true" value="#{invoice.facilityName}" />
		</af:panelHorizontal>
	</af:column>
	<af:column headerText="Invoice State" sortable="true"
		sortProperty="invoiceStateCd" formatType="text">
		<af:panelHorizontal valign="middle" halign="left">
			<af:inputText readOnly="true" value="#{invoice.invoiceStateDsc}" />
		</af:panelHorizontal>
	</af:column>
	<af:column headerText="Posted Amount" sortable="true" rendered="#{empty docsZipFile.fileName}"
		sortProperty="origAmount" formatType="number">
		<af:panelHorizontal valign="middle" halign="right">
			<af:inputText readOnly="true" value="#{invoice.prevInvoice != null?invoice.compInvoice.origAmount:invoice.origAmount}">
				<af:convertNumber type='currency' locale="en-US"
					minFractionDigits="2" />
			</af:inputText>
		</af:panelHorizontal>
	</af:column>	
	
	<af:column headerText="Printed Amount" sortable="true" rendered="#{! empty docsZipFile.fileName}"
		formatType="number">
		<af:panelHorizontal valign="middle" halign="right">
			<af:inputText readOnly="true" value="#{invoice.revenue.balanceDue}">				
			</af:inputText>
		</af:panelHorizontal>
	</af:column>
	
	<af:column headerText="Revenue ID" sortable="true"
		sortProperty="revenueId" formatType="number">
		<af:panelHorizontal valign="middle" halign="left">
			<af:inputText readOnly="true" value="#{invoice.revenueId}" />
		</af:panelHorizontal>
	</af:column>
	<af:column headerText="Invoice Document" formatType="text"
		rendered="#{! reportDetail.postState}">
		<h:outputLink
			rendered="#{! empty invoice.emissionsRptId}"
			value="#{invoice.reportInvDocument.docURL}"
			title="View Report Document">
			<af:outputText
				value="#{invoice.reportInvDocument.description}" />
		</h:outputLink>
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
