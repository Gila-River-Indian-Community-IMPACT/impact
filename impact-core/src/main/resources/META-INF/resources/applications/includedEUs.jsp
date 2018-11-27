<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>


<af:table emptyText=" " var="includedEU" bandingInterval="1"
  banding="row" value="#{applicationDetail.includedEmissionUnits}"
  width="98%" id="euTable" rows="#{applicationDetail.pageLimit}">
  <af:column formatType="text" headerText="Emissions Unit ID"
    id="euIDColumn">
    <af:outputText id="euIDText" value="#{includedEU.epaEmuId}" />
  </af:column>
  <af:column formatType="text" headerText="Company Equipment ID"
    id="compEqptIdColumn">
    <af:outputText id="compEqptIdText"
      value="#{includedEU.companyId}" />
  </af:column>
  <af:column formatType="text" headerText="Equipment Description"
    id="eqptDescColumn">
    <af:outputText id="eqptDescText"
      value="#{includedEU.regulatedUserDsc}" />
  </af:column>
</af:table>