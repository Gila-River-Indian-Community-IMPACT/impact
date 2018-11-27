<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600"
	partialTriggers="pressType">
	<af:selectOneChoice id="pressType" label="Press Type:"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.pressType}"
		showRequired="true" autoSubmit="true">
		<f:selectItems
			value="#{facilityReference.prnPressTypeDef.items[(empty facilityProfile.emissionUnit.emissionUnitType.pressType ? '' : facilityProfile.emissionUnit.emissionUnitType.pressType)]}" />
	</af:selectOneChoice>
	<af:inputText id="pressTypeOther" label="Press Type (Other):"
		rendered="#{facilityProfile.emissionUnit.emissionUnitType.pressType == 'other'}"
		readOnly="#{!facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.pressTypeOther}"
		autoSubmit="true" showRequired="true" columns="50" maximumLength="80">
	</af:inputText>
</af:panelForm>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:selectOneChoice id="substrateFeedMethod"
		label="Substrate Feed Method:"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.substrateFeedMethod}"
		showRequired="true">
		<f:selectItems
			value="#{facilityReference.prnSubstrateFeedMethodTypeDef.items[(empty facilityProfile.emissionUnit.emissionUnitType.substrateFeedMethod ? '' : facilityProfile.emissionUnit.emissionUnitType.substrateFeedMethod)]}" />
	</af:selectOneChoice>
</af:panelForm>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:inputText id="impressionArea" label=" Impression Area (sq in):"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.impressionArea}"
		showRequired="true" columns="12" maximumLength="6">
	</af:inputText>
</af:panelForm>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:inputText id="maxAnnualUsage" label="Maximum Annual Usage:"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.maxAnnualUsage}"
		showRequired="true" columns="12" maximumLength="6">
	</af:inputText>
	<af:selectOneChoice id="unitCd" label="Units:"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.unitCd}"
		showRequired="true">
		<f:selectItems
			value="#{facilityReference.prnMaxAnnualThroughputUnitsDefs.items[(empty facilityProfile.emissionUnit.emissionUnitType.unitCd ? '' : facilityProfile.emissionUnit.emissionUnitType.unitCd)]}" />
	</af:selectOneChoice>
</af:panelForm>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:inputText id="maxPctVOC" label="Maximum % VOC:"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.maxPctVOC}"
		showRequired="true" columns="12" maximumLength="3">
	</af:inputText>
</af:panelForm>



