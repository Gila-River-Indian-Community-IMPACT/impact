<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:table id="CRPTNotes" width="98%"
	rows="5" emptyText=" " var="note"
	value="#{complianceReport.complianceReport.notes}">

	<af:column headerText="Note ID" sortable="true" width="10%"
		sortProperty="noteId">
		<af:commandLink text="#{note.noteId}" id="viewNote" useWindow="true"
			windowWidth="650" windowHeight="300"
			action="#{complianceReport.startEditNote}" shortDesc="Edit Note">
			<t:updateActionListener property="#{complianceReport.modifyNote}" value="#{note}" />
		</af:commandLink>
	</af:column>

	<af:column headerText="Note" sortable="true" width="60%" sortProperty="noteTxt">
		<af:outputText 
			truncateAt="90"	
			value="#{note.noteTxt}" 
			shortDesc="#{note.noteTxt}"/>
	</af:column>

	<af:column sortProperty="userId" sortable="true" width="10%" headerText="User Name">
		<af:selectOneChoice value="#{note.userId}" readOnly="true">
			<f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
		</af:selectOneChoice>
	</af:column>

	<af:column sortProperty="dateEntered" sortable="true" width="10%" headerText="Date">
		<af:selectInputDate readOnly="true" value="#{note.dateEntered}" />
	</af:column>

	<f:facet name="footer">
		<afh:rowLayout halign="center">
			<af:panelButtonBar>
				<af:commandButton text="Add Note" id="AddButton" useWindow="true"
					windowWidth="650" windowHeight="300"
					rendered="#{!complianceReport.readOnlyUser}"
					action="#{complianceReport.startAddNote}" />
				<af:commandButton actionListener="#{tableExporter.printTable}"
					onclick="#{tableExporter.onClickScript}" text="Printable view" />
				<af:commandButton actionListener="#{tableExporter.excelTable}"
					onclick="#{tableExporter.onClickScript}" text="Export to excel" />
			</af:panelButtonBar>
		</afh:rowLayout>
	</f:facet>
</af:table>