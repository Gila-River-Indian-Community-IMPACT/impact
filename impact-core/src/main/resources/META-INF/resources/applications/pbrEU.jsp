<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<af:panelGroup layout="vertical">
  <af:panelHeader text="Emissions Unit">
    <%
    /* EU general info begin */
    %>
    <af:panelForm labelWidth="200">
      <af:selectBooleanCheckbox label="Validated :" rendered="false" readOnly="true"
        value="#{applicationDetail.selectedEU.validated}" />
      <af:inputText label="AQD EU ID :"
        value="#{applicationDetail.selectedEU.fpEU.epaEmuId}"
        readOnly="true" />
      <af:inputText label="AQD EU description :"
        value="#{applicationDetail.selectedEU.fpEU.euDesc}"
        readOnly="true" />
       <af:selectOneChoice label="EU Permit Status :"
        readOnly="#{!applicationDetail.editMode || applicationDetail.permit.effectiveDate == null}"
        rendered="#{applicationDetail.selectedPermitEU != null}"
        value="#{applicationDetail.selectedPermitEU.permitStatusCd}">
        <mu:selectItems value="#{permitReference.permitStatusDefs}" />
      </af:selectOneChoice>
    </af:panelForm>
    <af:showDetailHeader text="Active Permits" size="2" disclosed="true">
      <af:table id="euPermits" emptyText=" " var="eupermit" width="98%"
        value="#{applicationDetail.euPermits}"
        partialTriggers="euPermits:createRPR">
        <af:column headerText="Permit Number">
          <af:inputText value="#{eupermit.permitNumber}" readOnly="true" />
        </af:column>
        <af:column headerText="Permit Type">
          <af:selectOneChoice readOnly="true"
            value="#{eupermit.permitType}">
            <mu:selectItems value="#{permitReference.permitTypes}" />
          </af:selectOneChoice>
        </af:column>
        <af:column headerText="RPR Request"
          rendered="#{applicationDetail.internalApp && !empty applicationDetail.application.submittedDate}">
          <af:commandButton text="Work on RPR" id="createRPR"
            rendered="#{eupermit.permitType != 'TVPTO' && eupermit.permitType != 'PBR'}"
            action="#{applicationDetail.workOnRPR}">
            <af:setActionListener to="#{applicationDetail.permitId}"
              from="#{eupermit.permitID}" />
          </af:commandButton>
        </af:column>
      </af:table>
    </af:showDetailHeader>
  </af:panelHeader>
  <%
  /* EU general info end */
  %>
</af:panelGroup>

