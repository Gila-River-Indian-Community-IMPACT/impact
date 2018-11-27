<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:showDetailHeader text="Task List" disclosed="true">
  <af:table bandingInterval="1" banding="row" var="activities"
    emptyText=" " rows="50" width="98%"
    value="#{reassignActivities.activities}">
    <af:column sortProperty="processId" sortable="true">
      <f:facet name="header">
        <af:outputText value="Process Id" />
      </f:facet>
      <af:outputText value="#{activities.processId}" />
    </af:column>

    <af:column sortProperty="activityTemplateNm" sortable="true">
      <f:facet name="header">
        <af:outputText value="Task Name" />
      </f:facet>
      <af:outputText value="#{activities.activityTemplateNm}" />
    </af:column>

    <af:column sortProperty="activityStatusCd" sortable="true">
      <f:facet name="header">
        <af:outputText value="Task Status" />
      </f:facet>
      <af:selectOneChoice value="#{activities.activityStatusCd}"
        readOnly="true">
        <f:selectItems value="#{workFlowDefs.activityStatus}" />
      </af:selectOneChoice>
    </af:column>

    <af:column sortProperty="userId" sortable="true">
      <f:facet name="header">
        <af:outputText value="Staff" />
      </f:facet>
      <af:selectOneChoice value="#{activities.userId}" immediate="true"
        readOnly="#{!(activities.activityStatusCd == 'IP' || activities.activityStatusCd == 'ND' || activities.activityStatusCd == 'RF')}">
        <f:selectItems value="#{infraDefs.basicUsersDef.items[(empty activities.userId?0:activities.userId)]}" />
      </af:selectOneChoice>
    </af:column>

    <f:facet name="footer">
      <afh:rowLayout halign="center">
        <af:panelButtonBar>
          <af:commandButton text="Apply"
            rendered="#{!reassignActivities.readOnlyUser}"
            action="#{reassignActivities.apply}" />
          <af:commandButton actionListener="#{tableExporter.printTable}"
            onclick="#{tableExporter.onClickScript}"
            text="Printable view" />
          <af:commandButton actionListener="#{tableExporter.excelTable}"
            onclick="#{tableExporter.onClickScript}"
            text="Export to excel" />
        </af:panelButtonBar>
      </afh:rowLayout>
    </f:facet>

  </af:table>
</af:showDetailHeader>
