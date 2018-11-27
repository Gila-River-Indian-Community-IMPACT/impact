<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<afh:rowLayout halign="center">
  <h:panelGrid border="1" width="1100"
    rendered="#{issuanceReport.hasPtiResults}">
    <af:panelBorder>
      <af:showDetailHeader text="PTI Report: #{issuanceReport.drillDown}" disclosed="true">
        <af:table bandingInterval="1" banding="row" var="ptiDetails"
          emptyText='No Issued Permits' rows="#{issuanceDetailReport.pageLimit}" width="99%"
          value="#{issuanceDetailReport.ptiDetails}">

          <af:column sortProperty="permitNumber" sortable="true"
            noWrap="true">
            <f:facet name="header">
              <af:outputText value="Permit Number" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="left">
              <af:commandLink text="#{ptiDetails.permitNumber}"
                 action="#{permitDetail.loadPermit}" >
                <af:setActionListener to="#{permitDetail.permitID}"
                   from="#{ptiDetails.permitId}" />
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
                value="#{ptiDetails.facilityId}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="facilityName" sortable="true">
            <f:facet name="header">
              <af:outputText value="Facility Name" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true"
                value="#{ptiDetails.facilityName}" />
            </af:panelHorizontal>
          </af:column>
          
          <af:column sortProperty="euCount" sortable="true">
            <f:facet name="header">
              <af:outputText value="EU Count" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="center">
              <af:inputText readOnly="true"
                value="#{ptiDetails.euCount}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="reasonDsc" sortable="true">
            <f:facet name="header">
              <af:outputText value="Permit Reason" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true"
                value="#{ptiDetails.reasonDsc}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="issuanceTypeDsc" sortable="true">
            <f:facet name="header">
              <af:outputText value="Issuance Type" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true"
                value="#{ptiDetails.issuanceTypeDsc}" />
            </af:panelHorizontal>
          </af:column>

      <%-- General Permit not valid for WY    
      	<af:column sortProperty="generalPermitFlag" sortable="true">
            <f:facet name="header">
              <af:outputText value="General Permit (Yes/No)" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="center">
              <af:inputText readOnly="true"
                value="#{ptiDetails.generalPermitFlag}" />
            </af:panelHorizontal>
          </af:column> --%>

          <af:column sortProperty="effectiveDt" sortable="true">
            <f:facet name="header">
              <af:outputText value="Effective Date" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="center">
              <af:selectInputDate readOnly="true" value="#{ptiDetails.effectiveDt}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="draftIssuanceDt" sortable="true">
            <f:facet name="header">
              <af:outputText value="Draft Issuance Date" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="center">
              <af:selectInputDate readOnly="true" value="#{ptiDetails.draftIssuanceDt}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="finalIssuanceDt" sortable="true">
            <f:facet name="header">
              <af:outputText value="Final Issuance Date" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="center">
              <af:selectInputDate readOnly="true" value="#{ptiDetails.finalIssuanceDt}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="publicationDt" sortable="true">
            <f:facet name="header">
              <af:outputText value="Publication Date" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="center">
              <af:selectInputDate readOnly="true" value="#{ptiDetails.publicationDt}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="endOfCommentDt" sortable="true">
            <f:facet name="header">
              <af:outputText value="End of Comment Period" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="center">
              <af:inputText readOnly="true"
                value="#{ptiDetails.endOfCommentDt}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="invoiceAmount" sortable="true">
            <f:facet name="header">
              <af:outputText value="Invoice Amount" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="center">
              <af:inputText readOnly="true"
                value="#{ptiDetails.invoiceAmount}" />
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
