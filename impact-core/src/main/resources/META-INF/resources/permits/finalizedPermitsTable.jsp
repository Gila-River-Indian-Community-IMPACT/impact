<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:table value="#{finalizedPermits}" var="permit" rows="15" width="980">
	<af:column headerText="Facility ID" sortable="true"
		sortProperty="facilityId" formatType="text">
		<af:panelHorizontal valign="middle" halign="center">
			<af:outputText value="#{permit.facilityId}"
				inlineStyle="white-space: nowrap;" />
		</af:panelHorizontal>
	</af:column>
	<af:column headerText="Facility Name" sortable="true"
		sortProperty="facilityNm">
		<af:panelHorizontal valign="middle" halign="left">
			<af:inputText readOnly="true" value="#{permit.facilityNm}" />
		</af:panelHorizontal>
	</af:column>
	<af:column headerText="Company Name" sortable="true"
		sortProperty="companyName" formatType="text">
		<af:panelHorizontal valign="middle" halign="left">
			<af:selectInputDate readOnly="true" value="#{permit.companyName}" />
		</af:panelHorizontal>
	</af:column>
	<af:column headerText="Permit Number" sortable="true"
		sortProperty="permitNumber" formatType="text">
		<af:panelHorizontal valign="middle" halign="left">
			<af:outputText value="#{permit.permitNumber}" />
		</af:panelHorizontal>
	</af:column>
	<af:column headerText="Type" sortable="true" sortProperty="permitType"
		formatType="text">
		<af:panelHorizontal valign="middle" halign="left">
			<af:inputText readOnly="true" value="#{permit.permitTypeDsc}" />
		</af:panelHorizontal>
	</af:column>
	<af:column headerText="Publication Stage" sortable="true"
		sortProperty="permitGlobalStatusCD" formatType="text">
		<af:panelHorizontal valign="middle" halign="left">
			<af:selectOneChoice value="#{permit.permitGlobalStatusCD}"
				readOnly="true">
				<mu:selectItems value="#{permitReference.permitGlobalStatusDefs}" />
			</af:selectOneChoice>
		</af:panelHorizontal>
	</af:column>
	<af:column headerText="Invoice Document" formatType="text">
		<af:panelHorizontal valign="middle" halign="left">
			<af:goLink id="documentLink"
			  	text="#{permit.initialInvoiceDocument.description}"
              	destination="#{permit.initialInvoiceDocument.docURL}"
              	targetFrame="_self" 
              	rendered="#{permit.displayInvoiceColumn}"/>
		</af:panelHorizontal>
	</af:column>

	<f:facet name="footer">
		<afh:rowLayout halign="center">
			<af:panelButtonBar>
				<af:commandButton actionListener="#{tableExporter.printHeaderTable}"
					onclick="#{tableExporter.onClickScript}"
					text="Generate #{permit.permitType} Manifest">
					<t:updateActionListener property="#{tableExporter.headerText}"
						value="AQD #{permit.permitType} Issuance Log" />
				</af:commandButton>
				<af:commandButton actionListener="#{tableExporter.printTable}"
					onclick="#{tableExporter.onClickScript}" text="Printable view" />
				<af:commandButton actionListener="#{tableExporter.excelTable}"
					onclick="#{tableExporter.onClickScript}" text="Export to excel" />
			</af:panelButtonBar>
		</afh:rowLayout>
	</f:facet>
</af:table>
