<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>

<af:showDetailHeader text="Notes" disclosed="true" >
	<afh:rowLayout halign="center" width="70%" >
		<af:table id="projectNotes" width="98%" emptyText=" "
			var="note"
			value="#{projectTrackingDetail.project.notes}">

			<af:column headerText="Note ID" sortable="true" width="10%"
				sortProperty="noteId">
				<af:commandLink id="viewNote" 
					text="#{note.noteId}"  useWindow="true"
					windowWidth="650" windowHeight="300" 
					action="#{projectTrackingDetail.startToViewNote}">
					<t:updateActionListener
						property="#{projectTrackingDetail.projectNote}" value="#{note}" />
				</af:commandLink>
			</af:column>

			<af:column headerText="Note" sortable="true" width="65%"
				sortProperty="noteTxt">
				<af:outputText truncateAt="90" value="#{note.noteTxt}"
					shortDesc="#{note.noteTxt}" />
			</af:column>

			<af:column sortProperty="userId" sortable="true" width="15%"
				headerText="User Name">
				<af:selectOneChoice value="#{note.userId}" readOnly="true">
					<f:selectItems value="#{infraDefs.allUsersDef.allItems}" />
				</af:selectOneChoice>
			</af:column>

			<af:column sortProperty="dateEntered" sortable="true" width="10%"
				headerText="Date">
				<af:selectInputDate readOnly="true" value="#{note.dateEntered}" />
			</af:column>

			<f:facet name="footer">
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton id="addBtn"
							disabled="#{projectTrackingDetail.readOnlyUser}"
							text="Add Note"  
							useWindow="true"
							windowWidth="650" windowHeight="300" 
							action="#{projectTrackingDetail.startToAddNote}" />
						<af:commandButton actionListener="#{tableExporter.printTable}"
							onclick="#{tableExporter.onClickScript}" text="Printable view" />
						<af:commandButton actionListener="#{tableExporter.excelTable}"
							onclick="#{tableExporter.onClickScript}" text="Export to excel" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</f:facet>
		</af:table>
	</afh:rowLayout>
</af:showDetailHeader>