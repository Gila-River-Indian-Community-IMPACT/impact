<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>



<af:panelForm rows="1" maxColumns="1" labelWidth="150" width="400">
    <af:selectOneChoice id="unitType" label="Type of Unit:"
        readOnly="#{! facilityProfile.editable}"
        value="#{facilityProfile.emissionUnit.emissionUnitType.unitType}"
        showRequired="true">
        <f:selectItems
            value="#{facilityReference.cshUnitTypeDefs.items[(empty facilityProfile.emissionUnit.emissionUnitType.unitType ? '' : facilityProfile.emissionUnit.emissionUnitType.unitType)]}" />
    </af:selectOneChoice>
</af:panelForm>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="400">
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
            value="#{facilityReference.cshMaxAnnualThroughputUnitDefs.items[(empty facilityProfile.emissionUnit.emissionUnitType.unitCd ? '' : facilityProfile.emissionUnit.emissionUnitType.unitCd)]}" />
    </af:selectOneChoice>
</af:panelForm>

<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="400">
    <af:inputText id="modelNameAndNumber" label="Model Name and Number:"
        readOnly="#{! facilityProfile.editable}"
        value="#{facilityProfile.emissionUnit.emissionUnitType.modelNameNumber}"
        showRequired="true" columns="25" maximumLength="50">
    </af:inputText>
</af:panelForm>

<%@ include file="comEmissionUnitReplacementTable.jsp"%>