<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelBox background="light" width="1000">
  <af:panelForm rows="2" maxColumns="3">
    <af:inputText label="Facility ID: " readOnly="true"
      value="#{permitDetail.currentFacility.facilityId}" />
    <af:inputText label="Facility Name: " readOnly="true"
      value="#{permitDetail.currentFacility.name}" />
    <af:inputText label="Permit Number: " readOnly="true"
      value="#{permitDetail.permit.permitNumber}" />
    <af:inputText label="Permit Type: " readOnly="true"
      value="#{permitDetail.permit.permitType}" />
    <af:selectOneChoice tip=" " readOnly="true" label="Permit Publication Stage: "
      value="#{permitDetail.permit.permitGlobalStatusCD}">
      <mu:selectItems value="#{permitReference.permitGlobalStatusDefs}" />
    </af:selectOneChoice>
    <af:selectInputDate label="Final Issuance Date: " readOnly="true"
      value="#{permitDetail.permit.finalIssueDate}" />
  </af:panelForm>
</af:panelBox>

