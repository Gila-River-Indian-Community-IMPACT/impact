<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:selectOneChoice id="plantType" label="Plant Type:"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.plantType}"
		showRequired="true">
		<f:selectItems
			value="#{facilityReference.plantTypeDefs.items[(empty facilityProfile.emissionUnit.emissionUnitType.plantType ? '' : facilityProfile.emissionUnit.emissionUnitType.plantType)]}" />
	</af:selectOneChoice>
</af:panelForm>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:inputText id="maxProdRate" label="Maximum Production Rate:"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.maxProductionRate}"
		showRequired="true" columns="12" maximumLength="12">
	</af:inputText>

	<af:selectOneChoice id="unitsCd" label="Units:"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.unitCd}"
		showRequired="true">
		<f:selectItems
			value="#{facilityReference.hmaMaxProductionRateUnitsDefs.items[(empty facilityProfile.emissionUnit.emissionUnitType.unitCd ? '' : facilityProfile.emissionUnit.emissionUnitType.unitCd)]}" />
	</af:selectOneChoice>
</af:panelForm>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:inputText id="maxBurnerDesignRate"
		label="Maximum Burner Design Rate (MMBtu/hr):"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.maxBurnerDesignRate}"
		showRequired="true" columns="12" maximumLength="12">
	</af:inputText>
</af:panelForm>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:selectOneChoice id="powerSource" label="Power Source:"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.powerSource}"
		showRequired="true">
		<f:selectItems
			value="#{facilityReference.powerSourceTypeDefs.items[(empty facilityProfile.emissionUnit.emissionUnitType.powerSource ? '' : facilityProfile.emissionUnit.emissionUnitType.powerSource)]}" />
	</af:selectOneChoice>

	<af:selectOneChoice id="fuelType" label="Fuel Type:"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.fuelType}"
		showRequired="true">
		<f:selectItems
			value="#{facilityReference.fuelTypeDefs.items[(empty facilityProfile.emissionUnit.emissionUnitType.fuelType ? '' : facilityProfile.emissionUnit.emissionUnitType.fuelType)]}" />
	</af:selectOneChoice>
</af:panelForm>