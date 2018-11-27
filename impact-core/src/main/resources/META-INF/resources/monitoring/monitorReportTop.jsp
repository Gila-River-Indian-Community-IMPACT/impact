<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelBox background="light" width="1000px">
  <af:panelForm rows="1" maxColumns="3">
  	<af:inputText label="Monitor Report ID: " readOnly="true"
      value="#{monitorReportDetail.monitorReport.mrptId}" />
    <af:inputText label="Monitor Group ID: " readOnly="true"
      value="#{monitorReportDetail.monitorGroupId}" />
    <af:selectOneChoice label="Submitted: " readOnly="true"
      rendered="#{!monitorReportDetail.publicApp}"
      value="#{monitorReportDetail.monitorReport.submitted}">
      <f:selectItem itemValue="true" itemLabel="Yes"/>
      <f:selectItem itemValue="false" itemLabel="No"/>
    </af:selectOneChoice>    
  </af:panelForm>
</af:panelBox>