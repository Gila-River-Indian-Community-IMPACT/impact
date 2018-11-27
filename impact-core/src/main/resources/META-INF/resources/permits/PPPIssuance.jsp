<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
	<af:document title="Preliminary Proposed Issuance">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form usesUpload="true"
			partialTriggers="ThePage:reissue ThePage:delIssue ThePage:uploadIssuanceDoc">
			<af:page id="ThePage" var="foo" value="#{menuModel.model}"
				title="Preliminary Proposed Issuance">
				<%@ include file="../permits/header.jsp"%>

				<mu:setProperty property="#{permitDetail.currentIssuanceAction}"
					value="#{permitReference.pppIssuanceType}" />
				<h:panelGrid border="1">
					<af:panelBorder>

						<f:facet name="top">
							<f:subview id="permitDetailTop">
								<jsp:include page="permitDetailTop.jsp" />
							</f:subview>
						</f:facet>
						<%
							/* Top end */
						%>

						<%
							/* Content begin */
						%>
						<h:panelGrid columns="1" border="1"
							width="#{permitDetail.permitWidth}">
							<af:panelGroup>
								<%
									/* Draft begin */
								%>
								<af:panelHeader text="Preliminary Proposed">
									<af:panelGroup>
										<af:panelForm labelWidth="44%" rows="1" maxColumns="2"
											fieldWidth="56%">
											<af:selectOneChoice label="Issuance Status :" readOnly="true"
												id="pppIssuanceStatus"
												value="#{permitDetail.permit.pppIssuanceStatusCd}">
												<mu:selectItems
													value="#{permitReference.issuanceStatusDefs}" />
											</af:selectOneChoice>

											<af:selectInputDate label="Issuance Date :"
												readOnly="#{! permitDetail.editMode}"
												value="#{permitDetail.permit.pppIssueDate}">
												<af:validateDateTimeRange minimum="1900-01-01" />
											</af:selectInputDate>
										</af:panelForm>

										<afh:tableLayout>
											<afh:rowLayout>
												<afh:cellFormat partialTriggers="ipUploadButton" width="50%"
													halign="left" valign="top">
													<afh:rowLayout>
														<afh:cellFormat width="235" halign="right" valign="top">
															<af:inputText label="Introductory Package :"
																readOnly="true" />
														</afh:cellFormat>
														<afh:cellFormat width="190">
															<af:goLink targetFrame="_blank"
																rendered="#{permitDetail.docsMap[permitReference.introPackageCD][permitReference.pppFlag] != null}"
																destination="#{permitDetail.docsMap[permitReference.introPackageCD][permitReference.pppFlag].docURL}"
																text="#{permitDetail.docsMap[permitReference.introPackageCD][permitReference.pppFlag].description}" />
															<af:objectSpacer width="10" />
															<af:commandButton text="Upload" useWindow="true"
																windowWidth="500" windowHeight="300" id="ipUploadButton"
																rendered="#{permitDetail.editMode}"
																returnListener="#{permitDetail.docDialogDone}"
																action="#{permitDetail.uploadDoc}">
																<t:updateActionListener
																	value="#{permitReference.introPackageCD}"
																	property="#{permitDetail.docTypeCD}" />
																<t:updateActionListener
																	value="#{permitReference.pppFlag}"
																	property="#{permitDetail.issuanceStageFlag}" />
																<t:updateActionListener
																	value="#{permitDetail.docsMap[permitReference.introPackageCD][permitReference.pppFlag]}"
																	property="#{permitDetail.singleDoc}" />
															</af:commandButton>
														</afh:cellFormat>
													</afh:rowLayout>
												</afh:cellFormat>
												<afh:cellFormat partialTriggers="idUploadButton" width="50%"
													halign="left" valign="top">
													<afh:rowLayout>
														<afh:cellFormat width="255" halign="right" valign="top">
															<af:inputText label="Issuance Document :"
																id="pppIssuanceDocument" readOnly="true" />
														</afh:cellFormat>
														<afh:cellFormat width="200">
															<af:goLink targetFrame="_blank"
																rendered="#{permitDetail.docsMap[permitReference.issuanceDocCD][permitReference.pppFlag] != null}"
																destination="#{permitDetail.docsMap[permitReference.issuanceDocCD][permitReference.pppFlag].docURL}"
																text="#{permitDetail.docsMap[permitReference.issuanceDocCD][permitReference.pppFlag].description}" />
															<af:objectSpacer width="10" />
															<af:commandButton text="Upload" useWindow="true"
																windowWidth="500" windowHeight="300" id="idUploadButton"
																rendered="#{permitDetail.editMode}"
																returnListener="#{permitDetail.docDialogDone}"
																action="#{permitDetail.uploadDoc}">
																<t:updateActionListener
																	value="#{permitReference.issuanceDocCD}"
																	property="#{permitDetail.docTypeCD}" />
																<t:updateActionListener
																	value="#{permitReference.pppFlag}"
																	property="#{permitDetail.issuanceStageFlag}" />
																<t:updateActionListener
																	value="#{permitDetail.docsMap[permitReference.issuanceDocCD][permitReference.pppFlag]}"
																	property="#{permitDetail.singleDoc}" />
															</af:commandButton>
														</afh:cellFormat>
													</afh:rowLayout>
												</afh:cellFormat>
											</afh:rowLayout>
											<afh:rowLayout>
												<afh:cellFormat partialTriggers="ccUploadButton" width="50%"
													halign="left" valign="top">
													<afh:rowLayout>
														<afh:cellFormat width="235" halign="right" valign="top">
															<af:inputText label="CC Address Labels :" readOnly="true" />
														</afh:cellFormat>
														<afh:cellFormat width="190">
															<af:goLink targetFrame="_blank"
																rendered="#{permitDetail.docsMap[permitReference.addressLabelsCD][permitReference.pppFlag] != null}"
																destination="#{permitDetail.docsMap[permitReference.addressLabelsCD][permitReference.pppFlag].docURL}"
																text="#{permitDetail.docsMap[permitReference.addressLabelsCD][permitReference.pppFlag].description}" />
															<af:objectSpacer width="10" />
															<af:commandButton text="Upload" useWindow="true"
																windowWidth="500" windowHeight="300" id="ccUploadButton"
																rendered="#{permitDetail.editMode}"
																returnListener="#{permitDetail.docDialogDone}"
																action="#{permitDetail.uploadDoc}">
																<t:updateActionListener
																	value="#{permitReference.addressLabelsCD}"
																	property="#{permitDetail.docTypeCD}" />
																<t:updateActionListener
																	value="#{permitReference.pppFlag}"
																	property="#{permitDetail.issuanceStageFlag}" />
																<t:updateActionListener
																	value="#{permitDetail.docsMap[permitReference.addressLabelsCD][permitReference.pppFlag]}"
																	property="#{permitDetail.singleDoc}" />
															</af:commandButton>
														</afh:cellFormat>
													</afh:rowLayout>
												</afh:cellFormat>
												<afh:cellFormat
													rendered="#{permitDetail.permit.pppIssuanceStatusCd == 'N'}"
													width="50%" halign="left" valign="top">
													<afh:rowLayout>
														<afh:cellFormat width="255" halign="right" valign="top">
															<af:inputText label="Analysis Document :" readOnly="true" />
														</afh:cellFormat>
														<afh:cellFormat width="200">
															<af:goLink targetFrame="_blank"
																destination="#{permitDetail.topTCDoc.docURL}"
																text="#{permitDetail.topTCDoc.description}" />
														</afh:cellFormat>
													</afh:rowLayout>
												</afh:cellFormat>
											</afh:rowLayout>
											<afh:rowLayout
												rendered="#{permitDetail.permit.permitType == 'TVPTO' || permitDetail.permit.permitType == 'TIVPTO'}">
												<afh:cellFormat width="50%" halign="left" valign="top">
													<afh:rowLayout>
														<afh:cellFormat width="235" halign="right" valign="top">
															<af:inputText label="Response to Comments :"
																readOnly="true" />
														</afh:cellFormat>
														<afh:cellFormat width="190">
															<af:goLink targetFrame="_blank"
																rendered="#{permitDetail.docsMap[permitReference.responseToCommentsCD][permitReference.allFlag] != null}"
																destination="#{permitDetail.docsMap[permitReference.responseToCommentsCD][permitReference.allFlag].docURL}"
																text="#{permitDetail.docsMap[permitReference.responseToCommentsCD][permitReference.allFlag].description}" />
														</afh:cellFormat>
													</afh:rowLayout>
												</afh:cellFormat>
												<afh:cellFormat width="50%" halign="left" valign="top">
													<afh:rowLayout>
														<afh:cellFormat width="255" halign="right" valign="top">

														</afh:cellFormat>
														<afh:cellFormat width="200">

														</afh:cellFormat>
													</afh:rowLayout>
												</afh:cellFormat>
											</afh:rowLayout>
										</afh:tableLayout>

										<afh:tableLayout
											rendered="#{permitDetail.permit.permitType == 'TIVPTO'}">
											<afh:rowLayout partialTriggers="ndUploadButton">
												<afh:cellFormat width="235" halign="right" valign="top">
													<af:inputText label="Title IV Application :"
														readOnly="true" />
												</afh:cellFormat>
												<afh:cellFormat>
													<af:goLink targetFrame="_blank"
														rendered="#{permitDetail.recentAppDoc != null}"
														destination="#{permitDetail.recentAppDoc.docURL}"
														text="#{permitDetail.recentAppDoc.description}" />
												</afh:cellFormat>
											</afh:rowLayout>
										</afh:tableLayout>

										<af:panelForm labelWidth="44%" rows="1" partialTriggers="cro"
											maxColumns="2" fieldWidth="56%">
											<af:selectOneChoice id="cro" label="Company Review Outcome :"
												autoSubmit="true" readOnly="#{! permitDetail.editMode}"
												value="#{permitDetail.permit.pppReviewWaived}">
												<f:selectItem itemValue="true" itemLabel="Waived" />
												<f:selectItem itemValue="false" itemLabel="Not Waived" />
											</af:selectOneChoice>

											<af:selectInputDate id="endOfCommentPeriod"
												label="Comment Period End Date :"
												disabled="#{permitDetail.permit.pppReviewWaived}"
												readOnly="#{! permitDetail.editMode}"
												value="#{permitDetail.permit.pppIssuance.publicCommentEndDate}">
												<af:validateDateTimeRange minimum="1900-01-01" />
											</af:selectInputDate>
										</af:panelForm>
									</af:panelGroup>
								</af:panelHeader>
								<%
									/* Draft end */
								%>

								<af:objectSpacer height="5" />
								<af:panelForm maxColumns="1" rows="1" labelWidth="21%"
									fieldWidth="69%">
									<af:panelGroup rendered="#{permitDetail.supersededDoc != null}">
										<af:outputText inlineStyle="font-size: 12px"
											value="Note: this is an APA permit - please use this " />
										<af:goLink targetFrame="_blank" inlineStyle="font-size: 12px"
											destination="#{permitDetail.supersededDoc.docURL}"
											text="Standard Terms and Conditions" />
										<af:outputText inlineStyle="font-size: 12px"
											value=" from the superseded permit document being amended as the Standard Terms and Conditions for this permit" />
									</af:panelGroup>
								</af:panelForm>

								<af:objectSpacer height="10" />

								<afh:rowLayout halign="center">
									<af:panelButtonBar rendered="#{permitDetail.editAllowed}">
										<af:commandButton text="Re-issue" id="reissue"
											rendered="#{permitDetail.permit.pppIssuanceStatusCd == permitReference.issuanceStatusIssued}"
											returnListener="#{permitDetail.reissuePpp}"
											action="#{confirmWindow.confirm}" useWindow="true"
											windowWidth="#{confirmWindow.width}"
											windowHeight="#{confirmWindow.height}">
											<t:updateActionListener property="#{confirmWindow.type}"
												value="#{confirmWindow.yesNo}" />
											<t:updateActionListener property="#{confirmWindow.message}"
												value="Re-issue function will remove all data from PPP Issuance. Click 'Yes' to continue or 'No' to cancel the function." />
										</af:commandButton>
									</af:panelButtonBar>
									<af:panelButtonBar
										rendered="#{!permitDetail.editMode && permitDetail.editAllowed && permitDetail.permit.pppIssuanceStatusCd != permitReference.issuanceStatusIssued}">
										<af:commandButton text="Prepare Documents"
											rendered="#{permitDetail.permit.pppIssuanceStatusCd != permitReference.issuanceStatusReady}"
											action="#{permitDetail.prepareIssuance}">
										</af:commandButton>
										<af:commandButton text="Upload issuance document"
											useWindow="true" windowWidth="500" windowHeight="300"
											id="uploadIssuanceDoc"
											rendered="#{permitDetail.permit.pppIssuanceStatusCd != permitReference.issuanceStatusReady}"
											returnListener="#{permitDetail.issuanceDocDone}"
											action="#{permitDetail.uploadIssuanceDoc}">
											<t:updateActionListener
												value="#{permitReference.issuanceDocCD}"
												property="#{permitDetail.docTypeCD}" />
											<t:updateActionListener value="#{permitReference.pppFlag}"
												property="#{permitDetail.issuanceStageFlag}" />
											<t:updateActionListener
												value="#{permitDetail.docsMap[permitReference.issuanceDocCD][permitReference.pppFlag]}"
												property="#{permitDetail.singleDoc}" />
										</af:commandButton>
										<af:commandButton text="Zip Documents"
											action="#{permitDetail.getPermitDocsZipFile}" />
										<af:commandButton text="Add Permit to Issuance List"
											rendered="#{permitDetail.permit.pppIssuanceStatusCd == permitReference.issuanceStatusNotReady}"
											action="#{permitDetail.markReadyIssuance}" />
										<af:commandButton text="Deselect From Issuance List"
											rendered="#{permitDetail.permit.pppIssuanceStatusCd == permitReference.issuanceStatusReady}"
											action="#{permitDetail.unprepareIssuance}" />
										<af:commandButton text="Skip Issuance" rendered="false"
											action="#{permitDetail.skipIssuance}" />
										<af:commandButton text="Validate Issuance"
											rendered="#{permitDetail.permit.pppIssuance != null}"
											action="#{permitDetail.validateIssuance}">
										</af:commandButton>
										<af:commandButton text="Finalize Issuance"
											rendered="#{permitDetail.permit.pppIssuanceStatusCd == permitReference.issuanceStatusReady && false}"
											action="#{permitDetail.finalizeIssuance}" />
										<af:commandButton text="Reset Issuance" id="delIssue"
											rendered="#{permitDetail.permit.pppIssuanceStatusCd != permitReference.issuanceStatusReady}"
											returnListener="#{permitDetail.deleteIssuance}"
											action="#{confirmWindow.confirm}" useWindow="true"
											windowWidth="#{confirmWindow.width}"
											windowHeight="#{confirmWindow.height}">
											<t:updateActionListener property="#{confirmWindow.type}"
												value="#{confirmWindow.yesNo}" />
										</af:commandButton>
									</af:panelButtonBar>
								</afh:rowLayout>

								<af:objectSpacer height="10" />

								<%
									/* Buttons begin */
								%>
								<f:subview id="permitIssuanceButtons">
									<jsp:include page="editOnlyButtons.jsp" />
								</f:subview>
								<%
									/* Buttons end */
								%>

							</af:panelGroup>
						</h:panelGrid>
						<%
							/* Content end */
						%>
					</af:panelBorder>
				</h:panelGrid>
			</af:page>
			<af:iterator value="#{permitDetail}" var="validationBean" id="v">
				<%@ include file="../util/validationComponents.jsp"%>
			</af:iterator>
		</af:form>
	</af:document>
</f:view>
