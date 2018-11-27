<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<af:outputText id="euTypeLabel" value="Emission Unit Type : Hot Mix Asphalt Mixer"
	inlineStyle="font-size: 13px; font-weight: bold;"/>
<af:objectSpacer height="15"/>
<af:panelForm width="600" labelWidth="150" maxColumns="2">

	
	<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
		<af:inputText id="manufactureName" label="Manufacturer Name :"
			columns="50" maximumLength="50"
			readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.manufactureName}"/>

		<af:inputText id="modelNameAndNum" label="Model Name and Number :"
			columns="50" maximumLength="50"
			readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.modelNameAndNum}"/>
	</af:panelForm>
	
	<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
		<af:inputText id="fuelSulfurContent" label="Fuel Sulfur Content :"
			columns="12" maximumLength="12"
			readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.fuelSulfurContent}"
			showRequired="true">
			<af:convertNumber pattern=".00" />
		</af:inputText>

		<af:selectOneChoice id="hmaSulfurUnitsCd" label="Units :"
			readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.hmaSulfurUnitsCd}"
			showRequired="true">
			<f:selectItems value="#{applicationReference.appEUFuelSulfurDefs.items[(empty applicationDetail.selectedEU.euType.hmaSulfurUnitsCd ? '' : applicationDetail.selectedEU.euType.hmaSulfurUnitsCd)]}" />
		</af:selectOneChoice>
	</af:panelForm>

	<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
		<af:inputText id="fuelConsumption" label="Fuel Consumption :"
			columns="12" maximumLength="12"
			readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.fuelConsumption}"
			showRequired="true">
			<af:convertNumber pattern=".00" />
		</af:inputText>

		<af:selectOneChoice id="hmafuelConsUnitsCd" label="Units :"
			readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.hmafuelConsUnitsCd}"
			showRequired="true">
			<f:selectItems value="#{applicationReference.appEUFuelConsumptionDefs.items[(empty applicationDetail.selectedEU.euType.hmafuelConsUnitsCd ? '' : applicationDetail.selectedEU.euType.hmafuelConsUnitsCd)]}" />
		</af:selectOneChoice>
	</af:panelForm>
	
	<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
		<af:inputText id="fuelGasHeatingVal" label="Fuel Gas Heating Value :" columns="12"
			maximumLength="12" readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.fuelGasHeatingVal}"
			showRequired="true">
			<af:convertNumber pattern=".00" />
		</af:inputText>

		<af:selectOneChoice id="fuelGasHeatingUnitsCd" label="Units :"
			readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.fuelGasHeatingUnitsCd}"
			showRequired="true">
			<f:selectItems value="#{applicationReference.appEUFuelGasHeatingDefs.items[(empty applicationDetail.selectedEU.euType.fuelGasHeatingUnitsCd ? '' : applicationDetail.selectedEU.euType.fuelGasHeatingUnitsCd)]}" />
		</af:selectOneChoice>
	</af:panelForm>
	
	<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	    <af:inputText id="stackVolumetricFlow" label="Stack Volumetric Flow (dscfm) :"
			columns="12" maximumLength="12"
			readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.stackVolumetricFlow}"
			showRequired="true">
			<af:convertNumber pattern=".00" />
		</af:inputText>
	</af:panelForm>
	
	<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600" partialTriggers = "plantProcessRecycledAsphaltCd">
	   <af:selectOneChoice id="plantProcessRecycledAsphaltCd" label="Plant Processes Recycled Asphalt :"
			readOnly="#{! applicationDetail.editMode}" autoSubmit="true" unselectedLabel="" 
			value="#{applicationDetail.selectedEU.euType.plantProcessRecycledAsphaltCd}"
			showRequired="true">
			<f:selectItem itemLabel="Yes" itemValue="Y" />
			<f:selectItem itemLabel="No" itemValue="N" />
		</af:selectOneChoice>
		 <af:inputText id="maxRapPercent" label="Maximum Percent RAP (%) :"
			columns="12" maximumLength="12"
			readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.maxRapPercent}"
			rendered ="#{applicationDetail.selectedEU.euType.plantProcessRecycledAsphalt}"
			showRequired="true" />
	</af:panelForm>

</af:panelForm>
