<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<af:outputText id="euTypeLabel" value="Emission Unit Type : Dehydration Unit"
	inlineStyle="font-size: 13px; font-weight: bold;"/>
<af:objectSpacer height="15"/>
<af:panelForm rows="1" maxColumns="2" labelWidth="250" width="750">
	<af:inputText id="temperatureOfWetGas"
		label="Temperature of Wet Gas (F) :" columns="25" maximumLength="12"
		readOnly="#{!applicationDetail.editMode}"
		value="#{applicationDetail.selectedEU.euType.temperatureOfWetGas}"
		showRequired="true" />

	<af:inputText id="pressureOfWetGas" label="Pressure of Wet Gas (psig) :"
		columns="25" maximumLength="12"
		readOnly="#{!applicationDetail.editMode}"
		value="#{applicationDetail.selectedEU.euType.pressureOfWetGas}"
		showRequired="true">
		<af:convertNumber pattern=".00" />
	</af:inputText>

	<af:inputText id="waterContentOfWetGas"
		label="Water Content of Wet Gas (lbs H2O/MMscf) :" columns="25"
		maximumLength="12" readOnly="#{!applicationDetail.editMode}"
		value="#{applicationDetail.selectedEU.euType.waterContentOfWetGas}">
	</af:inputText>

	<af:inputText id="flowRateOfDryGas"
		label="Flow Rate of Dry Gas (MMscfd) :" columns="25" maximumLength="12"
		readOnly="#{!applicationDetail.editMode}"
		value="#{applicationDetail.selectedEU.euType.flowRateOfDryGas}"
		showRequired="true">
		<af:convertNumber pattern=".00" />
	</af:inputText>

	<af:inputText id="waterContentOfDryGas"
		label="Water Content of Dry Gas (lbs H2O/MMscf) :" columns="25"
		maximumLength="12" readOnly="#{!applicationDetail.editMode}"
		value="#{applicationDetail.selectedEU.euType.waterContentOfDryGas}"
		showRequired="true">
		<af:convertNumber pattern=".00" />
	</af:inputText>

	<af:inputText id="manufactureNameOfGlycolCircPump"
		label="Manufacturer Name of Glycol Circulation Pump :" columns="25"
		maximumLength="50" readOnly="#{!applicationDetail.editMode}"
		value="#{applicationDetail.selectedEU.euType.manufactureNameOfGlycolCircPump}"
		showRequired="true" />

	<af:inputText id="modelNameAndNoOfGlycolCircPump"
		label="Model Name and Number of Glycol Circulation Pump :" columns="25"
		maximumLength="50" readOnly="#{!applicationDetail.editMode}"
		value="#{applicationDetail.selectedEU.euType.modelNameAndNoOfGlycolCircPump}"
		showRequired="true" />
</af:panelForm>

<af:panelGroup
	partialTriggers="typeOfGlycolCirculationPumpCd additionalGasStrippingCd includeGlycolFlashTankSeparatorCd">
	<af:panelForm rows="1" maxColumns="1" labelWidth="250" width="750">
		<af:selectOneChoice id="typeOfGlycolCirculationPumpCd"
			label="Type of Glycol Circulation Pump :"
			readOnly="#{! applicationDetail.editMode}" autoSubmit="true"
			value="#{applicationDetail.selectedEU.euType.typeOfGlycolCirculationPumpCd}"
			showRequired="true">
			<f:selectItems
				value="#{applicationReference.appEUCirculationPumpTypeDefs.items[(empty applicationDetail.selectedEU.euType.typeOfGlycolCirculationPumpCd ? '' : applicationDetail.selectedEU.euType.typeOfGlycolCirculationPumpCd)]}" />
		</af:selectOneChoice>
	</af:panelForm>

	<af:panelGroup
		rendered="#{applicationDetail.selectedEU.euType.gasClycolCirculationPump}">
		<af:panelForm rows="1" maxColumns="2" labelWidth="250" width="750">
			<af:inputText id="pumpVolumeRatio"
				label="Pump Volume Ratio (acfm/gpm) :" columns="25"
				maximumLength="12" readOnly="#{!applicationDetail.editMode}"
				value="#{applicationDetail.selectedEU.euType.pumpVolumeRatio}"
				showRequired="true">
				<af:convertNumber pattern=".00" />
			</af:inputText>

			<af:inputText id="maxLeanGlycolCirculationRate"
				label="Maximum LEAN Glycol Circulation Rate (gallons/minute) :"
				columns="25" maximumLength="12"
				readOnly="#{!applicationDetail.editMode}"
				value="#{applicationDetail.selectedEU.euType.maxLeanGlycolCirculationRate}"
				showRequired="true" />

			<af:inputText id="actualLeanGlycolCirculationRate"
				label="Actual LEAN Glycol Circulation Rate (gallons/minute) :"
				columns="25" maximumLength="12"
				readOnly="#{!applicationDetail.editMode}"
				value="#{applicationDetail.selectedEU.euType.actualLeanGlycolCirculationRate}"
				showRequired="true" />

			<af:inputText id="sourceOfMotiveGasForPump"
				label="Source of Motive Gas for Pump :" columns="25"
				maximumLength="12" readOnly="#{!applicationDetail.editMode}"
				value="#{applicationDetail.selectedEU.euType.sourceOfMotiveGasForPump}"
				showRequired="true" />
		</af:panelForm>

		<af:panelForm rows="1" maxColumns="1" labelWidth="250" width="750">
			<af:selectOneChoice id="additionalGasStrippingCd"
				label="Additional Gas Stripping :" autoSubmit="true"
				showRequired="true" readOnly="#{!applicationDetail.editMode}"
				value="#{applicationDetail.selectedEU.euType.additionalGasStrippingCd}">
				<f:selectItem itemLabel=" " itemValue="" />
				<f:selectItem itemLabel="Yes" itemValue="Y" />
				<f:selectItem itemLabel="No" itemValue="N" />
			</af:selectOneChoice>
		</af:panelForm>

		<af:panelForm rows="1" maxColumns="2" labelWidth="250" width="750"
			rendered="#{applicationDetail.selectedEU.euType.additionalGasStrippingCd == 'Y'}">

			<af:inputText id="strippingGasRate"
				label="Stripping Gas Rate (scf/minute) :" columns="20"
				maximumLength="12" readOnly="#{!applicationDetail.editMode}"
				value="#{applicationDetail.selectedEU.euType.strippingGasRate}"
				showRequired="true">
				<af:convertNumber pattern=".00" />
			</af:inputText>

			<af:selectOneChoice id="sourceOfStrippingGasCd"
				label="Source of Stripping Gas :"
				readOnly="#{! applicationDetail.editMode}" autoSubmit="true"
				value="#{applicationDetail.selectedEU.euType.sourceOfStrippingGasCd}"
				showRequired="true">
				<f:selectItems
					value="#{applicationReference.appEUStrippingGasSourceDefs.items[(empty applicationDetail.selectedEU.euType.sourceOfStrippingGasCd ? '' : applicationDetail.selectedEU.euType.sourceOfStrippingGasCd)]}" />
			</af:selectOneChoice>
		</af:panelForm>

		<af:panelForm rows="1" maxColumns="1" labelWidth="250" width="750">
			<af:selectOneChoice id="includeGlycolFlashTankSeparatorCd"
				label="Include Glycol Flash Tank/Separator :" autoSubmit="true"
				showRequired="true" readOnly="#{!applicationDetail.editMode}"
				value="#{applicationDetail.selectedEU.euType.includeGlycolFlashTankSeparatorCd}">
				<f:selectItem itemLabel=" " itemValue="" />
				<f:selectItem itemLabel="Yes" itemValue="Y" />
				<f:selectItem itemLabel="No" itemValue="N" />
			</af:selectOneChoice>
		</af:panelForm>
		<af:panelForm rows="1" maxColumns="2" labelWidth="250" width="750"
			rendered="#{applicationDetail.selectedEU.euType.includeGlycolFlashTankSeparatorCd == 'Y'}">
			<af:inputText id="flashTankOffGasStream"
				label="Flash Tank Off Gas Stream (scf/hr) :" columns="20"
				maximumLength="12" readOnly="#{!applicationDetail.editMode}"
				value="#{applicationDetail.selectedEU.euType.flashTankOffGasStream}"
				showRequired="true">
				<af:convertNumber pattern=".00" />
			</af:inputText>

			<af:inputText id="flashVaporsRouted"
				label="Where are Flash vapors Routed? :" columns="20"
				maximumLength="50" readOnly="#{!applicationDetail.editMode}"
				value="#{applicationDetail.selectedEU.euType.flashVaporsRouted}"
				showRequired="true" />

			<af:selectOneChoice id="isVesselHeatedCd" label="Is Vessel Heated :"
				showRequired="true" readOnly="#{!applicationDetail.editMode}"
				value="#{applicationDetail.selectedEU.euType.isVesselHeatedCd}">
				<f:selectItem itemLabel=" " itemValue="" />
				<f:selectItem itemLabel="Yes" itemValue="Y" />
				<f:selectItem itemLabel="No" itemValue="N" />
			</af:selectOneChoice>

			<af:inputText id="operatingTemperature"
				label="Operating Temperature (F) :" columns="20" maximumLength="12"
				readOnly="#{!applicationDetail.editMode}"
				value="#{applicationDetail.selectedEU.euType.operatingTemperature}"
				showRequired="true" />

			<af:inputText id="operatingPressure"
				label="Operating Pressure (psig) :" columns="20" maximumLength="12"
				readOnly="#{!applicationDetail.editMode}"
				value="#{applicationDetail.selectedEU.euType.operatingPressure}"
				showRequired="true">
				<af:convertNumber pattern=".00" />
			</af:inputText>
		</af:panelForm>
	</af:panelGroup>
</af:panelGroup>