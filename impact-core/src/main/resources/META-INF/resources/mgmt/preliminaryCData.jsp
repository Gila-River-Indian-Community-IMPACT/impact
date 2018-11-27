<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<afh:rowLayout halign="center">
  <h:panelGrid border="1" width="1100"
    rendered="#{prelimCompleted.hasSearchResults}">
    <af:panelBorder>
      <af:showDetailHeader text="Preliminary Completed Report"
        disclosed="true">
        <af:table bandingInterval="1" banding="row" var="details"
          emptyText='No Issued Permits'
          rows="#{prelimCompleted.pageLimit}" width="99%"
          value="#{prelimCompleted.details}">

          <af:column sortProperty="doLaaShortDsc" sortable="true"
            headerText="District">
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true" value="#{details.doLaaShortDsc}" />
            </af:panelHorizontal>
          </af:column>

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

          <af:column sortProperty="permitType" sortable="true"
            headerText="Permit Type">
            <af:panelHorizontal valign="middle" halign="left">
              <af:selectOneChoice unselectedLabel=" " readOnly="true"
                value="#{details.permitType}">
                <mu:selectItems value="#{permitReference.permitTypes}" />
              </af:selectOneChoice>
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="reason" sortable="true"
            headerText="Permit Reason">
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true" value="#{details.reason}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="issueDraft" sortable="true"
            headerText="Issue Draft">
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true"
                value="#{details.issueDraft == 'Y' ? 'Yes': 'No'}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="general" sortable="true"
            headerText="General">
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true"
                value="#{details.general == 'Y' ? 'Yes': 'No'}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="rush" sortable="true"
            headerText="Rush">
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true"
                value="#{details.rush == 'Y' ? 'Yes': 'No'}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="reviewerName" sortable="true"
            headerText="District Technical Reviewer">
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true"
                value="#{details.reviewerName}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="userName" sortable="true"
            headerText="Current Task Assignment">
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true" value="#{details.userName}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="activityName" sortable="true"
            headerText="Current Task Name">
            <af:panelHorizontal valign="middle" halign="left">
              <af:selectInputDate value="#{details.activityName}"
                readOnly="true" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="activityStatusDesc" sortable="true"
            headerText="Task State">
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true"
                value="#{details.activityStatusDesc}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="activityDuration" sortable="true"
            headerText="Task Days">
            <af:panelHorizontal valign="middle" halign="center">
              <af:inputText readOnly="true"
                value="#{details.activityDuration}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="processId" sortable="true"
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

          <af:column sortProperty="startDt" sortable="true"
            headerText="Workflow Start Date">
            <af:panelHorizontal valign="middle" halign="center">
              <af:selectInputDate value="#{details.startDt}"
                readOnly="true" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="days" sortable="true"
            headerText="Total Preliminary Days">
            <af:panelHorizontal valign="middle" halign="center">
              <af:inputText readOnly="true" value="#{details.days}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="epaDays" sortable="true"
            headerText="District/CO Days">
            <af:panelHorizontal valign="middle" halign="center">
              <af:inputText readOnly="true" value="#{details.epaDays}" />
            </af:panelHorizontal>
          </af:column>

          <f:facet name="footer">
            <afh:rowLayout halign="center">
              <af:panelButtonBar>
                <af:commandButton
                  text="#{prelimCompleted.hideShowTitle}"
                  action="#{prelimCompleted.submitChart}" />
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

