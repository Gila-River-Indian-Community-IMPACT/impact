<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%
/* General info begin */
%>
<af:panelForm labelWidth="21%" fieldWidth="79%">
  <af:selectInputDate label="PTO Application Received Date :"
    readOnly="true" value="#{permitDetail.permit.receivedDate}" />
  <af:inputText label="Adjudication :" readOnly="true"
    value="#{permitDetail.permit.displayAdjudCode}" />
  <af:inputText label="Emergency Action Plan :" readOnly="true"
    value="#{permitDetail.permit.displayEapStatus}" />
  <af:selectInputDate label="Date PACN entered at CO to be issued :"
    readOnly="true" value="#{permitDetail.permit.processDate}" />
  <af:inputText label="Emission Activity Category (EAC) form:"
    columns="20" readOnly="true"
    value="#{permitDetail.permit.appendixCode}" />
</af:panelForm>
