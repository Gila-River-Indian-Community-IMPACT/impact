<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>

<af:panelGroup layout="vertical"
	rendered="#{facilityProfile.renderComponent == 'emissionProcesses'}"
	partialTriggers="sccIdL1 sccIdL2 sccIdL3 sccIdL4 selectLevelScc selectSearchScc">
	<af:objectSpacer height="10" />
	<af:panelHeader text="Process Information" size="0" />
	<af:panelForm rows="14" maxColumns="2">
		<af:inputText label="Process ID:" id="processId" columns="20"
			value="#{facilityProfile.emissionProcess.processId}" readOnly="true"
			showRequired="true" />
		<af:inputText label="Process Name:" id="processName" columns="20"
			maximumLength="50"
			value="#{facilityProfile.emissionProcess.processName}"
			readOnly="#{!facilityProfile.editable}" />
		<af:inputText label="Company Process Description:"
			value="#{facilityProfile.emissionProcess.emissionProcessNm}"
			maximumLength="100" readOnly="#{! facilityProfile.editable}"
			id="companyProcDescription" />
		<af:inputText label="Source Classification Code (SCC):" id="sccId"
			columns="11" maximumLength="11" showRequired="true"
			value="#{facilityProfile.emissionProcess.sccCode.sccId} #{facilityProfile.emissionProcess.sccCode.createDeprecatePhrase}"
			inlineStyle="#{facilityProfile.emissionProcess.sccCode.deprecatedYear != null?'color: orange; font-weight: bold;':';'}"
			rendered="#{! facilityProfile.editable}" readOnly="true">
			<f:validateLength maximum="11" minimum="8" />
		</af:inputText>
		<af:inputText label="Source Classification Code (SCC):" id="sccId2"
			columns="11" maximumLength="11" showRequired="true"
			value="#{facilityProfile.emissionProcess.sccCode.sccId}"
			rendered="#{facilityProfile.editable && facilityProfile.selectSccMethod != 'inputSCC'}"
			readOnly="true">
			<f:validateLength maximum="11" minimum="8" />
		</af:inputText>
		<af:inputText label="Source Classification Code (SCC):" id="sccId3"
			columns="11" maximumLength="11" showRequired="true"
			value="#{facilityProfile.emissionProcess.sccCode.sccId}"
			rendered="#{facilityProfile.editable && facilityProfile.selectSccMethod == 'inputSCC'}"
			readOnly="false" tip="Enter as 1-22-333-44 or 12233344"
			converter="#{facilityProfile.sccCodeConverter}">
			<f:validateLength maximum="11" minimum="8" />
		</af:inputText>
		<af:selectOneChoice label="SCC Level 1 Description:"
			value="#{facilityProfile.emissionProcess.sccDesc1}" 
			id="sccIdL1" autoSubmit="true"
			readOnly="#{! facilityProfile.editable}"
			rendered="#{! facilityProfile.editable || facilityProfile.selectSccMethod == 'levelSCC'}"
			showRequired="true">
			<f:selectItems value="#{infraDefs.sccLevel1Codes}" />
		</af:selectOneChoice>
		<af:selectOneChoice label="SCC Level 2 Description:"
			value="#{facilityProfile.emissionProcess.sccDesc2}"
			id="sccIdL2" autoSubmit="true"
			readOnly="#{! facilityProfile.editable}"
			rendered="#{! facilityProfile.editable || facilityProfile.selectSccMethod == 'levelSCC'}"
			showRequired="true">
			<f:selectItems value="#{facilityProfile.emissionProcess.sccLevel2Codes}" />
		</af:selectOneChoice>
		<af:selectOneChoice label="SCC Level 3 Description:"
			value="#{facilityProfile.emissionProcess.sccDesc3}"
			id="sccIdL3" autoSubmit="true"
			readOnly="#{! facilityProfile.editable}"
			rendered="#{! facilityProfile.editable || facilityProfile.selectSccMethod == 'levelSCC'}"
			showRequired="true">
			<f:selectItems value="#{facilityProfile.emissionProcess.sccLevel3Codes}" />
		</af:selectOneChoice>
		<af:selectOneChoice label="SCC Level 4 Description:"
			value="#{facilityProfile.emissionProcess.sccDesc4}"
			id="sccIdL4" autoSubmit="true"
			readOnly="#{! facilityProfile.editable}"
			rendered="#{! facilityProfile.editable || facilityProfile.selectSccMethod == 'levelSCC'}"
			showRequired="true">
			<f:selectItems value="#{facilityProfile.emissionProcess.sccLevel4Codes}" />
		</af:selectOneChoice>
		<af:inputText label="SCC Search Key Word:" id="sccSearchKey"
			rendered="#{facilityProfile.editable && facilityProfile.selectSccMethod == 'searchSCC'}"
			value="#{facilityProfile.sccSearchkeyWords}" />
		<afh:rowLayout halign="center">
			<af:commandButton text="Search"
				action="#{facilityProfile.searchSccCodes}" useWindow="true"
				windowWidth="950" windowHeight="950"
				returnListener="#{facilityProfile.dialogDone}"
				rendered="#{facilityProfile.editable && facilityProfile.selectSccMethod == 'searchSCC'}"
				id="searchScc">
			</af:commandButton>
		</afh:rowLayout>
		<afh:rowLayout halign="center">
			<af:panelButtonBar>
				<af:commandButton text="Select SCC through cascading levels"
					action=""
					rendered="#{facilityProfile.editable && facilityProfile.selectSccMethod == 'inputSCC'}"
					id="selectLevelScc" immediate="true">
					<t:updateActionListener
						property="#{facilityProfile.selectSccMethod}" value="levelSCC" />
				</af:commandButton>
				<af:commandButton text="search SCCs by keyword" action=""
					rendered="#{facilityProfile.editable && facilityProfile.selectSccMethod == 'inputSCC'}"
					id="selectSeachScc" immediate="true">
					<t:updateActionListener
						property="#{facilityProfile.selectSccMethod}" value="searchSCC" />
				</af:commandButton>
			</af:panelButtonBar>
		</afh:rowLayout>
		<af:objectSpacer height="10" />
		<afh:rowLayout halign="center">
			<af:goLink text="SCC reference information" targetFrame="_new"
				rendered="#{! facilityProfile.editable && !facilityProfile.publicApp}"
				destination="../util/externalReferences.jsf" />
		</afh:rowLayout>
	</af:panelForm>
	<af:panelHeader
		text="Branching Flow Percentages to Connected Control Equipment or Stacks"
		size="0"
		rendered="#{facilityProfile.emissionProcess.emissionFlowsAccess}" />
	<af:panelForm
		rendered="#{facilityProfile.emissionProcess.emissionFlowsAccess}">
		<af:table value="#{facilityProfile.emissionProcess.epEmissionFlows}"
			bandingInterval="1" banding="row" var="ef" id="epEmisFactTab"
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
				<t:outputText value="missing %" style="color: rgb(0,0,255);"
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
		<af:showDetailHeader text="Explanation" disclosed="false" size="20"
			rendered="#{facilityProfile.emissionProcess.emissionFlowsAccess}">
			<af:objectSpacer height="5" />
			<af:outputFormatted
				rendered="#{facilityProfile.emissionProcess.emissionFlowsAccess}"
				inlineStyle="font-size:75%;"
				value="#{facilityProfile.branchingAirFlowMsg}" />
		</af:showDetailHeader>
	</af:panelForm>
	<af:objectSpacer height="20" />
	<afh:rowLayout halign="center">
		<af:panelButtonBar>
			<af:commandButton text="Edit"
				action="#{facilityProfile.editEmissionProcess}"
				disabled="#{facilityProfile.migrationHappened || facilityProfile.disabledUpdateButton}"
				rendered="#{!facilityProfile.publicApp && !facilityProfile.editable}"
				id="emissionProcEditBtn" />
			<af:commandButton text="Delete"
				action="#{facilityProfile.confirmRemoveOp}" partialSubmit="true"
				useWindow="true" windowWidth="600" windowHeight="150"
				returnListener="#{facilityProfile.confirmReturned}"
				disabled="#{facilityProfile.deleteEpDisabled}"
				rendered="#{!facilityProfile.publicApp && !facilityProfile.editable}"
				id="emissionProcDeleteBtn" />
			<af:commandButton text="Save"
				action="#{facilityProfile.saveEmissionProcess}"
				rendered="#{facilityProfile.editable}"
				id="emissionProcSaveBtn" />
			<af:commandButton text="#{facilityProfile.cancelLabel}"
				action="#{facilityProfile.cancelEmissionProc}"
				rendered="#{facilityProfile.editable}" immediate="true"
				id="emissionProcCancelBtn" />
		</af:panelButtonBar>
	</afh:rowLayout>
	<af:objectSpacer height="10" />
	<afh:rowLayout halign="center">
		<af:panelButtonBar>
			<af:commandButton text="Create and Associate Control Equipment"
				action="#{facilityProfile.createControlEquipment}"
				disabled="#{facilityProfile.migrationHappened || facilityProfile.disabledUpdateButton}"
				rendered="#{!facilityProfile.publicApp && !facilityProfile.editable}"
				id="emissionProcCreateAssociateCE" />
			<af:commandButton text="Create and Associate Release Point"
				action="#{facilityProfile.createEgressPoint}"
				disabled="#{facilityProfile.migrationHappened || facilityProfile.disabledUpdateButton}"
				rendered="#{!facilityProfile.publicApp && !facilityProfile.editable}"
				id="emissionProcCreateAssociateRP" />
		</af:panelButtonBar>
	</afh:rowLayout>
	<af:objectSpacer height="10" />
	<afh:rowLayout halign="center">
		<af:panelButtonBar>
			<af:commandButton text="Associate Existing Control Equipment"
				action="#{facilityProfile.addControlEquipment}"
				disabled="#{facilityProfile.migrationHappened || facilityProfile.disabledUpdateButton}"
				rendered="#{!facilityProfile.publicApp && !facilityProfile.editable}"
				id="emissionProcAssociateExistingCE" />
			<af:commandButton text="Associate Existing Release Point"
				action="#{facilityProfile.addEgressPoint}"
				disabled="#{facilityProfile.migrationHappened || facilityProfile.disabledUpdateButton}"
				rendered="#{!facilityProfile.publicApp && !facilityProfile.editable}"
				id="emissionProcAssociateExistingRP" />
		</af:panelButtonBar>
	</afh:rowLayout>
	<af:objectSpacer height="10" />
	<afh:rowLayout halign="center">
		<af:panelButtonBar>
			<af:commandButton text="Disassociate Control Equipment"
				action="#{facilityProfile.removeControlEquipment}"
				rendered="#{!facilityProfile.publicApp && !facilityProfile.editable}"
				disabled="#{facilityProfile.migrationHappened || facilityProfile.removeCeDisabled}"
				id="emissionProcDisassociateCE" />
			<af:commandButton text="Disassociate Release Point"
				action="#{facilityProfile.removeEgressPoint}"
				disabled="#{facilityProfile.migrationHappened || facilityProfile.removeEgpDisabled}"
				rendered="#{!facilityProfile.publicApp && !facilityProfile.editable}"
				id="emissionProcDisassociateRP" />
		</af:panelButtonBar>
	</afh:rowLayout>
</af:panelGroup>
