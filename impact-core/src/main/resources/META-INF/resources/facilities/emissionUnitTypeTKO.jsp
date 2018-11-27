<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:inputText id="materialType" label="Material Type:"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.materialType}"
		showRequired="true" columns="50" maximumLength="80">
	</af:inputText>
</af:panelForm>

<af:panelForm rows="1" maxColumns="4" labelWidth="150" width="600"
	 partialTriggers="platingLineFlag platingType platingTypeOther" >
	<af:selectOneChoice id="platingLineFlag"
		label="Is this for a Plating Line?:"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.platingLineFlag}" 
		valueChangeListener="#{facilityProfile.emissionUnit.emissionUnitType.platingLineFlagListener}"
		autoSubmit="true" showRequired="true">
		<f:selectItem itemLabel="Yes" itemValue="Y" />
		<f:selectItem itemLabel="No" itemValue="N" />
	</af:selectOneChoice>
<af:selectOneChoice id="platingType" label="Type of Plating:"
	readOnly="#{! facilityProfile.editable}"
	value="#{facilityProfile.emissionUnit.emissionUnitType.platingType}"
	rendered="#{facilityProfile.emissionUnit.emissionUnitType.platingLine}"
	showRequired="#{facilityProfile.emissionUnit.emissionUnitType.platingLine}"  partialTriggers="platingLineFlag" autoSubmit="true">
	<f:selectItems
		value="#{facilityReference.tkoPlatingTypeDef.items[(empty facilityProfile.emissionUnit.emissionUnitType.platingType ? '' : facilityProfile.emissionUnit.emissionUnitType.platingType)]}" />
</af:selectOneChoice>
<af:inputText id="platingTypeOther" label="Type of Plating (Other):"
	partialTriggers="platingType"
	rendered="#{facilityProfile.emissionUnit.emissionUnitType.otherPlatingType && facilityProfile.emissionUnit.emissionUnitType.platingLine}"
	readOnly="#{!facilityProfile.editable}"
	value="#{facilityProfile.emissionUnit.emissionUnitType.platingTypeOther}"
	showRequired="#{facilityProfile.emissionUnit.emissionUnitType.otherPlatingType}"
	autoSubmit="true" columns="50" maximumLength="80">
</af:inputText>
</af:panelForm>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600" partialTriggers="metalType">
	<af:selectOneChoice id="metalType" label="Type of Metal:"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.metalType}"
		valueChangeListener="#{facilityProfile.emissionUnit.emissionUnitType.metalTypeListener}"
		showRequired="true" autoSubmit="true" >
		<f:selectItems
			value="#{facilityReference.tkoMetalTypeDef.items[(empty facilityProfile.emissionUnit.emissionUnitType.metalType ? '' : facilityProfile.emissionUnit.emissionUnitType.metalType)]}" />
	</af:selectOneChoice>
	<af:inputText id="metalTypeOther" label="Type of Metal (Other):"
		partialTriggers="metalType"
		rendered="#{facilityProfile.emissionUnit.emissionUnitType.otherMetalType}"
		readOnly="#{!facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.metalTypeOther}"
		showRequired="#{facilityProfile.emissionUnit.emissionUnitType.otherMetalType}"
		autoSubmit="true"  columns="50" maximumLength="80">
	</af:inputText>
</af:panelForm>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:inputText id="capacity" label=" Tank Capacity (gals):"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.capacity}"
		showRequired="true" columns="12" maximumLength="6">
	</af:inputText>
</af:panelForm>

<af:panelForm rows="1" maxColumns="1" labelWidth="150" width="600">
	<af:inputText id="maxAnnualUsage" label="Maximum Annual Usage (lbs/yr):"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.maxAnnualUsage}"
		showRequired="true" columns="12" maximumLength="6">
	</af:inputText>
</af:panelForm>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:inputText id="concentrationPct" label="Concentration %:"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.concentrationPct}"
		showRequired="true" columns="12" maximumLength="3">
	</af:inputText>
</af:panelForm>




