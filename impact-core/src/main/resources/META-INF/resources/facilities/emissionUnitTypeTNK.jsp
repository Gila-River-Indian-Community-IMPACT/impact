<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelForm rows="2" maxColumns="1" labelWidth="150" width="600"
	partialTriggers="materialTypeStored liquidMaterialTypeStored materialTypeStoredDesc">
    <af:selectOneChoice id="materialTypeStored" label="Material Type:"
        readOnly="#{! facilityProfile.editable}" autoSubmit="true"
        value="#{facilityProfile.emissionUnit.emissionUnitType.materialTypeStored}"
        showRequired="true">
        <f:selectItems
            value="#{facilityReference.tnkMaterialStoredTypeDefs.items[(empty facilityProfile.emissionUnit.emissionUnitType.materialTypeStored ? '' : facilityProfile.emissionUnit.emissionUnitType.materialTypeStored)]}" />
    </af:selectOneChoice>
	<af:selectOneChoice id="liquidMaterialTypeStored" label="Type of Liquid:"
       	readOnly="#{! facilityProfile.editable}" autoSubmit="true"
        rendered="#{facilityProfile.emissionUnit.emissionUnitType.materialTypeStored == 'Liquid'}"
   	    value="#{facilityProfile.emissionUnit.emissionUnitType.liquidMaterialTypeStored}"
       	showRequired="true">
       	<f:selectItems
       	    value="#{facilityReference.tnkMaterialStoredTypeLiquidDefs.items[(empty facilityProfile.emissionUnit.emissionUnitType.liquidMaterialTypeStored ? '' : facilityProfile.emissionUnit.emissionUnitType.liquidMaterialTypeStored)]}" />
   	</af:selectOneChoice>
    <af:inputText id="materialTypeStoredDesc" label="Description of Material Stored:"
        readOnly="#{! facilityProfile.editable}" autoSubmit="true"
        rendered="#{facilityProfile.emissionUnit.emissionUnitType.materialTypeStored == 'Solid'
			|| (facilityProfile.emissionUnit.emissionUnitType.materialTypeStored == 'Liquid'
				&& facilityProfile.emissionUnit.emissionUnitType.liquidMaterialTypeStored == 'Other')}"
        value="#{facilityProfile.emissionUnit.emissionUnitType.materialTypeStoredDesc}"
        showRequired="true" columns="80" rows="4" maximumLength="240">
    </af:inputText>
</af:panelForm>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
	<af:selectOneChoice id="submergedFillPipeFlag"
		label="Submerged Fill Pipe?:" unselectedLabel=" "
		readOnly="#{! facilityProfile.editable}"
		value="#{facilityProfile.emissionUnit.emissionUnitType.submergedFillPipeFlag}">
		<f:selectItem itemLabel="Yes" itemValue="Y" />
		<f:selectItem itemLabel="No" itemValue="N" />
	</af:selectOneChoice>
</af:panelForm>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
    <af:inputText id="capacity" label="Capacity:"
        readOnly="#{! facilityProfile.editable}"
        value="#{facilityProfile.emissionUnit.emissionUnitType.capacity}"
        showRequired="true" columns="12" maximumLength="12">
    </af:inputText>

    <af:selectOneChoice id="capacityUnit" label="Units:"
        readOnly="#{! facilityProfile.editable}"
        value="#{facilityProfile.emissionUnit.emissionUnitType.capacityUnit}"
        showRequired="true">
        <f:selectItems
            value="#{facilityReference.tnkCapacityUnitDefs.items[(empty facilityProfile.emissionUnit.emissionUnitType.capacityUnit ? '' : facilityProfile.emissionUnit.emissionUnitType.capacityUnit)]}" />
    </af:selectOneChoice>
</af:panelForm>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
    <af:inputText id="maxThroughput" label="Maximum Throughput:"
        readOnly="#{! facilityProfile.editable}"
        value="#{facilityProfile.emissionUnit.emissionUnitType.maxThroughput}"
        showRequired="true" columns="12" maximumLength="12">
    </af:inputText>
    
    <af:selectOneChoice id="unitCd" label="Units:"
        readOnly="#{! facilityProfile.editable}"
        value="#{facilityProfile.emissionUnit.emissionUnitType.unitCd}"
        showRequired="true">
        <f:selectItems
            value="#{facilityReference.tnkMaxThroughputUnitDefs.items[(empty facilityProfile.emissionUnit.emissionUnitType.unitCd ? '' : facilityProfile.emissionUnit.emissionUnitType.unitCd)]}" />
    </af:selectOneChoice>
</af:panelForm>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
    <af:selectOneChoice id="tankLocation" label="Tank Location:"  unselectedLabel=" "
        readOnly="#{! facilityProfile.editable}"
        value="#{facilityProfile.emissionUnit.emissionUnitType.tankLocation}"
        showRequired="false">
        <f:selectItems
            value="#{facilityReference.tnkTankLocationTypeDefs.items[(empty facilityProfile.emissionUnit.emissionUnitType.tankLocation ? '' : facilityProfile.emissionUnit.emissionUnitType.tankLocation)]}" />
    </af:selectOneChoice>
</af:panelForm>

