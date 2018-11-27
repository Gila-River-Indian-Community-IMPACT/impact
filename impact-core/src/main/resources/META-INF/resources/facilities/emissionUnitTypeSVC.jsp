<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelForm rows="2" maxColumns="1" labelWidth="150" width="600" partialTriggers="equipmentType">
	<af:selectOneChoice id="equipmentType" label="Equipment Type:"
		autoSubmit="true" readOnly="#{!facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.equipmentType}"
		showRequired="true">
		<f:selectItems
			value="#{facilityReference.svcEquipTypeDefs.items[(empty facilityProfile.emissionUnit.emissionUnitType.equipmentType ? '' : facilityProfile.emissionUnit.emissionUnitType.equipmentType)]}" />
	</af:selectOneChoice>
	<af:inputText id="equipmentTypeOther" label="Equipment Type (Other):"
		partialTriggers="equipmentType"
		rendered="#{facilityProfile.emissionUnit.emissionUnitType.equipmentType == 'other'}"
		readOnly="#{!facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.equipmentTypeOther}"
		autoSubmit="true" showRequired="true" columns="50" maximumLength="80">
	</af:inputText>
</af:panelForm>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:inputText id="solventType" label="Solvent Type:"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.solventType}"
		showRequired="true" columns="50" maximumLength="80">
	</af:inputText>
</af:panelForm>

<af:panelForm rows="1" maxColumns="1" labelWidth="150" width="600">
	<af:inputText id="maxAnnualUsage" label="Maximum Annual Usage (lbs/yr):"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.maxAnnualUsage}"
		showRequired="true" columns="12" maximumLength="6">
	</af:inputText>
</af:panelForm>




