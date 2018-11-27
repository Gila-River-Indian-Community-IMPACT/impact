<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:inputText id="nameAndTypeOfMaterial"
		label="Name and Type of Material:"
		readOnly="#{!facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.nameAndTypeOfMaterial}"
		showRequired="true" columns="50" maximumLength="80">
	</af:inputText>
</af:panelForm>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:inputText id="maxAnnualUsage" label="Maximum Annual Usage (lbs/yr):"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.maxAnnualUsage}"
		showRequired="true" columns="12" maximumLength="6">
	</af:inputText>
</af:panelForm>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:inputText id="maxPctVOC" label="Maximum % VOC Content:"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.maxPctVOC}"
		showRequired="true" columns="12" maximumLength="3">
	</af:inputText>
</af:panelForm>
<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:inputText id="maxPctStyrene" label="Maximum % Styrene Content:"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.maxPctStyrene}"
		showRequired="true" columns="12" maximumLength="3">
	</af:inputText>
</af:panelForm>
<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:selectOneChoice id="applicationMethod" label="Application Method:"
		readOnly="#{!facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.applicationMethod}"
		showRequired="true">
		<f:selectItems
			value="#{facilityReference.resApplicationMethodTypeDef.items[(empty facilityProfile.emissionUnit.emissionUnitType.applicationMethod ? '' : facilityProfile.emissionUnit.emissionUnitType.applicationMethod)]}" />
	</af:selectOneChoice>
</af:panelForm>

