<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelForm rows="1" maxColumns="1" labelWidth="180" width="600">
	<af:selectOneChoice id="firingType" label="Firing Type:"
		readOnly="#{!facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.firingType}"
		showRequired="true">
		<f:selectItems
			value="#{facilityReference.firingTypeDefs.items[(empty facilityProfile.emissionUnit.emissionUnitType.firingType ? '' : facilityProfile.emissionUnit.emissionUnitType.firingType)]}" />
	</af:selectOneChoice>
</af:panelForm>
<af:panelForm rows="1" maxColumns="2" labelWidth="180" width="600">
	<af:inputText id="heatInputRating" label="Heat Input Rating:"
		readOnly="#{!facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.heatInputRating}"
		showRequired="true" columns="12" maximumLength="12">
	</af:inputText>
	<af:selectOneChoice id="unitCd" label="Units:"
		readOnly="#{!facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.unitCd}"
		showRequired="true">
		<f:selectItems
			value="#{facilityReference.unitTypeDefs.items[(empty facilityProfile.emissionUnit.emissionUnitType.unitCd ? '' : facilityProfile.emissionUnit.emissionUnitType.unitCd)]}" />
	</af:selectOneChoice>
</af:panelForm>

<af:panelForm rows="1" maxColumns="2" labelWidth="180" width="600">
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

<af:panelForm rows="1" maxColumns="2" labelWidth="180" width="600">
	<af:inputText id="fuelHeatContent" label="Heat Content of Fuel:"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.fuelHeatContent}"
		showRequired="true" columns="12" maximumLength="50">
	</af:inputText>
	<af:selectOneChoice id="fuelHeatContentUnitsCd" label="Units:"
		readOnly="#{!facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.fuelHeatContentUnitsCd}"
		showRequired="true">
		<f:selectItems
			value="#{facilityReference.heatContentFuelUnitsDefs.items[(empty facilityProfile.emissionUnit.emissionUnitType.fuelHeatContentUnitsCd ? '' : facilityProfile.emissionUnit.emissionUnitType.fuelHeatContentUnitsCd)]}" />
	</af:selectOneChoice>
</af:panelForm>