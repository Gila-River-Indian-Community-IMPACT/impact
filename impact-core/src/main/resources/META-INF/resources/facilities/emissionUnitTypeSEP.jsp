<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:selectOneChoice id="typeOfVessel" label="Type Of Vessel:"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.vesselType}"
		showRequired="true">
	<f:selectItems
			value="#{facilityReference.sepVesselTypeDef.items[(empty facilityProfile.emissionUnit.emissionUnitType.vesselType ? '' : facilityProfile.emissionUnit.emissionUnitType.vesselType)]}" />
	</af:selectOneChoice>

	<af:selectOneChoice id="vesselHeated" label="is Vessel Heated:"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.vesselHeated}"
		showRequired="true">
		<f:selectItem itemLabel="Yes" itemValue="true" />
		<f:selectItem itemLabel="No" itemValue="false" />
	</af:selectOneChoice>
</af:panelForm>
