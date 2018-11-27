<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:table id="STCKComments" width="98%" rows="5" emptyText=" " var="dc"
	value="#{stackTestDetail.stackTest.stackTestNotes}">

	<af:column headerText="Note ID" sortable="true" width="10%"
		sortProperty="noteId">
		<af:panelHorizontal valign="middle" halign="left">
			<af:commandLink text="#{dc.noteId}" id="viewNote" useWindow="true"
				windowWidth="650" windowHeight="300"
				action="#{stackTestDetail.startEditComment}">
				<t:updateActionListener property="#{stackTestDetail.modifyComment}"
					value="#{dc}" />
			</af:commandLink>
		</af:panelHorizontal>
	</af:column>

	<af:column headerText="Note" sortable="true" width="60%"
		sortProperty="noteTxt">
		<af:panelHorizontal valign="middle" halign="left">
			<af:outputText 
				truncateAt="90"	
				value="#{dc.noteTxt}" 
				shortDesc="#{dc.noteTxt}"/>
		</af:panelHorizontal>
	</af:column>

	<af:column sortProperty="userId" sortable="true" width="10%"
		headerText="User Name">
		<af:panelHorizontal valign="middle" halign="left">
			<af:selectOneChoice value="#{dc.userId}" readOnly="true">
				<f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
			</af:selectOneChoice>
		</af:panelHorizontal>
	</af:column>

	<af:column sortProperty="dateEntered" sortable="true" width="10%"
		headerText="Date">
		<af:panelHorizontal valign="middle" halign="left">
			<af:selectInputDate readOnly="true" value="#{dc.dateEntered}" />
		</af:panelHorizontal>
	</af:column>

	<f:facet name="footer">
		<afh:rowLayout halign="center">
			<af:panelButtonBar>
				<af:commandButton text="Add Note" id="AddButton" useWindow="true"
					windowWidth="650" windowHeight="300"
					rendered="#{!stackTestDetail.readOnlyUser}"
					action="#{stackTestDetail.startAddComment}" />
				<af:commandButton actionListener="#{tableExporter.printTable}"
					onclick="#{tableExporter.onClickScript}" text="Printable view" />
				<af:commandButton actionListener="#{tableExporter.excelTable}"
					onclick="#{tableExporter.onClickScript}" text="Export to excel" />
			</af:panelButtonBar>
		</afh:rowLayout>
	</f:facet>
</af:table>