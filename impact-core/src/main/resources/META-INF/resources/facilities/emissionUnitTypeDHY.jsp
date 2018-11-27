<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelForm rows="1" maxColumns="1" labelWidth="180" width="600">		
	<af:selectOneChoice id="dehydrationType" label="Dehydration Type:"
		readOnly="#{!facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.dehydrationType}"
		showRequired="true">
		<f:selectItems
				value="#{facilityReference.dehydrationTypeDefs.items[(empty facilityProfile.emissionUnit.emissionUnitType.dehydrationType ? '' : facilityProfile.emissionUnit.emissionUnitType.dehydrationType)]}" />
	</af:selectOneChoice>
	<af:inputText id="designCapacity" label="Design Capacity (MMscf/day):"
		readOnly="#{!facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.designCapacity}"
		showRequired="true" columns="50" maximumLength="12">
	</af:inputText>
</af:panelForm>