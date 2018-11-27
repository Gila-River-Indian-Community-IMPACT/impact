<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600"
	partialTriggers="typeOfContaminantBeingTreated  otherTypeOfContaminantBeingTreated">
	<af:selectOneChoice id="typeOfContaminantBeingTreated"
		label="Type of Contaminant being Treated:"
		readOnly="#{!facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.typeOfContaminantBeingTreated}"
		valueChangeListener="#{facilityProfile.emissionUnit.emissionUnitType.typeOfContaminantBeingTreatedListener}"
		autoSubmit="true" showRequired="true">
		<f:selectItems
			value="#{facilityReference.remContaminantTypeDef.items[(empty facilityProfile.emissionUnit.emissionUnitType.typeOfContaminantBeingTreated ? '' : facilityProfile.emissionUnit.emissionUnitType.typeOfContaminantBeingTreated)]}" />
	</af:selectOneChoice>
	<af:inputText id="otherTypeOfContaminantBeingTreated"
		label="Type of Contaminant being Treated (Other):"
		rendered="#{facilityProfile.emissionUnit.emissionUnitType.typeOfContaminantBeingTreatedOther}"
		readOnly="#{!facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.otherTypeOfContaminantBeingTreated}"
		showRequired="#{facilityProfile.emissionUnit.emissionUnitType.typeOfContaminantBeingTreatedOther}" 
		autoSubmit="true" columns="50" maximumLength="80">
	</af:inputText>
</af:panelForm>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:selectOneChoice id="contaminatedMaterial" label="Contaminated Material:"
		readOnly="#{!facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.contaminatedMaterial}"
		showRequired="true">
		<f:selectItems
			value="#{facilityReference.remContaminatedMaterialTypeDef.items[(empty facilityProfile.emissionUnit.emissionUnitType.contaminatedMaterial ? '' : facilityProfile.emissionUnit.emissionUnitType.contaminatedMaterial)]}" />
	</af:selectOneChoice>
</af:panelForm>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:inputText id="VOCEmissionRate" label="VOC Emission Rate (lbs/day) :"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.VOCEmissionRate}"
		showRequired="true" columns="12" maximumLength="3">
	</af:inputText>
</af:panelForm>

