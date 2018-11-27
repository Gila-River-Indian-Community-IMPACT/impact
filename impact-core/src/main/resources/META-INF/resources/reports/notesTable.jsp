<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:table value="#{reportProfile.report.notes}" bandingInterval="1"
  id="NotesTab"
  banding="row" width="98%"
  rows="#{reportProfile.pageLimit}"
  var="note">
  <af:column sortProperty="noteId" sortable="true" headerText="Note ID">
    <af:commandLink text="#{note.noteId}" id="viewNote" useWindow="true"
      windowWidth="650" windowHeight="300"
      action="#{reportProfile.startEditNote}" shortDesc="Edit Note">
      <t:updateActionListener property="#{reportProfile.modifyEINote}"
        value="#{note}" />
    </af:commandLink>
  </af:column>
  <af:column sortProperty="noteTxt" sortable="true" headerText="Note">
    <af:outputText 
			truncateAt="90"	
			value="#{note.noteTxt}" 
			shortDesc="#{note.noteTxt}"/>
  </af:column>
  <af:column sortProperty="userId" sortable="true" headerText="User Name">
    <af:selectOneChoice value="#{note.userId}" readOnly="true">
      <f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
    </af:selectOneChoice>
  </af:column>
  <af:column sortProperty="dateEntered" sortable="true" headerText="Date">
    <af:selectInputDate value="#{note.dateEntered}" readOnly="true" />
  </af:column>
  <f:facet name="footer">
    <afh:rowLayout halign="center">
      <af:panelButtonBar>
        <af:commandButton text="Add" id="AddNoteButton"
          useWindow="true" windowWidth="650" windowHeight="300"
          rendered="#{!reportProfile.readOnlyUser}"
          action="#{reportProfile.startAddNote}">
        </af:commandButton>
        <af:commandButton actionListener="#{tableExporter.printTable}"
          onclick="#{tableExporter.onClickScript}" text="Printable view" />
        <af:commandButton actionListener="#{tableExporter.excelTable}"
          onclick="#{tableExporter.onClickScript}"
          text="Export to excel" />
      </af:panelButtonBar>
    </afh:rowLayout>
  </f:facet>
</af:table>