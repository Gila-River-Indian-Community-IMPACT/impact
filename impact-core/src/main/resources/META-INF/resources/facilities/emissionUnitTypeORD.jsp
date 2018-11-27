<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600"
	partialTriggers="typeOfExplosive typeOfExplosiveOther">
	<af:selectOneChoice id="typeOfExplosive" label="Type of Explosive:"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.typeOfExplosive}"
		valueChangeListener="#{facilityProfile.emissionUnit.emissionUnitType.typeOfExplosiveListener}"
		showRequired="true" autoSubmit="true">
		<f:selectItems
			value="#{facilityReference.ordExplosiveTypeDef.items[(empty facilityProfile.emissionUnit.emissionUnitType.typeOfExplosive ? '' : facilityProfile.emissionUnit.emissionUnitType.typeOfExplosive)]}" />
	</af:selectOneChoice>
	<af:inputText id="typeOfExplosiveOther" label="Type of Explosive (Other):"
		partialTriggers="typeOfExplosive"
		rendered="#{facilityProfile.emissionUnit.emissionUnitType.typeOfExplosive == 'other'}"
		readOnly="#{!facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.typeOfExplosiveOther}"
		showRequired="#{facilityProfile.emissionUnit.emissionUnitType.typeOfExplosiveOther}"
		autoSubmit="true" columns="50" maximumLength="80">
	</af:inputText>
</af:panelForm>

<af:panelForm rows="1" maxColumns="1" labelWidth="150" width="600">
<af:inputText id="maxNumberOfRoundsDetonated" label="Maximum Number of Rounds Detonated (#/yr):"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.maxNumberOfRoundsDetonated}"
		showRequired="true" columns="12" maximumLength="6">
</af:inputText>
</af:panelForm>

