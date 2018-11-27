<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>

<af:panelForm rows="1" maxColumns="2" labelWidth="180" width="600">

	<af:inputText id="chargeRate" label="Maximum Charge Rate (barrels/hr):"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.chargeRate}"
		showRequired="true" columns="12" maximumLength="12">
	</af:inputText>
</af:panelForm>