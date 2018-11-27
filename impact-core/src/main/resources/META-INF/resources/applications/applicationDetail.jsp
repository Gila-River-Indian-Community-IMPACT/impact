<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="Application Detail">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<%@ include file="../util/validate.js"%>
		<af:form
			partialTriggers="ThePage:ptioEU:euPurposesCheckbox ThePage:appBtns:rprReturned ThePage:appBtns:rpeDenied ThePage:appBtns:rpeDeadEnded 
			ThePage:appBtns:pbrAccepted ThePage:appBtns:pbrDenied ThePage:appBtns:pbrNoApplicable">
							<af:inputHidden value="#{applicationDetail.migratedData}"
					rendered="#{applicationDetail.internalApp}" />
			<af:page id="ThePage" var="foo" value="#{menuModel.model}"
				rendered="#{applicationDetail.application!= null}"
				title="Application Detail">
				<af:inputHidden value="#{submitTask.popupRedirect}"
					rendered="#{facilityProfile.portalApp}" />
				<af:inputHidden value="#{applicationDetail.popupRedirect}"
					rendered="#{applicationDetail.internalApp}" />
				<af:inputHidden value="#{submitTask.logUserOff}"
					rendered="#{facilityProfile.portalApp}" />
				<jsp:include flush="true" page="header.jsp" />

				<afh:rowLayout halign="center"
					rendered="#{! applicationDetail.applicationDeleted}" width="900">
					<h:panelGrid border="1">
						<af:panelBorder>
							<f:facet name="top">
								<f:subview id="applicationDetailTop">
									<jsp:include page="applicationDetailTop.jsp" />
								</f:subview>
							</f:facet>
							<f:facet name="left">
								<f:subview
									rendered="#{!applicationDetail.relocationClass && !applicationDetail.delegationClass}"
									id="applicationDetailTree">
									<jsp:include page="applicationDetailTree.jsp" />
								</f:subview>
							</f:facet>
							<h:panelGrid columns="1" border="0" width="900">

								<af:panelGroup>
									<af:switcher defaultFacet="application"
										facetName="#{applicationDetail.selectedTreeNode.type}">

										<f:facet name="application">
											<af:switcher
												facetName="#{applicationDetail.application.applicationTypeCD}"
												defaultFacet="PTIO">

												<f:facet name="SPA">
													<f:subview id="itrSPA">
														<jsp:include page="relocationDetail.jsp" />
													</f:subview>
												</f:facet>

												<f:facet name="RPS">
													<f:subview id="itrRPS">
														<jsp:include page="relocationDetail.jsp" />
													</f:subview>
												</f:facet>

												<f:facet name="ITR">
													<f:subview id="itrITR">
														<jsp:include page="relocationDetail.jsp" />
													</f:subview>
												</f:facet>

												<f:facet name="DOR">
													<f:subview id="dor">
														<jsp:include page="delegationDetail.jsp" />
													</f:subview>
												</f:facet>

												<f:facet name="PTIO">
													<f:subview id="ptioFacility">
														<jsp:include page="ptioFacility.jsp" />
													</f:subview>
												</f:facet>

												<f:facet name="TV">
													<f:subview id="tvFacility">
														<jsp:include page="tvFacility.jsp" />
													</f:subview>
												</f:facet>

												<f:facet name="PBR">
													<f:subview id="pbrFacility">
														<jsp:include page="pbrFacility.jsp" />
													</f:subview>
												</f:facet>

												<f:facet name="RPC">
													<f:subview id="rpcFacility">
														<jsp:include page="rpcFacility.jsp" />
													</f:subview>
												</f:facet>

												<f:facet name="RPR">
													<f:subview id="rprFacility">
														<jsp:include page="rprFacility.jsp" />
													</f:subview>
												</f:facet>

												<f:facet name="RPE">
													<f:subview id="rpeFacility">
														<jsp:include page="rpeFacility.jsp" />
													</f:subview>
												</f:facet>

												<f:facet name="TIV">
													<f:subview id="tivFacility">
														<jsp:include page="tivFacility.jsp" />
													</f:subview>
												</f:facet>

											</af:switcher>
										</f:facet>
										<f:facet name="eu">
											<af:switcher
												facetName="#{applicationDetail.application.requestType}">
												<f:facet name="NSR Application">
													<f:subview id="ptioEU">
														<jsp:include page="ptioEU.jsp" />
													</f:subview>
												</f:facet>

												<f:facet name="Title V Permit Application">
													<f:subview id="tvEU">
														<jsp:include page="tvEU.jsp" />
													</f:subview>
												</f:facet>

												<f:facet name="Permit-by-rule Notification">
													<f:subview id="pbrEU">
														<jsp:include page="pbrEU.jsp" />
													</f:subview>
												</f:facet>

												<f:facet name="Request Administrative Permit Modification">
													<f:subview id="rpcEU">
														<jsp:include page="rpcEU.jsp" />
													</f:subview>
												</f:facet>

												<f:facet name="Request Permit Rescission">
													<f:subview id="rprEU">
														<jsp:include page="rprEU.jsp" />
													</f:subview>
												</f:facet>

												<f:facet name="Request Permit Extension">
													<f:subview id="rpeEU">
														<jsp:include page="rpeEU.jsp" />
													</f:subview>
												</f:facet>

												<f:facet name="Title IV Acid Rain Application">
													<f:subview id="tivEU">
														<jsp:include page="tivEU.jsp" />
													</f:subview>
												</f:facet>
											</af:switcher>
											<f:facet name="scenario">
												<f:subview id="tvAltScenario">
													<jsp:include page="tvAltScenario.jsp" />
												</f:subview>
											</f:facet>

											<f:facet name="insignificantEU">
												<af:switcher
													facetName="#{applicationDetail.application.requestType}"
													defaultFacet="Title V Permit Application">
													<f:facet name="Title V Permit Application">
														<f:subview id="tvIEU">
															<jsp:include page="tvIEU.jsp" />
														</f:subview>
													</f:facet>
													<f:facet name="Request Administrative Permit Modification">
														<f:subview id="rpcEU">
															<jsp:include page="rpcEU.jsp" />
														</f:subview>
													</f:facet>
												</af:switcher>
											</f:facet>
										</f:facet>

										<f:facet name="excludedEU">
											<af:panelGroup layout="vertical">
												<af:panelHeader text="Emissions Unit" />
												<af:panelForm labelWidth="200">
													<af:inputText label="AQD EU ID :"
														value="#{applicationDetail.selectedExcludedEU.epaEmuId}"
														readOnly="true" />
													<af:inputText label="AQD EU description :"
														value="#{applicationDetail.selectedExcludedEU.euDesc}"
														readOnly="true" 
														columns="80" rows="4"/>
													<af:selectOneChoice label="Copy data from EU: "
														rendered="#{applicationDetail.euCopyAllowed}"
														value="#{applicationDetail.copyEUId}">
														<f:selectItems
															value="#{applicationDetail.eusAvailableForCopy}" />
													</af:selectOneChoice>
												</af:panelForm>
											</af:panelGroup>
										</f:facet>

										<f:facet name="notIncludableEU">
											<f:subview id="notIncludableEU">
												<jsp:include page="notIncludableEU.jsp" />
											</f:subview>
										</f:facet>

										<f:facet name="euShuttle">
											<af:panelHeader text="Emissions Units">
												<af:panelForm>
													<af:selectManyShuttle leadingHeader="Excluded EUs"
														trailingHeader="Included EUs" size="15"
														value="#{applicationDetail.includedEUs}">
														<f:selectItems value="#{applicationDetail.availableEUs}" />
													</af:selectManyShuttle>

													<af:outputFormatted
														inlineStyle="color: orange; font-weight: bold;"
														value="<b>Any data that you have entered in this application for an included unit will be deleted if you exclude the unit.</b>" />
										
												</af:panelForm>
												<af:panelForm>
													<af:selectOneChoice label="Copy data from EU: "
														value="#{applicationDetail.copyEUId}"
														rendered="#{applicationDetail.euCopyAllowed}">
														<f:selectItems
															value="#{applicationDetail.eusAvailableForCopy}" />
													</af:selectOneChoice>
												</af:panelForm>
											</af:panelHeader>
										</f:facet>

										<f:facet name="euGroup">
											<af:panelHeader text="Emissions Unit Group">
												<af:inputText id="euGroupNameText" label="EU Group Name : "
													readOnly="#{!applicationDetail.editMode}"
													value="#{applicationDetail.selectedEUGroup.tvEuGroupName }" />
												<af:selectManyShuttle id="euGroupShuttle"
													leadingHeader="Available EUs" trailingHeader="EUs in Group"
													size="15" rendered="#{applicationDetail.editMode}"
													value="#{applicationDetail.tvEusInGroup}">
													<f:selectItems
														value="#{applicationDetail.tvEusAvailableForGroup}" />
												</af:selectManyShuttle>
												<af:outputText id="euGroupTableCaption"
													value="EUs in Group: "
													rendered="#{!applicationDetail.editMode}"
													inlineStyle="font-size: 13px;font-weight:bold" />
												<af:table emptyText=" " var="groupEU" bandingInterval="1"
													banding="row"
													value="#{applicationDetail.tvEusObjectsInGroup}"
													width="98%" id="euTable"
													rows="#{applicationDetail.pageLimit}"
													rendered="#{!applicationDetail.editMode}">
													<af:column formatType="text" headerText="Emissions Unit ID"
														id="euIDColumn">
														<af:outputText id="euIDText" value="#{groupEU.epaEmuId}" />
													</af:column>
													<af:column formatType="text"
														headerText="Company Equipment ID" id="compEqptIdColumn">
														<af:outputText id="compEqptIdText"
															value="#{groupEU.companyId}" />
													</af:column>
												</af:table>
											</af:panelHeader>
										</f:facet>

										<f:facet name="euCopy">
											<af:panelHeader text="Copy EU Data">
												<af:panelForm labelWidth="200">
													<af:inputText id="EUIdText" label="AQD EU ID :"
														value="#{applicationDetail.selectedEU.fpEU.epaEmuId}"
														readOnly="true" />
													<af:inputText id="EUDescriptionText"
														label="AQD EU description :"
														value="#{applicationDetail.selectedEU.fpEU.euDesc}"
														readOnly="true" />
													<af:inputText id="CompanyEUIdText" label="Company EU ID :"
														value="#{applicationDetail.selectedEU.fpEU.companyId}"
														readOnly="true" />
													<af:inputText id="CompanyEUDescText"
														label="Company EU Description :"
														rows="4" columns="80" maximumLength="240"
														value="#{applicationDetail.selectedEU.fpEU.regulatedUserDsc}"
														readOnly="true" />
												</af:panelForm>
												<af:selectManyShuttle id="euCopyShuttle"
													leadingHeader="Available EUs"
													trailingHeader="Copy Data to EU(s)" size="15"
													readOnly="false"
													value="#{applicationDetail.eusTargetedForCopy}">
													<f:selectItems
														value="#{applicationDetail.eusAvailableForCopy}" />
												</af:selectManyShuttle>
											</af:panelHeader>
										</f:facet>

									</af:switcher>

								</af:panelGroup>
							</h:panelGrid>

							<f:facet name="bottom">
								<af:panelGroup layout="vertical">
									<f:subview id="app_attachments">
										<jsp:include page="app_attachments.jsp" />
									</f:subview>

									<af:showDetailHeader disclosed="true" text="Notes"
										rendered="#{applicationDetail.selectedTreeNode.type == 'application' && applicationDetail.internalApp}">
										<af:table id="DAPCComments" emptyText=" " var="notes"
											width="98%"
											value="#{applicationDetail.application.applicationNotes}"
											partialTriggers="DAPCComments:AddCommentButton">
											<af:column headerText="Note ID" sortable="true" sortProperty="noteId" formatType="text">
												<af:commandLink id="noteIdLink"
													text="#{notes.noteId}" useWindow="true" windowWidth="650"
													windowHeight="300"
													action="#{applicationDetail.startEditNote}"
													shortDesc="Edit Note">
												</af:commandLink>
											</af:column>
											<af:column headerText="Note" sortable="true" sortProperty="noteTxt" formatType="text">
												<af:outputText 
													truncateAt="90"	
													value="#{notes.noteTxt}" 
													shortDesc="#{notes.noteTxt}"/>
											</af:column>
											<af:column headerText="User Name" sortable="true" sortProperty="userId" formatType="text">
												<af:selectOneChoice value="#{notes.userId}" readOnly="true">
													<f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
												</af:selectOneChoice>
											</af:column>
											<af:column headerText="Date" sortable="true" sortProperty="dateEntered" formatType="text">
												<af:selectInputDate readOnly="true"
													value="#{notes.dateEntered}" />
											</af:column>

											<f:facet name="footer">
												<afh:rowLayout halign="center">
													<af:panelButtonBar>
														<af:commandButton text="Add"
															rendered="#{!applicationDetail.readOnlyUser}"
															id="AddCommentButton" useWindow="true" windowWidth="650"
															windowHeight="300"
															action="#{applicationDetail.startAddNote}" />
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
									</af:showDetailHeader>
									<af:objectSeparator />

									<af:panelGroup
										rendered="#{!applicationDetail.relocationClass && !applicationDetail.delegationClass}"
										layout="vertical">
										<afh:rowLayout halign="center">
											<af:switcher defaultFacet="application"
												facetName="#{applicationDetail.selectedTreeNode.type}">
												<f:facet name="application">
													<f:subview id="appBtns">
														<jsp:include page="appBtns.jsp" />
													</f:subview>
												</f:facet>
												<f:facet name="eu">
													<f:subview id="euBtns">
														<jsp:include page="euBtns.jsp" />
													</f:subview>
												</f:facet>
												<f:facet name="insignificantEU">
													<af:switcher
														facetName="#{applicationDetail.application.requestType}"
														defaultFacet="Title V Permit Application">
														<f:facet name="Title V Permit Application">
															<f:subview id="ieuBtns">
																<jsp:include page="ieuBtns.jsp" />
															</f:subview>
														</f:facet>
														<f:facet name="Request Administrative Permit Modification">
															<f:subview id="rpcEUBtns">
																<jsp:include page="euBtns.jsp" />
															</f:subview>
														</f:facet>
													</af:switcher>
												</f:facet>
												<f:facet name="excludedEU">
													<af:panelButtonBar>
														<af:commandButton id="euIncludeEUBtn"
															text="Include EU in application"
															rendered="#{applicationDetail.editAllowed}"
															action="#{applicationDetail.includeEU}" />
													</af:panelButtonBar>
												</f:facet>
												<f:facet name="notIncludableEU">
													<af:panelButtonBar>
													</af:panelButtonBar>
												</f:facet>
												<f:facet name="euShuttle">
													<af:panelButtonBar>
														<af:commandButton id="euShuttleSaveBtn" text="Save"
															action="#{applicationDetail.updateApplicationEUs}" />
														<af:commandButton id="euShuttleCancelBtn" text="Done"
															action="#{applicationDetail.cancelEUShuttle}" />
													</af:panelButtonBar>
												</f:facet>
												<f:facet name="euGroup">
													<f:subview id="euGroupBtns">
														<jsp:include page="euGroupBtns.jsp" />
													</f:subview>
												</f:facet>
												<f:facet name="euCopy">
													<af:panelButtonBar>
														<af:commandButton id="euCopySaveBtn" text="Copy Data"
															action="#{applicationDetail.copyApplicationEUData}" />
													</af:panelButtonBar>
												</f:facet>
												<f:facet name="scenario">
													<f:subview id="scenarioBtns">
														<jsp:include page="scenarioBtns.jsp" />
													</f:subview>
												</f:facet>
											</af:switcher>
										</afh:rowLayout>
									</af:panelGroup>

									<af:panelGroup rendered="#{applicationDetail.relocationClass}"
										layout="vertical">
										<afh:rowLayout halign="center"
											rendered="#{!applicationDetail.readOnlyUser}">
											<af:panelButtonBar>
												<af:commandButton text="Save"
													disabled="#{!relocation.internalApp && !relocation.staging}"
													rendered="#{relocation.relocateRequest.applicationTypeCD != '' && relocation.editable}"
													action="#{relocation.save}" />
												<af:commandButton text="Submit"
													disabled="#{!relocation.internalApp && !relocation.staging || !applicationDetail.impactFullEnabled}"
													rendered="#{!relocation.editable && relocation.relocateRequest.applicationTypeCD != '' && relocation.relocateRequest.submittedDate==null && relocation.relocateRequest.requestId>0}"
													actionListener="#{relocation.submit}" />
												<af:commandButton text="Delete"
													disabled="#{!relocation.internalApp && !relocation.staging}"
													rendered="#{!relocation.editable && !relocation.relocateRequest.newRecord && relocation.relocateRequest.submittedDate==null}"
													action="#{relocation.deleteRequest}" />
												<af:commandButton text="Edit"
													disabled="#{!relocation.internalApp && !relocation.staging}"
													rendered="#{!relocation.editable || (!relocation.editable && relocation.admin)}"
													actionListener="#{relocation.startEditRequest}" />
												<af:commandButton text="Workflow Task"
													rendered="#{relocation.internalApp && relocation.fromTODOList}"
													action="#{relocation.goToCurrentWorkflow}" />
												<af:commandButton text="Cancel" immediate="true"
													rendered="#{relocation.editable || relocation.relocateRequest.newRecord}"
													action="#{relocation.cancelEditRequest}" />
											</af:panelButtonBar>
										</afh:rowLayout>
									</af:panelGroup>

									<af:panelGroup rendered="#{applicationDetail.delegationClass}"
										layout="vertical">
										<afh:rowLayout halign="center"
											rendered="#{!applicationDetail.readOnlyUser}">
											<af:panelButtonBar>
												<af:commandButton text="Save"
													rendered="#{delegation.delegationRequest.applicationTypeCD != '' && delegation.editable}"
													action="#{delegation.save}" />
												<af:commandButton text="Submit"
													rendered="#{!delegation.editable && delegation.delegationRequest.applicationTypeCD != '' && delegation.delegationRequest.submittedDate==null && delegation.delegationRequest.applicationID>0 || !applicationDetail.impactFullEnabled}"
													actionListener="#{delegation.submit}" />
												<af:commandButton text="Delete"
													rendered="#{!delegation.editable && !delegation.delegationRequest.newRecord && delegation.delegationRequest.submittedDate==null}"
													action="#{delegation.deleteApplication}" />
												<af:commandButton text="Edit" disabled="false"
													rendered="#{(!delegation.editable && delegation.delegationRequest.submittedDate==null) || (!delegation.editable && delegation.admin)}"
													actionListener="#{delegation.startEditRequest}" />
												<af:commandButton text="Cancel" immediate="true"
													rendered="#{delegation.editable || delegation.delegationRequest.newRecord}"
													action="#{delegation.cancelEditRequest}" />
											</af:panelButtonBar>
										</afh:rowLayout>
									</af:panelGroup>

								</af:panelGroup>
							</f:facet>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>

			</af:page>
			<af:iterator value="#{applicationDetail}" var="validationBean" id="v">
				<%@ include file="../util/validationComponents.jsp"%>
			</af:iterator>
		</af:form>
	</af:document>
</f:view>