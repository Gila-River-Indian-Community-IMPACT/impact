<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
	<af:document title="Draft Publication">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form usesUpload="true"
			partialTriggers="ThePage:reissue ThePage:delIssue ThePage:uploadIssuanceDoc">
			<af:page id="ThePage" var="foo" value="#{menuModel.model}"
				title="Draft Publication">
				<%@ include file="../permits/header.jsp"%>

				<mu:setProperty property="#{permitDetail.currentIssuanceAction}"
					value="#{permitReference.draftIssuanceType}" />

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
							<af:panelGroup>
								<%
									/* Draft begin */
								%>
								<af:panelHeader text="Draft">
									<af:panelGroup>
										<af:panelForm maxColumns="1" rows="1" labelWidth="21%"
											fieldWidth="69%" partialTriggers="pnt">
											<af:selectOneChoice label="Publication Status :"
												readOnly="true" id="draftIssuanceStatus"
												value="#{permitDetail.permit.draftIssuanceStatusCd}">
												<mu:selectItems id="issuanceStatus"
													value="#{permitReference.issuanceStatusDefs}" />
											</af:selectOneChoice>
											<af:selectInputDate
												label="#{permitDetail.permit.permitType=='NSR' ? 'Tech Analysis Date :' : 'Draft Publication Date :'}"
												readOnly="#{! permitDetail.editMode || (permitDetail.permit.draftIssuanceStatusCd == 'I' && !permitDetail.stars2Admin)}"
												rendered="#{permitDetail.permit.permitType=='NSR'}"
												value="#{permitDetail.permit.draftIssueDate}">
												<af:validateDateTimeRange minimum="1900-01-01" />
											</af:selectInputDate>
											<%-- temporary hide the public notice type/text until further update from WY
											<af:selectOneRadio label="Public Notice Type :"
												autoSubmit="true" immediate="true" readOnly="true" id="pnt"
												value="#{permitDetail.publicNoticeType}" layout="horizontal">
												<f:selectItems value="#{permitDetail.publicNoticeTypes}" />
											</af:selectOneRadio>
											<af:inputText label="Public Notice Text :" rows="5"
												columns="112" maximumLength="4000"
												readOnly="#{(!permitDetail.editMode) || (permitDetail.publicNoticeType == 'SW') || (permitDetail.publicNoticeType == 'PSW')}"
												value="#{permitDetail.permit.publicNoticeText}" /> --%>
										</af:panelForm>
										<af:panelHeader text="Public Notice" size="2" />
										<af:panelForm labelWidth="44%" rows="1" partialTriggers="pd"
											maxColumns="2" fieldWidth="56%">
											<af:selectInputDate label="Notice Request Date :"
												readOnly="#{! permitDetail.editMode}" rendered="false"
												value="#{permitDetail.permit.draftIssuance.publicNoticeRequestDate}">
												<af:validateDateTimeRange minimum="1900-01-01" />
											</af:selectInputDate>
											<af:selectOneChoice label="Newspaper :"
												readOnly="#{! permitDetail.editMode}"
												id="publicNoticeNewspaperCd"
												value="#{permitDetail.permit.publicNoticeNewspaperCd}"
												unselectedLabel="">
												<mu:selectItems id="publicNoticeNewspapers"
													value="#{permitReference.newspaperDefs}" />
											</af:selectOneChoice>
											<af:selectInputDate label="Newspaper Publish Date :"
												id="pd" readOnly="#{! permitDetail.editMode}"
												autoSubmit="true" immediate="true"
												value="#{permitDetail.permit.draftIssuance.publicNoticePublishDate}">
												<f:converter converterId="us.oh.state.epa.stars2.webcommon.converter.TwoDigitYearDateConverter" /> 
												<af:validateDateTimeRange
													minimum="#{null != permitDetail.oldestApplicationReceivedDate 
																? permitDetail.oldestApplicationReceivedDate 
																: permitDetail.defaultMinimumDate}" 
													minimumMessageDetail="#{null != permitDetail.oldestApplicationReceivedDate
																			 ? 'The date entered {1} is before the oldest application received date {2}'
																			 : 'The date entered {1} is before the first valid date {2}'}" />
											</af:selectInputDate>
											<af:selectInputDate id="endOfCommentPeriod"
												label="Comments Period End Date :"
												readOnly="#{! permitDetail.editMode}"
												value="#{permitDetail.permit.draftIssuance.publicCommentEndDate}">
												<af:validateDateTimeRange 
													minimum="#{null != permitDetail.oldestApplicationReceivedDate 
																? permitDetail.oldestApplicationReceivedDate 
																: permitDetail.defaultMinimumDate}" 
													minimumMessageDetail="#{null != permitDetail.oldestApplicationReceivedDate
																 ? 'The date entered {1} is before the oldest application received date {2}'
																 : 'The date entered {1} is before the first valid date {2}'}" />
											</af:selectInputDate>
											<af:selectInputDate label="Newspaper Affidavit Date :"
												id="newspaperAffidavitDate"
												readOnly="#{! (permitDetail.editMode || (permitDetail.semiEditMode && permitDetail.permit.permitType == 'TVPTO'))}"
												value="#{permitDetail.permit.draftIssuance.newspaperAffidavitDate}">
												<af:validateDateTimeRange 
													minimum="#{null != permitDetail.oldestApplicationReceivedDate 
																? permitDetail.oldestApplicationReceivedDate 
																: permitDetail.defaultMinimumDate}" 
													minimumMessageDetail="#{null != permitDetail.oldestApplicationReceivedDate
																 ? 'The date entered {1} is before the oldest application received date {2}'
																 : 'The date entered {1} is before the first valid date {2}'}" />
											</af:selectInputDate>
										</af:panelForm>
										<%
											/* Public Notice */
										%>

										<af:panelHeader text="Public Hearing" size="2" />
										<af:panelForm labelWidth="44%" rows="2" maxColumns="2"
											fieldWidth="56%">
											<af:selectInputDate label="Hearing Date :"
												readOnly="#{! permitDetail.editMode}"
												value="#{permitDetail.permit.draftIssuance.hearingDate}">
												<af:validateDateTimeRange minimum="1900-01-01" />
											</af:selectInputDate>
											<af:selectInputDate label="Request Date :"
												readOnly="#{! permitDetail.editMode}"
												value="#{permitDetail.permit.draftIssuance.hearingRequestedDate}">
												<af:validateDateTimeRange minimum="1900-01-01" />
											</af:selectInputDate>
											<af:selectInputDate label="Publish Date :"
												readOnly="#{! permitDetail.editMode}"
												value="#{permitDetail.permit.draftIssuance.hearingNoticePublishDate}">
												<af:validateDateTimeRange minimum="1900-01-01" />
											</af:selectInputDate>
										</af:panelForm>
										<%
											/* Public Hearing */
										%>
									</af:panelGroup>
								</af:panelHeader>

								<%
									/* Draft end */
								%>
								
								<f:subview id="permit_attachments">
										<jsp:include page="permit_attachments.jsp" />
								</f:subview>

								<%-- 								<af:objectSpacer height="5" />
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
								</af:panelForm> --%>

								<af:objectSpacer height="10" />

								<afh:rowLayout halign="center">
									<af:panelButtonBar rendered="#{permitDetail.editAllowed}">
										<af:commandButton text="Re-issue" id="reissue"
											rendered="#{permitDetail.permit.draftIssuanceStatusCd == permitReference.issuanceStatusIssued 
												&& !permitDetail.permit.legacyPermit}" disabled="#{permitDetail.readOnlyUser}"
											returnListener="#{permitDetail.reissueDraft}"
											action="#{confirmWindow.confirm}" useWindow="true"
											windowWidth="#{confirmWindow.width}"
											windowHeight="#{confirmWindow.height}">
											<t:updateActionListener property="#{confirmWindow.type}"
												value="#{confirmWindow.yesNo}" />
											<t:updateActionListener property="#{confirmWindow.message}"
												value="Re-issue function will remove all data from Draft Issuance. Click 'Yes' to continue or 'No' to cancel the function." />
										</af:commandButton>
									</af:panelButtonBar>
									<af:panelButtonBar
										rendered="#{!permitDetail.editMode && permitDetail.editAllowed && permitDetail.permit.draftIssuanceStatusCd != permitReference.issuanceStatusIssued}">
										<af:commandButton text="Zip Documents"
											disabled="#{permitDetail.readOnlyUser}"
											action="#{permitDetail.getPermitDocsZipFile}"
											rendered="false" />
										<af:commandButton text="Add Permit to Publication List"
											rendered="#{permitDetail.permit.draftIssuanceStatusCd == permitReference.issuanceStatusNotReady && !permitDetail.permit.legacyPermit}"
											disabled="#{permitDetail.readOnlyUser}"
											action="#{permitDetail.markReadyIssuance}">
										</af:commandButton>
										<af:commandButton text="Deselect From Publication List"
											rendered="#{permitDetail.permit.draftIssuanceStatusCd == permitReference.issuanceStatusReady}"
											action="#{permitDetail.unprepareIssuance}">
										</af:commandButton>
										<af:commandButton text="Skip Publication" rendered="false"
											action="#{permitDetail.skipIssuance}">
										</af:commandButton>
										<af:commandButton text="Validate Publication"
											rendered="#{permitDetail.permit.draftIssuance != null  && !permitDetail.permit.legacyPermit}"
											disabled="#{permitDetail.readOnlyUser}"
											action="#{permitDetail.validateIssuance}">
										</af:commandButton>
										<af:commandButton text="Finalize Publication"
											rendered="#{permitDetail.permit.draftIssuanceStatusCd == permitReference.issuanceStatusReady && false}"
											action="#{permitDetail.finalizeIssuance}">
										</af:commandButton>
										<af:commandButton text="Reset Publication Status" id="delIssue"
											rendered="#{permitDetail.permit.draftIssuanceStatusCd != permitReference.issuanceStatusReady  && !permitDetail.permit.legacyPermit}"
											disabled="#{permitDetail.readOnlyUser}"
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

								<%-- <f:subview id="permitIssuanceButtons">
									<jsp:include page="editOnlyButtons.jsp" />
								</f:subview>--%>
								
								<afh:rowLayout halign="center" rendered="#{!permitDetail.readOnlyUser}">
								  <af:switcher defaultFacet="view"
								    facetName="#{(permitDetail.editMode || permitDetail.semiEditMode)? 'edit': 'view'}">
								    <f:facet name="view">
								      <af:panelButtonBar>
								        <af:commandButton text="Edit"
								          rendered="#{permitDetail.permit.permitType == 'NSR' ? permitDetail.editAllowed : permitDetail.semiEditAllowed}"
								          action="#{permitDetail.enterEditMode}" />
								        <af:commandButton text="Workflow Task"
								          disabled="#{!permitDetail.fromTODOList}"
								          action="#{permitDetail.goToCurrentWorkflow}" />
								      </af:panelButtonBar>
								    </f:facet>
								    <f:facet name="edit">
								      <af:panelButtonBar>
								        <af:commandButton text="Save changes"
								          action="#{permitDetail.updatePermit}" />
								        <af:commandButton text="Discard changes" immediate="true"
								          action="#{permitDetail.undoPermit}" />
								      </af:panelButtonBar>
								    </f:facet>
								  </af:switcher>
								
								</afh:rowLayout>
								
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
