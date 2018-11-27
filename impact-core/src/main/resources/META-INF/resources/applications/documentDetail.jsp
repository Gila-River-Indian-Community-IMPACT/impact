<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<f:view>
	<af:document id="body" title="Application Attachment">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:messages />
			<af:panelForm maxColumns="2" width="600" labelWidth="150"
				partialTriggers="docTypeChoice attachmentWarningMsg tvDocTypeChoice eacFormTypeChoice tradeSecretFile">
				<af:outputText id="attachmentWarningMsg" 
					rendered="#{(not empty applicationDetail.applicationDoc.applicationDocumentTypeCD) && 
								!applicationDetail.tradeSecretAllowed &&
								applicationDetail.publicFileInfo == null && 
	            				applicationDetail.applicationDoc.documentId == null &&  
	            				!applicationDetail.docUpdate}" 
					value="Do not attach any facility claimed confidential attachments" inlineStyle="color: orange; font-weight: bold;"  />
				<af:selectOneChoice label="Attachment Type :" id="docTypeChoice"
					unselectedLabel="Please select" autoSubmit="true" immediate="true"
					rendered="#{!applicationDetail.pbrRprRpe && applicationDetail.applicationType != 'TV'}"
					value="#{applicationDetail.applicationDoc.applicationDocumentTypeCD}"
					readOnly="#{!applicationDetail.okToEditAttachment || applicationDetail.docUpdate
            || applicationDetail.usingExistingDoc}">
					<f:selectItems
						value="#{applicationReference.applicationDocTypeDefs.items[(empty applicationDetail.applicationDoc.applicationDocumentTypeCD? '': applicationDetail.applicationDoc.applicationDocumentTypeCD)]}" />
				</af:selectOneChoice>

				<af:selectOneChoice label="Attachment Type :" id="tvDocTypeChoice"
					unselectedLabel="Please select" autoSubmit="true" immediate="true"
					rendered="#{!applicationDetail.pbrRprRpe && applicationDetail.applicationType == 'TV'}"
					value="#{applicationDetail.applicationDoc.applicationDocumentTypeCD}"
					readOnly="#{!applicationDetail.okToEditAttachment || applicationDetail.docUpdate
            || applicationDetail.usingExistingDoc}">
					<f:selectItems
						value="#{applicationReference.tvApplicationDocTypeDefs.items[(empty applicationDetail.applicationDoc.applicationDocumentTypeCD? '': applicationDetail.applicationDoc.applicationDocumentTypeCD)]}" />
				</af:selectOneChoice>

				<af:panelLabelAndMessage label="Existing Attachment(s) : "
					for="selectAttachmentTable" valign="middle"
					rendered="#{applicationDetail.renderSelectAttachmentTable && !applicationDetail.docUpdate}"
					tip="You may select an existing attachment from the table above instead of uploading a new file.">
					<af:table id="selectAttachmentTable" emptyText=" " var="attachment"
						value="#{applicationDetail.availableAttachments}"
						rendered="#{applicationDetail.renderSelectAttachmentTable}">
						<f:facet name="selection">
							<af:tableSelectOne id="selectAttachmentTableSelection"
								rendered="#{!applicationDetail.usingExistingDoc}" />
						</f:facet>
						<af:column headerText="Attachment Type"
							rendered="#{!applicationDetail.pbrRprRpe}">
							<af:outputText
								rendered="#{applicationDetail.applicationType != 'TV'}"
								value="#{applicationReference.applicationDocTypeDefs.itemDesc[attachment.applicationDocumentTypeCD]}" />
							<af:outputText
								rendered="#{applicationDetail.applicationType == 'TV'}"
								value="#{applicationReference.tvApplicationDocTypeDefs.itemDesc[attachment.applicationDocumentTypeCD]}" />

						</af:column>
						<af:column headerText="Description">
							<af:outputText id="descriptionText"
								value="#{attachment.description}" truncateAt="80"
								shortDesc="#{attachment.description}" />
						</af:column>
						<af:column headerText="Modified By">
							<af:selectOneChoice readOnly="true"
								value="#{attachment.lastModifiedBy}">
								<f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
							</af:selectOneChoice>
						</af:column>
						<af:column headerText="Last Modified">
							<af:outputText id="lastModifiedText"
								value="#{attachment.lastModifiedTS}" />
						</af:column>
						<f:facet name="footer">
							<afh:rowLayout halign="center">
								<af:panelButtonBar>
									<af:commandButton text="Use Selected Attachment"
										id="selectAttachmentButton"
										actionListener="#{applicationDetail.initActionTable}"
										action="#{applicationDetail.loadSelectedAttachment}"
										disabled="#{applicationDetail.usingExistingDoc}" />
								</af:panelButtonBar>
							</afh:rowLayout>
						</f:facet>
					</af:table>
				</af:panelLabelAndMessage>
				<af:inputText id="descriptionText" label="Description :"
					value="#{applicationDetail.applicationDoc.description}"
					maximumLength="500"
					readOnly="#{!applicationDetail.okToEditAttachment || applicationDetail.usingExistingDoc}"
					showRequired="true" />

				<af:panelForm maxColumns="2" width="600" labelWidth="150">
					<af:inputFile
						label="#{applicationDetail.pbrRprRpe ? 'File to Upload : ' : 'Public File to Upload : '}"
						id="publicFile"
						rendered="#{applicationDetail.publicFileInfo == null && 
	            applicationDetail.applicationDoc.documentId == null &&  
	            !applicationDetail.docUpdate}"
						value="#{applicationDetail.fileToUpload}" />
					<af:inputText id="publicFileName"
						label="#{applicationDetail.pbrRprRpe ? 'File to Upload : ' : 'Public File to Upload : '}"
						rendered="#{applicationDetail.publicFileInfo != null}"
						value="#{applicationDetail.publicFileInfo.fileName}"
						readOnly="true" />
				</af:panelForm>
				<af:panelForm maxColumns="2" width="600" labelWidth="150"
					rendered="#{!empty applicationDetail.applicationDoc.applicationDocumentTypeCD}">
					<af:inputFile id="tradeSecretFile"
						label="Trade Secret File to Upload :"
						rendered="#{applicationDetail.tradeSecretVisible && !applicationDetail.pbrRprRpe &&
	            applicationDetail.tsFileInfo == null && applicationDetail.applicationDoc.tradeSecretDocId == null &&
	            !applicationDetail.docUpdate && !applicationDetail.usingExistingDoc && applicationDetail.tradeSecretAllowed}"
						value="#{applicationDetail.tsFileToUpload}" />
					<af:inputText id="tsFileName" label="Trade Secret File to Upload :"
						rendered="#{applicationDetail.tsFileInfo != null && applicationDetail.tradeSecretAllowed}"
						value="#{applicationDetail.tsFileInfo.fileName}" readOnly="true" />
					<af:inputText id="tradeSecretReason"
						label="Trade Secret Justification :" rows="6" columns="100"
						maximumLength="500"
						rendered="#{!applicationDetail.pbrRprRpe
	            && (!applicationDetail.docUpdate || applicationDetail.applicationDoc.tradeSecretDocId != null)
	            &&  !applicationDetail.usingExistingDoc
	            && applicationDetail.tradeSecretAllowed
	            &&  !applicationDetail.publicReadOnlyUser}"
						value="#{applicationDetail.applicationDoc.tradeSecretReason}"
						readOnly="#{!applicationDetail.okToEditAttachment && applicationDetail.tradeSecretVisible}" />
				</af:panelForm>
				<f:facet name="footer">
					<af:panelButtonBar>
						<af:commandButton text="Apply"
							actionListener="#{applicationDetail.applyEditDoc}"
							rendered="#{applicationDetail.okToEditAttachment}" />
						<af:commandButton text="Cancel" immediate="true"
							actionListener="#{applicationDetail.closeViewDoc}" />
						<af:commandButton text="Delete"
							actionListener="#{applicationDetail.removeEditDoc}"
							rendered="#{applicationDetail.deleteDocAllowed && applicationDetail.okToEditAttachment && applicationDetail.docUpdate}" />
					</af:panelButtonBar>
				</f:facet>
			</af:panelForm>
		</af:form>
	</af:document>
</f:view>
