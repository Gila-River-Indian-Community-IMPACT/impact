<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>


<af:table value="#{invoiceDetail.otherInvoices}" bandingInterval="1"
	banding="row" var="invoiceList" rows="#{invoiceDetail.pageLimit}">
	<af:column sortProperty="invoiceId" sortable="true"
		headerText="Invoice ID" formatType="text" width="90">
		<af:commandLink action="#{invoiceDetail.submit}">
			<af:inputText readOnly="true" value="#{invoiceList.invoiceId}" />
			<t:updateActionListener property="#{invoiceDetail.invoiceId}"
				value="#{invoiceList.invoiceId}" />
		</af:commandLink>
	</af:column>
	<af:column sortProperty="invoiceStateCd" sortable="true"
		headerText="Invoice State" formatType="text" width="90">
		<af:inputText readOnly="true" value="#{invoiceList.invoiceStateCd}" />
	</af:column>
	<af:column sortProperty="creationDate" sortable="true"
		formatType="text" headerText="Created Date">		
		<af:selectInputDate readOnly="true"
        	value="#{invoiceList.creationDate}" />	
	</af:column>
	<af:column sortProperty="origAmount" sortable="true"
		headerText="Stars2 Amount" formatType="number" width="140">
		<af:inputText readOnly="true" value="#{invoiceList.origAmount}">
			<af:convertNumber type='currency' locale="en-US"
		             minFractionDigits="2" />
		</af:inputText>
	</af:column>
	<af:column sortable="false"
		formatType="text" headerText="Report ID" noWrap="true">
		<af:commandLink action="#{reportDetail.submit}"
			rendered="#{! empty invoiceList.emissionsRptId}">
			<af:outputText value="#{invoiceList.emissionsRptId}" />
			<t:updateActionListener property="#{reportDetail.reportId}"
				value="#{invoiceList.emissionsRptId}" />
			<t:updateActionListener property="#{reportDetail.emissionsRptCd}"
				value="#{invoiceList.emissionsRptCd}" />				
			<t:updateActionListener property="#{reportDetail.fromTODOList}"
          		value="false" />
		</af:commandLink>
		<af:commandLink action="#{permitDetail.loadPermit}"
			rendered="#{! empty invoiceList.permitId}">
			<af:outputText value="#{invoiceList.permitId}" />
			<t:updateActionListener property="#{permitDetail.permitID}"
				value="#{invoiceList.permitId}" />
		</af:commandLink>
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