<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelForm rows="2" maxColumns="1" labelWidth="150" width="600">
	<af:selectOneChoice id="equipmentType" label="Equipment Type:"
		autoSubmit="true" readOnly="#{!facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.equipmentType}"
		showRequired="true">
		<f:selectItems
			value="#{facilityReference.pamEquipTypeDefs.items[(empty facilityProfile.emissionUnit.emissionUnitType.equipmentType ? '' : facilityProfile.emissionUnit.emissionUnitType.equipmentType)]}" />
	</af:selectOneChoice>
</af:panelForm>
<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
    <af:inputText id="maximumAnnualThroughput" label="Maximum Annual Throughput (tons/yr):"
        readOnly="#{! facilityProfile.editable}"
        value="#{facilityProfile.emissionUnit.emissionUnitType.maximumAnnualThroughput}"
        showRequired="true" columns="12" maximumLength="9">
    </af:inputText>
</af:panelForm>

