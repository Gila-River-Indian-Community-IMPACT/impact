<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:table value="#{invoiceDetail.invoice.revenue.adjustmentList}" bandingInterval="1"
	banding="row" var="adjustments" width="980"
  rows="#{invoiceDetail.pageLimit}">
	<af:column sortProperty="description" sortable="true"
		headerText="Type" formatType="text">
		<af:outputText value="#{adjustments.type.name}" />
	</af:column>
	<af:column sortProperty="amount" sortable="true" headerText="Amount"
		formatType="number">
		<af:outputText value="#{adjustments.amount}" />
	</af:column>
	<af:column sortProperty="reason" sortable="true" headerText="Reason"
		formatType="text">
		<af:outputText value="#{adjustments.reason}" />
	</af:column>
	<af:column sortProperty="dateApplied" sortable="true"
		headerText="Date Applied" formatType="text">
		<af:selectInputDate readOnly="true"
				value="#{adjustments.dateApplied}" />
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
