<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelForm rows="1" maxColumns="1" labelWidth="180" width="600"
	partialTriggers="equipmentType bleedRate gasConsumptionRate">
	<af:selectOneChoice id="equipmentType" label="Type of Equipment:"
		autoSubmit="true" readOnly="#{!facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.equipmentType}"
		showRequired="true">
		<f:selectItems
			value="#{facilityReference.equipTypeDefs.items[(empty facilityProfile.emissionUnit.emissionUnitType.equipmentType ? '' : facilityProfile.emissionUnit.emissionUnitType.equipmentType)]}" />
	</af:selectOneChoice>
	<af:inputText id="bleedRate" label="Bleed Rate per Unit (cu. ft/hr):"
		rendered="#{facilityProfile.emissionUnit.emissionUnitType.equipmentType == 'Controller'}"
		readOnly="#{!facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.bleedRate}"
		showRequired="true" columns="12" maximumLength="12">
	</af:inputText>
	<af:inputText id="gasConsumptionRate" label="Gas Consumption Rate per Unit (cu. ft/hr):"
		rendered="#{facilityProfile.emissionUnit.emissionUnitType.equipmentType == 'Pump'}"
		readOnly="#{!facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.gasConsumptionRate}"
		showRequired="true" columns="12" maximumLength="12">
	</af:inputText>
	<af:inputText id="equipmentCount" label="Total Count of Equipment:"
		value="#{facilityProfile.emissionUnit.emissionUnitType.eqptCount}"
		readOnly="#{!facilityProfile.editable}"
		showRequired="true" columns="3" maximumLength="3">
	</af:inputText>
</af:panelForm>