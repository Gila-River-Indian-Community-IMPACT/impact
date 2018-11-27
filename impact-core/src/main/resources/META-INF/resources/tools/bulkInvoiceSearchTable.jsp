<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<afh:rowLayout halign="center">
	<h:panelGrid border="1"
		rendered="#{bulkOperationsCatalog.hasInvoiceSearchResults}">
		<af:panelBorder>
			<af:showDetailHeader text="Invoice List" disclosed="true">

				<af:table value="#{bulkOperationsCatalog.invoices}"
					bandingInterval="1" banding="row" var="invoice"
					rows="#{bulkOperationsCatalog.pageLimit}">
					<f:facet name="header">
						<afh:rowLayout halign="left">
							<af:panelButtonBar>
								<af:commandButton action="#{bulkOperationsCatalog.selectAllI}"
									text="Select All" />
							</af:panelButtonBar>
							<af:objectSpacer height="1" width="6" />
							<af:panelButtonBar>
								<af:commandButton action="#{bulkOperationsCatalog.selectNoneI}"
									text="Select None" />
							</af:panelButtonBar>
						</afh:rowLayout>
					</f:facet>
					<af:column sortable="true" sortProperty="selected"
						formatType="icon" headerText="Select">
						<af:selectBooleanCheckbox label="Selected"
							value="#{invoice.selected}" rendered="true">
							<f:selectItems value="#{invoice.selected}" />
						</af:selectBooleanCheckbox>
					</af:column>

					<af:column sortProperty="invoiceId" sortable="true"
						formatType="text" headerText="Invoice Id">
						<af:commandLink action="#{invoiceDetail.submit}">
							<af:outputText value="#{invoice.invoiceId}" />
							<t:updateActionListener property="#{invoiceDetail.invoiceId}"
								value="#{invoice.invoiceId}" />
							<t:updateActionListener
								property="#{invoiceDetail.emissionsRptCd}"
								value="#{invoice.emissionsRptCd}" />
						</af:commandLink>
					</af:column>

					<af:column sortProperty="facilityId" sortable="true" noWrap="true"
						formatType="text" headerText="Facility Id">
						<af:commandLink action="#{facilityProfile.submitProfile}"
							text="#{invoice.facilityId}">
							<t:updateActionListener property="#{facilityProfile.fpId}"
								value="#{invoice.fpId}" />
							<t:updateActionListener
								property="#{menuItem_facProfile.disabled}" value="false" />
						</af:commandLink>
					</af:column>

					<af:column sortProperty="facilityName" sortable="true"
						formatType="text" headerText="Facility Name">
						<af:outputText value="#{invoice.facilityName}" />
					</af:column>

					<af:column sortProperty="revenueId" sortable="true"
						formatType="text" headerText="Revenue Id">
						<af:outputText value="#{invoice.revenueId}" />
					</af:column>

					<af:column sortProperty="revenueTypeCd" sortable="true"
						formatType="text" headerText="Revenue Type">
						<af:outputText value="#{invoice.revenueTypeCd}" />
					</af:column>

					<af:column sortProperty="emissionsRptId" sortable="true"
						formatType="text" headerText="Report/ Permit Id" noWrap="true">
						<af:commandLink action="#{reportDetail.submit}"
							rendered="#{! empty invoice.emissionsRptId}">
							<af:outputText value="#{invoice.emissionsRptId}" />
							<t:updateActionListener property="#{reportDetail.reportId}"
								value="#{invoice.emissionsRptId}" />
							<t:updateActionListener property="#{reportDetail.emissionsRptCd}"
								value="#{invoice.emissionsRptCd}" />
							<t:updateActionListener property="#{reportDetail.fromTODOList}"
								value="false" />
						</af:commandLink>
						<af:commandLink action="#{permitDetail.loadPermit}"
							rendered="#{! empty invoice.permitId}">
							<af:outputText value="#{invoice.permitId}" />
							<t:updateActionListener property="#{permitDetail.permitID}"
								value="#{invoice.permitId}" />
						</af:commandLink>
					</af:column>

					<af:column sortProperty="invoiceStateDesc" sortable="true"
						formatType="text" headerText="Stars2 Invoice State">
						<af:outputText value="#{invoice.invoiceStateDesc}" />
					</af:column>

					<af:column sortProperty="revenueStateDesc" sortable="true"
						formatType="text" headerText="Revenue State">
						<af:outputText value="#{invoice.revenueStateDesc}" />
					</af:column>
					<af:column sortProperty="creationDate" sortable="true"
						formatType="text" headerText="Invoice Date">
						<af:selectInputDate readOnly="true"
							value="#{invoice.creationDate}" />
					</af:column>
					<f:facet name="footer">
						<afh:rowLayout halign="center">
							<af:panelButtonBar>

								<af:commandButton
									text="#{bulkOperationsCatalog.bulkOperation.buttonName}"
									useWindow="true" windowWidth="800" windowHeight="600"
									action="#{bulkOperationsCatalog.setSelectedInvoices}" />
								<af:commandButton actionListener="#{tableExporter.printTable}"
									onclick="#{tableExporter.onClickScript}" text="Printable view" />
								<af:commandButton actionListener="#{tableExporter.excelTable}"
									onclick="#{tableExporter.onClickScript}" text="Export to excel" />

							</af:panelButtonBar>
						</afh:rowLayout>
					</f:facet>
				</af:table>

			</af:showDetailHeader>
		</af:panelBorder>
	</h:panelGrid>
</afh:rowLayout>

