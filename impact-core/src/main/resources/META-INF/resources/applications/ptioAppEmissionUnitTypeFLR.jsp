<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<af:outputText id="euTypeLabel" value="Emission Unit Type : Flare"
	inlineStyle="font-size: 13px; font-weight: bold;" />
<af:objectSpacer height="15" />
<af:panelForm width="600" labelWidth="150" maxColumns="2"
	partialTriggers="assitGasUtilizedCd continuouslyMonitoredCd">
	<af:panelForm rows="1" maxColumns="2" width="600" labelWidth="150">
		<af:selectOneChoice id="emergencyFlareOnlyCd"
			label="Emergency Flare Only :" showRequired="true"
			readOnly="#{!applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.emergencyFlareOnlyCd}">
			<f:selectItem itemLabel="Yes" itemValue="Yes" />
			<f:selectItem itemLabel="No" itemValue="No" />
		</af:selectOneChoice>
		<af:selectOneChoice id="ignitionDeviceTypeCd"
			label="Ignition Device Type :" showRequired="true"
			readOnly="#{!applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.ignitionDeviceTypeCd}">
			<f:selectItems
				value="#{applicationReference.appEUIgnitionDeviceTypeDefs.items[(empty applicationDetail.selectedEU.euType.ignitionDeviceTypeCd ? '' : applicationDetail.selectedEU.euType.ignitionDeviceTypeCd)]}" />
		</af:selectOneChoice>
	</af:panelForm>
	<af:panelForm rows="1" maxColumns="2" width="600" labelWidth="150">
		<af:inputText id="btuContent" label="Btu Content (Btu/scf) :"
			columns="12" maximumLength="12" showRequired="true"
			readOnly="#{!applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.btuContent}">
			<af:convertNumber pattern=".00" />
		</af:inputText>
		<af:selectOneChoice id="smokelessDesignCd" label="Smokeless Design :"
			showRequired="true" readOnly="#{!applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.smokelessDesignCd}">
			<f:selectItem itemLabel="Yes" itemValue="Yes" />
			<f:selectItem itemLabel="No" itemValue="No" />
		</af:selectOneChoice>
	</af:panelForm>
	<af:panelForm rows="1" maxColumns="2" width="600" labelWidth="150">
		<af:selectOneChoice id="assitGasUtilizedCd"
			label="Assist Gas Utilized :" showRequired="true" autoSubmit="true"
			readOnly="#{!applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.assitGasUtilizedCd}">
			<f:selectItem itemLabel="Yes" itemValue="Yes" />
			<f:selectItem itemLabel="No" itemValue="No" />
		</af:selectOneChoice>
	</af:panelForm>
	<af:panelForm rows="1" maxColumns="2" width="600" labelWidth="150">
		<af:inputText id="assistGasUtilizedBtu"
			label="BTU Content (BTU/scf) :" columns="12" maximumLength="12"
			showRequired="true" readOnly="#{!applicationDetail.editMode}"
			rendered="#{applicationDetail.selectedEU.euType.assistGasUtilized}"
			value="#{applicationDetail.selectedEU.euType.assistGasUtilizedBtu}">
			<af:convertNumber pattern=".00" />
		</af:inputText>
	</af:panelForm>
	<af:panelForm rows="1" maxColumns="2" width="600" labelWidth="150"
		rendered="#{applicationDetail.selectedEU.euType.assistGasUtilized}">
		<af:inputText id="fuelSulfurContent" label="Fuel Sulfur Content :"
			columns="12" maximumLength="12" showRequired="true"
			readOnly="#{!applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.fuelSulfurContent}">
			<af:convertNumber pattern=".00" />
		</af:inputText>
		<af:selectOneChoice id="fuelSulfurContentUnitsCd" label="Units :"
			showRequired="true" readOnly="#{!applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.fuelSulfurContentUnitsCd}">
			<f:selectItems
				value="#{applicationReference.appEUFuelSulfurDefs.items[(empty applicationDetail.selectedEU.euType.fuelSulfurContentUnitsCd ? '' : applicationDetail.selectedEU.euType.fuelSulfurContentUnitsCd)]}" />
		</af:selectOneChoice>
	</af:panelForm>
	<af:panelForm rows="1" maxColumns="2" width="600" labelWidth="150">
		<af:inputText id="wasteGasVolume" label="Waste Gas Volume :"
			columns="12" maximumLength="12" showRequired="true"
			readOnly="#{!applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.wasteGasVolume}">
			<af:convertNumber pattern=".00" />
		</af:inputText>
		<af:selectOneChoice id="wasteGasVolumeUnitsCd" label="Units :"
			showRequired="true" readOnly="#{!applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.wasteGasVolumeUnitsCd}">
			<f:selectItems
				value="#{applicationReference.appEUWasteGasVolUnitsDefs.items[(empty applicationDetail.selectedEU.euType.wasteGasVolumeUnitsCd ? '' : applicationDetail.selectedEU.euType.wasteGasVolumeUnitsCd)]}" />
		</af:selectOneChoice>
	</af:panelForm>
	<af:panelForm rows="1" maxColumns="2" width="600" labelWidth="150">
		<af:selectInputDate id="installationDate" label="Installation Date :"
			readOnly="#{!applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.installationDate}">
			<af:validateDateTimeRange minimum="1900-01-01"
				maximum="#{infraDefs.currentDate}" />
		</af:selectInputDate>
	</af:panelForm>
	<af:panelForm rows="1" maxColumns="2" width="600" labelWidth="150">
		<af:selectOneChoice id="continuouslyMonitoredCd"
			label="Continuously Monitored :" showRequired="true"
			autoSubmit="true" readOnly="#{!applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.continuouslyMonitoredCd}">
			<f:selectItem itemLabel="Yes" itemValue="Yes" />
			<f:selectItem itemLabel="No" itemValue="No" />
		</af:selectOneChoice>
	</af:panelForm>
	<af:panelForm rows="1" maxColumns="2" width="600" labelWidth="150">
		<af:inputText id="continuousMonitoringDesc"
			label="Describe Continuous Monitoring :" columns="100" rows="5"
			maximumLength="500" showRequired="true"
			readOnly="#{!applicationDetail.editMode}"
			rendered="#{applicationDetail.selectedEU.euType.continuouslyMonitored}"
			value="#{applicationDetail.selectedEU.euType.continuousMonitoringDesc}" />
	</af:panelForm>
</af:panelForm>