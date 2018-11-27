<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelGroup layout="vertical"
	rendered="#{reportProfile.renderComponent == 'report' && !reportProfile.err}">
	<af:panelHeader text="Emissions Inventory Summary">
		<afh:rowLayout halign="center"
			rendered="#{!reportProfile.submitted && !reportProfile.facilityEditable && reportProfile.facility.versionId != -1}">
			<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
				value="Note this Inventory is associated with an Historical Facility Inventory. If it is not the correct Facility Inventory, you can:<lu><li>Modify (a copy) of this historic facility inventory for this emissions inventory, or</li><li>Use a different inventory by clicking on <b>Associate with Different Facility Inventory</b>.</li>" />
		</afh:rowLayout>
		<%--
		<afh:rowLayout halign="center"
			rendered="#{!reportProfile.submitted && !reportProfile.facilityEditable && reportProfile.internalApp && reportProfile.facility.versionId == -1}">
			<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
				value="Note this Inventory is associated with the current facility inventory which is preserved. To make any required updates, click on <b>Update Facility Inventory</b>" />
		</afh:rowLayout>
		 --%>
		<afh:rowLayout halign="center"
			rendered="#{reportProfile.doRptCompare}">
			<af:outputFormatted
				value="Values in the two inventories are compared by displaying the original inventory value first, followed by the comparison inventory value.<br><br>If certain numeric values in the original inventory differ more than the specified percent from the corresponding value in the comparison inventory, then the value from the original inventory is highlighted.<br><br>" />
		</afh:rowLayout>

		<af:showDetailHeader text="Explanation" disclosed="true">
			<af:outputFormatted styleUsage="instruction"
				value="<ul>
						<li>Use the Exclude/Include Emissions Units button to indicate which emissions units:</li>
							<ul>
								<li>Did not operate at all during the year</li>
								<li>Emitted less than the reporting requirement</li>
								<li>Do require detailed emissions inventory reporting</li>
							</ul>
						<li>For each Emissions Process that requires detailed emissions inventory reporting, navigate to that Process and provide the necessary information</li>
						<li>Attach any files needed to support the reported emissions</li>
					  </ul>"/>
		</af:showDetailHeader>

		<afh:rowLayout>
			<afh:cellFormat halign="left">
				<af:inputText label="Regulatory Requirement(s):" readOnly="True"
					value="#{reportProfile.regulatoryRequirements}" />
			</afh:cellFormat>
		</afh:rowLayout>

		<afh:rowLayout rendered="#{reportProfile.internalApp}">
			<afh:cellFormat halign="left">
				<af:selectInputDate id="receiveDate" label="Date inventory received:"
					readOnly="#{! reportProfile.editable && ! reportProfile.updatingInvoiceInfo}"
					value="#{reportProfile.report.receiveDate}">
					<af:validateDateTimeRange minimum="1900-01-01" />
				</af:selectInputDate>
			</afh:cellFormat>
			<afh:cellFormat halign="left">
				<af:outputFormatted value="&nbsp;&nbsp;" />
			</afh:cellFormat>
			<afh:cellFormat halign="left"
				rendered="#{reportProfile.doRptCompare}">
				<af:selectInputDate id="receiveDate" readOnly="True"
					value=": #{reportProfile.compareReport.receiveDate}" />
			</afh:cellFormat>
		</afh:rowLayout>
	</af:panelHeader>
	<af:showDetailHeader text="Facility Emissions" disclosed="true">
		<afh:rowLayout halign="center">
			<af:table value="#{reportProfile.emissionReportWrapper}"
				bandingInterval="#{reportProfile.doRptCompare?2:1}"
				binding="#{reportProfile.emissionReportWrapper.table}"
				id="EmissionsTab" banding="row" width="1005" var="emissionLine"
				rows="#{reportProfile.pageLimit}">
				<%@ include file="emissionsTable.jsp"%>
				<f:facet name="footer">
					<h:panelGrid width="100%">
						<afh:rowLayout halign="center">
							<af:panelButtonBar>
								<af:commandButton actionListener="#{tableExporter.printTable}"
									onclick="#{tableExporter.onClickScript}" text="Printable view" />
								<af:commandButton actionListener="#{tableExporter.excelTable}"
									onclick="#{tableExporter.onClickScript}" text="Export to excel" />
							</af:panelButtonBar>
							<af:objectSpacer width="150" height="3"
								rendered="#{!reportProfile.editable && ! reportProfile.updatingInvoiceInfo}" />
							<%--
							<af:outputLabel value="Total Chargeable Pollutants:"
								rendered="#{!reportProfile.editable}" />
							<af:objectSpacer width="7" height="5" />
							<af:outputFormatted value="#{reportProfile.displayTotal}"
								rendered="#{!reportProfile.editable}" />
							--%>
						</afh:rowLayout>
					</h:panelGrid>
				</f:facet>
			</af:table>
		</afh:rowLayout>
		<afh:rowLayout halign="center">
			<af:objectSpacer width="150" height="10" />
		</afh:rowLayout>
		<afh:rowLayout halign="center">
			<af:outputFormatted
				value="<b>_____________________________________________________________________________________________________________</b>"
				rendered="#{reportProfile.hapTable}" />
		</afh:rowLayout>
		<afh:rowLayout halign="center">
			<af:outputFormatted value="#{reportProfile.hapNonAttestationMsg}"
				inlineStyle="font-size:75%" rendered="#{reportProfile.hapTable}" />
		</afh:rowLayout>
		<afh:rowLayout halign="center">
			<af:table value="#{reportProfile.emissionReportWrapperHAP}"
				rendered="#{reportProfile.hapTable}"
				bandingInterval="#{reportProfile.doRptCompare?2:1}"
				binding="#{reportProfile.emissionReportWrapperHAP.table}"
				id="EmissionsTab2" banding="row" width="1005" var="emissionLine"
				rows="#{reportProfile.displayHapRows}">
				<%@ include file="emissionsTableHAPs.jsp"%>
				<f:facet name="footer">
					<h:panelGrid width="100%">
						<afh:rowLayout halign="center">
							<af:panelButtonBar>								
								<af:commandButton text="Display Some Rows" id="displaySomeRows"
									action="#{reportProfile.displaySomeRows}"
									inlineStyle="font-weight:bold; background-color: rgb(255,255,119)"
									rendered="#{reportProfile.showDisplaySome}">
								</af:commandButton>
								<af:commandButton text="Add Emission" id="AddEmission"
									action="#{reportProfile.addEmission}"
									rendered="#{reportProfile.editable && !reportProfile.doRptCompare && !reportProfile.renderSum}">
								</af:commandButton>
								<af:commandButton text="Delete Selected Emission(s)"
									shortDesc="Only emissions not pre-specified by report or FIRE database may be deleted"
									rendered="#{reportProfile.editable && !reportProfile.doRptCompare && !reportProfile.renderSum}"
									action="#{reportProfile.deleteEmission}">
								</af:commandButton>
								<af:commandButton actionListener="#{tableExporter.printTable}"
									onclick="#{tableExporter.onClickScript}" text="Printable view" />
								<af:commandButton actionListener="#{tableExporter.excelTable}"
									onclick="#{tableExporter.onClickScript}" text="Export to excel" />
							</af:panelButtonBar>
						</afh:rowLayout>
					</h:panelGrid>
				</f:facet>
			</af:table>
		</afh:rowLayout>
	</af:showDetailHeader>
	<af:showDetailHeader text="Invoice Tracking Info" disclosed="true"
		rendered="#{reportProfile.internalApp && !reportProfile.doRptCompare}">
		<af:panelForm labelWidth="44%" fieldWidth="56%" rows="1"
			maxColumns="2" rendered="#{reportProfile.billable}">
			<af:inputText id="invoiceAmount" label="Invoice Amount :" 
				columns="10" maximumLength="50"
				readOnly="#{!reportProfile.editable && !reportProfile.updatingInvoiceInfo}"
				rendered="#{!reportProfile.editable && !reportProfile.updatingInvoiceInfo}"
				value="#{reportProfile.report.invoiceAmount}">					
			</af:inputText>
			<af:inputText label="Invoice Amount :" columns="10" 
				value="#{reportProfile.report.invoiceAmount}"
				readOnly="#{!reportProfile.editable && !reportProfile.updatingInvoiceInfo}"
				rendered="#{reportProfile.editable || reportProfile.updatingInvoiceInfo}">
			</af:inputText>
			<af:selectInputDate id="invoiceDate" label="Invoice Date:"
				readOnly="#{!reportProfile.editable && !reportProfile.updatingInvoiceInfo}"
				value="#{reportProfile.report.invoiceDate}">
				<af:validateDateTimeRange minimum="1900-01-01" 
										  		maximum="#{infraDefs.currentDate}" />
			</af:selectInputDate>
			<af:selectInputDate id="paymentReceivedDate"
				label="Payment Received Date:"
				readOnly="#{!reportProfile.editable && !reportProfile.updatingInvoiceInfo}"
				value="#{reportProfile.report.paymentReceivedDate}">
				<af:validateDateTimeRange minimum="1900-01-01" 
												maximum="#{infraDefs.currentDate}" />
			</af:selectInputDate>
		</af:panelForm>
		<af:panelForm labelWidth="44%" fieldWidth="56%" rows="1"
			maxColumns="2" rendered="#{!reportProfile.billable}">
			<af:outputFormatted value="This report is not billable."/>
		</af:panelForm>
	</af:showDetailHeader>
	<af:showDetailHeader text="Attachments" disclosed="true"
		rendered="#{!reportProfile.doRptCompare && !reportProfile.publicApp}">
		<jsp:include page="er_attachments.jsp" />
	</af:showDetailHeader>
	<af:showDetailHeader
		text="Reason/Explanation for Emissions Inventory Revision"
		rendered="#{!empty reportProfile.report.reportModified}"
		disclosed="true">
		<af:inputText id="revisionReason"
			readOnly="#{! reportProfile.editable}"
			value="#{reportProfile.report.revisionReason}" rows="4" columns="160"
			maximumLength="1000" />
	</af:showDetailHeader>
	<af:showDetailHeader text="Notes" disclosed="true"
		rendered="#{!reportProfile.doRptCompare && reportProfile.internalApp}">
		<jsp:include flush="true" page="notesTable.jsp" />
	</af:showDetailHeader>
	<af:objectSpacer height="10" />
	<afh:rowLayout halign="center"
		rendered="#{!reportProfile.doRptCompare}">
		<af:panelButtonBar id="editButtons"
			rendered="#{!reportProfile.readOnlyUser}"
			partialTriggers="eg71_zero2">
			<af:commandButton text="Data Entry Wizard"
				id="dataEntryWizard"
				rendered="#{!reportProfile.closedForEdit && !reportProfile.editable && reportProfile.allowJumpStartDataEntry}"
				useWindow="true" windowWidth="600" windowHeight="200"
				action="#{reportProfile.confirmWizardLaunch}" />
			<af:commandButton text="Reopen Edit"
				id="reopenEditButton"
				useWindow="true"  windowWidth="700" windowHeight="250"
				disabled="#{!reportProfile.stars2Admin}"
				shortDesc="Reopen this submitted Emissions Inventory for Edit"
				action="#{reportProfile.confirmReopenEdit}"
				rendered="#{!reportProfile.editable && reportProfile.showReopenEdit && !reportProfile.updatingInvoiceInfo}" />
			<af:commandButton text="Edit"
				id="eiEditButton"
				action="#{reportProfile.editReport}"
				disabled="#{!reportProfile.modTV_SMTV}"
				rendered="#{(!reportProfile.editable && !reportProfile.closedForEdit && reportProfile.internalEditable && reportProfile.internalApp) || (!empty reportProfile.report.reportModified && (!reportProfile.editable && !reportProfile.closedForEdit && reportProfile.internalEditable))}" />
			<af:commandButton text="Edit Invoice Tracking Info"
				id="editInvoiceTrackingInfoButton"
				action="#{reportProfile.updateInvoiceInfo}"
				disabled="#{!reportProfile.modTV_SMTV || !reportProfile.billable}"
				rendered="#{!reportProfile.editable && reportProfile.closedForEdit && !reportProfile.updatingInvoiceInfo && reportProfile.internalApp}" />
			<af:commandButton text="Save"
				id="eiSaveButton1"
				action="#{reportProfile.saveReport}"
				rendered="#{(reportProfile.editable || reportProfile.updatingInvoiceInfo) && (empty reportProfile.haveEmissions)}" />
			<af:commandButton text="Save"
				id="eiSaveButton2"
				action="#{reportProfile.confirmSaveReport}" 
				windowWidth="600" windowHeight="800"
				rendered="#{reportProfile.editable && !(empty reportProfile.haveEmissions)}" />
			<af:commandButton text="Cancel"
				id="eiCancelButton"
				action="#{reportProfile.cancelReportEdit}"
				rendered="#{reportProfile.editable || reportProfile.updatingInvoiceInfo}" immediate="true" />
			<af:commandButton text="Recompute Totals/Save"
				id="recomputeTotalsSaveButton"
				action="#{reportProfile.recomputeTotals}"
				rendered="#{!reportProfile.editable && reportProfile.showRecomputeTotals}" />
			<af:commandButton text="Create Group"
				id="createGroupButton"
				useWindow="true"
				windowWidth="900" windowHeight="800"
				action="#{reportProfile.createGroupPop}"
				rendered="false" />
			<af:switcher defaultFacet="view71"
				rendered="#{(!reportProfile.editable && !reportProfile.updatingInvoiceInfo) || (!reportProfile.editable && !reportProfile.updatingInvoiceInfo && erNTVDetail.generalUser)}"
				facetName="#{(!reportProfile.editable && reportProfile.allow71Save) ? 'edit71' : 'view71'}">
				<f:facet name="view71">
					<af:commandButton text="Excluded/Included Emissions Units"
						id="excludedIncludedEmissionsUnits"
						useWindow="true" windowWidth="1100" windowHeight="800"
						shortDesc="Allows you to see all the Emissions Units which ones did not operate during the reporting year or emitted less than reporting requirement."
						action="#{reportProfile.createEUListPop}" />
				</f:facet>
				<f:facet name="edit71">
					<af:commandButton text="Exclude/Include Emissions Units"
						id="excludeIncludeEmissionsUnits"
						useWindow="true" windowWidth="1100" windowHeight="800"
						shortDesc="Allows you to identify and exclude Emissions Units that did not operate during the reporting year and those that emitted less than reporting requirement."
						action="#{reportProfile.createEditEUListPop}" />
				</f:facet>
			</af:switcher>
		</af:panelButtonBar>
		<af:objectSpacer width="5" />
		<af:panelButtonBar id="submitButtons"
			rendered="#{!reportProfile.readOnlyUser && !reportProfile.updatingInvoiceInfo && !reportProfile.publicApp}">
			<af:commandButton text="Validate"
				id="validateButton"
				action="#{reportProfile.submitVerify}"
				disabled="#{!reportProfile.modTV_SMTV}"
				rendered="#{!reportProfile.editable && reportProfile.internalEditable && (!reportProfile.submitted || (reportProfile.openedForEdit && reportProfile.hasBeenOpenedForEdit))}">
				<t:updateActionListener property="#{facilityProfile.validTradeSecretNotifFrom}"
								value="emissionReport" />
			</af:commandButton>
			<af:switcher defaultFacet="internal"
				rendered="#{reportProfile.showSubmitButton && !reportProfile.openedForEdit}"
				facetName="#{reportProfile.internalApp ? 'internal' : 'external'}">
				<f:facet name="internal">
					<af:commandButton id="InternalSubmit" text="Internal Submit"
						action="#{reportProfile.submitReport}" />
				</f:facet>
				<f:facet name="external">
					<af:panelButtonBar>
						<af:commandButton id="ExtSubmit" text="Submit"
							action="#{reportProfile.submitReport}" useWindow="true"
							windowWidth="#{submitTask.attestWidth}"
							windowHeight="#{submitTask.attestHeight}"
							disabled="#{!myTasks.impactFullEnabled || !myTasks.hasSubmit}"
							shortDesc="#{myTasks.hasSubmit ? '' : 
	                	  'Submit'}">
							<t:updateActionListener property="#{submitTask.type}"
								value="#{submitTask.yesNo}" />
							<t:updateActionListener property="#{submitTask.task}"
								value="#{reportProfile.createRTask}" />
						</af:commandButton>
						<%-- hide the download attestation button until it is not supported in IMPACT
						<af:goButton id="attestationDocButton"
							text="Download Attestation Document" targetFrame="_blank"
							destination="#{reportProfile.attestationDocURL}" />--%>
					</af:panelButtonBar>
				</f:facet>
			</af:switcher>
			<af:commandButton text="Delete Inventory"
				id="deleteInventoryButton"
				disabled="#{!reportProfile.modTV_SMTV}"
				shortDesc="Delete entire inventory including attachments and all emissions"
				rendered="#{!reportProfile.openedForEdit && !reportProfile.editable && !reportProfile.submitted  && reportProfile.internalApp && !reportProfile.updatingInvoiceInfo}"
				action="#{reportProfile.startDeleteReport}" useWindow="true"
				windowWidth="800" windowHeight="300" />
			<af:commandButton text="Workflow Task"
				id="workflowTaskButton"
				shortDesc="Return to the workflow step"
				disabled="#{!reportDetail.fromTODOList}"
				rendered="#{!reportProfile.openedForEdit && reportProfile.submitted && reportProfile.internalApp && !reportProfile.updatingInvoiceInfo}"
				action="#{reportDetail.goToCurrentWorkflow}" />
			<%-- not needed for WY, at least for now
			<af:commandButton text="EIS Approve"
				disabled="#{!reportProfile.eisApprover}"
				shortDesc="Approve only the EIS inventory"
				action="#{reportProfile.approveEISReport}"
				rendered="#{!reportProfile.openedForEdit && reportProfile.report.eisStatusCd == '3ur' && reportProfile.internalApp}" /> --%>
			<af:commandButton text="Create Revised Emissions Inventory"
				disabled="#{!reportProfile.submitted}"
				rendered="#{!reportProfile.openedForEdit && !reportProfile.editable && reportProfile.internalApp && !reportProfile.updatingInvoiceInfo}"
				shortDesc="#{reportProfile.submitted?'Create revised emissions inventory based upon this inventory and its facility inventory':'Emissions inventory can still be modified.  You cannot create a revised emissions inventory until this inventory is submitted.'}"
				id="CreateRevisedButton" useWindow="true" windowWidth="700"
				windowHeight="310"
				action="#{reportProfile.startCreateRevisedReport}">
			</af:commandButton>
		</af:panelButtonBar>
	</afh:rowLayout>
	<af:objectSpacer height="10" />
	<afh:rowLayout halign="center"
		rendered="#{!reportProfile.doRptCompare && !reportProfile.readOnlyUser && !reportProfile.updatingInvoiceInfo}">
		<af:panelButtonBar id="approveButtons"
			rendered="#{reportProfile.closedForEdit && reportProfile.internalApp && !reportProfile.report.reportNotNeeded}">
			<af:commandButton text="Revision Requested"
				id="revisionRequestedButton"
				disabled="#{!reportProfile.ferEsApprover}"
				shortDesc="A Revised Emissions Inventory is needed from the facility"
				action="#{reportProfile.toStateRR}"
				rendered="#{reportProfile.report.rptReceivedStatusCd != '07rr' && !reportProfile.approved}" />
			<af:commandButton text="Approve/Revision Requested"
				id="approveRevisionRequestedButton"
				disabled="#{!reportProfile.ferEsApprover}"
				shortDesc="Approve in order to Invoice, but a Revised Emissions Inventory is still needed from the facility"
				action="#{reportProfile.toStateAR}"
				rendered="#{reportProfile.report.rptReceivedStatusCd != '09ar'}" />
			<af:commandButton text="Approve" shortDesc="Approve the emissions inventory"
				id="approveButton"
				disabled="#{!reportProfile.ferEsApprover}"
				action="#{reportProfile.toStateAA}"
				rendered="#{reportProfile.report.rptReceivedStatusCd != '10aa'}" />
			<af:commandButton text="Inventory Prior/Invalid" useWindow="true"  windowWidth="700" windowHeight="250"
				id="inventoryPriorInvalidButton"
				disabled="#{!reportProfile.ferEsApprover}"
				shortDesc="This emissions inventory is prior or invalid."
				action="#{reportProfile.confirmToStateNN}" />
		</af:panelButtonBar>
	</afh:rowLayout>
	<afh:rowLayout halign="center"
		rendered="#{reportProfile.doRptCompare && !reportProfile.readOnlyUser}">
		<af:panelButtonBar>
			<af:commandButton text="Exit Comparison Mode" action="#{reportProfile.compareReportOK}"
				id="exitComparisonModeButton" />
		</af:panelButtonBar>
	</afh:rowLayout>
	<af:objectSpacer height="10" />
	<afh:rowLayout halign="center"
		rendered="#{!reportProfile.editable && !reportProfile.doRptCompare && !reportProfile.updatingInvoiceInfo}">
		<af:panelButtonBar id="facilityButtons4"
			rendered="#{!reportProfile.openedForEdit}">
			<af:switcher defaultFacet="internalF"
				facetName="#{reportProfile.internalApp ? 'internalF' : 'externalF'}">
				<f:facet name="internalF">
					<af:panelGroup>
						<af:commandButton text="View Invoice"
							rendered="#{reportProfile.report.rptApprovedStatusDate != null && false}"
							id="viewInvoiceButton" action="#{reportDetail.viewInvoice}">
							<t:updateActionListener
								value="#{reportProfile.report.emissionsRptId}"
								property="#{reportDetail.reportId}"></t:updateActionListener>
						</af:commandButton>
						<af:objectSpacer width="5" />
						<af:commandButton text="View/Update Facility Inventory"
							rendered="#{reportProfile.facilityEditable && !reportProfile.closedForEdit}"
							id="viewUpdateFacilityButton"
							action="#{reportProfile.submitProfile}">
							<t:updateActionListener
								property="#{menuItem_facProfile.disabled}" value="false" />
						</af:commandButton>
						<af:commandButton text="Show Associated Facility Inventory"
							rendered="#{!reportProfile.facilityEditable || reportProfile.closedForEdit}"
							id="viewFacilityButton" action="#{reportProfile.submitProfile}">
							<t:updateActionListener
								property="#{menuItem_facProfile.disabled}" value="false" />
						</af:commandButton>
						<af:objectSpacer width="5" />
							<%-- rendered="#{!reportProfile.facilityEditable && !reportProfile.closedForEdit && !reportProfile.readOnlyUser}" --%>
						<af:commandButton text="Update Facility Inventory"
							id="editFacilityButton"
							rendered="false"
							action="#{reportProfile.requestEditFP}" useWindow="true"
							disabled="#{!reportProfile.modTV_SMTV}" windowWidth="700"
							windowHeight="250">
							<t:updateActionListener
								property="#{menuItem_facProfile.disabled}" value="false" />
						</af:commandButton>
					</af:panelGroup>
				</f:facet>
				<f:facet name="externalF">
					<af:panelGroup>
						<%-- hide this button on external system - TFS task: 4170
						<af:commandButton text="View/Update Facility Inventory"
							rendered="#{reportProfile.inStaging}"
							id="viewUpdateFacilityButton2"
							action="#{reportProfile.submitProfile}">
						</af:commandButton>--%>
						<af:commandButton text="Show Associated Facility Inventory"
							rendered="#{!reportProfile.inStaging}" id="viewFacilityButton2"
							action="#{reportProfile.submitProfile}">
						</af:commandButton>
					</af:panelGroup>
				</f:facet>
			</af:switcher>
		</af:panelButtonBar>
		<af:objectSpacer width="5" />
		<af:commandButton text="Associate with Different Facility Inventory"
			id="associateFP"
			disabled="#{!reportProfile.modTV_SMTV}"
			rendered="#{!reportProfile.closedForEdit && reportProfile.internalEditable && !reportProfile.readOnlyUser && !reportProfile.updatingInvoiceInfo}"
			action="#{reportProfile.associateFP}" useWindow="true"
			windowWidth="1200"
			windowHeight="875">
		</af:commandButton>
		<af:objectSpacer width="5" />
		<af:panelButtonBar id="DocButtons"
			rendered="#{!reportProfile.editable && !reportProfile.doRptCompare && !reportProfile.updatingInvoiceInfo}">
			<af:commandButton text="Compare Emissions Inventories"
				shortDesc="Compare this inventory to another emissions inventory for this facility"
				rendered="#{reportProfile.internalApp}" id="CompareReportButton"
				useWindow="true" windowWidth="1000" windowHeight="500"
				action="#{reportProfile.selectCompareReport}">
			</af:commandButton>
			<af:commandButton text="Compare Previous" rendered="false"
				shortDesc="Compare this emissions inventory to the inventory that this inventory replaces"
				id="ComparePrevReportButton"
				action="#{reportProfile.compareToPrevious}">
			</af:commandButton>
			<af:commandButton text="Download/Print" id="printPublicBtn"
				action="#{reportProfile.printEmissionReport}" useWindow="true"
				windowWidth="800" windowHeight="300">
				<t:updateActionListener value="true"
					property="#{reportProfile.hideTradeSecret}" />
			</af:commandButton>
			<af:commandButton text="Download/Print Trade Secret Version"
				id="printTradeSecretBtn"
				rendered="#{reportProfile.eiConfidentialDataAccess && !applicationDetail.publicReadOnlyUser}"
				action="#{reportProfile.printEmissionReport}" useWindow="true"
				windowWidth="800" windowHeight="300">
				<t:updateActionListener value="false"
					property="#{reportProfile.hideTradeSecret}" />
			</af:commandButton>
			<af:commandButton immediate="true" text="Show Invoice Details" id="showInvoiceDetailsButton"
					rendered="#{reportProfile.internalApp && (reportProfile.hasPassedValidation || reportProfile.report.validated)}"						
					 useWindow="true" windowWidth="1500" windowHeight="1300"	
					 disabled="#{!reportProfile.billable}"
					action="#{reportProfile.showInvoiceDetails}">
			</af:commandButton>
		</af:panelButtonBar>
	</afh:rowLayout>
</af:panelGroup>
