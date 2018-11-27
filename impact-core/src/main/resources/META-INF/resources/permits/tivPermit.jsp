<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%
/* General info begin */
%>

<af:panelForm maxColumns="2" rows="1" partialTriggers="tivReasons early">
  <af:selectManyCheckbox label="Reason(s) :" valign="top" id="tivReasons"
    readOnly="#{!permitDetail.editMode}" autoSubmit="true"
    value="#{permitDetail.permit.permitReasonCDs}">
    <f:selectItems value="#{permitDetail.permitReasons}" />
  </af:selectManyCheckbox>
  <afh:cellFormat  rendered="#{permitDetail.permit.renewal}">
    <af:selectBooleanCheckbox label="Issue Early Renewal :" id="early"
      readOnly="#{! permitDetail.editMode}" autoSubmit="true"
      rendered="#{permitDetail.permit.renewal}"
      value="#{permitDetail.permit.earlyRenewalFlag}" />
    <af:outputText value="WARNING: This permit will not be issued final until the date is < or = 540 days before the next expiration date." 
       inlineStyle="color: orange; font-weight: bold; font-size:11px;" rendered="#{permitDetail.permit.earlyRenewalFlag}" />
  </afh:cellFormat>
  <af:outputText value ="" rendered="#{!permitDetail.permit.renewal}"/>
  </af:panelForm>
  
  <af:panelForm labelWidth="21%" fieldWidth="79%">
  <af:inputText id="tivDesc" label="Permit Description :" columns="80" rows="3"
    inlineStyle="#{!permitDetail.editMode ? 'border-width: 0px; background-color: #ffffff;' : ''}"
    readOnly="#{! permitDetail.editMode}" maximumLength="2048"
    value="#{permitDetail.permit.description}" />
</af:panelForm>

