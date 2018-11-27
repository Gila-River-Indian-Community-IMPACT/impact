<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelForm rows="1" maxColumns="1" labelWidth="150" width="600">
	<af:inputText id="maxTimeOperated" label="Maximum Time Operated (hrs/yr):"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.maxTimeOperated}"
		showRequired="true" columns="12" maximumLength="4">
	</af:inputText>	
</af:panelForm>
<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
    <af:inputText id="amtOfMaterialRemoved" label="Amount of Material Removed (lbs/yr):"
        readOnly="#{! facilityProfile.editable}"
        value="#{facilityProfile.emissionUnit.emissionUnitType.amtOfMaterialRemoved}"
        showRequired="true" columns="12" maximumLength="9">
    </af:inputText>
</af:panelForm>
