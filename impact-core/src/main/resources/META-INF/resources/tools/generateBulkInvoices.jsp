<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="Generated Invoices">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form usesUpload="true">
		<af:messages />
		<f:subview id="invoices">

			<afh:rowLayout halign="center">
				<h:panelGrid border="1" rendered="true">
					<af:panelBorder>
						<af:showDetailHeader text="Generated Invoices" disclosed="true">

							<af:objectSpacer width="100%" height="15" />

							<af:table value="#{bulkOperationsCatalog.bulkOperation.results}"
								bandingInterval="1" banding="row" var="formResultNSRInvoice">

								<af:column sortable="false" noWrap="true" formatType="text"
									headerText="#{bulkOperationsCatalog.bulkSearchType}">
									<af:panelHorizontal valign="middle" halign="left">
										<af:outputText value="#{formResultNSRInvoice.id}" />
									</af:panelHorizontal>
								</af:column>
								
								<af:column sortable="false" noWrap="true" formatType="text"
									headerText="Facility ID">
									<af:panelHorizontal valign="middle" halign="left">
										<af:outputText value="#{formResultNSRInvoice.facilityId}" />
									</af:panelHorizontal>
								</af:column>

								<af:column sortable="false" noWrap="true" formatType="number"
									headerText="Invoice Amount">
									<af:inputText label="Final Invoice Amount :" columns="10"
										maximumLength="50"
										value="#{formResultNSRInvoice.finalInvoiceAmount}"
										readOnly="true">
										<af:convertNumber type='currency' locale="en-US"
											minFractionDigits="2" />
									</af:inputText>
								</af:column>

								<af:column sortable="false" formatType="text"
									headerText="Link To Document">
									<af:panelHorizontal valign="middle" halign="left">
										<h:outputLink value="#{formResultNSRInvoice.formURL}">
											<af:outputText value="#{formResultNSRInvoice.fileName}" />
										</h:outputLink>
									</af:panelHorizontal>
								</af:column>

								<af:column headerText="Notes, Errors, and Warnings"
									sortable="false" formatType="text">
									<af:panelHorizontal valign="middle" halign="left">
										<af:outputText value="#{formResultNSRInvoice.notes}" />
									</af:panelHorizontal>
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

						</af:showDetailHeader>
					</af:panelBorder>
				</h:panelGrid>
			</afh:rowLayout>
			</f:subview>

			<af:panelForm>
				<af:objectSpacer width="100%" height="15" />
				<afh:rowLayout halign="center">
					 <af:panelButtonBar>
						<af:goButton text="Download Merged Invoice Document" 
							disabled="#{bulkOperationsCatalog.bulkOperation.mergedInvoiceDocEmpty} "
							destination="#{bulkOperationsCatalog.bulkOperation.mergedInvoiceDocURL}"
							shortDesc="Concatenated document of all the successfully generated final invoice documents"
							targetFrame="_self"/>
						<af:commandButton text="Close" immediate="true"
							actionListener="#{bulkOperationsCatalog.applyFinalAction}" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</af:panelForm>
		</af:form>
	</af:document>
</f:view>
