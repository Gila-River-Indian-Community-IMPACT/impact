<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>

<af:panelGroup layout="vertical" partialTriggers="CntEquipType"
	rendered="#{facilityProfile.renderComponent == 'controlEquipment' }">
	<af:objectSpacer height="10" />
	<af:panelHeader text="Control Equipment Information" size="0" />
	<af:panelForm rows="8" maxColumns="1" labelWidth="30%" width="600">
		<af:inputText label="AQD ID:"
			value="#{facilityProfile.controlEquipment.controlEquipmentId}"
			id="controlEquipmentId" columns="12" readOnly="true" />
		<af:selectOneChoice label="Control Equipment Type:" immediate="true"
			value="#{facilityProfile.cntEquipType}" id="CntEquipType"
			unselectedLabel=" " autoSubmit="true"
			readOnly="#{! facilityProfile.editable}" showRequired="true">
			<f:selectItems
				value="#{infraDefs.contEquipTypes.items[(empty facilityProfile.cntEquipType ? '' : facilityProfile.cntEquipType)]}" />
		</af:selectOneChoice>
		<af:inputText label="AQD Description:" columns="80" rows="4"
			maximumLength="240"
			value="#{facilityProfile.controlEquipment.dapcDesc}" id="aqdDesc"
			readOnly="#{! facilityProfile.editable}"
			disabled="#{! facilityProfile.dapcUser}"
			showRequired="#{facilityProfile.dapcUser}" />
		<af:inputText label="AQD WISE Control Equipment ID:"
			value="#{facilityProfile.controlEquipment.wiseViewId}"
			id="wiseViewId" columns="12" maximumLength="12"
			readOnly="#{! facilityProfile.editable}"
			rendered="#{infraDefs.hidden && (infraDefs.stars2Admin || facilityProfile.allowedToEditWiseViewId)}" />
		<af:inputText label="Company Control Equipment ID:" showRequired="true"
			value="#{facilityProfile.controlEquipment.companyId}" id="companyId"
			columns="12" maximumLength="12"
			readOnly="#{! facilityProfile.editable}" />
		<af:inputText label="Company Control Equipment Description:"
			value="#{facilityProfile.controlEquipment.regUserDesc}"
			id="regUserDesc" columns="80" rows="4" maximumLength="240"
			readOnly="#{! facilityProfile.editable}" showRequired="true" />
		<af:selectOneChoice label="Operating Status:" id="ceOperatingStatusCd"
			value="#{facilityProfile.controlEquipment.operatingStatusCd}"
			unselectedLabel=" " readOnly="#{! facilityProfile.editable}"
			showRequired="true">
			<f:selectItems
				value="#{facilityReference.ceOperatingStatusDefs.items[(empty facilityProfile.controlEquipment.operatingStatusCd ? '' : facilityProfile.controlEquipment.operatingStatusCd)]}" />
		</af:selectOneChoice>
		<af:selectInputDate label="Initial Installation Date:"
			id="installDate"
			value="#{facilityProfile.controlEquipment.contEquipInstallDate}"
			readOnly="#{! facilityProfile.editable}">
			<af:validateDateTimeRange minimum="1900-01-01" />
		</af:selectInputDate>
	</af:panelForm>
	<af:panelForm rows="1" maxColumns="2" labelWidth="168" width="650">
		<af:inputText label="Manufacturer Name:" columns="50"
			maximumLength="50"
			value="#{facilityProfile.controlEquipment.manufacturer}"
			readOnly="#{! facilityProfile.editable}"
			id="manufacturerName" />
		<af:inputText label="Model Name and Number:"
			value="#{facilityProfile.controlEquipment.model}" columns="50"
			maximumLength="50" readOnly="#{! facilityProfile.editable}"
			id="modelNameNumber" />
	</af:panelForm>
	<af:showDetailHeader text="Control Equipment Type Specific Information"
		disclosed="true" id="ceTypeSpec">
		<af:panelForm binding="#{facilityProfile.contEquipTypeData}" />
	</af:showDetailHeader>
	<af:showDetailHeader text="Pollutants Controlled" disclosed="true"
		id="cePollutants">
		<af:showDetailHeader text="Explanation" disclosed="false">
			<af:objectSpacer height="5" />
			<af:outputFormatted inlineStyle="font-size:75%;"
				value="#{facilityProfile.controlEquipAttribDescMsg}" />
		</af:showDetailHeader>
		<af:objectSpacer height="10" />
		<af:panelForm>
			<af:outputText styleClass="x3s" value="*You must specify at least one pollutant in the Pollutants Controlled table"  />
			<af:table value="#{facilityProfile.pollutantsContWrapper}"
				bandingInterval="1" banding="row" var="pollutant"
				id="CePollutantTab"
				binding="#{facilityProfile.pollutantsContWrapper.table}"
				rows="#{facilityProfile.pageLimit}" width="98%">
				<f:facet name="selection">
					<af:tableSelectMany rendered="#{facilityProfile.editable}" />
				</f:facet>
				<af:column sortProperty="c01" sortable="true" id="pollutantCd"
					formatType="text" headerText="Pollutant">
					<af:selectOneChoice value="#{pollutant.pollutantCd}"
						unselectedLabel=" " readOnly="#{! facilityProfile.editable}" id="pollutant">
						<f:selectItems
							value="#{facilityReference.pollutantDefs.items[(empty pollutant.pollutantCd ? '' : pollutant.pollutantCd)]}" />
					</af:selectOneChoice>
				</af:column>
				<af:column sortProperty="designContEff" sortable="true"
					formatType="number" headerText="Design Control Efficiency(%)">
					<af:inputText value="#{pollutant.designContEff}" id="designContEff"
						columns="10" maximumLength="10"
						readOnly="#{!facilityProfile.editable}">
						<mu:convertSigDigNumber pattern="##0.#####" />
						<f:validateDoubleRange minimum="0.0" maximum="100.0" />
					</af:inputText>
				</af:column>
				<af:column sortProperty="operContEff" sortable="true"
					formatType="number" headerText="Operating Control Efficiency(%)">
					<af:inputText value="#{pollutant.operContEff}" id="operContEff"
						columns="10" maximumLength="10"
						readOnly="#{!facilityProfile.editable}">
						<mu:convertSigDigNumber pattern="##0.#####" />
						<f:validateDoubleRange minimum="0.0" maximum="100.0" />
					</af:inputText>
					<t:outputText value="no reduction" style="color: orange; font-weight: bold;"
						rendered="#{pollutant.operContEffZero && !facilityProfile.editable}" />
				</af:column>
				<af:column sortProperty="captureEff" sortable="true"
					formatType="number" headerText="Capture Efficiency(%)">
					<af:inputText value="#{pollutant.captureEff}" id="captureEff"
						columns="10" maximumLength="10"
						readOnly="#{!facilityProfile.editable}">
						<mu:convertSigDigNumber pattern="##0.#####" />
						<f:validateDoubleRange minimum="0.0" maximum="100.0" />
					</af:inputText>
					<t:outputText value="does not enter" style="color: orange; font-weight: bold;"
						rendered="#{pollutant.captureEffZero && !facilityProfile.editable}" />
				</af:column>
				<af:column sortProperty="totCaptureEff" sortable="true"
					formatType="number" headerText="Total Capture Control(%)" id="totalCaptureEff">
					<af:inputText value="#{pollutant.totCaptureEff}" readOnly="true" />
				</af:column>
				<f:facet name="footer">
					<afh:rowLayout halign="center">
						<af:panelButtonBar>
							<af:commandButton text="Add Pollutant"
								rendered="#{facilityProfile.editable}"
								action="#{facilityProfile.addPollutantControlled}"
								id="addPollutant">
							</af:commandButton>
							<af:commandButton text="Delete Selected Pollutants"
								rendered="#{facilityProfile.editable}"
								action="#{facilityProfile.deletePollutantsControlled}"
								id="deleteSelectedPollutants">
							</af:commandButton>
							<af:commandButton actionListener="#{tableExporter.printTable}"
								onclick="#{tableExporter.onClickScript}" text="Printable view" />
							<af:commandButton actionListener="#{tableExporter.excelTable}"
								onclick="#{tableExporter.onClickScript}" text="Export to excel" />
						</af:panelButtonBar>
					</afh:rowLayout>
				</f:facet>
			</af:table>
		</af:panelForm>
	</af:showDetailHeader>
	<af:showDetailHeader
		text="Branching Flow Percentages to Connected Control Equipment or Stacks"
		disclosed="true" id="ceFlowFactors"
		rendered="#{facilityProfile.controlEquipment.emissionFlowsAccess}">
		<af:panelForm
			rendered="#{facilityProfile.controlEquipment.emissionFlowsAccess}">
			<af:table value="#{facilityProfile.controlEquipment.ceEmissionFlows}"
				bandingInterval="1" banding="row" var="ef" id="ceEmisFactTab"
				width="98%">
				<af:column sortProperty="type" sortable="true" id="efType"
					formatType="text" headerText="Type">
					<af:inputText value="#{ef.type}" columns="30" maximumLength="30"
						readOnly="true" />
				</af:column>
				<af:column sortProperty="id" sortable="true" id="efId"
					formatType="text" headerText="Id">
					<af:inputText value="#{ef.id}" columns="30" maximumLength="30"
						readOnly="true" />
				</af:column>
				<af:column sortProperty="flowFactor" sortable="true" id="flowFactor"
					rendered="false" formatType="number" headerText="Flow Factor">
					<af:inputText readOnly="true" value="#{ef.flowFactor}" columns="7"
						maximumLength="7">
					</af:inputText>
				</af:column>
				<af:column sortProperty="percent" sortable="true" formatType="icon"
					headerText="Flow Percentage">
					<af:inputText value="#{ef.percent}" columns="5" maximumLength="5"
						id="flowpercent" readOnly="#{!facilityProfile.editable}">
						<mu:convertSigDigNumber pattern="##0.##" />
						<f:validateDoubleRange minimum="0.0" />
					</af:inputText>
					<t:outputText value="missing %" style="color: orange; font-weight: bold;"
						rendered="#{empty ef.percent}" />
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
			<af:objectSpacer height="5" />
			<af:outputFormatted
				inlineStyle="font-size:75%; color: rgb(20,0,255);"
				value="#{facilityProfile.branchingAirFlowMsg}" />
		</af:panelForm>
	</af:showDetailHeader>
	<af:objectSpacer height="20" />
	<afh:rowLayout halign="center">
		<af:panelButtonBar id="contEquipCmds">
			<af:commandButton text="Edit"
				action="#{facilityProfile.editControlEquipment}"
				disabled="#{facilityProfile.migrationHappened || facilityProfile.disabledUpdateButton}"
				rendered="#{!facilityProfile.publicApp && !facilityProfile.editable}"
				id="ceEditBtn" />
			<af:commandButton text="Delete"
				action="#{facilityProfile.confirmRemoveOp}" partialSubmit="true"
				useWindow="true" windowWidth="600" windowHeight="150"
				returnListener="#{facilityProfile.confirmReturned}"
				disabled="#{facilityProfile.deleteCntEquipDisabled}"
				rendered="#{!facilityProfile.publicApp && !facilityProfile.editable}"
				id="ceDeleteBtn" />
			<af:objectSpacer width="20" />
			<af:commandButton text="Create Cloned Control Equipment"
				action="#{facilityProfile.cloneControlEquipment}"
				disabled="#{facilityProfile.migrationHappened || facilityProfile.disabledUpdateButton}"
				rendered="#{!facilityProfile.publicApp && !facilityProfile.editable}"
				id="ceCreateClonedCEBtn" />
			<af:commandButton text="Save"
				action="#{facilityProfile.saveControlEquipment}"
				rendered="#{facilityProfile.editable}"
				id="ceSaveBtn" />
			<af:commandButton text="#{facilityProfile.cancelLabel}"
				action="#{facilityProfile.cancelControlEquipment}"
				rendered="#{facilityProfile.editable}" immediate="true"
				id="ceCancelBtn" />
		</af:panelButtonBar>
	</afh:rowLayout>
	<af:objectSpacer height="10" />
	<afh:rowLayout halign="center">
		<af:panelButtonBar>
			<af:commandButton
				text="Create And Associate Subsequent Control Equipment"
				action="#{facilityProfile.createControlEquipment}"
				disabled="#{facilityProfile.migrationHappened || facilityProfile.addToCeDisabled}"
				rendered="#{!facilityProfile.publicApp && !facilityProfile.editable}" />
			<af:commandButton text="Create And Associate Release Point"
				action="#{facilityProfile.createEgressPoint}"
				disabled="#{facilityProfile.migrationHappened || facilityProfile.addToCeDisabled}"
				rendered="#{!facilityProfile.publicApp && !facilityProfile.editable}"
				id="ceCreateAssociateReleasePoint" />
		</af:panelButtonBar>
	</afh:rowLayout>
	<af:objectSpacer height="10" />
	<afh:rowLayout halign="center">
		<af:panelButtonBar>
			<af:commandButton
				text="Associate Existing Subsequent Control Equipment"
				action="#{facilityProfile.addControlEquipment}"
				disabled="#{facilityProfile.migrationHappened || facilityProfile.addToCeDisabled}"
				rendered="#{!facilityProfile.publicApp && !facilityProfile.editable}" />
			<af:commandButton text="Associate Existing Release Point"
				action="#{facilityProfile.addEgressPoint}"
				disabled="#{facilityProfile.migrationHappened || facilityProfile.addToCeDisabled}"
				rendered="#{!facilityProfile.publicApp && !facilityProfile.editable}"
				id="ceAssociateExistingReleasePoint" />
		</af:panelButtonBar>
	</afh:rowLayout>
	<af:objectSpacer height="10" />
	<afh:rowLayout halign="center">
		<af:panelButtonBar>
			<af:commandButton text="Disassociate Subsequent Control Equipment"
				action="#{facilityProfile.removeControlEquipment}"
				rendered="#{!facilityProfile.publicApp && !facilityProfile.editable}"
				disabled="#{facilityProfile.migrationHappened || facilityProfile.removeCeDisabled}"
				 />
			<af:commandButton text="Disassociate Release Point"
				action="#{facilityProfile.removeEgressPoint}"
				disabled="#{facilityProfile.migrationHappened || facilityProfile.removeEgpDisabled}"
				rendered="#{!facilityProfile.publicApp && !facilityProfile.editable}"
				id="ceDisassociateReleasePoint" />
		</af:panelButtonBar>
	</afh:rowLayout>
</af:panelGroup>

<af:panelGroup layout="vertical"
	rendered="#{facilityProfile.renderComponent == 'addControlEquip' && !facilityProfile.readOnlyUser}">
	<af:objectSpacer height="10" />
	<af:showDetailHeader text="Select Control Equipment to Associate"
		disclosed="true">
		<af:panelForm>
			<af:selectOneChoice label="AQD Control Equipment ID:"
				value="#{facilityProfile.cntEquipId}" required="true"
				id="aqdControlEquipmentIDAssoc">
				<f:selectItems value="#{facilityProfile.controlEquipsList}" />
			</af:selectOneChoice>
			<afh:rowLayout halign="center">
				<af:objectSpacer height="20" />
				<af:panelButtonBar>
					<af:commandButton text="Save"
						action="#{facilityProfile.saveAddControlEquipment}"
						id="associateCESaveBtn" />
					<af:commandButton text="Cancel"
						action="#{facilityProfile.cancelAddControlEquipment}"
						immediate="true"
						id="associateCECancelBtn" />
				</af:panelButtonBar>
			</afh:rowLayout>
		</af:panelForm>
	</af:showDetailHeader>
</af:panelGroup>

<af:panelGroup layout="vertical"
	rendered="#{facilityProfile.renderComponent == 'removeControlEquip' && !facilityProfile.readOnlyUser}">
	<af:objectSpacer height="10" />
	<af:showDetailHeader text="Select Control Equipment to Disassociate"
		disclosed="true">
		<af:panelForm>
			<af:selectOneChoice label="AQD Control Equipment ID:"
				value="#{facilityProfile.cntEquipId}" required="true"
				id="aqdControlEquipmentIDDis">
				<f:selectItems value="#{facilityProfile.controlEquipsRemoveList}" />
			</af:selectOneChoice>
			<afh:rowLayout halign="center">
				<af:objectSpacer height="20" />
				<af:panelButtonBar>
					<af:commandButton text="Save"
						action="#{facilityProfile.saveRemoveControlEquipment}"
						 id="disassociateCESaveBtn" />
					<af:commandButton text="Cancel"
						action="#{facilityProfile.cancelAddControlEquipment}"
						immediate="true"
						id="disassociateCECancelBtn" />
				</af:panelButtonBar>
			</afh:rowLayout>
		</af:panelForm>
	</af:showDetailHeader>
</af:panelGroup>
