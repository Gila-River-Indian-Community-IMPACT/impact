<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<af:panelGroup layout="vertical">
  <af:panelHeader text="Emissions Unit">
    <af:panelForm labelWidth="44%" rows="1" maxColumns="2"
      fieldWidth="56%">
      <af:inputText label="EU ID :"
        value="#{permitDetail.selectedEU.fpEU.epaEmuId}" readOnly="true" />
      <af:inputText label="Company Equipment ID :" maximumLength="60"
        value="#{permitDetail.selectedEU.companyId}"
        readOnly="#{! permitDetail.editMode}" />
      <af:selectOneChoice label="EU Permit Status :"
        readOnly="#{! permitDetail.editMode || permitDetail.permit.finalIssuance.issuanceStatusCd != 'I'}"
        value="#{permitDetail.selectedEU.permitStatusCd}">
        <mu:selectItems value="#{permitReference.permitStatusDefs}" />
      </af:selectOneChoice>
      <af:selectOneChoice label="EU Group :"
        readOnly="#{! permitDetail.editMode}"
        value="#{permitDetail.selectedEUEUGroup}"
        rendered="#{! permitDetail.selectedEU.euGroup.individualEUGroup}">
        <f:selectItems value="#{permitDetail.euGroupSelectItems}" />
      </af:selectOneChoice>

      <af:selectInputDate label="Install Date :" readOnly="true"
        value="#{permitDetail.selectedEU.fpEU.euInstallDate}" />
      <af:selectInputDate label="Terminated Date :"
        readOnly="#{! permitDetail.editMode}"
        value="#{permitDetail.selectedEU.terminatedDate}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
      <af:selectInputDate label="Rescission Date :"
        readOnly="#{! permitDetail.editMode}"
        value="#{permitDetail.selectedEU.revocationDate}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
    </af:panelForm>

    <af:panelForm maxColumns="1" rows="1" labelWidth="21%"
      fieldWidth="79%">
      <af:inputText label="AQD Description :" columns="70" rows="2"
        inlineStyle="#{!permitDetail.editMode ? 'border-width: 0px; background-color: #ffffff;' : ''}"
        value="#{permitDetail.selectedEU.dapcDescription}" maximumLength="1000"
        readOnly="#{! permitDetail.editMode}" />
    </af:panelForm>

    <%
    /* EU general info end */
    %>
    
  </af:panelHeader>

</af:panelGroup>
