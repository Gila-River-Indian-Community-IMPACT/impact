<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
	<af:document title="Attachments">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:page id="ThePage" var="foo" value="#{menuModel.model}"
				title="Attachments">
				<%@ include file="../permits/header.jsp"%>

				<h:panelGrid border="1">
					<af:panelBorder>

						<f:facet name="top">
							<f:subview id="permitDetailTop">
								<jsp:include page="permitDetailTop.jsp" />
							</f:subview>
						</f:facet>

						<%
							/* Content begin */
						%>
						<h:panelGrid columns="1" border="1"
							width="#{permitDetail.permitWidth}">
							<afh:rowLayout halign="center">
								<af:panelGroup>
									<af:objectSpacer height="5" />
									<af:outputText inlineStyle="font-weight:bold"
										value="These attachments are supporting documentation only. They will not be attached to the issued permit document." />
									<af:objectSpacer height="5" />
									<af:table value="#{permitDetail.attachments}" id="AttachTab"
										width="#{permitDetail.permitTableWidth}" var="doc"
										emptyText=" "
										partialTriggers="AttachTab:UploadDocButton AttachTab:EditDocButton AttachTab:delButton">

										<af:column headerText="Attachment ID" width="15%"
											sortProperty="documentID" sortable="true">
											<af:commandLink action="dialog:permitAttach" id="delButton"
												useWindow="true" windowWidth="#{confirmWindow.width}"
												windowHeight="#{confirmWindow.height}"
												disabled="#{permitDetail.permitSomethingFinal}"
												shortDesc="Click to Delete the attachment or to Edit the attachment description"
												returnListener="#{permitDetail.docDialogDone}">
												<af:outputText value="#{doc.documentID}" />
												<t:updateActionListener property="#{permitDetail.singleDoc}"
													value="#{doc}" />
												<t:updateActionListener
													property="#{permitDetail.dialogEdit}" value="true" />
											</af:commandLink>
										</af:column>

										<af:column headerText="Description" sortProperty="description"
											sortable="true">
											<af:goLink targetFrame="_blank" destination="#{doc.docURL}"
												text="#{doc.description}" />
										</af:column>

										<af:column headerText="Uploaded By"
											sortProperty="lastModifiedBy" sortable="true" width="15%">
											<af:selectOneChoice readOnly="true"
												value="#{doc.lastModifiedBy}">
												<f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
											</af:selectOneChoice>
										</af:column>

										<af:column headerText="Upload Date"
											sortProperty="uploadDate" sortable="true" width="15%">
											<af:selectInputDate readOnly="true"
												value="#{doc.uploadDate}" />
										</af:column>

										<f:facet name="footer">
											<afh:rowLayout halign="center">
												<af:panelButtonBar>
													<af:commandButton text="Add" id="UploadDocButton"
														useWindow="true" windowWidth="500" windowHeight="300"
														rendered="#{!permitDetail.readOnlyUser}"
														returnListener="#{permitDetail.docDialogDone}"
														action="#{permitDetail.startUploadAttachDoc}" />
													<af:commandButton
														actionListener="#{tableExporter.printTable}"
														onclick="#{tableExporter.onClickScript}"
														text="Printable view" />
													<af:commandButton
														actionListener="#{tableExporter.excelTable}"
														onclick="#{tableExporter.onClickScript}"
														text="Export to excel" />
												</af:panelButtonBar>
											</afh:rowLayout>
										</f:facet>
									</af:table>

									<af:outputText inlineStyle="font-size:75%;color:#666"
										rendered="#{permitDetail.editMode}"
										value="To Delete the attachment, or to Edit attachment description, click in the Attachment ID column." />
									<af:objectSpacer height="10" />
									<f:subview id="permitIssuanceButtons">
										<jsp:include page="editOnlyButtonsAttachments.jsp" />
									</f:subview>
								</af:panelGroup>
							</afh:rowLayout>
						</h:panelGrid>
						<%
							/* Attachments end */
						%>

						<%
							/* Content end */
						%>

					</af:panelBorder>
				</h:panelGrid>
			</af:page>
		</af:form>
	</af:document>
</f:view>
