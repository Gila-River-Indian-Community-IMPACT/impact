<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<afh:tableLayout halign="left" width="1000"
	partialTriggers="changeInvoiceDt">
	<afh:rowLayout width="90%">
		<afh:cellFormat halign="right">
			<af:inputText label="Posted Date:" readOnly="True" />
		</afh:cellFormat>
		<afh:cellFormat halign="left">
		    <af:selectInputDate readOnly="true" value="#{invoiceDetail.invoice.revenueImmediate.postingDate}" />
		</afh:cellFormat>
		<afh:cellFormat halign="right">
			<af:inputText label="Revenues Original Amount:" readOnly="True" />
		</afh:cellFormat>
		<afh:cellFormat width="50" halign="left">
			<af:inputText readOnly="True"
				value="#{invoiceDetail.invoice.revenue.originalAmount}" />
		</afh:cellFormat>
		<afh:cellFormat columnSpan="2" halign="center">
			<af:outputLabel value="#{invoiceDetail.contactComment}"
							inlineStyle="color:rgb(0,0,255);" />
		</afh:cellFormat>
	</afh:rowLayout>

	<afh:rowLayout width="90%">
		<afh:cellFormat halign="right">
			<af:inputText label="Due Date:" readOnly="True" />
		</afh:cellFormat>
		<afh:cellFormat halign="left">
			<af:selectInputDate readOnly="true" value="#{invoiceDetail.invoice.revenueImmediate.dueDate}" />
		</afh:cellFormat>
		<afh:cellFormat halign="right">
			<af:inputText label="Revenues Current Amount:" readOnly="True" />
		</afh:cellFormat>
		<afh:cellFormat width="50" halign="left">
			<af:inputText readOnly="True"
				value="#{invoiceDetail.invoice.revenue.currentAmount}" />
		</afh:cellFormat>
		<afh:cellFormat halign="right">
			<af:inputText label="Billing Contact Name:" readOnly="True" />
		</afh:cellFormat>
		<afh:cellFormat halign="left">
			<af:inputText readOnly="True" 
				value="#{invoiceDetail.invoice.name} #{invoiceDetail.invoice.title}" />
		</afh:cellFormat>	
	</afh:rowLayout>
	
	<afh:rowLayout width="90%">
		<afh:cellFormat halign="right">
			<af:inputText label="Invoice Date:" readOnly="True" />
		</afh:cellFormat>
		<afh:cellFormat halign="left">
			<af:selectInputDate readOnly="true" value="#{invoiceDetail.invoice.revenueImmediate.effectiveDate}"
				rendered="#{! empty invoiceDetail.invoice.revenueImmediate}" />

			<af:selectInputDate value="#{invoiceDetail.invoiceDate}"
				readOnly="#{! invoiceDetail.editable}"
				rendered="#{empty invoiceDetail.invoice.revenueImmediate}"
				showRequired="true" >
				<af:validateDateTimeRange minimum="#{invoiceDetail.minInvoiceDate}"
				maximum="#{invoiceDetail.maxInvoiceDate}" />
			</af:selectInputDate>
		</afh:cellFormat>
		<afh:cellFormat halign="right">
			<af:inputText label="Balance Due:" readOnly="True" />
		</afh:cellFormat>
		<afh:cellFormat width="50" halign="left">
			<af:inputText readOnly="True"
				value="#{invoiceDetail.invoice.revenue.balanceDue}" />
		</afh:cellFormat>
		
		<afh:cellFormat halign="right" valign="top">
			<af:inputText label="Billing Address:" readOnly="true" />
		</afh:cellFormat>
		<afh:cellFormat halign="left">
			<af:inputText columns="35" inlineStyle="border-width:0px"
				value="#{invoiceDetail.invoice.billingAddressLine1}" readOnly="True" />
		
			<af:inputText columns="35" inlineStyle="border-width:0px"
				value="#{invoiceDetail.invoice.billingAddressLine2}" readOnly="True" />
		</afh:cellFormat>


	</afh:rowLayout>

	<afh:rowLayout width="90%">
		<afh:cellFormat halign="right">
			<af:inputText label="Created Date:" readOnly="True" />
		</afh:cellFormat>
		<afh:cellFormat halign="left">
			<af:selectInputDate readOnly="True"
				value="#{invoiceDetail.invoice.creationDate}" />
		</afh:cellFormat>
		<afh:cellFormat halign="right">
			<af:inputText label="Stars2 Amount:" readOnly="true" />
		</afh:cellFormat>
		<afh:cellFormat width="50" halign="left">
			<af:inputText readOnly="#{! invoiceDetail.editable}"
				value="#{invoiceDetail.invoice.origAmount}" columns="20"
				rendered="#{! invoiceDetail.editable}">
				<af:convertNumber type='currency' locale="en-US"
					minFractionDigits="2" />
			</af:inputText>

			<af:inputText readOnly="#{! invoiceDetail.editable}"
				value="#{invoiceDetail.invoice.origAmount}" columns="10"
				rendered="#{invoiceDetail.editable}">
				<af:convertNumber pattern="###,##0.##" minFractionDigits="2" />
			</af:inputText>	
		</afh:cellFormat>
		<afh:cellFormat></afh:cellFormat>
		
		<afh:cellFormat halign="left">
			<af:inputText columns="35" inlineStyle="border-width:0px"
				value="#{invoiceDetail.invoice.billingCityState}" readOnly="True" />
		</afh:cellFormat>

	</afh:rowLayout>

	<afh:rowLayout width="90%">
		<afh:cellFormat halign="right">
			<af:inputText rendered="#{! empty invoiceDetail.invoice.emissionsRptId}"
				label="Report Id:" readOnly="True" />
			<af:inputText rendered="#{! empty invoiceDetail.invoice.permitId}"
				label="Permit Id:" readOnly="True" />
		</afh:cellFormat>
		<afh:cellFormat halign="left" width="120">
			<af:commandLink
				rendered="#{! empty invoiceDetail.invoice.emissionsRptId}"
				action="#{reportDetail.submit}">
				<af:outputText value="#{invoiceDetail.invoice.emissionsRptId}" />
				<t:updateActionListener property="#{reportDetail.reportId}"
					value="#{invoiceDetail.invoice.emissionsRptId}" />
				<t:updateActionListener property="#{reportDetail.emissionsRptCd}"
				value="#{invoiceDetail.invoice.emissionsRptCd}" />
				<t:updateActionListener property="#{reportDetail.emissionsReportRow}"
					value="#{null}" />	
				<t:updateActionListener property="#{reportDetail.fromTODOList}"
          			value="false" />
			</af:commandLink>
			<af:commandLink rendered="#{! empty invoiceDetail.invoice.permitId}"
				action="#{permitDetail.loadPermit}">
				<af:outputText value="#{invoiceDetail.invoice.permitId}" />
				<t:updateActionListener property="#{permitDetail.permitID}"
					value="#{invoiceDetail.invoice.permitId}" />
			</af:commandLink>
		</afh:cellFormat>
		<afh:cellFormat halign="right">
			<af:inputText label="Print Invoice:" readOnly="true"></af:inputText>
		</afh:cellFormat>
		<afh:cellFormat halign="left" valign="bottom">
			<af:panelHorizontal halign="left">
				<h:outputLink rendered="#{! empty invoiceDetail.invoice.permitInvDocument.documentID}"
					value="#{invoiceDetail.invoice.permitInvDocument.docURL}"
					title="View Permit Document">
					<af:outputText
						value="#{invoiceDetail.invoice.permitInvDocument.description}" />
				</h:outputLink>

				<h:outputLink
					rendered="#{! empty invoiceDetail.invoice.reportInvDocument.documentID}"
					value="#{invoiceDetail.invoice.reportInvDocument.docURL}"
					title="View Report Document">
					<af:outputText
						value="#{invoiceDetail.invoice.reportInvDocument.description}" />
				</h:outputLink>			

				<af:objectSpacer width="10" />
				<!-- af:selectBooleanCheckbox id="changeInvoiceDt"
					value="#{invoiceDetail.modifyInvoiceDate}"
					disabled="#{invoiceDetail.invoice.invoiceStateCd =='9inv'}"
					rendered="#{invoiceDetail.postToRevenuesVisible 	
							&&! empty invoiceDetail.invoice.emissionsRptId}"
					label="Change Invoice Date:" autoSubmit="true" -->

				<!-- af:selectInputDate value="#{invoiceDetail.invoice.effectiveDate}"
					rendered="#{invoiceDetail.modifyInvoiceDate}" -->

			</af:panelHorizontal>
		</afh:cellFormat>
		<afh:cellFormat halign="right">
			<af:inputText label="Billing Contact Phone:" readOnly="True" />
		</afh:cellFormat>
		<afh:cellFormat halign="left">
			<af:inputText readOnly="True"
				value="#{invoiceDetail.invoice.contact.phoneNo}"
				converter="#{infraDefs.phoneNumberConverter}" />
		</afh:cellFormat>
	</afh:rowLayout>	
</afh:tableLayout>
