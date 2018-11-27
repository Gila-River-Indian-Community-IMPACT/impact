<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelGroup layout="vertical"
	rendered="#{invoiceDetail.invoice.adjustedInvoice}">

	<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="700">
		<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
			value="<b> ** Difference between Original Report and Revised Report /Adjustment to Revenues.</b>" />
	</af:panelForm>

	<af:panelForm rows="1" maxColumns="4" labelWidth="150" width="700">
		<af:inputText readOnly="true" label="Compared Invoice:" />

		<af:inputText readOnly="true"
			rendered="#{invoiceDetail.invoice.adjustedInvoice}"
			value="#{invoiceDetail.invoice.compInvoice.invoiceId}" />

		<af:inputText readOnly="true" label="Difference:" />

		<af:inputText readOnly="true"
			rendered="#{invoiceDetail.invoice.adjustedInvoice && ! invoiceDetail.editable}"
			value="#{invoiceDetail.invoice.compInvoice.origAmount}">
			<af:convertNumber type="currency" currencySymbol="$" />
		</af:inputText>				
		
		<!-- af:inputText readOnly="#{! invoiceDetail.editable}" columns="10"
			rendered="#{invoiceDetail.invoice.adjustedInvoice && invoiceDetail.editable}"
			value="#{invoiceDetail.invoice.compInvoice.origAmount}" -->
	</af:panelForm>
</af:panelGroup>
