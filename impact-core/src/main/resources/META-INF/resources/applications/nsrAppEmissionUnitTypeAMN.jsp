<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:outputText id="euTypeLabel" value="Emission Unit Type : Amine"
		inlineStyle="font-size: 13px; font-weight: bold;"/>
<af:objectSpacer height="15"/>
<af:panelForm partialTriggers="amineCircPumpTypeCd">
	<af:panelForm maxColumns="2" rows="1" labelWidth="150px" width="600px">
		<af:inputText id="inletCO2Conc" label="Inlet CO2 Concentration (%) :"
			columns="12" maximumLength="12"
			readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.inletCO2Conc}"
			showRequired="true" />

		<af:inputText id="inletH2SConc" label="Inlet H2S Concentration (%) :"
			columns="12" maximumLength="12"
			readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.inletH2SConc}"
			showRequired="true" />
	</af:panelForm>

	<af:panelForm maxColumns="2" rows="1" labelWidth="150px" width="600px">
		<af:inputText id="acidGasCO2Conc"
			label="Acid Gas CO2 Concentration (%) :" columns="12"
			maximumLength="12" readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.acidGasCO2Conc}"
			showRequired="true" />

		<af:inputText id="acidGasH2SConc"
			label="Acid Gas H2S Concentration (%) :" columns="12"
			maximumLength="12" readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.acidGasH2SConc}"
			showRequired="true" />
	</af:panelForm>

	<af:panelForm maxColumns="2" rows="1" labelWidth="150px" width="600px">
		<af:inputText id="amineCircRate" label="Amine Circulation Rate :"
			columns="12" maximumLength="12"
			readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.amineCircRate}"
			showRequired="true" />

		<af:selectOneChoice id="amineCircUnitsCd" label="Units :"
			readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.amineCircUnitsCd}"
			unselectedLabel="Please select" showRequired="true">
			<f:selectItems
				value="#{applicationReference.appEUCirculationRateDefs.items[(empty applicationDetail.selectedEU.euType.amineCircUnitsCd? '': applicationDetail.selectedEU.euType.amineCircUnitsCd)]}" />
		</af:selectOneChoice>
	</af:panelForm>

	<af:panelForm maxColumns="2" rows="1" labelWidth="150px" width="600px">
		<af:selectOneChoice id="amineTypeCd" label="Type of Amine :"
			readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.amineTypeCd}"
			unselectedLabel="Please select" showRequired="true">
			<f:selectItems
				value="#{applicationReference.appEUAmineTypeDefs.items[(empty applicationDetail.selectedEU.euType.amineTypeCd? '': applicationDetail.selectedEU.euType.amineTypeCd)]}" />
		</af:selectOneChoice>
	</af:panelForm>

	<af:panelForm maxColumns="2" rows="1" labelWidth="150px" width="600px">
		<af:inputText id="inletGasTemp" label="Temperature of Inlet Gas (F) :"
			columns="12" maximumLength="12"
			readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.inletGasTemp}"
			showRequired="true" />

		<af:inputText id="inletGasPressure"
			label="Pressure of Inlet Gas (psig) :" columns="12" maximumLength="12"
			readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.inletGasPressure}"
			showRequired="true" />
	</af:panelForm>

	<af:panelForm maxColumns="2" rows="1" labelWidth="150px" width="600px">
		<af:inputText id="outletGasFlowRate"
			label="Flow Rate of Outlet Gas (MMscf/day) :" columns="12"
			maximumLength="12" readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.outletGasFlowRate}"
			showRequired="true" />

		<af:inputText id="acidGasFlowRate"
			label="Flow Rate of Acid Gas (MMscf/day) :" columns="12"
			maximumLength="12" readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.acidGasFlowRate}"
			showRequired="true" />
	</af:panelForm>

	<af:panelForm maxColumns="2" rows="1" labelWidth="150px" width="600px">
		<af:selectOneChoice id="amineCircPumpTypeCd"
			label="Type of Amine Circulation Pump :"
			readOnly="#{! applicationDetail.editMode}"
			unselectedLabel="Please select"
			value="#{applicationDetail.selectedEU.euType.amineCircPumpTypeCd}"
			showRequired="true" autoSubmit="true">
			<f:selectItems
				value="#{applicationReference.appEUCirculationPumpTypeDefs.items[(empty applicationDetail.selectedEU.euType.amineCircPumpTypeCd? '': applicationDetail.selectedEU.euType.amineCircPumpTypeCd)]}" />
		</af:selectOneChoice>

		<af:inputText id="pumpVolumeRatio"
			label="Pump Volume Ratio (acfm/gpm) :" columns="12" maximumLength="12"
			readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.pumpVolumeRatio}"
			rendered="#{applicationDetail.selectedEU.euType.circulationPumpTypeGas}"
			showRequired="true" />
	</af:panelForm>

	<af:panelForm maxColumns="2" rows="1" labelWidth="150px" width="600px">
		<af:inputText id="maxLeanAmineCircRate"
			label="Maximum LEAN Amine Circulation Rate (gallons/minute) :"
			columns="12" maximumLength="12"
			readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.maxLeanAmineCircRate}"
			showRequired="true" />
		<af:inputText id="actualLeanAmineCircRate"
			label="Actual LEAN Amine Circulation Rate (gallons/minute) :"
			columns="12" maximumLength="12"
			readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.actualLeanAmineCircRate}"
			showRequired="true" />
	</af:panelForm>

	<af:panelForm maxColumns="2" rows="1" labelWidth="150px" width="600px">
		<af:inputText id="motiveGasPumpSource"
			label="Source of Motive Gas for Pump :" columns="50" maximumLength="50"
			readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.motiveGasPumpSource}"
			showRequired="true" />
	</af:panelForm>

</af:panelForm>
