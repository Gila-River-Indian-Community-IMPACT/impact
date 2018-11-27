<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" title="Printable Project Tracking Documents">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:messages />
			

				<af:objectSpacer width="100%" height="15" />

				<af:outputText value="Project Tracking Documents" />

				<af:table id="printableDocumentsTable"
					value="#{projectTrackingDetail.documents}" bandingInterval="1"
					width="100%" banding="row" var="printableDocument">
					<af:column sortable="true" formatType="text"
						headerText="Document Description">
						<af:goLink id="documentLink"
							text="#{printableDocument.description}"
							destination="#{printableDocument.docURL}" targetFrame="_blank" />
					</af:column>
				</af:table>
				<af:objectSpacer width="100%" height="15" />

				<af:outputText value="Attachments that are part of the Project:"
					rendered="#{projectTrackingDetail.project.hasAttachments}" />
					
				<af:outputText
					value="Note: No attachments were uploaded for this Project. " 
					rendered="#{!projectTrackingDetail.project.hasAttachments}" />
					
				<af:objectSpacer width="100%" height="15" />

				<af:table id="pribtableAttachmentsTable"
					rendered="#{projectTrackingDetail.project.hasAttachments}"
					value="#{projectTrackingDetail.project.attachments}" bandingInterval="1"
					width="100%" banding="row" var="printableDocument">
					<af:column sortable="true" formatType="text"
						headerText="Document Description">
						<af:goLink id="documentLink"
							text="#{printableDocument.description}"
							destination="#{printableDocument.attachment.docURL}" targetFrame="_blank" />
					</af:column>
				</af:table>
				<af:outputText
					value="Select a link in an above table to view or download a 
        document to print." rendered="#{projectTrackingDetail.project.hasAttachments}" />
        
        		<af:outputText
					value="Select a link in the above table to view or download a 
        document to print." rendered="#{!projectTrackingDetail.project.hasAttachments}" />

				<af:objectSpacer width="100%" height="20" />

			

			<afh:rowLayout halign="center">
				<af:panelButtonBar>
					<af:commandButton text="Close" immediate="true">
						<af:returnActionListener />
					</af:commandButton>
				</af:panelButtonBar>
			</afh:rowLayout>
		</af:form>
	</af:document>
</f:view>
