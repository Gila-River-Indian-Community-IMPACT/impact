<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:table value="#{monitorSiteDetail.notesWrapper}" bandingInterval="1"
	binding="#{monitorSiteDetail.notesWrapper.table}" id="NotesTab"
	banding="row" width="98%" var="note"
	rows="#{monitorSiteDetail.pageLimit}">
	<af:column sortProperty="c01" sortable="true" formatType="text"
		headerText="Note ID">
		<af:commandLink text="#{note.noteId}" id="viewNote" useWindow="true"
			windowWidth="650" windowHeight="300" disabled="false"
			returnListener="#{monitorSiteDetail.dialogDone}"
			action="#{monitorSiteDetail.startViewNote}">
			<t:updateActionListener property="#{monitorSiteDetail.modifyNote}"
				value="#{note}" />
		</af:commandLink>
	</af:column>
	<af:column sortProperty="c02" sortable="true" formatType="text"
		headerText="Note">
		<af:outputText 
			truncateAt="90"	
			value="#{note.noteTxt}" 
			shortDesc="#{note.noteTxt}"/>
	</af:column>
	<af:column sortProperty="c03" sortable="true" formatType="text"
		headerText="User Name">
		<af:selectOneChoice value="#{note.userId}" readOnly="true">
			<f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
		</af:selectOneChoice>
	</af:column>
	<af:column sortProperty="c04" sortable="true" formatType="text"
		headerText="Date">
		<af:selectInputDate value="#{note.dateEntered}" readOnly="true" />
	</af:column>
	<f:facet name="footer">
		<afh:rowLayout halign="center">
			<af:panelButtonBar>
				<af:commandButton text="Add Note" id="AddNoteButton"
					useWindow="true" windowWidth="650" windowHeight="300"
					disabled="false" returnListener="#{monitorSiteDetail.dialogDone}"
					rendered="#{!monitorSiteDetail.readOnlyUser}" action="#{monitorSiteDetail.startAddNote}">
				</af:commandButton>
				<af:commandButton actionListener="#{tableExporter.printTable}"
					onclick="#{tableExporter.onClickScript}" text="Printable view" />
				<af:commandButton actionListener="#{tableExporter.excelTable}"
					onclick="#{tableExporter.onClickScript}" text="Export to excel" />
			</af:panelButtonBar>
		</afh:rowLayout>
	</f:facet>
</af:table>