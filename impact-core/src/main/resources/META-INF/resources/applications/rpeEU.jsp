<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<af:panelGroup layout="vertical">
  <af:panelHeader text="Emissions Unit">
    <%
    /* EU general info begin */
    %>
    <af:panelForm labelWidth="200">
      <af:inputText label="AQD EU ID :"
        value="#{applicationDetail.selectedEU.fpEU.epaEmuId}"
        readOnly="true" />
      <af:inputText label="AQD EU description :"
        value="#{applicationDetail.selectedEU.fpEU.euDesc}"
        readOnly="true" />
      <af:selectBooleanCheckbox label="Validated :" rendered="false" readOnly="true"
        value="#{applicationDetail.selectedEU.validated}" />
    </af:panelForm>
    <%
    /* EU general info end */
    %>
  </af:panelHeader>
</af:panelGroup>

