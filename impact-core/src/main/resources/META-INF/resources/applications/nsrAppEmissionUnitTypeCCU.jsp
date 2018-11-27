<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<af:outputText id="euTypeLabel"
	value="Emission Unit Type : Cracking/Coking Unit"
	inlineStyle="font-size: 13px; font-weight: bold;" />
<af:objectSpacer height="15" />
<af:panelForm rows="1" maxColumns="2" labelWidth="250" width="750" 
	partialTriggers="typeOfEquipmentCd">
	<af:selectOneChoice id="typeOfEquipmentCd" label="Type of Equipment :"
		readOnly="#{! applicationDetail.editMode}"
		value="#{applicationDetail.selectedEU.euType.typeOfEquipmentCd}"
		showRequired="true" autoSubmit="true">
		<f:selectItems
			value="#{applicationReference.appEUCCUEquipTypeDefs.items[(empty applicationDetail.selectedEU.euType.typeOfEquipmentCd ? '' : applicationDetail.selectedEU.euType.typeOfEquipmentCd)]}" />
	</af:selectOneChoice>
	
	<af:inputText id="requestedChargeRate"
		label="Requested Charge Rate (Barrels/day) :" columns="25"
		maximumLength="12" readOnly="#{!applicationDetail.editMode}"
		value="#{applicationDetail.selectedEU.euType.requestedChargeRate}"
		showRequired="true" />

	<af:selectOneChoice id="odorMaskingAgentUsedCd"
		rendered="#{applicationDetail.selectedEU.euType.equipTypeDelayedCoking || applicationDetail.selectedEU.euType.equipTypeOther}"
		label="Odor Masking Agent Used ? :" showRequired="true"
		readOnly="#{!applicationDetail.editMode}"
		value="#{applicationDetail.selectedEU.euType.odorMaskingAgentUsedCd}">
		<f:selectItem itemLabel=" " itemValue="" />
		<f:selectItem itemLabel="Yes" itemValue="Y" />
		<f:selectItem itemLabel="No" itemValue="N" />
	</af:selectOneChoice>

	<af:inputText id="batchCycleTime" label="Batch Cycle Time (hr) :"
		rendered="#{applicationDetail.selectedEU.euType.equipTypeDelayedCoking || applicationDetail.selectedEU.euType.equipTypeOther}"
		columns="25" maximumLength="12"
		readOnly="#{!applicationDetail.editMode}"
		value="#{applicationDetail.selectedEU.euType.batchCycleTime}"
		showRequired="true" />

	<af:inputText id="quenchCycleTime" label="Quench Cycle Time (hr) :"
		rendered="#{applicationDetail.selectedEU.euType.equipTypeDelayedCoking || applicationDetail.selectedEU.euType.equipTypeOther}"
		columns="25" maximumLength="12"
		readOnly="#{!applicationDetail.editMode}"
		value="#{applicationDetail.selectedEU.euType.quenchCycleTime}"
		showRequired="true" />

	<af:inputText id="quenchGasVolums" label="Quench Gas Volume (scf/hr) :"
		rendered="#{applicationDetail.selectedEU.euType.equipTypeDelayedCoking || applicationDetail.selectedEU.euType.equipTypeOther}"
		columns="25" maximumLength="12"
		readOnly="#{!applicationDetail.editMode}"
		value="#{applicationDetail.selectedEU.euType.quenchGasVolums}"
		showRequired="true">
		<af:convertNumber pattern=".00" />
	</af:inputText>

	<af:inputText id="sulfurContentOfQuenchGas"
		rendered="#{applicationDetail.selectedEU.euType.equipTypeDelayedCoking || applicationDetail.selectedEU.euType.equipTypeOther}"
		label="Sulfur Content of Quench Gas (%) :" columns="25"
		maximumLength="12" readOnly="#{!applicationDetail.editMode}"
		value="#{applicationDetail.selectedEU.euType.sulfurContentOfQuenchGas}" />

</af:panelForm>