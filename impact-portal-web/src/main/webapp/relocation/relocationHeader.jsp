<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<af:panelBox background="light" width="100%">
  <af:panelForm rows="2" maxColumns="3">
    <af:inputText label="Facility ID:" readOnly="true"
      value="#{myTasks.facility.facilityId}" />
    <af:inputText label="Facility Name:" readOnly="true"
      value="#{myTasks.facility.name}" />
    <af:inputText
      label="#{relocation.portalRelReq.requestType} Number: "
      readOnly="true"
      value="#{relocation.portalRelReq.applicationNumber}" />
    <af:inputText label="Request type: " readOnly="true"
      value="#{relocation.portalRelReq.requestType}" />
    <af:inputText label="Submitted:" readOnly="true"
      value="No" />
  </af:panelForm>
</af:panelBox>