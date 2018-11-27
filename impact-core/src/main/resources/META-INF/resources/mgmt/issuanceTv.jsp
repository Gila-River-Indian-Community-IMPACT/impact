<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<afh:rowLayout halign="center">
  <h:panelGrid border="1" width="1100"
    rendered="#{issuanceReport.hasTvResults}">
    <af:panelBorder>
      <af:showDetailHeader text="TV Report: #{issuanceReport.drillDown}" disclosed="true">
        <af:table bandingInterval="1" banding="row" var="tvDetails"
          emptyText='No Issued Permits' rows="#{issuanceDetailReport.pageLimit}" width="99%"
          value="#{issuanceDetailReport.tvDetails}">

          <af:column sortProperty="permitNumber" sortable="true"
            noWrap="true">
            <f:facet name="header">
              <af:outputText value="Permit Number" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="left">
              <af:commandLink text="#{tvDetails.permitNumber}"
                 action="#{permitDetail.loadPermit}" >
                <af:setActionListener to="#{permitDetail.permitID}"
                   from="#{tvDetails.permitId}" />
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
                value="#{tvDetails.facilityId}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="facilityName" sortable="true">
            <f:facet name="header">
              <af:outputText value="Facility Name" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true"
                value="#{tvDetails.facilityName}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="euCount" sortable="true">
            <f:facet name="header">
              <af:outputText value="EU Count" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="center">
              <af:inputText readOnly="true"
                value="#{tvDetails.euCount}" />
            </af:panelHorizontal>
          </af:column>
          
          <af:column sortProperty="reasonDsc" sortable="true">
            <f:facet name="header">
              <af:outputText value="Permit Reason" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true"
                value="#{tvDetails.reasonDsc}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="issuanceTypeDsc" sortable="true">
            <f:facet name="header">
              <af:outputText value="Issuance Type" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true"
                value="#{tvDetails.issuanceTypeDsc}" />
            </af:panelHorizontal>
          </af:column>
          
          <af:column sortProperty="effectiveDt" sortable="true">
            <f:facet name="header">
              <af:outputText value="Effective Date" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="center">
              <af:selectInputDate readOnly="true" value="#{tvDetails.effectiveDt}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="expirationDt" sortable="true">
            <f:facet name="header">
              <af:outputText value="Expiration Date" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="center">
              <af:selectInputDate readOnly="true" value="#{tvDetails.expirationDt}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="draftIssuanceDt" sortable="true">
            <f:facet name="header">
              <af:outputText value="Draft Issuance Date" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="center">
              <af:selectInputDate readOnly="true" value="#{tvDetails.draftIssuanceDt}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="pppIssuanceDt" sortable="true">
            <f:facet name="header">
              <af:outputText value="PPP Issuance Date" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="center">
              <af:selectInputDate readOnly="true" value="#{tvDetails.pppIssuanceDt}" />
            </af:panelHorizontal>
          </af:column>
          
          <af:column sortProperty="ppIssuanceDt" sortable="true">
            <f:facet name="header">
              <af:outputText value="PP Issuance Date" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="center">
              <af:selectInputDate readOnly="true" value="#{tvDetails.ppIssuanceDt}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="finalIssuanceDt" sortable="true">
            <f:facet name="header">
              <af:outputText value="Final Issuance Date" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="center">
              <af:selectInputDate readOnly="true" value="#{tvDetails.finalIssuanceDt}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="publicationDt" sortable="true">
            <f:facet name="header">
              <af:outputText value="Publication Date" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="center">
              <af:selectInputDate readOnly="true" value="#{tvDetails.publicationDt}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="endOfCommentDt" sortable="true">
            <f:facet name="header">
              <af:outputText value="End of Comment Period" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="center">
              <af:inputText readOnly="true"
                value="#{tvDetails.endOfCommentDt}" />
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

