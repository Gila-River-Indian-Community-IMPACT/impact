<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<af:panelGroup layout="vertical">
  <af:panelHeader text="Emissions Unit">
    <af:panelForm labelWidth="44%" rows="1" maxColumns="2" fieldWidth="56%">
      <af:inputText label="EU ID :"
        value="#{permitDetail.selectedEU.fpEU.epaEmuId}" readOnly="true" />
      <af:selectOneChoice label="EU Permit Status :"
        readOnly="#{! permitDetail.editMode || permitDetail.permit.finalIssuance.issuanceStatusCd != 'I'}"
        value="#{permitDetail.selectedEU.permitStatusCd}">
        <mu:selectItems value="#{permitReference.permitStatusDefs}" />
      </af:selectOneChoice>
      <af:inputText label="EU Facility description :"
        value="#{permitDetail.selectedEU.fpEU.euDesc}" readOnly="true" />
      <af:inputText label="EU AQD description :"
        value="#{permitDetail.selectedEU.dapcDescription}"
        readOnly="#{! permitDetail.editMode}" />

      <af:selectOneChoice label="EU group :"
        readOnly="#{! permitDetail.editMode}"
        value="#{permitDetail.selectedEUEUGroup}"
        rendered="#{! permitDetail.selectedEU.euGroup.individualEUGroup}">
        <f:selectItems value="#{permitDetail.euGroupSelectItems}" />
      </af:selectOneChoice>
      <af:selectInputDate label="Install date :" readOnly="true"
        value="#{permitDetail.selectedEU.fpEU.euInstallDate}" />
      <af:selectInputDate label="Terminated date :"
        readOnly="#{! permitDetail.editMode}"
        value="#{permitDetail.selectedEU.terminatedDate}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
    </af:panelForm>
    <afh:tableLayout width="100%">
      <afh:rowLayout>
        <afh:cellFormat width="50%" halign="left" valign="top">
          <af:panelForm labelWidth="44%" fieldWidth="56%">
            <af:selectBooleanCheckbox label="Rescission :" id="revocation"
              autoSubmit="TRUE" readOnly="#{! permitDetail.editMode}"
              value="#{permitDetail.permit.revocation}" />
          </af:panelForm>
        </afh:cellFormat>
        <afh:cellFormat width="50%" halign="left" valign="top">
          <af:panelForm labelWidth="44%" fieldWidth="56%"
            partialTriggers="revocation">
            <af:selectOneChoice label="RPR Number :"
              rendered="#{permitDetail.permit.revocation}"
              readOnly="#{! permitDetail.editMode}"
              unselectedLabel="Please select"
              value="#{permitDetail.permit.rprNumber}">
              <mu:selectItems value="#{permitDetail.rprNumbers}" />
            </af:selectOneChoice>
          </af:panelForm>
        </afh:cellFormat>
      </afh:rowLayout>
    </afh:tableLayout>
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
