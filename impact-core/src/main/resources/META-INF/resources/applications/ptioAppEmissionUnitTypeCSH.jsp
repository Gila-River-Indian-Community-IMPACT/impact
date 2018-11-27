<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<af:outputText id="euTypeLabel"
	value="Emission Unit Type : Crushing/Screening/Handling"
	inlineStyle="font-size: 13px; font-weight: bold;" />
<af:objectSpacer height="10" />
<af:switcher
	facetName="#{applicationDetail.selectedEU.euType.fpEuUnitType}"
	defaultFacet="default">
	<f:subview id="unitTypeDefault">
		<f:facet name="default" />
	</f:subview>
	<f:facet name="crushing">
		<af:outputText id="unitTypeLabel" value="Unit Type : Crushing"
			inlineStyle="font-size: 13px; font-weight: bold;" />
	</f:facet>
	<f:facet name="screening">
		<af:outputText id="unitTypeLabel" value="Unit Type : Screening"
			inlineStyle="font-size: 13px; font-weight: bold;" />
	</f:facet>
	<f:facet name="materialhandling">
		<af:outputText id="unitTypeLabel"
			value="Unit Type : Material Handling"
			inlineStyle="font-size: 13px; font-weight: bold;" />
	</f:facet>
	<f:facet name="other">
		<af:outputText id="unitTypeLabel"
			value="Unit Type : Crushing/Screening/Handling (Other)"
			inlineStyle="font-size: 13px; font-weight: bold;" />
	</f:facet>
</af:switcher>
<af:objectSpacer height="15" />
<af:panelForm maxColumns="2" width="600" labelWidth="150">
	<af:panelForm rows="1" maxColumns="1"
		rendered="#{applicationDetail.selectedEU.euType.fpEuUnitType == 'crushing'}">
		<af:inputText id="crushedMaterialType"
			label="Type of Material Crushed :" columns="50" maximumLength="50"
			showRequired="true" readOnly="#{!applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.crushedMaterialType}" />
		<af:selectOneChoice id="crusherTypeCd" label="Type of Crusher :"
			showRequired="true" readOnly="#{!applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.crusherTypeCd}">
			<f:selectItems
				value="#{applicationReference.appEUCrusherTypeDefs.items[(empty applicationDetail.selectedEU.euType.crusherTypeCd ? '' : applicationDetail.selectedEU.euType.crusherTypeCd)]}" />
		</af:selectOneChoice>
		<af:selectInputDate id="crusherManufactureDate"
			label="Manufacture Date :" readOnly="#{!applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.crusherManufactureDate}">
			<af:validateDateTimeRange minimum="1900-01-01"
				maximum="#{infraDefs.currentDate}" />
		</af:selectInputDate>
		<af:selectOneChoice id="crusherPowerSourceCd" label="Power Source :"
			showRequired="true" readOnly="#{!applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.crusherPowerSourceCd}">
			<f:selectItems
				value="#{applicationReference.appEUPowerSourceDefs.items[(empty applicationDetail.selectedEU.euType.crusherPowerSourceCd ? '' : applicationDetail.selectedEU.euType.crusherPowerSourceCd)]}" />
		</af:selectOneChoice>
		<af:inputText id="maxCrusherCapacity"
			label="Max Crusher Capacity (tons/hr) :" columns="12"
			maximumLength="12" showRequired="true"
			readOnly="#{!applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.maxCrusherCapacity}" />
	</af:panelForm>
	<af:panelForm rows="1" maxColumns="1"
		partialTriggers="operatingInConjunctionCd"
		rendered="#{applicationDetail.selectedEU.euType.fpEuUnitType == 'screening'}">
		<af:selectOneChoice id="screenCd" label="Screen :" showRequired="true"
			readOnly="#{!applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.screenCd}">
			<f:selectItems
				value="#{applicationReference.appEUScreenDefs.items[(empty applicationDetail.selectedEU.euType.screenCd ? '' : applicationDetail.selectedEU.euType.screenCd)]}" />
		</af:selectOneChoice>
		<af:inputText id="screenType" label="Screen Type :" columns="100"
			rows="2" maximumLength="150" showRequired="true"
			readOnly="#{!applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.screenType}" />
		<af:inputText id="materialScreenedType"
			label="Type of Material Screened :" columns="50" maximumLength="50"
			showRequired="true" readOnly="#{!applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.materialScreenedType}" />
		<af:selectInputDate id="screenManufactureDate"
			label="Manufacture Date :" readOnly="#{!applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.screenManufactureDate}">
			<af:validateDateTimeRange minimum="1900-01-01"
				maximum="#{infraDefs.currentDate}" />
		</af:selectInputDate>
		<af:selectOneChoice id="screenPowerSourceCd" label="Power Source :"
			showRequired="true" readOnly="#{!applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.screenPowerSourceCd}">
			<f:selectItems
				value="#{applicationReference.appEUPowerSourceDefs.items[(empty applicationDetail.selectedEU.euType.screenPowerSourceCd ? '' : applicationDetail.selectedEU.euType.screenPowerSourceCd)]}" />
		</af:selectOneChoice>
		<af:selectOneChoice id="operatingInConjunctionCd"
			label="Operating in Conjunction with a Crusher :" showRequired="true"
			autoSubmit="true" readOnly="#{!applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.operatingInConjunctionCd}">
			<f:selectItem itemLabel="Yes" itemValue="Yes" />
			<f:selectItem itemLabel="No" itemValue="No" />
		</af:selectOneChoice>
		<af:inputText id="maxScreeningCapacity"
			label="Max Screening Capacity (tons/hr) :" columns="12"
			maximumLength="12" showRequired="true"
			readOnly="#{!applicationDetail.editMode}"
			rendered="#{applicationDetail.selectedEU.euType.operatingInConjunction}"
			value="#{applicationDetail.selectedEU.euType.maxScreeingCapacity}" />
	</af:panelForm>
	<af:panelForm rows="1" maxColumns="1"
		rendered="#{applicationDetail.selectedEU.euType.fpEuUnitType == 'materialhandling'}">
		<af:inputText id="conveyorTransferDropPoints"
			label="Number of Conveyor transfer and drop points :" columns="12"
			maximumLength="12" showRequired="true"
			readOnly="#{!applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.conveyorTransferDropPoints}" />
		<af:inputText id="materialTransferredType"
			label="Type of Material being Transferred :" columns="50"
			maximumLength="50" showRequired="true"
			readOnly="#{!applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.materialTransferredType}" />
	</af:panelForm>
	<af:panelForm rows="1" maxColumns="1"
		rendered="#{applicationDetail.selectedEU.euType.fpEuUnitType == 'other'}">
		<af:inputText id="detailedDescription"
			label="Detailed Description of Unit :" columns="100" rows="5"
			maximumLength="500" showRequired="true"
			readOnly="#{!applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.detailedDescription}"
			tip="*Provide detailed calculations documenting the potential emissions and emission factors used to calculate emissions from this source." />
	</af:panelForm>
</af:panelForm>