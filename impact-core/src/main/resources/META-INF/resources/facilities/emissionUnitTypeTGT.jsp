<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:inputText id="heatInputRating" label="Heat Input Rating (MMBtu/hr):"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.heatInputRating}"
		showRequired="true" columns="12" maximumLength="12">
	</af:inputText>
</af:panelForm>