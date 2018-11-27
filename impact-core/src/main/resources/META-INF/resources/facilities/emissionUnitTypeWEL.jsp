<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelForm rows="2" maxColumns="1" labelWidth="150" width="600">
 	<af:selectOneChoice id="weldingProcess" label="Welding Process:"
		readOnly="#{!facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.weldingProcess}"
		showRequired="true">
		<f:selectItems
				value="#{facilityReference.weldingProcessTypeDef.items[(empty facilityProfile.emissionUnit.emissionUnitType.weldingProcess ? '' : facilityProfile.emissionUnit.emissionUnitType.weldingProcess)]}" />
	</af:selectOneChoice>

	<af:inputText id="maxAmtOfElectrodeConsumed" label="Maximum Amount of Electrode Consumed (lbs/yr):"
		readOnly="#{!facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.maxAmtOfElectrodeConsumed}"
		showRequired="true" columns="12" maximumLength="6">
	</af:inputText>
</af:panelForm>
