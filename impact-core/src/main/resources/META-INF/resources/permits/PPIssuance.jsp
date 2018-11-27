<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
	<af:document title="Proposed Publication">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form usesUpload="true"
			partialTriggers="ThePage:reissue ThePage:delIssue ThePage:uploadIssuanceDoc">
			<af:page id="ThePage" var="foo" value="#{menuModel.model}"
				title="Proposed Publication">
				<%@ include file="../permits/header.jsp"%>

				<mu:setProperty property="#{permitDetail.currentIssuanceAction}"
					value="#{permitReference.ppIssuanceType}" />

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
								<af:panelHeader text="Proposed">
									<af:panelGroup>
										<af:panelForm labelWidth="21%" rows="1" maxColumns="1"
											fieldWidth="69%">
											<af:selectOneChoice label="Publication Status :" readOnly="true"
												id="ppIssuanceStatus"
												value="#{permitDetail.permit.ppIssuanceStatusCd}">
												<mu:selectItems
													value="#{permitReference.issuanceStatusDefs}" />
											</af:selectOneChoice>
										</af:panelForm>

										<af:panelHeader text="USEPA review" size="2" />
										<af:panelForm labelWidth="44%" rows="1" maxColumns="2"
											fieldWidth="56%" partialTriggers="usepaReceivedPermitDate">
											<af:selectInputDate label="Date Permit Sent to USEPA :"
												readOnly="#{! permitDetail.editMode}"
												value="#{permitDetail.permit.usepaPermitSentDate}">
												<af:validateDateTimeRange
													minimum="1900-01-01" />
											</af:selectInputDate>	
											<af:selectInputDate label="Date USEPA Received Permit :"
												id="usepaReceivedPermitDate"
												readOnly="#{! permitDetail.editMode}"
												autoSubmit="true" immediate="true"
												value="#{permitDetail.permit.usepaReceivedPermitDate}">
												<f:converter converterId="us.oh.state.epa.stars2.webcommon.converter.TwoDigitYearDateConverter" /> 
												<af:validateDateTimeRange
													minimum="1900-01-01" />
											</af:selectInputDate>
											<af:selectInputDate label="USEPA Review End Date :" id="usepaReviewEndDate"
												readOnly="#{! permitDetail.editMode}"
												value="#{permitDetail.permit.usepaCompleteDate}">
												<af:validateDateTimeRange
													minimum="1900-01-01" />
											</af:selectInputDate>
											<af:selectBooleanCheckbox inlineStyle="margin-left: 0px;"
												label="Expedited Review Requested :"
												readOnly="#{! permitDetail.editMode}"
												value="#{permitDetail.permit.usepaExpedited}" />
											<af:selectOneChoice label="Review Outcome :"
												readOnly="#{! permitDetail.editMode}"
												value="#{permitDetail.permit.usepaOutcomeCd}">
												<mu:selectItems value="#{permitReference.usepaOutcomeDefs}" />
											</af:selectOneChoice>
										</af:panelForm>
									</af:panelGroup>
								</af:panelHeader>
								<%
									/* Draft end */
								%>
								<f:subview id="permit_attachments">
										<jsp:include page="permit_attachments.jsp" />
								</f:subview>
								<af:objectSpacer height="5" />
								<af:panelForm maxColumns="1" rows="1" labelWidth="21%"
									fieldWidth="69%">
									<af:panelGroup rendered="#{permitDetail.supersededDoc != null}">
										<af:outputText inlineStyle="font-size: 12px"
											value="Note: this is an APA permit - please use this " />
										<af:goLink targetFrame="_blank"
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
											rendered="#{permitDetail.permit.ppIssuanceStatusCd == permitReference.issuanceStatusIssued}"
											returnListener="#{permitDetail.reissuePp}"
											disabled="#{permitDetail.readOnlyUser}"
											action="#{confirmWindow.confirm}" useWindow="true"
											windowWidth="#{confirmWindow.width}"
											windowHeight="#{confirmWindow.height}">
											<t:updateActionListener property="#{confirmWindow.type}"
												value="#{confirmWindow.yesNo}" />
											<t:updateActionListener property="#{confirmWindow.message}"
												value="Re-issue function will remove all data from PP Publication. Click 'Yes' to continue or 'No' to cancel the function." />
										</af:commandButton>
									</af:panelButtonBar>
									<af:panelButtonBar
										rendered="#{!permitDetail.editMode && permitDetail.editAllowed && permitDetail.permit.ppIssuanceStatusCd != permitReference.issuanceStatusIssued}">
										<af:commandButton text="Zip Documents" rendered="false"
											action="#{permitDetail.getPermitDocsZipFile}" />
										<af:commandButton text="Add Permit to Publication List"
											disabled="#{permitDetail.readOnlyUser}"
											rendered="#{permitDetail.permit.ppIssuanceStatusCd == permitReference.issuanceStatusNotReady}"
											action="#{permitDetail.markReadyIssuance}" />
										<af:commandButton text="Deselect From Publication List"
											disabled="#{permitDetail.readOnlyUser}"
											rendered="#{permitDetail.permit.ppIssuanceStatusCd == permitReference.issuanceStatusReady}"
											action="#{permitDetail.unprepareIssuance}" />
										<af:commandButton text="Skip Publication" rendered="false"
											action="#{permitDetail.skipIssuance}" />
										<af:commandButton text="Validate Publication"
											disabled="#{permitDetail.readOnlyUser}"
											rendered="#{permitDetail.permit.ppIssuance != null}"
											action="#{permitDetail.validateIssuance}">
										</af:commandButton>
										<af:commandButton text="Finalize Publication"
											disabled="#{permitDetail.readOnlyUser}"
											rendered="#{permitDetail.permit.ppIssuanceStatusCd == permitReference.issuanceStatusReady && false}"
											action="#{permitDetail.finalizeIssuance}" />
										<af:commandButton text="Reset Publication" id="delIssue"
											disabled="#{permitDetail.readOnlyUser}"
											rendered="#{permitDetail.permit.ppIssuanceStatusCd != permitReference.issuanceStatusReady}"
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
