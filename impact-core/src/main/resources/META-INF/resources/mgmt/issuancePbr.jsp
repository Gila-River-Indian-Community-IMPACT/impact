<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<afh:rowLayout halign="center">
  <h:panelGrid border="1" width="1100"
    rendered="#{issuanceReport.hasPbrResults}">
    <af:panelBorder>
      <af:showDetailHeader text="PBR Report: #{issuanceReport.drillDown}" disclosed="true">
        <af:table bandingInterval="2" banding="row" var="pbrDetails"
          emptyText='No Issued Permits' rows="#{issuanceDetailReport.pageLimit}" width="99%"
          value="#{issuanceDetailReport.pbrDetails}">

          <af:column sortProperty="permitNumber" sortable="true" noWrap="true">
            <f:facet name="header">
              <af:outputText value="PBR Number" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true" value="#{pbrDetails.permitNumber}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="facilityId" sortable="true">
            <f:facet name="header">
              <af:outputText value="Facility ID" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true"
                value="#{pbrDetails.facilityId}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="facilityName" sortable="true">
            <f:facet name="header">
              <af:outputText value="Facility Name" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true"
                value="#{pbrDetails.facilityName}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="euId" sortable="true">
            <f:facet name="header">
              <af:outputText value="EU ID" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="center">
              <af:inputText readOnly="true"
                value="#{pbrDetails.euId}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="euDesc" sortable="true">
            <f:facet name="header">
              <af:outputText value="EU Description" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="center">
              <af:inputText readOnly="true"
                value="#{pbrDetails.euDesc}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="submittedDt" sortable="true">
            <f:facet name="header">
              <af:outputText value="Request Date" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="left">
            	<af:selectInputDate readOnly="true" value="#{pbrDetails.submittedDt}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="pbrType" sortable="true">
            <f:facet name="header">
              <af:outputText value="PBR Type" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true"
                value="#{pbrDetails.pbrType}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="dispositionCd" sortable="true">
            <f:facet name="header">
              <af:outputText value="Disposition" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="center">
              <af:inputText readOnly="true"
                value="#{pbrDetails.dispositionCd}" />
            </af:panelHorizontal>
          </af:column>

          <f:facet name="footer">
            <afh:rowLayout halign="center">
              <af:panelButtonBar>
                <af:commandButton
                  actionListener="#{tableExporter.printTable}"
                  onclick="#{tableExporter.onClickScript}"
                  text="Printable view" />
                <af:commandButton
                  actionListener="#{tableExporter.excelTable}"
                  onclick="#{tableExporter.onClickScript}"
                  text="Export to excel" />
              </af:panelButtonBar>
            </afh:rowLayout>
          </f:facet>
        </af:table>
      </af:showDetailHeader>
    </af:panelBorder>
  </h:panelGrid>
</afh:rowLayout>

