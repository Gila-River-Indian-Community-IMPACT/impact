<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="400">
    <af:selectOneChoice id="incineratorType" label="Incinerator Type:"
        readOnly="#{! facilityProfile.editable}"
        value="#{facilityProfile.emissionUnit.emissionUnitType.incineratorType}"
        showRequired="true">
        <f:selectItems
            value="#{facilityReference.incineratorTypeDefs.items[(empty facilityProfile.emissionUnit.emissionUnitType.incineratorType ? '' : facilityProfile.emissionUnit.emissionUnitType.incineratorType)]}" />
    </af:selectOneChoice>
    <af:selectOneChoice id="burnerSystem" label="Burner System:"
        readOnly="#{! facilityProfile.editable}"
        value="#{facilityProfile.emissionUnit.emissionUnitType.burnerSystem}"
        showRequired="true">
        <f:selectItems
            value="#{facilityReference.burnerTypeDefs.items[(empty facilityProfile.emissionUnit.emissionUnitType.burnerSystem ? '' : facilityProfile.emissionUnit.emissionUnitType.burnerSystem)]}" />
    </af:selectOneChoice>
</af:panelForm>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="400">
    <af:inputText id="maxDesignCapacity" label="Maximum Design Capacity:"
        readOnly="#{! facilityProfile.editable}"
        value="#{facilityProfile.emissionUnit.emissionUnitType.maxDesignCapacity}"
        showRequired="true" columns="12" maximumLength="12">
    </af:inputText>
    <af:selectOneChoice id="unitCd" label="Units:"
        readOnly="#{! facilityProfile.editable}"
        value="#{facilityProfile.emissionUnit.emissionUnitType.unitCd}"
        showRequired="true">
        <f:selectItems
            value="#{facilityReference.incineratorDesignCapacityUnitsDefs.items[(empty facilityProfile.emissionUnit.emissionUnitType.unitCd ? '' : facilityProfile.emissionUnit.emissionUnitType.unitCd)]}" />
    </af:selectOneChoice>
    
</af:panelForm>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="400">
    <af:inputText id="minDesignCapacity" label="Minimum Design Capacity:"
        readOnly="#{! facilityProfile.editable}"
        value="#{facilityProfile.emissionUnit.emissionUnitType.minDesignCapacity}"
        showRequired="true" columns="12" maximumLength="12">
    </af:inputText>
    <af:selectOneChoice id="capacityUnit" label="Units:"
        readOnly="#{! facilityProfile.editable}"
        value="#{facilityProfile.emissionUnit.emissionUnitType.capacityUnit}"
        showRequired="true">
        <f:selectItems
            value="#{facilityReference.incineratorDesignCapacityUnitsDefs.items[(empty facilityProfile.emissionUnit.emissionUnitType.capacityUnit ? '' : facilityProfile.emissionUnit.emissionUnitType.capacityUnit)]}" />
    </af:selectOneChoice>
</af:panelForm>

<af:panelForm rows="1" maxColumns="1" labelWidth="150" width="400">
    <af:inputText id="pilotGasVolume" label="Pilot Gas Volume (scf/min):"
        readOnly="#{! facilityProfile.editable}"
        value="#{facilityProfile.emissionUnit.emissionUnitType.pilotGasVolume}"
        showRequired="false" columns="12" maximumLength="12">
    </af:inputText>
</af:panelForm>

<af:panelForm rows="1" maxColumns="1" labelWidth="250" width="400">
    <af:inputText id="primaryChamberOpTemp" label="Primary Chamber Operating Temperature (F):"
        readOnly="#{! facilityProfile.editable}"
        value="#{facilityProfile.emissionUnit.emissionUnitType.primaryChamberOpTemp}"
        showRequired="false" columns="12" maximumLength="12">
    </af:inputText>
</af:panelForm>
<af:panelForm rows="1" maxColumns="1" labelWidth="250" width="400">
    
    <af:inputText id="secondaryChamberOpTemp" label="Secondary Chamber Operating Temperature (F):"
        readOnly="#{! facilityProfile.editable}"
        value="#{facilityProfile.emissionUnit.emissionUnitType.secondaryChamberOpTemp}"
        showRequired="false" columns="12" maximumLength="12">
    </af:inputText>
</af:panelForm>
    
