<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="New Attachment">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:messages />
			<af:panelForm maxColumns="2" width="600" labelWidth="160">
				<af:selectOneChoice id="attachmentType" label="Attachment Type :"
					showRequired="true" unselectedLabel=" "
					value="#{projectTrackingDetail.projectAttachment.attachment.docTypeCd}" >
					<f:selectItems
						value="#{projectTrackingDetail.projectAttachmentTypeDefs.items[
									(empty projectTrackingDetail.projectAttachment.attachment.docTypeCd 
										? '' : projectTrackingDetail.projectAttachment.attachment.docTypeCd)]}" />
				</af:selectOneChoice>

				<af:inputText id="descriptionText" label="Description :"
					showRequired="true" maximumLength="500"
					value="#{projectTrackingDetail.projectAttachment.attachment.description}" />

				<af:selectOneChoice id="trackingEventId" label="Tracking Event :"
					unselectedLabel=" "
					rendered="#{projectTrackingDetail.project.hasEvent}"
					value="#{projectTrackingDetail.projectAttachment.trackingEventId}">
					<f:selectItems
						value="#{projectTrackingDetail.projectTrackingEventDefs}" />
				</af:selectOneChoice>


				<af:panelForm maxColumns="2" width="600" labelWidth="160">
					<af:inputFile id="publicFile"
						rendered="#{attachments.newAttachment}"
						label="#{attachments.tradeSecretSupported ? 'Public File to Upload : ' : 'File to Upload : '}"
						showRequired="true" value="#{attachments.fileToUpload}" />
					<af:inputText id="publicFileName"
						label="#{attachments.tradeSecretSupported ? 'Public File to Upload : ' : 'File to Upload : '}"
						rendered="#{attachments.publicAttachmentInfo != null}"
						value="#{attachments.publicAttachmentInfo.fileName}"
						readOnly="true" />
				</af:panelForm>

				<f:facet name="footer">
					<af:panelButtonBar>
						<af:commandButton text="Apply"
							disabled="#{projectTrackingDetail.readOnlyUser}"
							rendered="#{attachments.newAttachment}"
							actionListener="#{attachments.createAttachment}" />
						<af:commandButton text="Save"
							disabled="#{projectTrackingDetail.readOnlyUser}"
							rendered="#{!attachments.newAttachment}"
							actionListener="#{attachments.updateAttachment}" />
						<af:commandButton text="Delete"
							disabled="#{projectTrackingDetail.readOnlyUser}"
							rendered="#{attachments.deletePermitted && !attachments.newAttachment}"
							useWindow="true" 
							windowHeight="#{confirmWindow.height}" windowWidth="#{confirmWindow.width}"
							returnListener="#{projectTrackingDetail.deleteAttachment}" 
							action="#{confirmWindow.confirm}" >
							<t:updateActionListener property="#{confirmWindow.type}"
								value="#{confirmWindow.yesNo}" />
							<t:updateActionListener property="#{confirmWindow.message}"
								value="The attachment will be deleted. Would you like to continue?" />
						</af:commandButton>
						<af:commandButton text="Cancel" immediate="true"
							actionListener="#{attachments.cancelAttachment}" />
					</af:panelButtonBar>
				</f:facet>
			</af:panelForm>
		</af:form>
	</af:document>
</f:view>
