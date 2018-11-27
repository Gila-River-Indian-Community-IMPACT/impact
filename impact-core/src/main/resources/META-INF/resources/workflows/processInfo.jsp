<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelBox background="light" width="1000">
  <af:panelForm rows="2" maxColumns="3">
    <af:inputText readOnly="true" label="Facility ID:"
      value="#{workFlow2DDraw.process.facilityIdString}" />
    <af:inputText readOnly="true" label="Facility Name:"
      value="#{workFlow2DDraw.process.facilityNm}" />
    <af:selectOneChoice label="Status:"
      value="#{workFlow2DDraw.process.status}" readOnly="true">
      <f:selectItems value="#{workFlowDefs.statusDef}" />
    </af:selectOneChoice>
    <af:selectInputDate readOnly="true" label="Due Date:"
      value="#{workFlow2DDraw.process.dueDt}" />
    <af:selectOneChoice readOnly="true" label="Workflow:"
      value="#{workFlow2DDraw.process.processCd}">
      <f:selectItems value="#{workFlowDefs.processTypes}" />
    </af:selectOneChoice>
  </af:panelForm>
</af:panelBox>

