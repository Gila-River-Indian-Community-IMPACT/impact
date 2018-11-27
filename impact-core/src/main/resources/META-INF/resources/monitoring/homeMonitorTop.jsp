<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelBox background="light" width="1000px">
  <af:panelForm rows="6" maxColumns="2" labelWidth="220px" width="98%">
    <af:inputText label="Group ID: " readOnly="true"
      value="#{homeMonitorDetail.monitor.mgrpId}" />
    <af:inputText label="Group Name: " readOnly="true"
      value="#{homeMonitorDetail.monitor.groupName}" />
    <af:inputText label="Group Description: " readOnly="true" rows="4"
      value="#{homeMonitorDetail.monitor.groupDescription}"
	  columns="80" maximumLength="255" />
    <af:inputText label="Monitor ID: " readOnly="true"
      value="#{homeMonitorDetail.monitor.mntrId}" />
    <af:inputText label="Site ID: " readOnly="true"
      value="#{homeMonitorDetail.monitor.mstId}" />
    <af:inputText label="Local Site Name: " readOnly="true"
      value="#{homeMonitorDetail.monitor.siteName}" />
  </af:panelForm>
</af:panelBox>
