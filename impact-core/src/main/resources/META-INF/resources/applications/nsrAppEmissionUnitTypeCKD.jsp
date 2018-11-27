<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<af:outputText id="euTypeLabel"
	value="Emission Unit Type : Calciner/Kiln/Dryer/Smelter/Foundary Furnace"
	inlineStyle="font-size: 13px; font-weight: bold;" />
<af:objectSpacer height="15" />
<af:panelGroup partialTriggers="fuelFiredSource">
	<af:panelForm rows="1" width="600" labelWidth="150" maxColumns="2">
			<af:selectOneChoice id="fuelFiredSource" showRequired="true"
				valueChangeListener="#{applicationDetail.refreshAttachments}" 
				label="Is this a fuel-fired source?"
				readOnly="#{! applicationDetail.editMode}" unselectedLabel=" "
				value="#{applicationDetail.selectedEU.euType.fuelFiredSourceFlag}" autoSubmit="true">
				<af:selectItem label="Yes" value="Y" />
				<af:selectItem label="No" value="N" />
		</af:selectOneChoice>
	</af:panelForm>
	
	<af:panelForm rows="1" width="600" labelWidth="150" maxColumns="2"
		rendered="#{applicationDetail.selectedEU.euType.fuelFiredSource}">
		<af:inputText id="btu" label="Btu Content :" columns="12"
			maximumLength="12" readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.btu}" showRequired="true">
			<af:convertNumber pattern=".00" />
		</af:inputText>
	
	
		<af:selectOneChoice id="unitsBtuCd" label="Units :"
			readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.unitsBtuCd}"
			showRequired="true">
			<f:selectItems
				value="#{applicationReference.appEUBtuUnitsDefs.items[(empty applicationDetail.selectedEU.euType.unitsBtuCd ? '': applicationDetail.selectedEU.euType.unitsBtuCd)]}" />
		</af:selectOneChoice>
	</af:panelForm>
	
	<af:panelForm rows="1" width="600" labelWidth="150" maxColumns="2"
		rendered="#{applicationDetail.selectedEU.euType.fuelFiredSource}">
		<af:inputText id="fuelSulfur" label="Fuel Sulfur Content :"
			columns="12" maximumLength="12"
			readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.fuelSulfur}"
			showRequired="true">
			<af:convertNumber pattern=".00" />
		</af:inputText>
	
		<af:selectOneChoice id="unitsFuelSulfurCd" label="Units :"
			readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.unitsFuelSulfurCd}"
			showRequired="true">
			<f:selectItems
				value="#{applicationReference.appEUFuelSulfurDefs.items[(empty applicationDetail.selectedEU.euType.unitsFuelSulfurCd ? '' : applicationDetail.selectedEU.euType.unitsFuelSulfurCd)]}" />
		</af:selectOneChoice>
	</af:panelForm>
</af:panelGroup>

<af:panelForm rows="1" width="600" labelWidth="150" maxColumns="2">
	<af:selectOneChoice id="typeOfMaterialProcessedCd"
		label="Type of Material Processed :"
		readOnly="#{! applicationDetail.editMode}"
		value="#{applicationDetail.selectedEU.euType.typeOfMaterialProcessedCd}"
		showRequired="true">
		<f:selectItems
			value="#{applicationReference.appEUMateProcessedTypeDefs.items[(empty applicationDetail.selectedEU.euType.typeOfMaterialProcessedCd ? '' : applicationDetail.selectedEU.euType.typeOfMaterialProcessedCd)]}" />
	</af:selectOneChoice>
</af:panelForm>
