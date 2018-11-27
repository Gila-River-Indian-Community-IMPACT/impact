<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>


<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:selectOneChoice id="unitType" label="Unit Type:"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.unitType}"
		showRequired="true">
		<f:selectItems
			value="#{facilityReference.eguUnitTypeDef.items[(empty facilityProfile.emissionUnit.emissionUnitType.unitType ? '' : facilityProfile.emissionUnit.emissionUnitType.unitType)]}" />
	</af:selectOneChoice>
</af:panelForm>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:inputText id="heatInputRating"
		label="Heat Input Rating (MMBtu/hr):"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.heatInputRating}"
		showRequired="true" columns="12" maximumLength="12">
	</af:inputText>
</af:panelForm>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:selectOneChoice id="primaryFuelType" label="Primary Fuel Type:"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.primaryFuelType}"
		showRequired="true">
		<f:selectItems
			value="#{facilityReference.primaryFuelTypeDefs.items[(empty facilityProfile.emissionUnit.emissionUnitType.primaryFuelType ? '' : facilityProfile.emissionUnit.emissionUnitType.primaryFuelType)]}" />
	</af:selectOneChoice>
	<af:selectOneChoice id="secondaryFuelType" label="Secondary Fuel Type:"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.secondaryFuelType}"
		showRequired="true">
		<f:selectItems
			value="#{facilityReference.secondaryFuelTypeDefs.items[(empty facilityProfile.emissionUnit.emissionUnitType.secondaryFuelType ? '' : facilityProfile.emissionUnit.emissionUnitType.secondaryFuelType)]}" />
	</af:selectOneChoice>
</af:panelForm>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:inputText id="manufacturerName" label="Manufacturer Name:"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.manufacturerName}"
		showRequired="true" columns="50" maximumLength="50">
	</af:inputText>
	<af:inputText id="modelNameAndNumber" label="Model Name and Number:"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.modelNameNumber}"
		showRequired="true" columns="50" maximumLength="50">
	</af:inputText>
</af:panelForm>
