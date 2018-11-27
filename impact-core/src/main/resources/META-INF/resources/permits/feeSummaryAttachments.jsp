<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<af:panelGroup layout="vertical" id="attachmentList">
			<af:showDetailHeader text="Attachments" disclosed="true"
				rendered="#{permitDetail.selectedTreeNode.type == 'permit'}">
					<af:objectSpacer height="15" />
				<afh:rowLayout halign="center">
					<af:table value="#{permitDetail.fixedPotentialAttachments}" id="AttachTab"
						width="#{permitDetail.contentTableWidth}" var="doc" emptyText=" "
						partialTriggers="AttachTab:UploadDocButton AttachTab:EditDocButton AttachTab:delButton">

						<af:column headerText="Status" 
							inlineStyle="vertical-align: middle;" sortProperty="requiredDoc"
							sortable="true" width="40">
							<af:objectImage
								rendered="#{doc.fixed}"
								shortDesc="Fixed Attachment" height="16"
								inlineStyle="padding-left:10px;" 
								source="#{ (empty doc.documentID) ? '/images/Caution.gif' : '/images/Check.gif'}" />

							<af:objectImage rendered="#{!doc.fixed}"
								shortDesc="Non Fixed Attachment" height="16"
								inlineStyle="padding-left:10px;" source="/images/blackCheck.gif"  />
						</af:column>

						<af:column headerText="ID" sortProperty="documentID"
							sortable="true" width="40">
							<t:div>
								<af:commandLink action="dialog:feeSummaryAttach" id="delButton"
									useWindow="true" windowWidth="#{confirmWindow.width}"
									windowHeight="#{confirmWindow.height}"
									shortDesc="Click to Delete the attachment or to Edit the attachment type/description"
									disabled="#{permitDetail.readOnlyUser}"
									returnListener="#{permitDetail.docDialogDone}">
									<af:outputText value="#{doc.documentID}" />
									<t:updateActionListener property="#{permitDetail.singleDoc}"
										value="#{doc}" />
									<t:updateActionListener property="#{permitDetail.dialogEdit}"
										value="true" />
								</af:commandLink>
								<af:commandButton text="Upload" useWindow="true"
								windowWidth="600" windowHeight="400"
								returnListener="#{permitDetail.docDialogDone}"
								disabled="#{permitDetail.readOnlyUser 
									|| !permitDetail.NSRFeeSummaryEditAllowed}"
								action="#{permitDetail.startUploadPotentialAttachDoc}"
								rendered="#{(empty doc.documentID)}"
								shortDesc="Click to upload the attachment"
								id="uploadButton" />
							</t:div>
						</af:column>

						<af:column headerText="Attachment Type"
							sortProperty="permitDocTypeCD" sortable="true" >
							<af:goLink targetFrame="_blank" destination="#{doc.docURL}"
								text="#{permitDetail.permitDocTypeDefs.itemDesc[doc.permitDocTypeCD]}" 
								disabled = "#{(empty doc.documentID)}"/>
						</af:column>

						<af:column headerText="Description" sortProperty="description"
							sortable="true" >
							<af:outputText value="#{doc.description}" />
						</af:column>

						<af:column headerText="Uploaded By" sortProperty="lastModifiedBy"
							sortable="true" >
							<af:selectOneChoice readOnly="true" value="#{doc.lastModifiedBy}">
								<f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
							</af:selectOneChoice>
						</af:column>

						<af:column headerText="Upload Date" sortProperty="uploadDate"
							sortable="true">
							<af:selectInputDate readOnly="true" value="#{doc.uploadDate}" />
						</af:column>
						
						<af:column headerText="Document Generation" 
							 width="40" sortable="true">
								<af:commandButton immediate="true" text="Generate"
									useWindow="true" windowWidth="500" windowHeight="300"
									disabled="#{!permitDetail.editAllowed}"
                  					action="#{permitDetail.generateLetterDoc}"   
                  					rendered="#{doc.permitDocumentID != null}">         
                				</af:commandButton>
						</af:column>

						<f:facet name="footer">
							<afh:rowLayout halign="center">
								<af:panelButtonBar>
									<af:commandButton text="Add" id="UploadDocButton"
										useWindow="true" windowWidth="500" windowHeight="300"
										rendered="#{!permitDetail.readOnlyUser}"
										disabled="#{!permitDetail.NSRFeeSummaryEditAllowed}"
										returnListener="#{permitDetail.docDialogDone}"
										action="#{permitDetail.startUploadAttachDoc}" />
									<af:commandButton actionListener="#{tableExporter.printTable}"
										onclick="#{tableExporter.onClickScript}" text="Printable view" />
									<af:commandButton actionListener="#{tableExporter.excelTable}"
										onclick="#{tableExporter.onClickScript}"
										text="Export to excel" />
								</af:panelButtonBar>
							</afh:rowLayout>
						</f:facet>
					</af:table>
				</afh:rowLayout>
				<af:outputText inlineStyle="font-size:75%;color:#666"
					value="To Delete the attachment, or to Edit attachment description, click in the Attachment ID column." />
			</af:showDetailHeader>
</af:panelGroup>
