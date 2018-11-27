<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelForm rows="2" maxColumns="1" labelWidth="150" width="600">
	<af:selectOneChoice id="unitType" label="Unit Type:"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.unitType}"
		showRequired="true">
		<f:selectItems
			value="#{facilityReference.sebUnitTypeDef.items[(empty facilityProfile.emissionUnit.emissionUnitType.unitType ? '' : facilityProfile.emissionUnit.emissionUnitType.unitType)]}" />
	</af:selectOneChoice>

	<af:inputText id="unitDescription" label="Unit Description:"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.unitDesc}"
		showRequired="true" columns="80" rows="4" maximumLength="240">
	</af:inputText>
</af:panelForm>
