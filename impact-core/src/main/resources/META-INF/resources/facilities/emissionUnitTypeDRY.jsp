<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:selectOneChoice id="dryCleaningType" label="Type of Dry Cleaning:"
		readOnly="#{!facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.dryCleaningType}"
		showRequired="true">
		<f:selectItems
			value="#{facilityReference.dryTypeOfDryCleaningTypeDef.items[(empty facilityProfile.emissionUnit.emissionUnitType.dryCleaningType ? '' : facilityProfile.emissionUnit.emissionUnitType.dryCleaningType)]}" /> 
	</af:selectOneChoice>
</af:panelForm>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:inputText id="maxAnnualUsage"
		label="Maximum Annual Usage (gals/yr):"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.maxAnnualUsage}"
		showRequired="true" columns="12" maximumLength="6">
	</af:inputText>
</af:panelForm>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:selectOneChoice id="operationType" label="Type of Operation:"
		readOnly="#{!facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.operationType}"
		showRequired="true">
		<f:selectItems
			value="#{facilityReference.dryOperationTypeDef.items[(empty facilityProfile.emissionUnit.emissionUnitType.operationType ? '' : facilityProfile.emissionUnit.emissionUnitType.operationType)]}" />
	</af:selectOneChoice>
</af:panelForm>

