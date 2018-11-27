<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelBox background="light" width="1000">
  <af:panelForm rows="2" maxColumns="3">
    <af:inputText label="Monitor Group ID: " readOnly="true"
      value="#{homeMonitorGroupDetail.monitorGroup.mgrpId}" />
  </af:panelForm>
</af:panelBox>