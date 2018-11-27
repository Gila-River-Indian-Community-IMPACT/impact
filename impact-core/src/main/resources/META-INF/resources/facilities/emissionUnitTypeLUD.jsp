<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelForm rows="2" maxColumns="1" labelWidth="150" width="600">
    <af:selectOneChoice id="materialType" label="Type of Material:"
        readOnly="#{! facilityProfile.editable}"
        value="#{facilityProfile.emissionUnit.emissionUnitType.materialType}"
        showRequired="true">
        <f:selectItems
            value="#{facilityReference.materialTypeDefs.items[(empty facilityProfile.emissionUnit.emissionUnitType.materialType ? '' : facilityProfile.emissionUnit.emissionUnitType.materialType)]}" />
    </af:selectOneChoice>
    
    <af:inputText id="materialDescription" label="Material Description:"
        readOnly="#{! facilityProfile.editable}"
        value="#{facilityProfile.emissionUnit.emissionUnitType.materialDescription}"
        showRequired="true" columns="80" rows="4" maximumLength="240">
    </af:inputText>
            
</af:panelForm>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
    <af:inputText id="maxAnnualThroughput" label="Maximum Annual Throughput:"
        readOnly="#{! facilityProfile.editable}"
        value="#{facilityProfile.emissionUnit.emissionUnitType.maxAnnualThroughput}"
        showRequired="true" columns="12" maximumLength="12">
    </af:inputText>
    
    <af:selectOneChoice id="unitCd" label="Units:"
        readOnly="#{! facilityProfile.editable}"
        value="#{facilityProfile.emissionUnit.emissionUnitType.unitCd}"
        showRequired="true">
        <f:selectItems
            value="#{facilityReference.ludMaxAnnualThroughputUnitsDefs.items[(empty facilityProfile.emissionUnit.emissionUnitType.unitCd ? '' : facilityProfile.emissionUnit.emissionUnitType.unitCd)]}" />
    </af:selectOneChoice>
</af:panelForm>

