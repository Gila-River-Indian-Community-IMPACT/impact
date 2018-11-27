<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600"
	partialTriggers="unitType">
	<af:selectOneChoice id="unitType" label="Type of Unit:"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.unitType}"
		autoSubmit="true"
		showRequired="true">
		<f:selectItems
			value="#{facilityReference.fatUnitTypeDef.items[(empty facilityProfile.emissionUnit.emissionUnitType.unitType ? '' : facilityProfile.emissionUnit.emissionUnitType.unitType)]}" />
	</af:selectOneChoice>
	<af:inputText id="unitTypeOther"
		label="Type of Unit (Other):"
		partialTriggers="unitType"
		rendered="#{facilityProfile.emissionUnit.emissionUnitType.unitType == 'other'}"
		readOnly="#{!facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.unitTypeOther}"
		autoSubmit="true"
		showRequired="true" columns="50" maximumLength="80">
	</af:inputText>	
</af:panelForm>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:inputText id="maxProdRate" label="Maximum Production Rate (tons/yr):"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.maxProductionRate}"
		showRequired="true" columns="12" maximumLength="6">
	</af:inputText>
</af:panelForm>


