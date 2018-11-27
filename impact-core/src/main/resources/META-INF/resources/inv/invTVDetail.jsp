<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>


<af:column>
	<af:column sortProperty="description" sortable="true"
		headerText="Description" formatType="text">
		<af:outputText value="#{emissions.printableDesc}" />
	</af:column>
	<af:column sortProperty="amount" sortable="true" headerText="Tons"
		formatType="number">
		<af:inputText value="#{emissions.amount}" readOnly="#{! invoiceDetail.editable}"
				rendered="#{! invoiceDetail.editable}" columns="15">
			<af:convertNumber type='number' locale="en-US"
					minFractionDigits="2" />	
		</af:inputText>
		
		<af:inputText value="#{emissions.amount}" readOnly="#{! invoiceDetail.editable}"
				rendered="#{invoiceDetail.editable}" columns="15">
					<af:convertNumber pattern="###,##0.##" minFractionDigits="2" />
		</af:inputText>
		
	</af:column>
	<af:column sortProperty="difference" sortable="true"
		headerText="Difference" formatType="number"
		rendered="#{invoiceDetail.invoice.adjustedInvoice}">
		<af:outputText value="#{emissions.difference}">
			<af:convertNumber pattern="###,##0.##" minFractionDigits="2" />
		</af:outputText>
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




