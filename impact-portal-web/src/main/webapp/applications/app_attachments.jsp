<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<af:panelGroup layout="vertical"
	rendered="#{applicationDetail.renderAttachments}" id="attachmentList">
	<af:switcher defaultFacet="applicationDoc"
		facetName="#{applicationDetail.useOtherAttachments ? 'otherAttachments' : 'applicationDoc'}">
		<f:facet name="applicationDoc">
			<af:showDetailHeader disclosed="true" text="Attachments">
				<af:objectSpacer height="5" />
				<af:outputText
					value="*There are #{applicationDetail.requiredAttachmentCount} required attachments"
					inlineStyle="color:#FFA500;"
					rendered="#{applicationDetail.requiredAttachmentCount != 0}" />
				<af:objectSpacer height="5" />

				<af:table id="DocTab" emptyText=" " var="doc" width="98%"
					value="#{applicationDetail.applicationDocuments}"
					partialTriggers="DocTab:AddDocButton DocTab:EditDocButton">
					<af:column headerText="Status"
						inlineStyle="vertical-align: middle;" width="40">
						<af:objectImage rendered="#{doc.requiredDoc}"
							shortDesc="Required Attachment" height="16"
							inlineStyle="padding-left:10px;"
							source="#{doc.requiredDoc && (empty doc.documentId) ? '/images/Caution.gif' : '/images/Check.gif'}" />

						<af:objectImage rendered="#{!doc.requiredDoc}"
							shortDesc="Required Attachment" height="16"
							inlineStyle="padding-left:10px;" source="/images/blackCheck.gif" />
					</af:column>
					<af:column headerText="ID" inlineStyle="vertical-align: middle;"
						width="40">
							<af:commandLink id="attachmentIdLink"
								text="#{doc.applicationDocId}" useWindow="true"
								windowWidth="600" windowHeight="400"
								returnListener="#{applicationDetail.docDialogDone}"
								action="#{applicationDetail.startEditDoc}"
								rendered="#{!doc.requiredDoc}"
								shortDesc="Click to Delete the attachment or to Edit the attachment description">
								<t:updateActionListener property="#{applicationDetail.deleteDocAllowed}" value="true" />
							</af:commandLink>
							<af:commandLink text="#{doc.applicationDocId}" useWindow="true"
								windowWidth="600" windowHeight="400"
								returnListener="#{applicationDetail.docDialogDone}"
								action="#{applicationDetail.startEditDoc}"
								rendered="#{doc.requiredDoc && !(empty doc.documentId)}"
								shortDesc="Click to Delete the attachment or to Edit the attachment description">
								<t:updateActionListener property="#{applicationDetail.deleteDocAllowed}" value="true" />
							</af:commandLink>
							<af:commandButton text="Upload" useWindow="true"
								windowWidth="600" windowHeight="400"
								returnListener="#{applicationDetail.docDialogDone}"
								action="#{applicationDetail.startEditDoc}"
								
								rendered="#{doc.requiredDoc && (empty doc.documentId)}"
								shortDesc="Click to Delete the attachment or to Edit the attachment description" />
					</af:column>
					<af:column headerText="Attachment Type"
						rendered="#{!applicationDetail.pbrRprRpe}">
						<af:switcher defaultFacet="hasPublicDoc"
							facetName="#{doc.tradeSecretOnly ? 'tsOnly': 'hasPublicDoc'}">
							<f:facet name="hasPublicDoc">
								<af:panelGroup>
									<af:goLink id="attachmentTypeLink"
										text="#{empty doc.eacFormTypeCD ? 
	                  						applicationReference.applicationDocTypeDefs.itemDesc[doc.applicationDocumentTypeCD] :
	                  						applicationReference.eacFormTypeDefs.itemDesc[doc.eacFormTypeCD]}"
										targetFrame="_blank"
										destination="#{applicationDetail.publicDocURL}"
										rendered="#{!doc.requiredDoc && applicationDetail.applicationType != 'TV'}"
										shortDesc="Download public document (#{(empty doc.publicDoc) ? '' : doc.publicDoc.extension})"
										disabled="#{relocation.editable || delegation.editable || applicationDetail.editMode}" />
									<af:goLink id="tvAttachmentTypeLink"
										text="#{empty doc.eacFormTypeCD ? 
	                  						applicationReference.tvApplicationDocTypeDefs.itemDesc[doc.applicationDocumentTypeCD] :
	                  						applicationReference.eacFormTypeDefs.itemDesc[doc.eacFormTypeCD]}"
										targetFrame="_blank"
										destination="#{applicationDetail.publicDocURL}"
										rendered="#{!doc.requiredDoc && applicationDetail.applicationType == 'TV'}"
										shortDesc="Download public document (#{(empty doc.publicDoc) ? '' : doc.publicDoc.extension})"
										disabled="#{relocation.editable || delegation.editable || applicationDetail.editMode}" />

									<af:goLink id="requiredAttachmentTypeLink"
										text="#{empty doc.eacFormTypeCD ? applicationReference.applicationDocTypeDefs.itemDesc[doc.applicationDocumentTypeCD] : applicationReference.eacFormTypeDefs.itemDesc[doc.eacFormTypeCD]}"
										targetFrame="_blank"
										rendered="#{doc.requiredDoc && applicationDetail.applicationType != 'TV'}"
										destination="#{applicationDetail.publicDocURL}"
										shortDesc="Download public document (#{(empty doc.publicDoc) ? '' : doc.publicDoc.extension})"
										disabled="#{relocation.editable || delegation.editable || applicationDetail.editMode}" />

									<af:goLink id="tvRequiredAttachmentTypeLink"
										text="#{applicationReference.tvApplicationDocTypeDefs.itemDesc[doc.applicationDocumentTypeCD]}"
										targetFrame="_blank"
										rendered="#{doc.requiredDoc && applicationDetail.applicationType == 'TV'}"
										destination="#{applicationDetail.publicDocURL}"
										shortDesc="Download public document (#{(empty doc.publicDoc) ? '' : doc.publicDoc.extension})"
										disabled="#{relocation.editable || delegation.editable || applicationDetail.editMode}" />
								</af:panelGroup>		
							</f:facet>
							<f:facet name="tsOnly">
								<af:outputText
									value="#{applicationReference.applicationDocTypeDefs.itemDesc[doc.applicationDocumentTypeCD]}" />
							</f:facet>
						</af:switcher>
					</af:column>
					<af:column headerText="Description">
						<af:outputText id="descriptionText" value="#{doc.description}"
							truncateAt="80" shortDesc="#{doc.description}" />
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					</af:column>
					<af:column headerText="Trade Secret Document"
																		>
						<af:switcher defaultFacet="noTS"
							facetName="#{(doc.tradeSecret && doc.tradeSecretAllowed)? 'TS': 'noTS'}">
							<f:facet name="noTS">
								<af:outputText value="None Provided" />
							</f:facet>
							<f:facet name="TS">
								<af:goLink id="tradeSecretDocDownloadLink" text="Download"
									targetFrame="_blank"
									destination="#{applicationDetail.tradeSecretDocURL}"
									shortDesc="Download trade secret document (#{(empty doc.tradeSecretDoc) ? '' : doc.tradeSecretDoc.extension})"
									disabled="#{relocation.editable || delegation.editable || applicationDetail.editMode}" >
								</af:goLink>
							</f:facet>
						</af:switcher>
					</af:column>
					<af:column headerText="Trade Secret Justification"
																					>
						<af:switcher defaultFacet="noJustification"
							facetName="#{(empty doc.tradeSecretReason || !doc.tradeSecretAllowed)? 'noJustification': 'Justification'}">
							<f:facet name="noJustification">
								<af:switcher defaultFacet="noTSDoc"
									facetName="#{(doc.tradeSecret && doc.tradeSecretAllowed)? 'TSDoc': 'noTSDoc'}">
									<f:facet name="TSDoc">
										<af:commandLink text="Add Justification"
											action="#{applicationDetail.startEditDoc}" useWindow="true"
											windowWidth="600" windowHeight="400">
											<t:updateActionListener property="#{applicationDetail.deleteDocAllowed}" value="false" />
										</af:commandLink>
									</f:facet>
									<f:facet name="noTSDoc">
										<af:outputText value="N/A" />
									</f:facet>
								</af:switcher>
							</f:facet>
							<f:facet name="Justification">
								<af:commandLink text="View Justification"
									action="#{applicationDetail.startEditDoc}"
									returnListener="#{applicationDetail.docDialogDone}"
									useWindow="true" windowWidth="600" windowHeight="400">
									<t:updateActionListener
										property="#{applicationDetail.deleteDocAllowed}" value="false" />
								</af:commandLink>
							</f:facet>
						</af:switcher>
					</af:column>
					<f:facet name="footer">
						<afh:rowLayout halign="center">
							<af:panelButtonBar>
								<af:commandButton text="Add" id="AddDocButton"
									rendered="#{!applicationDetail.readOnly}" useWindow="true"
									windowWidth="600" windowHeight="350"
									returnListener="#{applicationDetail.docDialogDone}"
									action="#{applicationDetail.startAddDoc}" />
								<af:commandButton actionListener="#{tableExporter.printTable}"
									onclick="#{tableExporter.onClickScript}" text="Printable view" />
								<af:commandButton actionListener="#{tableExporter.excelTable}"
									onclick="#{tableExporter.onClickScript}" text="Export to excel" />
							</af:panelButtonBar>
						</afh:rowLayout>
					</f:facet>
				</af:table>
				<af:outputText inlineStyle="font-size:75%;color:#666"
					value="To Delete the attachment, or to Edit attachment description, click in the Attachment ID column." />
			</af:showDetailHeader>
		</f:facet>
		<f:facet name="otherAttachments">
			<f:subview id="doc_attachments">
				<jsp:include flush="true" page="doc_attachments.jsp" />
			</f:subview>
		</f:facet>
	</af:switcher>
	<af:switcher
		facetName="#{applicationDetail.application.applicationTypeCD}"
		defaultFacet="PTIO">
		<f:facet name="PTIO">
			<af:commandLink text="Attachment type explanation" useWindow="true"
				blocking="false" windowWidth="800" windowHeight="600"
				inlineStyle="margin-left:10px;padding:5px;"
				returnListener="#{applicationDetail.dialogDone}"
				action="#{applicationDetail.displayAppDocTypeExp}" />
		</f:facet>
		<f:facet name="TV">
			<af:commandLink text="Attachment type explanation" useWindow="true"
				blocking="false" windowWidth="800" windowHeight="600"
				inlineStyle="margin-left:10px;padding:5px;"
				returnListener="#{applicationDetail.dialogDone}"
				action="#{applicationDetail.displayTvAppDocTypeExp}" />
		</f:facet>
	</af:switcher>
</af:panelGroup>