<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<afh:rowLayout halign="center">
	<h:panelGrid border="1"
		rendered="#{bulkOperationsCatalog.hasPermitSearchResults}">
		<af:panelBorder>
			<af:showDetailHeader text="Permit List" disclosed="true">

				<af:table value="#{bulkOperationsCatalog.permits}"
					bandingInterval="1" banding="row" var="permit"
					rows="#{bulkOperationsCatalog.pageLimit}">
					<f:facet name="header">
						<afh:rowLayout halign="left">
							<af:panelButtonBar>
								<af:commandButton action="#{bulkOperationsCatalog.selectAllP}"
									text="Select All" />
							</af:panelButtonBar>
							<af:objectSpacer height="1" width="6" />
							<af:panelButtonBar>
								<af:commandButton action="#{bulkOperationsCatalog.selectNoneP}"
									text="Select None" />
							</af:panelButtonBar>
						</afh:rowLayout>
					</f:facet>
					<af:column sortable="true" sortProperty="selected" formatType="icon" headerText="Select">
						<af:selectBooleanCheckbox label="Selected"
							value="#{permit.selected}" rendered="true">
							<f:selectItems value="#{permit.selected}" />
						</af:selectBooleanCheckbox>
					</af:column>

					<af:column headerText="Permit Number" sortable="true"
						sortProperty="permitNumber" formatType="text">
						<af:panelHorizontal valign="middle" halign="left">
							<af:commandLink text="#{permit.permitNumber}"
								action="#{permitDetail.loadPermit}">
								<af:setActionListener to="#{permitDetail.permitID}"
									from="#{permit.permitID}" />
								<t:updateActionListener property="#{permitDetail.fromTODOList}"
									value="false" />
							</af:commandLink>
						</af:panelHorizontal>
					</af:column>
					<af:column headerText="Facility ID" sortable="true"
						sortProperty="facilityId" formatType="text"
						rendered="#{permitSearch.fromFacility == 'false'}">
						<af:panelHorizontal valign="middle" halign="center">
							<af:commandLink text="#{permit.facilityId}"
								action="#{facilityProfile.submitProfile}"
								inlineStyle="white-space: nowrap;">
								<t:updateActionListener property="#{facilityProfile.fpId}"
									value="#{permit.fpId}" />
								<t:updateActionListener
									property="#{menuItem_facProfile.disabled}" value="false" />
							</af:commandLink>
						</af:panelHorizontal>
					</af:column>

					<af:column headerText="Facility Name" sortable="true"
						sortProperty="facilityNm">
						<af:panelHorizontal valign="middle" halign="left">
							<af:inputText readOnly="true" value="#{permit.facilityNm}" />
						</af:panelHorizontal>
					</af:column>

					<af:column headerText="Application Numbers" sortable="true"
						sortProperty="applicationNumbers">
						<af:panelHorizontal valign="middle" halign="left">
							<af:inputText readOnly="true"
								value="#{permit.applicationNumbers}" />
						</af:panelHorizontal>
					</af:column>

					<af:column headerText="Type" sortable="true"
						sortProperty="permitType" formatType="text">
						<af:panelHorizontal valign="middle" halign="left">
							<af:selectOneChoice unselectedLabel=" " readOnly="true"
								value="#{permit.permitType}">
								<mu:selectItems value="#{permitReference.permitTypes}" />
							</af:selectOneChoice>
						</af:panelHorizontal>
					</af:column>

					<af:column headerText="Issuance Stage" sortable="true"
						sortProperty="permitGlobalStatusCD" formatType="text">
						<af:panelHorizontal valign="middle" halign="left">
							<af:selectOneChoice label="Permit status"
								value="#{permit.permitGlobalStatusCD}" readOnly="true" id="soc2">
								<mu:selectItems
									value="#{permitReference.permitGlobalStatusDefs}" id="soc2" />
							</af:selectOneChoice>
						</af:panelHorizontal>
					</af:column>

					<af:column headerText="Reason(s)" sortable="true"
						sortProperty="primaryReasonCD">
						<af:panelHorizontal valign="middle" halign="left">
							<af:selectManyCheckbox label="Reason(s) :" valign="top"
								readOnly="true" value="#{permit.permitReasonCDs}"
								inlineStyle="white-space: nowrap;">
								<f:selectItems value="#{permitSearch.allPermitReasons}" />
							</af:selectManyCheckbox>
						</af:panelHorizontal>
					</af:column>

					<af:column headerText="Effective Date" sortable="true"
						sortProperty="effectiveDate" formatType="text">
						<af:panelHorizontal valign="middle" halign="left">
							<af:selectInputDate readOnly="true"
								value="#{permit.effectiveDate}" />
						</af:panelHorizontal>
					</af:column>

					<af:column headerText="Expiration Date" sortable="true"
						sortProperty="expirationDate" formatType="text">
						<af:panelHorizontal valign="middle" halign="left">
							<af:selectInputDate readOnly="true"
								value="#{permit.expirationDate}" />
						</af:panelHorizontal>
					</af:column>

					<af:column headerText="Draft Issuance Date" sortable="true"
						sortProperty="draftIssueDate" formatType="text">
						<af:panelHorizontal valign="middle" halign="left">
							<af:selectInputDate readOnly="true"
								value="#{permit.draftIssueDate}" />
						</af:panelHorizontal>
					</af:column>

					<af:column headerText="Final Issuance Date" sortable="true"
						sortProperty="finalIssueDate" formatType="text">
						<af:panelHorizontal valign="middle" halign="left">
							<af:selectInputDate readOnly="true"
								value="#{permit.finalIssueDate}" />
						</af:panelHorizontal>
					</af:column>

					<f:facet name="footer">
						<afh:rowLayout halign="center">
						
							<af:panelGroup layout="vertical">
							<af:panelGroup layout="vertical">
								<af:poll pollListener="#{bulkOperationsCatalog.refreshInvoiceProgress}"
										interval="5000" id="invoicePoll" rendered="#{bulkOperationsCatalog.generateNSRInvoiceFlag}"/>
							</af:panelGroup>
							
							<af:panelButtonBar partialTriggers="invoicePoll">
								<af:commandButton
									id="generate"
									text="#{bulkOperationsCatalog.bulkOperation.buttonName}"
									useWindow="true" windowWidth="800" windowHeight="600"
									disabled="#{bulkOperationsCatalog.generateNSRInvoiceFlag && !bulkOperationsCatalog.NSRFeeSummaryEditAllowed}" 	
									action="#{bulkOperationsCatalog.setSelectedPermits}" />
								<af:commandButton actionListener="#{tableExporter.printTable}"
									onclick="#{tableExporter.onClickScript}" text="Printable view" />
								<af:commandButton actionListener="#{tableExporter.excelTable}"
									onclick="#{tableExporter.onClickScript}" text="Export to excel" />

							</af:panelButtonBar>

							<af:panelGroup layout="vertical"
								partialTriggers="invoicePoll">
								<af:progressIndicator id="progressidInvoiceGeneration"
									rendered="#{bulkOperationsCatalog.showProgressBarInvoiceGeneration}"
									partialTriggers="invoicePoll">
									<af:outputFormatted
										value="Generating #{bulkOperationsCatalog.value} out of #{bulkOperationsCatalog.maximum}."
										rendered="#{bulkOperationsCatalog.showProgressBarInvoiceGeneration}" />
								</af:progressIndicator>
							</af:panelGroup>
							</af:panelGroup>

						</afh:rowLayout>
					</f:facet>
				</af:table>

			</af:showDetailHeader>
		</af:panelBorder>
	</h:panelGrid>
</afh:rowLayout>

