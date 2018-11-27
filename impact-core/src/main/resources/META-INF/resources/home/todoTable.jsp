<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:table bandingInterval="1" banding="row" var="todos" emptyText=' '
  rows="#{toDoSearch.pageLimit}" width="99%"
  value="#{toDoSearch.shortActivities}">

  <af:column sortProperty="loopCnt" sortable="true"
    headerText="Task ID">
    <af:panelHorizontal valign="middle" halign="left">
      <af:commandLink action="#{activityProfile.submitProfile}">
        <af:outputText
          inlineStyle="#{todos.viewed ? 'font-weight:regular;' : 'font-weight:bold;'}"
          value="#{todos.processId}-#{todos.activityTemplateId}-#{todos.loopCnt}" />
        <t:updateActionListener
          property="#{menuItem_activityProfile.disabled}" value="false" />
        <t:updateActionListener property="#{activityProfile.processId}"
          value="#{todos.processId}" />
        <t:updateActionListener property="#{workFlow2DDraw.processId}"
          value="#{todos.processId}" />
        <t:updateActionListener property="#{activityProfile.loopCnt}"
          value="#{todos.loopCnt}" />
        <t:updateActionListener
          property="#{activityProfile.activityTemplateId}"
          value="#{todos.activityTemplateId}" />
        <t:updateActionListener
          property="#{activityProfile.fromExternal}"
          value="#{toDoSearch.fromExternal}" />
        <t:updateActionListener property="#{activityProfile.aggregate}"
          value="#{todos.aggregate}" />
        <t:updateActionListener
          property="#{menuItem_workflowProfile.disabled}" value="false" />
      </af:commandLink>
    </af:panelHorizontal>
  </af:column>

  <af:column sortProperty="facilityId" sortable="true" noWrap="true"
    rendered="#{toDoSearch.showFacility}" headerText="Facility ID">
    <af:panelHorizontal valign="middle" halign="left">
      <af:commandLink text="#{todos.facilityId}"
        action="#{facilityProfile.submitProfile}"
        inlineStyle="white-space: nowrap;">
        <t:updateActionListener property="#{facilityProfile.fpId}"
          value="#{todos.fpId}" />
        <t:updateActionListener
          property="#{menuItem_facProfile.disabled}" value="false" />
      </af:commandLink>
    </af:panelHorizontal>
  </af:column>

  <af:column sortProperty="facilityNm" sortable="true"
    rendered="#{toDoSearch.showFacility}" headerText="Facility Name">
    <af:panelHorizontal valign="middle" halign="left">
      <af:inputText readOnly="true" value="#{todos.facilityNm}" />
    </af:panelHorizontal>
  </af:column>

  <af:column headerText="Task" bandingShade="light">

    <af:column sortProperty="activityTemplateNm" sortable="true"
      headerText="Name">
      <af:panelHorizontal valign="middle" halign="left">
        <af:commandLink action="#{activityProfile.submitActProfile}">
          <af:outputText
            inlineStyle="#{todos.viewed ? 'font-weight:regular;' : 'font-weight:bold;'}"
            value="#{todos.activityTemplateNm}" />
          <t:updateActionListener
            property="#{menuItem_activityProfile.disabled}"
            value="false" />
          <t:updateActionListener
            property="#{activityProfile.processId}"
            value="#{todos.processId}" />
          <t:updateActionListener property="#{workFlow2DDraw.processId}"
            value="#{todos.processId}" />
          <t:updateActionListener property="#{activityProfile.loopCnt}"
            value="#{todos.loopCnt}" />
          <t:updateActionListener
            property="#{activityProfile.activityTemplateId}"
            value="#{todos.activityTemplateId}" />
          <t:updateActionListener
            property="#{activityProfile.fromExternal}"
            value="#{toDoSearch.fromExternal}" />
          <t:updateActionListener
            property="#{activityProfile.aggregate}"
            value="#{todos.aggregate}" />
          <t:updateActionListener
            property="#{menuItem_workflowProfile.disabled}"
            value="false" />
        </af:commandLink>
      </af:panelHorizontal>
    </af:column>

    <af:column sortProperty="userName" headerText="Staff Assigned"
      sortable="true">
      <af:panelHorizontal valign="middle" halign="left">
        <af:inputText readOnly="true" value="#{todos.userName}"
          inlineStyle="#{todos.viewed ? 'font-weight:regular;' : 'font-weight:bold;'}" />
      </af:panelHorizontal>
    </af:column>

    <af:column sortProperty="activityStatusDesc" sortable="true"
      headerText="State">
      <af:panelHorizontal valign="middle" halign="left">
        <af:inputText readOnly="true"
          value="#{todos.activityStatusDesc}"
          inlineStyle="#{todos.viewed ? 'font-weight:regular;' : 'font-weight:bold;'}" />
      </af:panelHorizontal>
    </af:column>

    <af:column sortProperty="startDt" sortable="true"
      headerText="Start Date">
      <af:panelHorizontal valign="middle" halign="left">
        <af:selectInputDate value="#{todos.startDt}" readOnly="true"
          inlineStyle="#{todos.viewed ? 'font-weight:regular;' : 'font-weight:bold;'}" />
      </af:panelHorizontal>
    </af:column>

    <af:column sortProperty="duration" sortable="true" headerText="Days">
      <af:panelHorizontal valign="middle" halign="left">
        <af:inputText readOnly="true" value="#{todos.duration}"
          inlineStyle="#{todos.viewed ? 'font-weight:regular;' : 'font-weight:bold;'}" />
      </af:panelHorizontal>
    </af:column>

    <af:column sortProperty="endDt" sortable="true"
      headerText="End Date">
      <af:panelHorizontal valign="middle" halign="left">
        <af:selectInputDate value="#{todos.endDt}" readOnly="true"
          inlineStyle="#{todos.viewed ? 'font-weight:regular;' : 'font-weight:bold;'}" />
      </af:panelHorizontal>
    </af:column>
  </af:column>

  <af:column headerText="Workflow">

    <af:column sortProperty="processTemplateNm" sortable="true"
      headerText="Name">
      <af:panelHorizontal valign="middle" halign="left">
        <af:commandLink action="#{workFlow2DDraw.submitProfile}">
          <af:outputText
            value="#{todos.processTemplateNm} #{todos.permitTypeDesc} #{todos.permitNumber}" />
          <t:updateActionListener property="#{workFlow2DDraw.processId}"
            value="#{todos.processId}" />
          <t:updateActionListener
            property="#{menuItem_workflowProfile.disabled}"
            value="false" />
        </af:commandLink>
      </af:panelHorizontal>
    </af:column>

    <af:column sortProperty="status" sortable="true" headerText="Status">
      <af:panelHorizontal valign="middle" halign="left">
        <h:panelGrid border="0" width="100%" align="left"
          bgcolor="#{todos.statusColor}">
          <af:inputText readOnly="true" value="#{todos.status}"
            inlineStyle="#{todos.viewed ? 'font-weight:regular;' : 'font-weight:bold;'}" />
        </h:panelGrid>
      </af:panelHorizontal>
    </af:column>

    <af:column sortProperty="processStartDt" sortable="true"
      headerText="Start Date">
      <af:panelHorizontal valign="middle" halign="left">
        <af:selectInputDate value="#{todos.processStartDt}"
          readOnly="true" />
      </af:panelHorizontal>
    </af:column>

    <af:column sortProperty="processDuration" sortable="true"
      headerText="Days">
      <af:panelHorizontal valign="middle" halign="left">
        <af:inputText readOnly="true" value="#{todos.processDuration}" />
      </af:panelHorizontal>
    </af:column>

    <af:column sortProperty="processDueDt" sortable="true"
      headerText="Due Date">
      <af:panelHorizontal valign="middle" halign="left">
        <af:selectInputDate value="#{todos.processDueDt}"
          readOnly="true" />
      </af:panelHorizontal>
    </af:column>
  </af:column>

  <f:facet name="footer">
    <afh:rowLayout halign="center">
      <af:panelButtonBar>
        <af:commandButton actionListener="#{tableExporter.printTable}"
          onclick="#{tableExporter.onClickScript}" text="Printable view" />
        <af:commandButton actionListener="#{tableExporter.excelTable}"
          onclick="#{tableExporter.onClickScript}"
          text="Export to excel" />
      </af:panelButtonBar>
    </afh:rowLayout>
  </f:facet>
</af:table>


