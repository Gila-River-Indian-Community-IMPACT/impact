<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:table value="#{invoiceDetail.notesWrapper}" bandingInterval="1"
  binding="#{invoiceDetail.notesWrapper.table}" id="NotesTab"
  partialTriggers="NotesTab:AddNoteButton" banding="row" 
  rows="#{reportProfile.pageLimit}" width="980" var="note">
  <af:column sortProperty="noteId" sortable="true" headerText="Note ID">
    <af:commandLink text="#{note.noteId}" id="viewNote" useWindow="true"
      windowWidth="650" windowHeight="300"
      disabled="#{invoiceDetail.editable}"
      returnListener="#{invoiceDetail.dialogDone}"
      action="#{invoiceDetail.startViewNote}">
      <t:updateActionListener property="#{invoiceDetail.modifyInvoiceNote}"
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
        <af:commandButton text="Add Note" id="AddNoteButton"
          useWindow="true" windowWidth="650" windowHeight="300"
          returnListener="#{invoiceDetail.dialogDone}"
          rendered="#{!invoiceDetail.editable && !invoiceDetail.readOnlyUser}"
          action="#{invoiceDetail.startAddNote}">
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
