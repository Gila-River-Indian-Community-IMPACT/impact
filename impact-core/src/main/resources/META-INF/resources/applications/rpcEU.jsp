<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<af:panelGroup layout="vertical">
  <af:showDetailHeader text="Emissions Unit" disclosed="true">
    <%/* EU general info begin */%>
    <af:panelForm labelWidth="200">
      <af:inputText id="EUIdText" label="AQD EU ID :"
        value="#{applicationDetail.selectedEU.fpEU.epaEmuId}"
        readOnly="true" />
      <af:inputText id="EUDescriptionText"
        label="AQD EU description :"
        value="#{applicationDetail.selectedEU.fpEU.euDesc}"
        readOnly="true" />
      <af:inputText id="CompanyEUIdText" label="Company EU ID :"
        value="#{applicationDetail.selectedEU.fpEU.companyId}"
        readOnly="true" />
      <af:inputText id="CompanyEUDescText"
        label="Company EU Description :"
        rows="4" columns="80" maximumLength="240"
        value="#{applicationDetail.selectedEU.fpEU.regulatedUserDsc}"
        readOnly="true" />
    </af:panelForm>
  </af:showDetailHeader>
  <%/* EU general info end */%>
</af:panelGroup>

