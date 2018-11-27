<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>

<af:panelBorder rendered="#{!erNTVDetail.err && reportSearch.ntvReport}">
	<f:facet name="top">
		<f:subview id="reportHeader">
			<af:panelBox background="light" width="1000">

				<af:panelForm rows="2" maxColumns="4">
					<af:inputText label="Facility ID:" readOnly="True"
						value="#{erNTVDetail.facility.facilityId}" />
					<af:inputText label="Facility Name:" readOnly="true"
						value="#{erNTVDetail.facility.name}" />
					<af:inputText label="Emissions Inventory Category:" readOnly="True"
						value="#{facilityReference.emissionReportsDefs.itemDesc[(empty erNTVDetail.primaryReport.emissionsRptCd ? '' : erNTVDetail.primaryReport.emissionsRptCd)]}" />
					<af:inputText label="Emissions Inventory ID:" readOnly="True"
						value="#{erNTVDetail.primaryReport.emissionsInventoryId}" />
					<af:inputText label="#{erNTVDetail.primaryReport.submitLabel}"
						id="reportSubmitDate" readOnly="true"
						value="#{erNTVDetail.primaryReport.submitValue}">
					</af:inputText>
					<af:selectInputDate label="Approved Date:" id="reportApproveDate"
						readOnly="True" rendered="#{!erNTVDetail.primaryReport.submittedCode && erNTVDetail.primaryReport.approved}"
						value="#{erNTVDetail.primaryReport.rptApprovedStatusDate}">
					</af:selectInputDate>
					<af:selectInputDate label="Completed Date:"
						id="reportHandledDate" readOnly="True"
						rendered="#{!erNTVDetail.primaryReport.submittedCode && !erNTVDetail.primaryReport.approved}"
						value="#{erNTVDetail.primaryReport.rptApprovedStatusDate}">
					</af:selectInputDate>
					<af:inputText label=" " id="placeHolder"
						rendered="#{erNTVDetail.primaryReport.submittedCode}" readOnly="true"
						value=" ">
					</af:inputText>
					<af:inputText label="Reporting Years:"
						shortDesc="The year(s) the emissions inventory covers"
						readOnly="True" value="#{erNTVDetail.ntvReport.years}" />
					<af:inputText label="Reporting State:"
						shortDesc="State of Emissions Inventory (e.g., submitted, approved)"
						readOnly="True"
						value="#{facilityReference.reportReceivedStatusDefs.itemDesc[(empty erNTVDetail.primaryReport.rptReceivedStatusCd ? '' : erNTVDetail.primaryReport.rptReceivedStatusCd)]}" />
				</af:panelForm>
			</af:panelBox>
		</f:subview>
	</f:facet>
</af:panelBorder>

<af:showDetailHeader disclosed="true"
	rendered="#{!erNTVDetail.err && reportSearch.ntvReport}"
	text="Owner and/or Shutdown Changes">
	<h:panelGrid columns="1" border="1" id="ownShut">
		<af:selectInputDate id="receiveDate"
			label="Date inventory received by AQD:"
			rendered="#{erNTVDetail.internalApp || erNTVDetail.submitted}"
			showRequired="true" readOnly="#{!erNTVDetail.adminEditable}"
			value="#{erNTVDetail.primaryReport.receiveDate}">
			<af:validateDateTimeRange minimum="1900-01-01" maximum="#{infraDefs.currentDate}"/>
		</af:selectInputDate>
		<af:inputText
			label="If the facility has changed names, please enter the new name here:"
			readOnly="#{!erNTVDetail.adminEditable}" columns="60" id="nmChg"
			autoSubmit="true" maximumLength="60"
			value="#{erNTVDetail.primaryReport.facilityNm}" />
		<af:panelForm partialTriggers="ownerChange">
			<h:panelGrid columns="3" border="1" id="ownChng">
				<h:panelGrid columns="1" id="chgChk">
					<af:outputLabel
						value="AQD Records indicate facility ownership changed"
						inlineStyle="color:rgb(0,0,255);"
						rendered="#{erNTVDetail.systemOwnerChangeIndicator}" />
					<af:selectBooleanCheckbox
						value="#{erNTVDetail.ownerChangeIndicator}"
						label="Check here to specify facility ownership change"
						id="ownerChange" autoSubmit="true"
						readOnly="#{!erNTVDetail.adminEditable}" />
				</h:panelGrid>
				<af:panelForm>
					<afh:rowLayout rendered="#{erNTVDetail.ownerChangeIndicator}">
						<af:selectOneRadio disabled="#{!erNTVDetail.adminEditable}"
							id="whichOwner" value="#{erNTVDetail.purchaseSold}"
							showRequired="true">
							<f:selectItem itemLabel="I/The company purchased this facility"
								itemValue="1" />
							<f:selectItem itemLabel="I/The company sold this facility"
								itemValue="2" />
						</af:selectOneRadio>
					</afh:rowLayout>
					<afh:rowLayout rendered="#{erNTVDetail.ownerChangeIndicator}">
						<af:selectBooleanCheckbox
							value="#{erNTVDetail.primaryReport.provideBothYears}"
							readOnly="#{!erNTVDetail.adminEditable}" />
						<af:objectSpacer width="10" height="10" />
						<af:outputLabel
							value="I agree to report on and pay for any emissions, even though I may not have owned the facility for these years" />
					</afh:rowLayout>
				</af:panelForm>
				<af:panelForm partialTriggers="ownerChange"
					rendered="#{erNTVDetail.ownerChangeIndicator}">
					<af:selectOneChoice value="#{erNTVDetail.pickedTransferDate}"
						label="Owner Transfer Dates of Record"
						rendered="#{!erNTVDetail.pickListTransfersEmpty}"
						readOnly="#{!erNTVDetail.adminEditable}"
						unselectedLabel="select transfer date">
						<f:convertDateTime pattern="MM/dd/yyyy" />
						<f:selectItems value="#{erNTVDetail.pickListTransfers}" />
					</af:selectOneChoice>
					<af:inputText rendered="#{!erNTVDetail.pickListTransfersEmpty}"
						label="or" value="" readOnly="true" />
					<af:selectInputDate id="transferDate" label="Enter Transfer Date"
						readOnly="#{!erNTVDetail.adminEditable}"
						value="#{erNTVDetail.primaryReport.transferDate}"
						showRequired="true">
						<af:validateDateTimeRange minimum="1900-01-01" maximum="#{infraDefs.currentDate}"/>
					</af:selectInputDate>
				</af:panelForm>
			</h:panelGrid>
		</af:panelForm>
		<h:panelGrid columns="2" border="1" id="shut">
			<h:panelGrid columns="1" id="shut2">
				<af:selectBooleanCheckbox value="#{erNTVDetail.shutdownIndicator}"
					id="shutdown" autoSubmit="true"
					label="#{(empty erNTVDetail.knownShutdownDate)?'Check here if all contaminant sources have been permanently shut down or dismantled':'AQD Records indicate facility is shutdown'}"
					inlineStyle="#{(empty erNTVDetail.knownShutdownDate)?';':'color:rgb(0,0,255);'}"
					readOnly="#{!erNTVDetail.adminEditable || !(empty knownShutdownDate)}" />
			</h:panelGrid>
			<afh:rowLayout halign="right" partialTriggers="shutdown">
				<af:selectInputDate id="shutdownDate" label="Shutdown Date"
					rendered="#{erNTVDetail.shutdownIndicator && erNTVDetail.knownShutdownDate == null}"
					readOnly="#{!erNTVDetail.adminEditable || !(empty knownShutdownDate)}"
					value="#{erNTVDetail.primaryReport.shutdownDate}"
					showRequired="true">
					<af:validateDateTimeRange minimum="1900-01-01" maximum="#{infraDefs.currentDate}"/>
					</af:selectInputDate>
				<af:selectInputDate id="shutdownDate2" label="Shutdown Date"
					rendered="#{erNTVDetail.shutdownIndicator && erNTVDetail.knownShutdownDate != null}"
					readOnly="true" value="#{erNTVDetail.knownShutdownDate}" />
			</afh:rowLayout>
		</h:panelGrid>
		<afh:rowLayout halign="center">
			<af:panelButtonBar partialTriggers="nmChg ownerChange shutdown">
				<af:commandButton text="Edit" id="adminEdit"
					disabled="#{!erNTVDetail.ntvIssuance}"
					action="#{erNTVDetail.adminEdit}"
					rendered="#{!erNTVDetail.editable && !erNTVDetail.adminEditable && erNTVDetail.allowEdit}" />
				<af:commandButton text="Save" action="#{erNTVDetail.adminSave}"
					rendered="#{erNTVDetail.adminEditable}" />
				<af:commandButton text="Cancel" id="adminCancel"
					action="#{erNTVDetail.adminCancel}"
					rendered="#{erNTVDetail.adminEditable && erNTVDetail.reloadRpt}"
					immediate="true" />
				<af:commandButton text="Cancel" id="adminNoChg"
					action="#{erNTVDetail.adminCancel}"
					rendered="#{erNTVDetail.adminEditable && !erNTVDetail.reloadRpt && (erNTVDetail.primaryReport.facilityNm!=null && erNTVDetail.primaryReport.facilityNm!='' || erNTVDetail.ownerChangeIndicator || erNTVDetail.shutdownIndicator || erNTVDetail.internalApp)}"
					immediate="true" />
				<af:commandButton
					text="No ownership changes & not shutdown; continue with report"
					inlineStyle="font-weight:bold; background-color: rgb(255,255,119)"
					id="adminNoChg2" action="#{erNTVDetail.adminCancel}"
					rendered="#{erNTVDetail.adminEditable && !erNTVDetail.reloadRpt && !(erNTVDetail.primaryReport.facilityNm!=null && erNTVDetail.primaryReport.facilityNm!='' || erNTVDetail.ownerChangeIndicator || erNTVDetail.shutdownIndicator || erNTVDetail.internalApp)}"
					immediate="true" />
			</af:panelButtonBar>
		</afh:rowLayout>
	</h:panelGrid>
</af:showDetailHeader>

<af:showDetailHeader disclosed="true" text="Special Instructions"
	rendered="#{!erNTVDetail.err && reportSearch.ntvReport && (erNTVDetail.displayMsg != null || !erNTVDetail.allowContactChange && !erNTVDetail.approved || !erNTVDetail.reloadRpt || (erNTVDetail.primaryReport.rptReceivedStatusCd == '07sc' && erNTVDetail.internalApp) || erNTVDetail.allowContactChange && !erNTVDetail.allowPrimaryAddress && !erNTVDetail.approved || !erNTVDetail.existingPurchaseOwner && !erNTVDetail.needNewOwnerInfo && erNTVDetail.allowContactChange
	 || erNTVDetail.primaryReport.autoGenerated)}">
	<h:panelGrid columns="1" border="0"
		rendered="#{erNTVDetail.primaryReport.rptReceivedStatusCd == '07sc' && erNTVDetail.internalApp}">
		<afh:rowLayout halign="left">
			<af:outputText
				value="This emissions inventory is Submitted with Caution for AQD reviewers.  Please resolve any issues (see report notes below) prior to approving it."
				inlineStyle="color: orange; font-weight: bold;" />
		</afh:rowLayout>
	</h:panelGrid>
	<h:panelGrid columns="1" border="0"
		rendered="#{erNTVDetail.primaryReport.autoGenerated}">
		<afh:rowLayout halign="left">
			<af:selectInputDate id="autoGenId"
				label="This report was Auto-Generated because no report was submitted before"
				rendered="#{erNTVDetail.primaryReport.autoGenerated}"
				inlineStyle="color: orange; font-weight: bold;" readOnly="true"
				value="#{erNTVDetail.primaryReport.rptReceivedStatusDate}" />
		</afh:rowLayout>
	</h:panelGrid>
	<h:panelGrid columns="1" border="0"
		rendered="#{!erNTVDetail.reloadRpt}">
		<afh:rowLayout halign="left">
			<af:outputText
				value="First indicate whether ownership change or facility shutdown."
				inlineStyle="color:rgb(0,0,255);" />
		</afh:rowLayout>
	</h:panelGrid>
	<h:panelGrid columns="1" border="0"
		rendered="#{erNTVDetail.displayMsg != null}">
		<afh:rowLayout halign="left">
			<af:outputText value="#{erNTVDetail.displayMsg}"
				inlineStyle="color:rgb(0,0,255);" />
		</afh:rowLayout>
	</h:panelGrid>
	<h:panelGrid columns="1" border="0"
		rendered="#{!erNTVDetail.allowContactChange  && !erNTVDetail.approved}">
		<afh:rowLayout halign="left">
			<af:outputText
				value="Since there are other reports for more recent years or reports for the same year but not yet completed or a more recent owner(s) is already known, you cannot change Owner information or Primary contact information here."
				inlineStyle="color:rgb(0,0,255);" />
		</afh:rowLayout>
	</h:panelGrid>
	<h:panelGrid columns="1" border="0"
		rendered="#{erNTVDetail.allowContactChange && !erNTVDetail.allowPrimaryAddress && !erNTVDetail.approved}">
		<afh:rowLayout halign="left">
			<af:outputText
				value="The Previous Owner cannot supply Primary contact information."
				inlineStyle="color:rgb(0,0,255);" />
		</afh:rowLayout>
	</h:panelGrid>
	<h:panelGrid columns="1" border="0"
		rendered="#{!erNTVDetail.existingPurchaseOwner && !erNTVDetail.needNewOwnerInfo && erNTVDetail.allowContactChange}">
		<afh:rowLayout halign="left">
			<af:outputText
				value="The Previous Owner cannot supply Owner information here because the transfer of ownership already known."
				inlineStyle="color:rgb(0,0,255);" />
		</afh:rowLayout>
	</h:panelGrid>
</af:showDetailHeader>

<%/* Owner Address Information */%>

<af:showDetailHeader disclosed="true" text="Owner Information"
	partialTriggers="prevOwnerChange newOwnerChange billingChange primaryChange"
	rendered="#{!erNTVDetail.err && reportSearch.ntvReport}">
	<af:panelBorder>
		<f:facet name="left">
			<h:panelGrid columns="1" border="1" id="currOwn">
				<h:panelGrid id="currOwn2">
					<afh:rowLayout halign="center">
						<af:outputLabel value="Current Owner(s) of Record" />
					</afh:rowLayout>
				</h:panelGrid>
				<t:div style="overflow:auto;height:200px">
					<h:panelGrid border="0" id="currOwn3">
						<af:outputFormatted value="#{erNTVDetail.currentOwnersAddresses}" />
					</h:panelGrid>
				</t:div>
			</h:panelGrid>
		</f:facet>
		<f:facet name="innerLeft">
			<af:panelForm rendered="#{erNTVDetail.renderPrevOwnerAddr}">
				<h:panelGrid columns="1" border="1" id="prevOwn">
					<h:panelGrid id="prevOwn2">
						<afh:rowLayout halign="center">
							<af:outputLabel value="Previous Owner Forwarding Address" />
						</afh:rowLayout>
					</h:panelGrid>
					<af:panelForm>
						<af:outputFormatted value="#{erNTVDetail.prevOwnerAddress}" />
						<af:objectSpacer width="15" height="15" />
						<afh:rowLayout halign="center">
							<af:selectOneChoice value="#{erNTVDetail.prevOwnerCont}"
								autoSubmit="true" id="prevOwnerChange"
								rendered="#{!erNTVDetail.editable && !erNTVDetail.adminEditable}"
								disabled="#{!erNTVDetail.ntvIssuance}"
								unselectedLabel="Start with existing contact">
								<f:selectItems value="#{erNTVDetail.currentPrevOwnerPickList}" />
							</af:selectOneChoice>
							<af:objectSpacer width="15" height="10" />
							<af:panelButtonBar
								rendered="#{!erNTVDetail.editable && !erNTVDetail.adminEditable && erNTVDetail.allowEdit}">
								<af:commandButton text="Edit" id="editForward"
									disabled="#{!erNTVDetail.ntvIssuance}"
									action="#{erNTVDetail.editForward}" useWindow="true"
									windowWidth="700" windowHeight="635">
								</af:commandButton>
								<af:commandButton text="Remove" id="removeForward"
									disabled="#{!erNTVDetail.ntvIssuance}"
									rendered="#{erNTVDetail.prevOwnerAddress!=null}"
									action="#{erNTVDetail.removeForward}" />
							</af:panelButtonBar>
						</afh:rowLayout>
					</af:panelForm>
				</h:panelGrid>
			</af:panelForm>
		</f:facet>
		<f:facet name="innerRight">
			<af:panelForm rendered="#{erNTVDetail.renderNewOwnerAddr}">
				<h:panelGrid columns="1" border="1" id="newOwn">
					<h:panelGrid id="newOwn2">
						<afh:rowLayout halign="center">
							<af:outputLabel value="New Owner Information"
								rendered="#{erNTVDetail.needNewOwnerInfo}" />
							<af:outputLabel value="My Updated Owner Information"
								rendered="#{!erNTVDetail.needNewOwnerInfo}" />
						</afh:rowLayout>
					</h:panelGrid>
					<af:panelForm>
						<af:outputFormatted value="#{erNTVDetail.newOwnerAddress}" />
						<af:objectSpacer width="15" height="15" />
						<afh:rowLayout halign="center">
							<af:selectOneChoice value="#{erNTVDetail.newOwnerCont}"
								autoSubmit="true" id="newOwnerChange"
								rendered="#{!erNTVDetail.editable && !erNTVDetail.adminEditable}"
								disabled="#{!erNTVDetail.ntvIssuance}"
								unselectedLabel="Start with existing contact">
								<f:selectItems value="#{erNTVDetail.currentNewOwnerPickList}" />
							</af:selectOneChoice>
							<af:objectSpacer width="15" height="10" />
							<af:panelButtonBar
								rendered="#{!erNTVDetail.editable && !erNTVDetail.adminEditable && erNTVDetail.allowEdit}">
								<af:commandButton text="Edit"
									disabled="#{!erNTVDetail.ntvIssuance}"
									action="#{erNTVDetail.editNewOwner}" useWindow="true"
									windowWidth="700" windowHeight="635" immediate="true">
								</af:commandButton>
								<af:commandButton text="Remove"
									disabled="#{!erNTVDetail.ntvIssuance}"
									action="#{erNTVDetail.removeNewOwner}"
									rendered="#{erNTVDetail.newOwnerAddress!=null}" />
							</af:panelButtonBar>
						</afh:rowLayout>
					</af:panelForm>
				</h:panelGrid>
			</af:panelForm>
		</f:facet>
	</af:panelBorder>
</af:showDetailHeader>

<%/* Billing/Primary Address Information */%> 

<af:showDetailHeader disclosed="true"
	partialTriggers="prevOwnerChange newOwnerChange billingChange primaryChange"
	rendered="#{reportSearch.ntvReport && erNTVDetail.renderBillingPrimary}"
	text="Billing & Primary Contact Information">
	<af:panelBorder>
		<f:facet name="left">
			<h:panelGrid columns="2" border="1" id="currBill">
				<h:panelGrid columns="1" border="0" id="currBill2">
					<afh:rowLayout halign="center">
						<af:outputLabel value="Billing Contact Information of Record" />
					</afh:rowLayout>
					<afh:rowLayout halign="center">
						<af:outputLabel value="#{erNTVDetail.billingDateRange}" />
					</afh:rowLayout>
				</h:panelGrid>
				<h:panelGrid id="updatBill">
					<afh:rowLayout halign="center">
						<af:outputLabel value="My Updated Billing Contact Information" />
					</afh:rowLayout>
					<afh:rowLayout halign="center"
						rendered="#{!erNTVDetail.existingPurchaseOwner}">
						<af:outputLabel value="for this report only"
							inlineStyle="color:rgb(0,0,255);" />
					</afh:rowLayout>
					<afh:rowLayout halign="center"
						rendered="#{erNTVDetail.newBillingAddress!=null}">
						<af:outputLabel value="#{erNTVDetail.billingCompare}"
							inlineStyle="color:rgb(0,0,255);" />
					</afh:rowLayout>
				</h:panelGrid>
				<h:panelGrid id="currBill">
					<af:outputFormatted value="#{erNTVDetail.currentBillingAddress}" />
				</h:panelGrid>
				<af:panelForm>
					<af:outputFormatted value="#{erNTVDetail.newBillingAddress}" />
					<af:objectSpacer width="15" height="15" />
					<afh:rowLayout halign="center">
						<af:selectOneChoice value="#{erNTVDetail.newBillingCont}"
							autoSubmit="true" id="billingChange"
							rendered="#{!erNTVDetail.editable && !erNTVDetail.adminEditable}"
							disabled="#{!erNTVDetail.ntvIssuance}"
							unselectedLabel="Start with existing contact">
							<f:selectItems value="#{erNTVDetail.currentNewBillingPickList}" />
						</af:selectOneChoice>
						<af:objectSpacer width="15" height="10" />
						<af:panelButtonBar
							rendered="#{!erNTVDetail.editable && !erNTVDetail.adminEditable && erNTVDetail.allowEdit}">
							<af:commandButton text="Edit" id="editBilling"
								disabled="#{!erNTVDetail.ntvIssuance}"
								action="#{erNTVDetail.editBilling}" useWindow="true"
								windowWidth="700" windowHeight="635">
							</af:commandButton>
							<af:commandButton text="Remove" id="removeBilling"
								disabled="#{!erNTVDetail.ntvIssuance}"
								rendered="#{erNTVDetail.newBillingAddress!=null}"
								action="#{erNTVDetail.removeBilling}" />
						</af:panelButtonBar>
					</afh:rowLayout>
				</af:panelForm>
			</h:panelGrid>
		</f:facet>
		<f:facet name="right">
			<h:panelGrid columns="2" border="1" id="newPrim"
				rendered="#{erNTVDetail.newPrimaryAddress != null || erNTVDetail.allowPrimaryAddress}">
				<h:panelGrid columns="1" border="0" id="newPrim2">
					<afh:rowLayout halign="center">
						<af:outputLabel
							value="Current Primary Contact Information of Record" />
					</afh:rowLayout>
					<afh:rowLayout halign="center">
						<af:outputLabel value="#{erNTVDetail.primaryDateRange}" />
					</afh:rowLayout>
				</h:panelGrid>
				<h:panelGrid id="updatPrim">
					<afh:rowLayout halign="center">
						<af:outputLabel value="My Updated Primary Contact Information" />
					</afh:rowLayout>
					<afh:rowLayout halign="center"
						rendered="#{erNTVDetail.newPrimaryAddress!=null}">
						<af:outputLabel value="#{erNTVDetail.primaryCompare}"
							inlineStyle="color:rgb(0,0,255);" />
					</afh:rowLayout>
				</h:panelGrid>
				<h:panelGrid id="prevForward">
					<af:outputFormatted
						rendered="#{erNTVDetail.prevOwnerAddress != null && erNTVDetail.currentPrimaryAddress != null && !erNTVDetail.approved}"
						value="Previous Owner forwarding address will end the current Primary contact"
						inlineStyle="color:rgb(0,0,255);" />
					<af:outputFormatted value="#{erNTVDetail.currentPrimaryAddress}" />
				</h:panelGrid>
				<af:panelForm>
					<af:outputFormatted value="#{erNTVDetail.newPrimaryAddress}" />
					<af:objectSpacer width="15" height="15" />
					<afh:rowLayout halign="center">
						<af:selectOneChoice value="#{erNTVDetail.newPrimaryCont}"
							autoSubmit="true" id="primaryChange"
							rendered="#{!erNTVDetail.editable && !erNTVDetail.adminEditable}"
							disabled="#{!erNTVDetail.ntvIssuance}"
							unselectedLabel="Start with existing contact">
							<f:selectItems value="#{erNTVDetail.currentNewPrimaryPickList}" />
						</af:selectOneChoice>
						<af:objectSpacer width="15" height="10" />
						<af:panelButtonBar
							rendered="#{!erNTVDetail.editable && !erNTVDetail.adminEditable && erNTVDetail.allowEdit}">
							<af:commandButton text="Edit" id="editPrimary"
								disabled="#{!erNTVDetail.ntvIssuance}"
								action="#{erNTVDetail.editPrimary}" useWindow="true"
								windowWidth="700" windowHeight="635">
							</af:commandButton>
							<af:commandButton text="Remove" id="removePrimary"
								disabled="#{!erNTVDetail.ntvIssuance}"
								rendered="#{erNTVDetail.newPrimaryAddress!=null}"
								action="#{erNTVDetail.removePrimary}" />
						</af:panelButtonBar>
					</afh:rowLayout>
				</af:panelForm>
			</h:panelGrid>
		</f:facet>
	</af:panelBorder>
</af:showDetailHeader>

<%/* Emissions Information */%> 
<af:showDetailHeader disclosed="true" text="Fee Emission Information"
	rendered="#{reportSearch.ntvReport && erNTVDetail.renderEmissionsInfo}"
	partialTriggers="rangeEven rangeOdd">
	<h:panelGrid columns="3" border="1" id="emInfo"
		rendered="#{reportSearch.ntvReport && erNTVDetail.renderEmissionsInfo}">
		<af:outputLabel value="Emissions reported in previous years" />
		<h:panelGrid border="0" columns="1" id="emInfo2">
			<afh:rowLayout halign="center">
				<af:outputLabel value="#{erNTVDetail.prevRptEvenYear}" />
			</afh:rowLayout>
			<afh:rowLayout halign="center"
				rendered="#{erNTVDetail.prevRptEvenYearTons == null}">
				<af:inputText value="#{erNTVDetail.prevRptEvenYearString}"
					readOnly="true" />
			</afh:rowLayout>
			<afh:rowLayout halign="center"
				rendered="#{erNTVDetail.prevRptEvenYearTons != null}">
				<af:inputText value="#{erNTVDetail.prevRptEvenYearTons}"
					readOnly="true" />
				<af:objectSpacer width="5" height="5" />
				<af:outputLabel value="Tons" />
			</afh:rowLayout>
		</h:panelGrid>
		<h:panelGrid border="0" columns="1" id="emInfo3">
			<afh:rowLayout halign="center">
				<af:outputLabel value="#{erNTVDetail.prevRptOddYear}" />
			</afh:rowLayout>
			<afh:rowLayout halign="center"
				rendered="#{erNTVDetail.prevRptOddYearTons == null}">
				<af:inputText value="#{erNTVDetail.prevRptOddYearString}"
					readOnly="true" />
			</afh:rowLayout>
			<afh:rowLayout halign="center"
				rendered="#{erNTVDetail.prevRptOddYearTons != null}">
				<af:inputText value="#{erNTVDetail.prevRptOddYearTons}"
					readOnly="true" />
				<af:objectSpacer width="5" height="5" />
				<af:outputLabel value="Tons" />
			</afh:rowLayout>
		</h:panelGrid>
		<af:outputLabel
			value="Indicate the total for all the pollutants facility-wide for the year(s) specified by selecting the appropriate range for each year" />
		<h:panelGrid border="0" columns="1">
			<af:panelForm rendered="#{erNTVDetail.report1 != null}">
				<afh:rowLayout halign="center">
					<af:outputLabel value="#{erNTVDetail.report1.reportYear}" />
				</afh:rowLayout>
				<af:selectOneChoice value="#{erNTVDetail.fee1}" autoSubmit="true"
					unselectedLabel="Please Select TPY Range"
					readOnly="#{!erNTVDetail.editable}" id="rangeEven">
					<f:selectItems value="#{erNTVDetail.fee1PickList}" />
				</af:selectOneChoice>
			</af:panelForm>
		</h:panelGrid>
		<h:panelGrid border="0" columns="1">
			<af:panelForm rendered="#{erNTVDetail.report2 != null}">
				<afh:rowLayout halign="center">
					<af:outputLabel value="#{erNTVDetail.report2.reportYear}" />
				</afh:rowLayout>
				<af:selectOneChoice value="#{erNTVDetail.fee2}" autoSubmit="true"
					unselectedLabel="Please Select TPY Range"
					readOnly="#{!erNTVDetail.editable}" id="rangeOdd">
					<f:selectItems value="#{erNTVDetail.fee2PickList}" />
				</af:selectOneChoice>
			</af:panelForm>
		</h:panelGrid>
	</h:panelGrid>

	<%/* Additional Emissions Information */%>
	<h:panelGrid columns="3" border="1" rules="all" id="addInfo"
		rendered="#{reportSearch.ntvReport && erNTVDetail.renderAdditionalInfo}">
		<af:outputLabel
			value="Total Emissions are greater than #{erNTVDetail.mustEnumerateFER} Tons Per Year (TPY).  Please enter emissions for each pollutant listed  " />
		<h:panelGrid columns="1" rendered="true" id="addInfo2">
			<af:panelForm rendered="#{erNTVDetail.evenAdditionalEmissions}">
				<afh:rowLayout halign="center">
					<af:outputLabel value="#{erNTVDetail.report1.reportYear}" />
				</afh:rowLayout>
				<af:table value="#{erNTVDetail.emissionsFer1}"
					rows="#{erNTVDetail.pageLimit}" id="emissionsTab" emptyText=" "
					banding="row" var="emissions">
					<af:column sortable="false"
						headerText="Pollutant, amount expressed in tons per year">
						<af:selectOneChoice value="#{emissions.pollutantCd}"
							readOnly="true">
							<f:selectItems
								value="#{facilityReference.nonToxicPollutantDefs.items[(empty emissions.pollutantCd ? '' : emissions.pollutantCd)]}" />
						</af:selectOneChoice>
					</af:column>
					<af:column formatType="number" sortable="false" headerText="TPY">
						<af:inputText value="#{emissions.totalEmissions}" id="TPY"
							readOnly="#{!erNTVDetail.editable}" maximumLength="11">
							<mu:convertSigDigNumber pattern="###,###,##0.########" />
						</af:inputText>
					</af:column>
					<f:facet name="footer">
						<h:panelGrid width="100%">
							<afh:rowLayout halign="right">
								<af:outputLabel value="Total Chargeable Pollutants:" />
								<af:objectSpacer width="7" height="5" />
								<af:outputText value="#{erNTVDetail.displayTotal1}"
									rendered="#{!erNTVDetail.editable}" />
							</afh:rowLayout>
						</h:panelGrid>
					</f:facet>
				</af:table>
			</af:panelForm>
		</h:panelGrid>
		<h:panelGrid columns="1" id="addInfo3">
			<af:panelForm rendered="#{erNTVDetail.oddAdditionalEmissions}">
				<afh:rowLayout halign="center">
					<af:outputLabel value="#{erNTVDetail.report2.reportYear}" />
				</afh:rowLayout>
				<af:table value="#{erNTVDetail.emissionsFer2}"
					rows="#{erNTVDetail.pageLimit}" id="emissionsTab2" emptyText=" "
					banding="row" var="emissions">
					<af:column sortable="false"
						headerText="Pollutant, amount expressed in tons per year">
						<af:selectOneChoice value="#{emissions.pollutantCd}"
							readOnly="true">
							<f:selectItems
								value="#{facilityReference.nonToxicPollutantDefs.items[(empty emissions.pollutantCd ? '' : emissions.pollutantCd)]}" />
						</af:selectOneChoice>
					</af:column>
					<af:column formatType="number" sortable="false" headerText="TPY">
						<af:inputText value="#{emissions.totalEmissions}" id="TPY"
							readOnly="#{!erNTVDetail.editable}" maximumLength="11">
							<mu:convertSigDigNumber pattern="###,###,##0.########" />
						</af:inputText>
					</af:column>
					<f:facet name="footer">
						<h:panelGrid width="100%">
							<afh:rowLayout halign="right">
								<af:outputLabel value="Total Chargeable Pollutants:" />
								<af:objectSpacer width="7" height="5" />
								<af:outputText value="#{erNTVDetail.displayTotal2}"
									rendered="#{!erNTVDetail.editable}" />
							</afh:rowLayout>
						</h:panelGrid>
					</f:facet>
				</af:table>
			</af:panelForm>
		</h:panelGrid>
	</h:panelGrid>
</af:showDetailHeader>

<%/* Non Attainment  Emissions Statement */%>

<af:showDetailHeader text="Emissions Statement" disclosed="true"
	rendered="#{reportSearch.ntvReport && erNTVDetail.renderNonAttain}">
	<h:panelGrid columns="3" border="1" id="esInfo">
		<af:outputLabel
			value="These Emissions must be reported if Facility is in a non-attainment county and any one of these pollutant emissions is more than #{erNTVDetail.mustEnumerateES} Tons Per Year (TPY)" />
		<h:panelGrid columns="1" border="0" id="esInfo2">
			<af:panelForm rendered="#{erNTVDetail.report1 != null}"
				partialTriggers="rptInc1">
				<afh:rowLayout halign="center">
					<af:outputLabel value="#{erNTVDetail.report1.reportYear}" />
				</afh:rowLayout>
				<afh:rowLayout halign="center">
					<af:selectBooleanCheckbox value="#{erNTVDetail.nonAttainment1}"
						label="NonAttainment County" readOnly="true" />
					<af:objectSpacer width="20" height="10" />
					<af:selectBooleanCheckbox id="rptInc1" autoSubmit="true"
						value="#{erNTVDetail.includeES1}" label="Report Included"
						readOnly="#{! erNTVDetail.editable}" />
				</afh:rowLayout>
				<af:table
					value="#{erNTVDetail.report1.rptES?erNTVDetail.emissionsES1:erNTVDetail.emissionsES1Gray}"
					rows="#{erNTVDetail.pageLimit}" id="emissionsES1Tab" emptyText=" "
					banding="row" var="emissions">
					<af:column sortable="false"
						headerText="Pollutant, amount expressed in tons per year">
						<af:selectOneChoice value="#{emissions.pollutantCd}"
							inlineStyle="#{erNTVDetail.report1.rptES? '':'color:rgb(153,153,153)' }"
							readOnly="true">
							<f:selectItems
								value="#{facilityReference.nonToxicPollutantDefs.items[(empty emissions.pollutantCd ? '' : emissions.pollutantCd)]}" />
						</af:selectOneChoice>
					</af:column>
					<af:column formatType="number" sortable="false" headerText="TPY">
						<af:inputText value="#{emissions.totalEmissions}" id="TPY"
							readOnly="#{!erNTVDetail.editable || !erNTVDetail.report1.rptES}">
							<mu:convertSigDigNumber pattern="##,###,##0.########E00" />
						</af:inputText>
					</af:column>
				</af:table>
			</af:panelForm>
		</h:panelGrid>
		<h:panelGrid columns="1" border="0" id="esInfo4">
			<af:panelForm rendered="#{erNTVDetail.report2 != null}"
				partialTriggers="rptInc2">
				<afh:rowLayout halign="center">
					<af:outputLabel value="#{erNTVDetail.report2.reportYear}" />
				</afh:rowLayout>
				<afh:rowLayout halign="center">
					<af:selectBooleanCheckbox value="#{erNTVDetail.nonAttainment2}"
						label="NonAttainment County" readOnly="true" />
					<af:objectSpacer width="20" height="10" />
					<af:selectBooleanCheckbox id="rptInc2" autoSubmit="true"
						value="#{erNTVDetail.includeES2}" label="Report Included"
						readOnly="#{! erNTVDetail.editable}" />
				</afh:rowLayout>
				<af:table
					value="#{erNTVDetail.report2.rptES?erNTVDetail.emissionsES2:erNTVDetail.emissionsES2Gray}"
					rows="#{erNTVDetail.pageLimit}" id="emissionsES2Tab" emptyText=" "
					banding="row" var="emissions">
					<af:column sortable="false"
						headerText="Pollutant, amount expressed in tons per year">
						<af:selectOneChoice value="#{emissions.pollutantCd}"
							inlineStyle="#{erNTVDetail.report2.rptES? '':'color:rgb(153,153,153)' }"
							readOnly="true">
							<f:selectItems
								value="#{facilityReference.nonToxicPollutantDefs.items[(empty emissions.pollutantCd ? '' : emissions.pollutantCd)]}" />
						</af:selectOneChoice>
					</af:column>
					<af:column formatType="number" sortable="false" headerText="TPY">
						<af:inputText value="#{emissions.totalEmissions}" id="TPY"
							readOnly="#{!erNTVDetail.editable || !erNTVDetail.report2.rptES}">
							<mu:convertSigDigNumber pattern="##,###,##0.########E00" />
						</af:inputText>
					</af:column>
				</af:table>
			</af:panelForm>
		</h:panelGrid>
	</h:panelGrid>
</af:showDetailHeader>

<af:showDetailHeader text="Notes" disclosed="true"
	rendered="#{!erNTVDetail.err && reportSearch.ntvReport && erNTVDetail.internalApp}">
	<jsp:include flush="true" page="ntvNotesTable.jsp" />
</af:showDetailHeader>

<af:showDetailHeader text="Attachments" disclosed="true"
	rendered="#{!erNTVDetail.err && reportSearch.ntvReport}">
	<af:iterator value="#{erNTVDetail}" var="reportProfile" id="v">
		<jsp:include flush="true" page="er_attachments.jsp" />
	</af:iterator>
</af:showDetailHeader>

<%/* Button Panel */%>

<afh:rowLayout halign="center"
	rendered="#{!erNTVDetail.err && reportSearch.ntvReport}">
	<h:panelGrid id="but2">
		<af:objectSpacer width="10" height="6" />
		<afh:rowLayout halign="center" rendered="#{!erNTVDetail.readOnlyUser}">
			<af:panelButtonBar>
				<af:commandButton text="Edit Emissions" id="edit" action="#{erNTVDetail.edit}"
					disabled="#{!erNTVDetail.generalUser && !erNTVDetail.ntvIssuance}" inlineStyle="background-color:yellow;"
					rendered="#{!erNTVDetail.editable && !erNTVDetail.adminEditable && erNTVDetail.allowEdit}" />
				<af:commandButton text="Save" action="#{erNTVDetail.save}"
					rendered="#{erNTVDetail.editable}" />
				<af:commandButton text="Cancel" action="#{erNTVDetail.cancel}"
					rendered="#{erNTVDetail.editable}" immediate="true" />
				<af:commandButton text="Validate"
					action="#{erNTVDetail.ntvValidate}"
					disabled="#{!erNTVDetail.generalUser && !erNTVDetail.ntvIssuance}"
					rendered="#{!erNTVDetail.editable && !erNTVDetail.adminEditable && erNTVDetail.allowEdit}">
					<t:updateActionListener property="#{facilityProfile.validTradeSecretNotifFrom}"	value="emissionReport" />
				</af:commandButton>
				<af:commandButton text="Workflow Task"
					shortDesc="Return to the workflow step"
					disabled="#{!reportDetail.fromTODOList}"
					rendered="#{!erNTVDetail.editable && !erNTVDetail.adminEditable && erNTVDetail.submitted && erNTVDetail.internalApp}"
					action="#{reportDetail.goToCurrentWorkflow}" />
				<af:switcher defaultFacet="internalNtv"
					rendered="#{erNTVDetail.showNtvSubmitButton}"
					facetName="#{erNTVDetail.internalApp ? 'internalNtv' : 'externalNtv'}">
					<f:facet name="internalNtv">
						<af:panelButtonBar>
							<af:commandButton text="Submit" action="#{erNTVDetail.ntvSubmit}"
								disabled="#{!erNTVDetail.ntvIssuance}"
								rendered="#{erNTVDetail.hasPassedValidation}" />
							<af:commandButton text="Submit With Caution"
								action="#{erNTVDetail.ntvSubmitCaution}"
								disabled="#{!erNTVDetail.ntvIssuance}"
								shortDesc="Submit This Emissions Inventory but mark it requires caution prior to approval"
								rendered="#{erNTVDetail.hasPassedValidation}" />
							<af:commandButton text="Submit Anyway"
								disabled="#{!erNTVDetail.ntvIssuance}"
								action="#{erNTVDetail.ntvSubmit}"
								shortDesc="This emissions inventory fails validation but it will be the responsibility of the DO/LAA to complete it"
								rendered="#{!erNTVDetail.submitted && erNTVDetail.hasBeenValidated && !erNTVDetail.hasPassedValidation}" />
						</af:panelButtonBar>
					</f:facet>
					<f:facet name="externalNtv">
						<af:panelButtonBar>
							<af:commandButton id="ExtSubmit" text="Submit"
								action="#{erNTVDetail.ntvSubmit}" useWindow="true"
								disabled="#{!myTasks.hasSubmit}"
								rendered="#{erNTVDetail.hasPassedValidation}"
								windowWidth="#{submitTask.attestWidth}"
								windowHeight="#{submitTask.attestHeight}">
								<t:updateActionListener property="#{submitTask.type}"
									value="#{submitTask.yesNo}" />
								<t:updateActionListener property="#{submitTask.task}"
									value="#{erNTVDetail.createRTask}" />
							</af:commandButton>
							<%-- hide the download attestation button until it is not supported in IMPACT
							<af:goButton id="attestationDocButton"
									text="Download Attestation Document" targetFrame="_blank"
									destination="#{erNTVDetail.attestationDocURL}" />--%>
						</af:panelButtonBar>
					</f:facet>
				</af:switcher>
				<af:commandButton text="Create Revised Inventory"
					disabled="#{!erNTVDetail.generalUser && !erNTVDetail.submitted}"
					rendered="#{!erNTVDetail.editable && !erNTVDetail.adminEditable}"
					shortDesc="#{erNTVDetail.submitted?'Create revised emissions inventory based upon emissions in this inventory':'Emissions inventory can still be modified.  You cannot create a revised emissions inventory until this inventory is submitted.'}"
					id="CreateRevisedButton" useWindow="true" windowWidth="700"
					windowHeight="310" action="#{erNTVDetail.startCreateRevisedReport}">
				</af:commandButton>
				<af:commandButton text="Delete Inventory" id="deleteRpt"
					shortDesc="Delete entire emissions inventory including emissions, contact information and attachments"
					disabled="#{!erNTVDetail.generalUser && !erNTVDetail.ntvIssuance}"
					action="#{erNTVDetail.ntvDeleteRptReq}" useWindow="true"
					windowWidth="500" windowHeight="300"
					rendered="#{!erNTVDetail.editable && !erNTVDetail.adminEditable && !erNTVDetail.submitted && erNTVDetail.internalApp}" />
			</af:panelButtonBar>
		</afh:rowLayout>
		<af:objectSpacer height="10" />
		<afh:rowLayout halign="center" rendered="#{!erNTVDetail.readOnlyUser}">
			<af:panelButtonBar id="ntvButtons3"
				rendered="#{erNTVDetail.submitted && erNTVDetail.internalApp && !erNTVDetail.editable && !erNTVDetail.adminEditable && !erNTVDetail.ntvReport.primary.reportNotNeeded}">
				<af:commandButton text="Submitted/Revision Needed"
					disabled="#{!reportProfile.ferEsApprover}"
					shortDesc="A Revised Emissions Inventory is needed from the facility"
					action="#{erNTVDetail.toStateRR}"
					rendered="#{erNTVDetail.ntvReport.primary.rptReceivedStatusCd != '07rr' && !erNTVDetail.approved}" />
				<af:commandButton text="Submitted/Caution"
					disabled="#{!reportProfile.ferEsApprover}"
					shortDesc="This Submitted Emissions Inventory requires caution prior to approval"
					action="#{erNTVDetail.toStateSC}"
					rendered="#{erNTVDetail.ntvReport.primary.rptReceivedStatusCd != '07sc' && !erNTVDetail.approved}" />
				<af:commandButton text="Straight Submitted"
					disabled="#{!reportProfile.ferEsApprover}"
					shortDesc="Remove Conditions (Revision Needed or Caution) on Submitted Emissions Inventory"
					action="#{erNTVDetail.toStateSS}"
					rendered="#{erNTVDetail.ntvReport.primary.rptReceivedStatusCd != '08ur' && !erNTVDetail.approved}" />
				<af:commandButton text="Approve/Revision Needed"
					disabled="#{!reportProfile.ferEsApprover}"
					shortDesc="Approve in order to Invoice, but a Revised Emissions Inventory is still needed from the facility"
					action="#{erNTVDetail.toStateAR}"
					rendered="#{!erNTVDetail.needApproveConfirm && erNTVDetail.ntvReport.primary.rptReceivedStatusCd != '09ar'}" />
				<af:commandButton text="Approve/Revision Needed"
					disabled="#{!reportProfile.ferEsApprover}"
					shortDesc="Approve in order to Invoice, but a Revised Emissions Inventory is still needed from the facility"
					action="#{erNTVDetail.approveConfirmAR}" useWindow="true"
					windowWidth="500" windowHeight="300"
					rendered="#{erNTVDetail.needApproveConfirm && erNTVDetail.ntvReport.primary.rptReceivedStatusCd != '09ar'}" />
				<af:commandButton text="Approve" shortDesc="Approve the emissions inventory"
					disabled="#{!reportProfile.ferEsApprover}"
					action="#{erNTVDetail.toStateAA}"
					rendered="#{!erNTVDetail.needApproveConfirm && erNTVDetail.ntvReport.primary.rptReceivedStatusCd != '10aa'}" />
				<af:commandButton text="Approve" shortDesc="Approve the emissions inventory"
					disabled="#{!reportProfile.ferEsApprover}"
					action="#{erNTVDetail.approveConfirmAA}" useWindow="true"
					windowWidth="500" windowHeight="300"
					rendered="#{erNTVDetail.needApproveConfirm && erNTVDetail.ntvReport.primary.rptReceivedStatusCd != '10aa'}" />
				<af:commandButton text="Inventory Prior/Invalid"
					useWindow="true"  windowWidth="700" windowHeight="250"
					disabled="#{!reportProfile.ferEsApprover}"
					shortDesc="This emissions inventory is prior or invalid."
					action="#{erNTVDetail.confirmToStateNN}"
					rendered="#{erNTVDetail.ntvReport.primary.rptReceivedStatusCd != '06nn'}" />
			</af:panelButtonBar>
		</afh:rowLayout>
		<af:objectSpacer height="10" />
		<afh:rowLayout halign="center">
			<af:panelButtonBar id="ntvButtons4">
				<af:commandButton text="View Invoice"
					rendered="#{!erNTVDetail.editable && !erNTVDetail.adminEditable && erNTVDetail.internalApp && erNTVDetail.ntvReport.primary.rptApprovedStatusDate != null}"
					id="viewInvoiceButton" action="#{reportDetail.viewInvoice}">
					<t:updateActionListener property="#{reportDetail.reportId}"
						value="#{erNTVDetail.primaryReport.emissionsRptId}" />
				</af:commandButton>
				<af:commandButton text="Show Facility Inventory"
					rendered="#{!erNTVDetail.editable && !erNTVDetail.adminEditable}"
					id="viewFacilityButton" action="#{erNTVDetail.submitProfile}">
				</af:commandButton>
				<af:commandButton text="Download/Print" id="printPublicBtn"
					disabled="#{!erNTVDetail.generalUser}"
					rendered="#{!erNTVDetail.editable && !erNTVDetail.adminEditable && erNTVDetail.generalUser}"
					shortDesc="Print current public emissions information"
					action="#{erNTVDetail.printEmissionReport}" useWindow="true"
					windowWidth="800" windowHeight="300">
					<t:updateActionListener value="true"
						property="#{erNTVDetail.hideTradeSecret}" />
				</af:commandButton>
				<af:commandButton text="Download/Print Trade Secret Version"
					disabled="#{!erNTVDetail.generalUser}"
					id="printTradeSecretBtn"
					rendered="#{!erNTVDetail.editable && !erNTVDetail.adminEditable && erNTVDetail.tradeSecretVisible && !applicationDetail.publicReadOnlyUser}"
					shortDesc="Print current public and trade secret emissions information"
					action="#{erNTVDetail.printEmissionReport}" useWindow="true"
					windowWidth="800" windowHeight="300">
					<t:updateActionListener value="false"
						property="#{erNTVDetail.hideTradeSecret}" />
				</af:commandButton>
				
			</af:panelButtonBar>
		</afh:rowLayout>
	</h:panelGrid>
</afh:rowLayout>
