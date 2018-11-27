<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelForm rows="1" maxColumns="1" labelWidth="150" width="600">
	<af:selectOneChoice id="typeOfCoatingOperation" label="Type of Coating Operation:"
		readOnly="#{!facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.typeOfCoatingOperation}"
		showRequired="true">
		<f:selectItems
			value="#{facilityReference.cotCoatingOperationTypeDef.items[(empty facilityProfile.emissionUnit.emissionUnitType.typeOfCoatingOperation ? '' : facilityProfile.emissionUnit.emissionUnitType.typeOfCoatingOperation)]}" /> 
	</af:selectOneChoice>
</af:panelForm>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600"
	partialTriggers="typeOfMaterialBeingCoated">
	<af:selectOneChoice id="typeOfMaterialBeingCoated" label="Type of Material Being Coated:"
		readOnly="#{!facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.typeOfMaterialBeingCoated}"
		valueChangeListener="#{facilityProfile.emissionUnit.emissionUnitType.typeOfMaterialBeingCoatedListener}"
		showRequired="true" autoSubmit="true">
		<f:selectItems
			value="#{facilityReference.cotMaterialBeingCoatedTypeDef.items[(empty facilityProfile.emissionUnit.emissionUnitType.typeOfMaterialBeingCoated ? '' : facilityProfile.emissionUnit.emissionUnitType.typeOfMaterialBeingCoated)]}" /> 
	</af:selectOneChoice>	
	<af:inputText id="typeOfMaterialBeingCoatedOther" label="Type of Material Being Coated (Other):"
		partialTriggers="typeOfMaterialBeingCoated"
		rendered="#{facilityProfile.emissionUnit.emissionUnitType.typeOfMaterialBeingCoated == 'other'}"
		readOnly="#{!facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.typeOfMaterialBeingCoatedOther}" 
		autoSubmit="true" showRequired="true" columns="50"  maximumLength="80">
	</af:inputText>	
</af:panelForm>

<af:panelForm rows="1" maxColumns="1" labelWidth="150" width="600">
	<af:inputText id="typeOfProductBeingCoated" label="Type of Product Being Coated:"
		readOnly="#{!facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.typeOfProductBeingCoated}"
		showRequired="true" columns="50" maximumLength="80">
	</af:inputText>
</af:panelForm>

<af:panelForm rows="1" maxColumns="1" labelWidth="150" width="600">
	<af:inputText id="nameAndTypeOfMaterial" label="Name & Type of Material:"
		readOnly="#{!facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.nameAndTypeOfMaterial}"
		showRequired="true" columns="50" maximumLength="80">
	</af:inputText>
</af:panelForm>

<af:panelForm rows="1" maxColumns="1" labelWidth="150" width="600">
    <af:inputText id="maxAnnualThroughput" label="Maximum Throughput (gal/yr):"
        readOnly="#{! facilityProfile.editable}"
        value="#{facilityProfile.emissionUnit.emissionUnitType.maxAnnualThroughput}"
        showRequired="true" columns="12" maximumLength="6">
    </af:inputText>
</af:panelForm>
<af:panelForm rows="1" maxColumns="1" labelWidth="150" width="600">
	<af:inputText id="maxPctVOC" label="Maximum % VOC Content:"
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.maxPctVOC}"
		showRequired="true" columns="12" maximumLength="3">
	</af:inputText>
</af:panelForm>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600"
	partialTriggers='applicationMethod'>
	<af:selectOneChoice id="applicationMethod" label="Application Method:"
		readOnly="#{!facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.applicationMethod}"
		valueChangeListener="#{facilityProfile.emissionUnit.emissionUnitType.applicationMethodListener}"
		showRequired="true" autoSubmit="true">
		<f:selectItems
			value="#{facilityReference.cotApplicationMethodTypeDef.items[(empty facilityProfile.emissionUnit.emissionUnitType.applicationMethod ? '' : facilityProfile.emissionUnit.emissionUnitType.applicationMethod)]}" /> 
	</af:selectOneChoice>
	<af:inputText id="applicationMethodOther" label="Application Method (Other):"
		partialTriggers='applicationMethod'
		rendered="#{facilityProfile.emissionUnit.emissionUnitType.applicationMethod == 'other'}"
		readOnly="#{!facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.applicationMethodOther}"
		autoSubmit="true" showRequired="true" columns="50" maximumLength="80" >
	</af:inputText>
</af:panelForm>


