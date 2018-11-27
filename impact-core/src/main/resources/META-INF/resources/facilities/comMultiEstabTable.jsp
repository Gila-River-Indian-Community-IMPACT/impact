<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:table value="#{facilityProfile.multiEstabFacilities}" bandingInterval="1"
  banding="row" var="facility" width="98%">
  <af:column sortProperty="facilityId" sortable="true" noWrap="true"
    formatType="text" headerText="Facility ID">
    <af:commandLink action="#{facilityProfile.submitProfile}"
      text="#{facility.facilityId}"
      rendered="#{facilityProfile.dapcUser}" >
      <t:updateActionListener property="#{facilityProfile.fpId}"
        value="#{facility.fpId}" />
    </af:commandLink>
    <af:outputText value="#{facility.facilityId}" rendered="#{!facilityProfile.dapcUser}" />
  </af:column>
  <af:column sortProperty="name" sortable="true" noWrap="true" formatType="text"
    headerText="Facility Name">
    <af:outputText value="#{facility.name}" />
  </af:column>

  <f:facet name="footer">
    <afh:rowLayout halign="center">
      <af:panelButtonBar>
        <af:commandButton actionListener="#{tableExporter.printTable}"
          onclick="#{tableExporter.onClickScript}" text="Printable view" />
        <af:commandButton actionListener="#{tableExporter.excelTable}"
          onclick="#{tableExporter.onClickScript}" text="Export to excel" />
      </af:panelButtonBar>
    </afh:rowLayout>
  </f:facet>
</af:table>