<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<mu:setProperty value="#{facilityProfile.fpId}"
	property="#{reportProfile.fpId}" />
<mu:setProperty value="#{null}" property="#{reportProfile.savedFpId}" />

<af:panelGroup layout="vertical"
	rendered="#{facilityProfile.renderComponent == 'facility' }"
	partialTriggers="addressLine1 addressLine2 cityName zipCode portable state countyCd facPermitClassCd">
	<af:panelHeader text="Facility Information" size="0" />
	<af:panelForm maxColumns="1" labelWidth="160" width="780">
		<af:inputText label="Facility ID:"
			value="#{facilityProfile.facilityId}" columns="10" readOnly="true" />
		<af:inputText label="Facility Name:"
			value="#{facilityProfile.facility.name}" id="name" columns="60"
			maximumLength="55" readOnly="#{! facilityProfile.editable}"
			showRequired="true" />
		<af:inputText label="Facility Description:"
			value="#{facilityProfile.facility.desc}" columns="60" rows="3"
			maximumLength="3000" wrap="soft" readOnly="#{!facilityProfile.editable}" />
	</af:panelForm>
	
	
	<af:panelForm rows="1" maxColumns="2" labelWidth="160" width="780"
		>		
		<af:selectOneChoice label="Facility Class:" inlineStyle="font-size:10pt;"
			value="#{facilityProfile.facility.permitClassCd}" 
			showRequired="true" id="facPermitClassCd"
			styleClass=""
			readOnly="#{(facilityProfile.demReadOnlyAttr) || facilityProfile.facility.shutdownNotifEnabled}">
			<f:selectItems
				value="#{facilityReference.permitClassDefs.items[(empty facilityProfile.facility.permitClassCd ? '' : facilityProfile.facility.permitClassCd)]}" />
    	</af:selectOneChoice>
    	
		<af:selectOneChoice label="Facility Type:" inlineStyle="font-size:10pt;"
			value="#{facilityProfile.facility.facilityTypeCd}" autoSubmit="true"
			showRequired="true" id="facilityTypeCd" unselectedLabel=" "
			styleClass="FacilityTypeClass x6"
			readOnly="#{(facilityProfile.demReadOnlyAttr) || facilityProfile.facility.shutdownNotifEnabled}">
			<f:selectItems
				value="#{facilityReference.facilityTypeDefs.items[(empty facilityProfile.facility.facilityTypeCd ? '' : facilityProfile.facility.facilityTypeCd)]}" />
		</af:selectOneChoice>
	</af:panelForm>

	<af:panelForm rows="1" maxColumns="2" width="780" labelWidth="160">
		<af:selectOneChoice label="CERR Class:" inlineStyle="font-size:10pt;"
		styleClass=""
			rendered="#{facilityProfile.dapcUser}"
			value="#{facilityProfile.facility.cerrClassCd}" id="facCerrClass"
			unselectedLabel=" " readOnly="#{facilityProfile.demReadOnlyAttr}">
			<f:selectItems
				value="#{facilityReference.cerrClassDefs.items[(empty facilityProfile.facility.cerrClassCd ? '' : facilityProfile.facility.cerrClassCd)]}" />
		</af:selectOneChoice>

		<af:panelLabelAndMessage rendered="#{facilityProfile.internalApp}"
			label="Associated Monitor Group ID:" for="mgrpId">
			<af:commandLink id="mgrpId"
				action="#{facilityProfile.submitMonitorGroup}"
				text="#{facilityProfile.facility.associatedMonitorGroup.mgrpId}"
				rendered="#{facilityProfile.internalApp}"
				disabled="#{facilityProfile.editable}">
			</af:commandLink>
		</af:panelLabelAndMessage>

		<af:inputText label="Associated Monitor Group ID:" readOnly="true"
			rendered="#{!facilityProfile.internalApp}"
			value="#{facilityProfile.facility.associatedMonitorGroup.mgrpId}"
			inlineStyle="font-size:10pt;" maximumLength="10" styleClass="afs" />

	</af:panelForm>

	<af:panelForm rows="1" maxColumns="2" labelWidth="160" width="780"
		partialTriggers="facOperatingStatus">
		<af:selectOneChoice label="Operating Status:" inlineStyle="font-size:10pt;"
			value="#{facilityProfile.facility.operatingStatusCd}"
			styleClass=""
			readOnly="#{! facilityProfile.operStatusUpdatable}"
			id="facOperatingStatus" showRequired="true" autoSubmit="true">
			<f:selectItems
				value="#{facilityProfile.facOperatingStatusDefs.items[(empty facilityProfile.facility.operatingStatusCd ? '' : facilityProfile.facility.operatingStatusCd)]}" />
		</af:selectOneChoice>
		<af:inputText label="AFS:"
			readOnly="#{facilityProfile.demReadOnlyAttr}"
			value="#{facilityProfile.facility.afs}" inlineStyle="font-size:10pt;"
			maximumLength="10" styleClass="afs" />
		<af:selectInputDate label="Shutdown Date:" id="shutdownDate" inlineStyle="font-size:10pt;"
			value="#{facilityProfile.facility.shutdownDate}"
			styleClass=""
			readOnly="#{facilityProfile.demReadOnlyAttr}" showRequired="true"
			rendered="#{facilityProfile.facility.operatingStatusCd == 'sd'}">
			<af:validateDateTimeRange minimum="1900-01-01"
				maximum="#{infraDefs.currentDate}" />
		</af:selectInputDate>
		<af:selectInputDate label="Shutdown Notification Date:" inlineStyle="font-size:10pt;"
			id="shutdownNotifDate" showRequired="true"
			styleClass=""
			value="#{facilityProfile.facility.shutdownNotifDate}"
			readOnly="#{facilityProfile.demReadOnlyAttr }"
			rendered="#{facilityProfile.facility.shutdownNotifEnabled}">
			<af:validateDateTimeRange minimum="1900-01-01"
				maximum="#{infraDefs.currentDate}" />
		</af:selectInputDate>
		<af:selectInputDate label="Inactive Date:" id="inactiveDate" inlineStyle="font-size:10pt;"
			value="#{facilityProfile.facility.inactiveDate}"
			styleClass=""
			readOnly="#{! facilityProfile.editable}"
			rendered="#{facilityProfile.facility.operatingStatusCd == 'ia'}">
			<af:validateDateTimeRange minimum="1900-01-01"
				maximum="#{infraDefs.currentDate}" />
		</af:selectInputDate>
	</af:panelForm>
	
	<af:panelForm rows="1" maxColumns="2" labelWidth="160" fieldWidth="40" width="500" partialTriggers="adminHold">
		<af:selectBooleanCheckbox id="adminHold" autoSubmit="true"
			label="Administrative Hold:" readOnly="#{!facilityProfile.editable}"
			rendered="#{facilityProfile.internalApp}" disabled="#{!facilityProfile.administrativeHoldAdmin}"
            value="#{facilityProfile.facility.administrativeHold}" />
		<af:outputFormatted id="adminHoldText"
			rendered="#{facilityProfile.internalApp && facilityProfile.facility.administrativeHold}"
			value="Administrative Hold Active" inlineStyle="color: orange; font-weight: bold;"/>
	</af:panelForm>
	
	<af:panelForm rendered="#{facilityProfile.displayUncertainArea}">
		<af:panelForm rows="1" maxColumns="1" labelWidth="200" width="450">
			<af:selectOneChoice label="Transitional Status:" unselectedLabel=" "
				value="#{facilityProfile.facility.transitStatusCd}"
				id="facTransitStatusCd"
				readOnly="#{facilityProfile.demReadOnlyAttr}">
				<f:selectItems 
					value="#{facilityReference.transitStatusDefs.items[(empty facilityProfile.facility.transitStatusCd ? '' : facilityProfile.facility.transitStatusCd)]}" />
			</af:selectOneChoice>
		</af:panelForm>
		<af:panelForm rows="1" maxColumns="2" labelWidth="200" width="650"
			partialTriggers="facPermitClassCd">
			<af:selectOneChoice label="Title V Permit Status:"
				value="#{facilityProfile.facility.permitStatusCd}"
				unselectedLabel=" " id="facPermitStatusCd"
				readOnly="#{facilityProfile.demReadOnlyAttr || !infraDefs.stars2Admin}">
				<mu:selectItems value="#{facilityReference.permitStatusDefs}" />
			</af:selectOneChoice>
			<af:selectBooleanCheckbox label="OBSOLETE in IMPACT TV Type A:"
				value="#{facilityProfile.facility.tvTypeA}"
				disabled="#{!facilityProfile.editable}"
				rendered="#{facilityProfile.dapcUser && facilityProfile.facility.permitClassCd != null && facilityProfile.facility.permitClassCd == 'tv'}" />
			<af:inputText label="Title V Certification Report Due Date:"
				value="#{facilityProfile.facility.formattedTvCertRepdueDate}"
				readOnly="true" />
		</af:panelForm>		
	</af:panelForm>

	<af:showDetailHeader text="Location" disclosed="true" id="location">
		<%@ include file="comAddrHistory.jsp"%>
	</af:showDetailHeader>

	<af:showDetailHeader text="Compliance Details"
		id="cetaDetails" disclosed="true"
		rendered="#{facilityProfile.internalApp && facilityProfile.displayUncertainArea}"
		partialTriggers="facPermitClassCd">
		<af:panelForm rows="1" maxColumns="2"
			partialTriggers="facPermitClassCd">
			<af:selectOneChoice label="Owned or Operated By:"
				value="#{facilityProfile.facility.govtFacilityTypeCd}"
				readOnly="#{!facilityProfile.stars2Admin || !facilityProfile.editable}">
				<f:selectItems
					value="#{compEvalDefs.govtFacilityTypes.items[(empty facilityProfile.facility.govtFacilityTypeCd ? '' : facilityProfile.facility.govtFacilityTypeCd)]}" />
			</af:selectOneChoice>
		</af:panelForm>
		<af:panelForm rows="3" maxColumns="2"
			partialTriggers="facPermitClassCd"
			rendered="#{facilityProfile.facility.versionId == -1}">
			<af:inputText label="Inspection Scheduled:"
				value="#{facilityProfile.fceScheduled}" readOnly="true" />
			<af:selectInputDate label="Last Inspection Date:"
				rendered="#{facilityProfile.lastFCEDate != null}"
				value="#{facilityProfile.lastFCEDate}" readOnly="true" />
			<af:inputText label="Last Inspection Date:"
				rendered="#{facilityProfile.lastFCEDate == null}"
				value="None Completed" readOnly="true" />

			<af:inputText label="Last Site Visit Date:"
				rendered="#{facilityProfile.facility.versionId == -1}"
				value="#{facilityProfile.lastSiteVisitDate}" readOnly="true" />
			<af:selectInputDate label="Last Stack Test Date:"
				rendered="#{facilityProfile.lastStackTestDate != null}"
				value="#{facilityProfile.lastStackTestDate}" readOnly="true" />
			<af:inputText label="Last Stack Test Date:"
				rendered="#{facilityProfile.lastStackTestDate == null}" value="None"
				readOnly="true" />
			<af:selectOneChoice label="Compliance Status:"
				value="#{facilityProfile.overallComplianceStatus}" readOnly="true">
				<f:selectItems
					value="#{compEvalDefs.complianceStatusDefs.allSearchItems}" />
			</af:selectOneChoice>
		</af:panelForm>

		<afh:rowLayout halign="center">
			<af:goLink text="ECHO Data" targetFrame="_new"
				rendered="#{not empty facilityProfile.echoURL}"
				destination="#{facilityProfile.echoURL}" />
		</afh:rowLayout>
	</af:showDetailHeader>

	<af:panelGroup id="apiNaicsNotesHCASections" partialTriggers="facilityTypeCd" >
		<af:showDetailHeader text="API" disclosed="true" id="facApi"
			rendered="#{facilityProfile.facility.hasApis}">
			<%@ include file="apis.jsp"%>
		</af:showDetailHeader>
	
		<af:showDetailHeader text="Notes" disclosed="true" id="facNotes"
			rendered="#{facilityProfile.dapcUser}">
			<jsp:include flush="true" page="notesTable.jsp" />
		</af:showDetailHeader>
	
		<af:showDetailHeader text="NAICS" disclosed="true" id="facNaics">
			<%@ include file="naicses.jsp"%>
		</af:showDetailHeader>
		
		<af:showDetailHeader id="facHydrocarbonAnalysis" text="Hydrocarbon Analysis" 
			disclosed="#{facilityProfile.discloseHC}" disclosureListener="#{facilityProfile.hcDisclosureChanged}" 
			rendered="#{facilityProfile.showHCAnalysisSection}" >
			<%@ include file="comFacHydrocarbonAnalysis.jsp"%>
		</af:showDetailHeader>
	</af:panelGroup>

	<af:panelForm rendered="#{facilityProfile.displayUncertainArea}">
		<af:showDetailHeader text="Facility Details" disclosed="true"
			id="facDetails">
			<af:panelForm rendered="#{facilityProfile.displayUncertainArea}">
				<af:panelForm rows="1" maxColumns="2" labelWidth="160" width="780">
					<af:inputText label="Core Place ID:" columns="10"
						maximumLength="10" id="corePlaceId"
						value="#{facilityProfile.facility.corePlaceId}"
						readOnly="#{facilityProfile.corePlaceIdReadOnlyAttr}" />
					<af:inputText label="Federal SCSC ID:" columns="10"
						maximumLength="10"
						value="#{facilityProfile.facility.federalSCSCId}"
						readOnly="#{! facilityProfile.editable || ! facilityProfile.dapcUser}" />
					<af:selectBooleanCheckbox label="Intra State Voucher Flag:"
						value="#{facilityProfile.facility.intraStateVoucherFlag}"
						readOnly="#{! facilityProfile.editable}"
						rendered="#{facilityProfile.dapcUser}" />
				</af:panelForm>
				<af:panelHeader text="MultiEstablishment" size="1"
					rendered="#{facilityProfile.facility.multiEstabFacility}" />
				<afh:rowLayout halign="center">
					<af:panelForm rows="1" maxColumns="1"
						rendered="#{facilityProfile.facility.multiEstabFacility}">
						<jsp:include flush="true" page="comMultiEstabTable.jsp" />
					</af:panelForm>
				</afh:rowLayout>
			</af:panelForm>
		</af:showDetailHeader>
	</af:panelForm>
	<af:objectSpacer height="20" />
	<afh:rowLayout halign="center">
		<af:panelButtonBar>
			<af:commandButton text="Edit" id="facProfEdit"
				action="#{facilityProfile.editFacility}"
				disabled="#{facilityProfile.disabledUpdateButton}"
				rendered="#{!facilityProfile.publicApp && !facilityProfile.editable}" />
			<af:commandButton text="Validate" id="facProfValidate"
				action="#{facilityProfile.validateFacilityProfile}"
				disabled="#{facilityProfile.disabledUpdateButton}"
				rendered="#{!facilityProfile.publicApp && !facilityProfile.editable}" />
			<af:commandButton text="Preserve current inventory" id="facProfPreserve"
				action="#{facilityProfile.startCreateprofileHistory}"
				partialSubmit="true"
				shortDesc="Marks this facility detail version as 'preserved'"
				useWindow="true" windowWidth="600" windowHeight="150"
				returnListener="#{facilityProfile.dialogDone}"
				disabled="#{facilityProfile.disabledUpdateButton}"
				rendered="#{! facilityProfile.editable && facilityProfile.dapcUser}" />


			<af:commandButton text="Submit" id="submit"
				rendered="#{! facilityProfile.editable && facilityProfile.portalApp}"
				disabled="#{!myTasks.hasSubmit || !facilityProfile.facility.validated || facilityProfile.disabledUpdateButton}"
				action="#{facilityProfile.submitFacilityProfileUpdates}"
				shortDesc="Submit"
				useWindow="true" windowWidth="#{submitTask.attestWidth}"
				windowHeight="#{submitTask.attestHeight}">
				<t:updateActionListener property="#{submitTask.type}"
					value="#{submitTask.yesNo}" />
				<t:updateActionListener property="#{submitTask.task}"
					value="#{facilityProfile.task}" />
			</af:commandButton>

			<af:commandButton text="Download/Print Detail" id="facProfPrint"
				action="#{facilityProfile.reportFacilityProfile}" useWindow="true"
				windowWidth="500" windowHeight="300"
				rendered="#{! facilityProfile.editable}" />
			<af:commandButton text="Print Facility Tree" id="printFacTree"
				actionListener="#{facilityProfile.printFacilityTree}"
				rendered="#{! facilityProfile.editable}" />
			<af:commandButton text="Save" id="facProfSave"
				action="#{facilityProfile.saveFacilityProfile}"
				rendered="#{facilityProfile.editable}" />
			<af:commandButton text="Cancel" id="facProfCancel"
				action="#{facilityProfile.cancelEditFacility}"
				rendered="#{facilityProfile.editable}" immediate="true" />
		</af:panelButtonBar>
	</afh:rowLayout>
	<af:objectSpacer height="10" />
	<afh:rowLayout halign="center">
		<af:panelButtonBar>
			<af:commandButton text="Create Emissions Unit" id="createEU"
				action="#{facilityProfile.createEmissionUnit}"
				disabled="#{facilityProfile.migrationHappened || facilityProfile.disabledUpdateButton}"
				rendered="#{!facilityProfile.publicApp && !facilityProfile.editable}" />
			<af:commandButton text="Create Control Equipment" id="createCE"
				action="#{facilityProfile.createControlEquipment}"
				disabled="#{facilityProfile.migrationHappened || facilityProfile.disabledUpdateButton}"
				rendered="#{!facilityProfile.publicApp && !facilityProfile.editable}" />
			<af:commandButton text="Create Release Point" id="createRP"
				action="#{facilityProfile.createEgressPoint}"
				disabled="#{facilityProfile.migrationHappened || facilityProfile.disabledUpdateButton}"
				rendered="#{!facilityProfile.publicApp && !facilityProfile.editable}" />
			<af:commandButton text="Retrieve Modeling Data" id="RetrieveMData"
				action="#{facilityProfile.displayModelingData}"
				disabled="#{facilityProfile.facility.phyAddr.latitude=='0.00000' && facilityProfile.facility.phyAddr.longitude=='0.00000'}"
				rendered="#{facilityProfile.internalApp && !facilityProfile.editable}" />
			
		</af:panelButtonBar>
	</afh:rowLayout>
</af:panelGroup>
<f:verbatim><%@ include
		file="../scripts/jquery-1.9.1.min.js"%></f:verbatim>
<f:verbatim><%@ include file="../scripts/wording-filter.js"%></f:verbatim>
<f:verbatim><%@ include
		file="../scripts/FacilityType-Option.js"%></f:verbatim>
<f:verbatim><%@ include
		file="../scripts/facility-detail.js"%></f:verbatim>