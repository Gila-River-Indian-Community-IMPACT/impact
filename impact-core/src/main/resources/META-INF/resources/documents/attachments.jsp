
<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<afh:rowLayout halign="center" rendered="true">
	<af:table emptyText="No attachments " value="#{attachments.documents}"
		var="attachment">

		<af:column sortable="false" headerText="">
			<f:facet name="header">
				<af:outputText value="Document ID" />
			</f:facet>
			<af:goLink destination="#{attachment.docURL}"
				text="#{attachment.documentID}" />
		</af:column>
		<af:column sortable="false" headerText="">
			<f:facet name="header">
				<af:outputText value="Description" />
			</f:facet>
			<af:outputText value="#{attachment.description}" />
		</af:column>
		<af:column sortable="false" headerText="">
			<f:facet name="header">
				<af:outputText value="Path" />
			</f:facet>
			<af:outputText value="#{attachment.path}" />
		</af:column>
		<af:column sortable="false" headerText="">
			<f:facet name="header">
				<af:outputText value="Last Modified" />
			</f:facet>
			<af:outputText value="#{attachment.lastModifiedTS}" />
		</af:column>
		<af:column sortable="false" headerText="">
			<f:facet name="header">
				<af:outputText value="Owner/Modified By" />
			</f:facet>
			<af:outputText value="#{attachment.lastModifiedUserName}" />
		</af:column>
		<f:facet name="footer">
			<af:panelButtonBar>
				<af:commandButton text="Add Attachment" id="AddDocButton"
					useWindow="true" windowWidth="500" windowHeight="300"
					returnListener="#{applicationDetail.docDialogDone}"
					action="#{applicationDetail.startAddDoc}"
					rendered="#{attachments.newPermitted}" />
			</af:panelButtonBar>
		</f:facet>
	</af:table>
</afh:rowLayout>
