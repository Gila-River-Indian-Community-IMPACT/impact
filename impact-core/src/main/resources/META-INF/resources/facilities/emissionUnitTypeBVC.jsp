<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>

<af:panelForm rows="1" maxColumns="2" labelWidth="180" width="600">
	<af:selectOneChoice id="eventType" label="Type of Event:"
		readOnly="#{!facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.eventType}"
		showRequired="true">
		<f:selectItems
				value="#{facilityReference.eventTypeDefs.items[(empty facilityProfile.emissionUnit.emissionUnitType.eventType ? '' : facilityProfile.emissionUnit.emissionUnitType.eventType)]}" />
	</af:selectOneChoice>
</af:panelForm>