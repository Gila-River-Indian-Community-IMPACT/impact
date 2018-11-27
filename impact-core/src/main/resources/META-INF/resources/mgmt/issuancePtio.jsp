<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<afh:rowLayout halign="center">
  <h:panelGrid border="1" width="1100"
    rendered="#{issuanceReport.hasPtioResults}">
    <af:panelBorder>
      <af:showDetailHeader text="PTIO Report: #{issuanceReport.drillDown}" disclosed="true">
        <af:table bandingInterval="1" banding="row" var="ptioDetails"
          emptyText='No Issued Permits' rows="#{issuanceDetailReport.pageLimit}" width="99%"
          value="#{issuanceDetailReport.ptioDetails}">

          <af:column sortProperty="permitNumber" sortable="true"
            noWrap="true">
            <f:facet name="header">
              <af:outputText value="Permit Number" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="left">
              <af:commandLink text="#{ptioDetails.permitNumber}"
                 action="#{permitDetail.loadPermit}" >
                <af:setActionListener to="#{permitDetail.permitID}"
                   from="#{ptioDetails.permitId}" />
                <t:updateActionListener property="#{permitDetail.fromTODOList}"
                   value="false" />
              </af:commandLink>
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="facilityId" sortable="true">
            <f:facet name="header">
              <af:outputText value="Facility ID" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true"
                value="#{ptioDetails.facilityId}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="facilityName" sortable="true">
            <f:facet name="header">
              <af:outputText value="Facility Name" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true"
                value="#{ptioDetails.facilityName}" />
            </af:panelHorizontal>
          </af:column>
          
          <af:column sortProperty="euCount" sortable="true">
            <f:facet name="header">
              <af:outputText value="EU Count" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="center">
              <af:inputText readOnly="true"
                value="#{ptioDetails.euCount}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="reasonDsc" sortable="true">
            <f:facet name="header">
              <af:outputText value="Permit Reason" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true"
                value="#{ptioDetails.reasonDsc}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="issuanceTypeDsc" sortable="true">
            <f:facet name="header">
              <af:outputText value="Issuance Type" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true"
                value="#{ptioDetails.issuanceTypeDsc}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="generalPermitFlag" sortable="true">
            <f:facet name="header">
              <af:outputText value="General Permit (Yes/No)" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="center">
              <af:inputText readOnly="true"
                value="#{ptioDetails.generalPermitFlag}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="feptioFlag" sortable="true">
            <f:facet name="header">
              <af:outputText value="FEPTIO (Yes/No)" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="center">
              <af:inputText readOnly="true"
                value="#{ptioDetails.feptioFlag}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="effectiveDt" sortable="true">
            <f:facet name="header">
              <af:outputText value="Effective Date" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="center">
              <af:selectInputDate readOnly="true" value="#{ptioDetails.effectiveDt}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="expirationDt" sortable="true">
            <f:facet name="header">
              <af:outputText value="Expiration Date" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="center">
              <af:selectInputDate readOnly="true" value="#{ptioDetails.expirationDt}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="draftIssuanceDt" sortable="true">
            <f:facet name="header">
              <af:outputText value="Draft Issuance Date" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="center">
              <af:selectInputDate readOnly="true" value="#{ptioDetails.draftIssuanceDt}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="finalIssuanceDt" sortable="true">
            <f:facet name="header">
              <af:outputText value="Final Issuance Date" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="center">
              <af:selectInputDate readOnly="true" value="#{ptioDetails.finalIssuanceDt}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="invoiceAmount" sortable="true">
            <f:facet name="header">
              <af:outputText value="Invoice Amount" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="center">
              <af:inputText readOnly="true"
                value="#{ptioDetails.invoiceAmount}" />
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
