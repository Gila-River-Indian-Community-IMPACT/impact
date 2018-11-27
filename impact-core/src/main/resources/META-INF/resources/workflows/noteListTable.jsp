<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:showDetailHeader text="Workflow Note List" disclosed="true">
  <af:table value="#{activityProfile.processNotes}" bandingInterval="1" immediate="true"
    banding="row" var="processNotes" emptyText=" " rows="10"
    width="100%" partialTriggers="noteList:AddButton noteList:viewNote" id="noteList">

    <af:column headerText="Note ID" sortable="true" width="10%"
      sortProperty="noteId">
      <af:panelHorizontal valign="middle" halign="left">
        <af:commandLink text="#{processNotes.noteId}" id="viewNote"
          useWindow="true" windowWidth="650" windowHeight="300" immediate="true"
          action="#{activityProfile.startViewNote}">
          <t:updateActionListener property="#{activityProfile.modifyNote}"
            value="#{processNotes}" />
        </af:commandLink>
      </af:panelHorizontal>
    </af:column>

    <af:column sortProperty="note" sortable="true">
      <f:facet name="header">
        <af:outputText value="Note" />
      </f:facet>
      <af:outputText value="#{processNotes.note}" />
    </af:column>
    
    <af:column sortProperty="userId" sortable="true" width="20%">
      <f:facet name="header">
        <af:outputText value="User Name" />
      </f:facet>
      <af:selectOneChoice value="#{processNotes.userId}" readOnly="true">
        <f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
      </af:selectOneChoice>
    </af:column>

    <af:column sortProperty="postedDt" sortable="true" width="20%">
      <f:facet name="header">
        <af:outputText value="Date" />
      </f:facet>
      <af:selectInputDate readOnly="true" value="#{processNotes.postedDt}" />
    </af:column>

    <f:facet name="footer">
      <afh:rowLayout halign="center">
        <af:panelButtonBar>
          <af:commandButton text="Add" id="AddButton" useWindow="true"
            windowWidth="650" windowHeight="300" immediate="true"
            rendered="#{!activityProfile.readOnlyUser}"
            returnListener="#{activityProfile.noteDialogDone}"
            action="#{activityProfile.startAddNote}" />
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
