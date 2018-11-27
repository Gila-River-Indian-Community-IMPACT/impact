<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:table id="continuousMonitorNotesTable" emptyText=" " var="cmNote"
	width="100%" value="#{continuousMonitorDetail.continuousMonitor.notes}">
	<af:column headerText="Note ID">
		<af:commandLink id="noteIdLink" text="#{cmNote.noteId}"
			disabled="#{!continuousMonitorDetail.cetaUpdate}" useWindow="true"
			windowWidth="650" windowHeight="300"
			returnListener="#{continuousMonitorDetail.noteDialogDone}"
			action="#{continuousMonitorDetail.startEditNote}"
			shortDesc="Edit Note" />
	</af:column>
	<af:column headerText="Note">
		<af:outputText 
				truncateAt="90"	
				value="#{cmNote.noteTxt}" 
				shortDesc="#{cmNote.noteTxt}"/>
	</af:column>
	<af:column headerText="User Name">
		<af:selectOneChoice value="#{cmNote.userId}" readOnly="true">
			<f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
		</af:selectOneChoice>
	</af:column>
	<af:column headerText="Date">
		<af:selectInputDate readOnly="true" value="#{cmNote.dateEntered}" />
	</af:column>

	<f:facet name="footer">
		<afh:rowLayout halign="center">
			<af:panelButtonBar>
				<af:commandButton text="Add"
					disabled="#{!continuousMonitorDetail.cetaUpdate || !continuousMonitorDetail.continuousMonitorEditAllowed || facilityProfile.disabledUpdateButton}"
					id="AddCommentButton" useWindow="true" windowWidth="650"
					windowHeight="300"
					returnListener="#{continuousMonitorDetail.noteDialogDone}"
					action="#{continuousMonitorDetail.startAddNote}" />
				<af:commandButton actionListener="#{tableExporter.printTable}"
					onclick="#{tableExporter.onClickScript}" text="Printable view" />
				<af:commandButton actionListener="#{tableExporter.excelTable}"
					onclick="#{tableExporter.onClickScript}" text="Export to excel" />
			</af:panelButtonBar>
		</afh:rowLayout>
	</f:facet>
</af:table>