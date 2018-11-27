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

	<%-- General Permit not valid for WY     
	<af:panelForm maxColumns="1" rows="1" labelWidth="20%"
      partialTriggers="gpt" fieldWidth="80%">
      <af:selectOneChoice label="General Permit Type :" id="gpt"
        unselectedLabel="Not applicable" autoSubmit="true"
        readOnly="#{! permitDetail.editMode}"
        value="#{permitDetail.selectedEU.generalPermitTypeCd}">
        <mu:selectItems value="#{permitReference.generalPermitTypeDefs}" />
      </af:selectOneChoice>
      <af:selectOneChoice label="Model General Permit No :"
        unselectedLabel="Not applicable"
        readOnly="#{! permitDetail.editMode}"
        value="#{permitDetail.selectedEU.modelGeneralPermitCd}">
        <mu:selectItems value="#{permitDetail.modelGeneralPermitDefs}" />
      </af:selectOneChoice>
    </af:panelForm> --%>

    <%
    /* EU general info end */
    %>
    <%
                 /*
                 <afh:tableLayout>
                 <afh:rowLayout>
                 <afh:cellFormat width="160" halign="right" valign="top">
                 <af:inputText value="Superseded " readOnly="true" />
                 <af:inputText value="Permit Numbers :" readOnly="true" />
                 </afh:cellFormat>
                 <afh:cellFormat width="10" />
                 <afh:cellFormat halign="left">
                 <af:table value="#{permitDetail.selectedEU.supersededPermits}"
                 var="row" emptyText=" ">
                 <f:facet name="selection">
                 <af:tableSelectMany rendered="#{permitDetail.editMode}" />
                 </f:facet>
                 <f:facet name="footer">
                 <af:panelGroup layout="horizontal"
                 rendered="#{permitDetail.editMode}">
                 <af:inputText value="#{permitDetail.newSupersededPermitNumber}"
                 columns="10" />
                 <af:commandButton text="Add"
                 actionListener="#{permitDetail.addEUSupersededPermit}" />
                 <af:commandButton text="Delete selected items"
                 actionListener="#{permitDetail.deleteEUSupersededPermit}" />
                 </af:panelGroup>
                 </f:facet>
                 <af:column>
                 <af:outputText value="#{row.permitNumber}" />
                 </af:column>
                 </af:table>
                 </afh:cellFormat>
                 </afh:rowLayout>
                 </afh:tableLayout>
                 */
    %>
    <%
    /* Superseded permits end */
    %>
  </af:panelHeader>

</af:panelGroup>
