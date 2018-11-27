<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<afh:rowLayout halign="center">
  <h:panelGrid border="1" width="98%"
    rendered="#{latePermits.hasSearchResults}">
    <af:panelBorder>
      <af:showDetailHeader text="Late Permits Report" disclosed="true">
        <af:table bandingInterval="1" banding="row" var="details"
          emptyText='No Issued Permits' rows="#{latePermits.pageLimit}"
          width="99%" value="#{latePermits.details}">

          <af:column sortProperty="facilityId" sortable="true"
            noWrap="true" formatType="text" headerText="Facility ID">
            <af:commandLink action="#{facilityProfile.submitProfile}"
              text="#{details.facilityId}">
              <t:updateActionListener property="#{facilityProfile.fpId}"
                value="#{details.fpId}" />
              <t:updateActionListener
                property="#{menuItem_facProfile.disabled}" value="false" />
            </af:commandLink>
          </af:column>

          <af:column sortProperty="facilityNm" sortable="true"
            headerText="Facility Name">
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true"
                value="#{details.facilityNm}" />
            </af:panelHorizontal>
          </af:column>

          <af:column headerText="Permit Number" sortable="true"
            sortProperty="permitNumber" formatType="text">
            <af:panelHorizontal valign="middle" halign="left">
              <af:commandLink text="#{details.permitNumber}"
                action="#{permitDetail.loadPermit}">
                <af:setActionListener to="#{permitDetail.permitID}"
                  from="#{details.permitId}" />
                <t:updateActionListener
                  property="#{permitDetail.fromTODOList}" value="false" />
              </af:commandLink>
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="processId" sortable="true" rendered="false"
            headerText="Workflow ID">
            <af:panelHorizontal valign="middle" halign="center">
              <af:commandLink action="#{workFlow2DDraw.submitProfile}">
                <af:outputText value="#{details.processId}" />
                <t:updateActionListener
                  property="#{workFlow2DDraw.processId}"
                  value="#{details.processId}" />
                <t:updateActionListener
                  property="#{menuItem_workflowProfile.disabled}"
                  value="false" />
              </af:commandLink>
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="days" sortable="true"
            headerText="Total Days">
            <af:panelHorizontal valign="middle" halign="center">
              <af:inputText readOnly="true" value="#{details.days}" />
            </af:panelHorizontal>
          </af:column>
          
          <af:column sortProperty="referredDays" sortable="true">
            <f:facet name="header">
              <af:outputText value="Referred Days" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="center">
              <af:inputText readOnly="true"
                value="#{details.referredDays}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="activityName" sortable="true"
            headerText="Current Task">
            <af:panelHorizontal valign="middle" halign="left">
              <af:selectInputDate value="#{details.activityName}"
                readOnly="true" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="startDt" sortable="true"
            headerText="Received Date">
            <af:panelHorizontal valign="middle" halign="center">
              <af:selectInputDate value="#{details.startDt}"
                readOnly="true" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="permitType" sortable="true"
            headerText="Permit Type">
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true"
                value="#{details.permitType}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="general" sortable="true"
            headerText="General">
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true"
                value="#{details.general == 'Y' ? 'Yes': 'No'}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="issueDraft" sortable="true"
            headerText="Issue Draft">
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true"
                value="#{details.issueDraft == 'Y' ? 'Yes': 'No'}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="express" sortable="true"
            headerText="Express">
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true"
                value="#{details.express == 'Y' ? 'Yes': 'No'}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="reason" sortable="true"
            headerText="Reason">
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true" value="#{details.reason}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="rush" sortable="true"
            headerText="Rush">
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true"
                value="#{details.rush == 'Y' ? 'Yes': 'No'}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="statusStr" sortable="true"
            headerText="Permit Status">
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true" value="#{details.statusStr}" />
            </af:panelHorizontal>
          </af:column>

          <f:facet name="footer">
            <afh:rowLayout halign="center">
              <af:panelButtonBar>
                <af:commandButton text="#{latePermits.hideShowTitle}"
                  action="#{latePermits.submitChart}" />
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

