<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<af:panelGroup layout="vertical">
  <af:panelHeader text="Insignificant Emissions Unit">
    <%
    /* EU general info begin */
    %>
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
      <af:selectOneChoice id="basisForIEUChoice"
        label="Basis for IEU status : "
        readOnly="#{! applicationDetail.editMode}"
        value="#{applicationDetail.selectedEU.tvIeuReasonCd}">
        <f:selectItems value="#{applicationReference.tvIeuReasonDefs}" />
      </af:selectOneChoice>
    </af:panelForm>
  </af:panelHeader>

  <%
              /**************************************************************************/
              /* PTE Tables                                                             */
              /**************************************************************************/
  %>
  <f:subview id="tvEuPTE">
    <jsp:include page="tvEuPTE.jsp" />
  </f:subview>
</af:panelGroup>
