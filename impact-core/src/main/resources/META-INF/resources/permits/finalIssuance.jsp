<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
	<af:document title="Final Issuance">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form usesUpload="true"
			partialTriggers="ThePage:delIssue ThePage:uploadIssuanceDoc">
			<af:page id="ThePage" var="foo" value="#{menuModel.model}"
				title="Final Issuance">
				<%@ include file="../permits/header.jsp"%>

				<mu:setProperty property="#{permitDetail.currentIssuanceAction}"
					value="#{permitReference.finalIssuanceType}" />

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
								<af:panelHeader text="Final">
									<af:panelGroup>
										<af:panelForm labelWidth="44%" fieldWidth="56%" rows="1"
											maxColumns="2" partialTriggers="effectiveDate issuanceDate">
											<af:selectOneChoice label="Issuance Status :" readOnly="true"
												id="finalIssuanceStatus"
												value="#{permitDetail.permit.finalIssuanceStatusCd}">
												<mu:selectItems id="issuanceStatus"
													value="#{permitReference.issuanceStatusDefs}" />
											</af:selectOneChoice>
											
											<af:selectInputDate label="Permit Basis Date :"
												id="permitBasisDate" readOnly="#{! permitDetail.editMode}"
												value="#{permitDetail.permit.permitBasisDate}"
												rendered="#{permitDetail.permit.permitType == 'TVPTO'}">
												<af:validateDateTimeRange 
													minimum="1900-01-01"
													minimumMessageDetail="#{'The date entered {1} is before the first valid date {2}'}"
													maximum="#{infraDefs.currentDate}"
													maximumMessageDetail="#{'The date entered {1} is in the future.'}"/>
											</af:selectInputDate>

											<af:selectInputDate label="Issuance Date :" autoSubmit="true" immediate="true"
												id="issuanceDate" readOnly="#{! permitDetail.editMode}"
												value="#{permitDetail.finalIssueDate}">
												<f:converter converterId="us.oh.state.epa.stars2.webcommon.converter.TwoDigitYearDateConverter" /> 
												<af:validateDateTimeRange 
													minimum="#{null != permitDetail.oldestApplicationReceivedDate 
																? permitDetail.oldestApplicationReceivedDate 
																: permitDetail.defaultMinimumDate}" 
													minimumMessageDetail="#{null != permitDetail.oldestApplicationReceivedDate
																 ? 'The date entered {1} is before the oldest application received date {2}'
																 : 'The date entered {1} is before the first valid date {2}'}" />
											</af:selectInputDate>

											<af:inputText label="Initial Invoice Amount :" columns="10"
												maximumLength="50"
												value="#{permitDetail.permit.initialInvoiceAmount}"
												readOnly="true"
												rendered="#{permitDetail.permit.permitType == 'NSR'}">
												<af:convertNumber type='currency' locale="en-US"
													minFractionDigits="2" />
											</af:inputText>

											<af:inputText label="Final Invoice Amount :" columns="10"
												maximumLength="50"
												value="#{permitDetail.permit.finalInvoiceAmount}"
												readOnly="true"
												rendered="#{permitDetail.permit.permitType == 'NSR'}">
												<af:convertNumber type='currency' locale="en-US"
													minFractionDigits="2" />
											</af:inputText>

											<af:switcher facetName="#{permitDetail.permit.permitType}"
												defaultFacet="PTIO">
												<f:facet name="NSR">
													<af:selectInputDate label="Permit Sent Out Date :"
														id="permitSentOutDate" autoSubmit="true"
														readOnly="#{! (permitDetail.editMode || permitDetail.semiEditMode)}"
														value="#{permitDetail.permit.permitSentOutDate}">
														<f:converter converterId="us.oh.state.epa.stars2.webcommon.converter.TwoDigitYearDateConverter" />
														<af:validateDateTimeRange 
															minimum="#{null != permitDetail.oldestApplicationReceivedDate 
																? permitDetail.oldestApplicationReceivedDate 
																: permitDetail.defaultMinimumDate}" 
															minimumMessageDetail="#{null != permitDetail.oldestApplicationReceivedDate
																 ? 'The date entered {1} is before the oldest application received date {2}'
																 : 'The date entered {1} is before the first valid date {2}'}" />
													</af:selectInputDate>
												</f:facet>
												<f:facet name="TVPTO">
													<af:selectInputDate label="Effective Date :"
														id="effectiveDate" autoSubmit="true" immediate="true"
														readOnly="#{! permitDetail.editMode}"
														value="#{permitDetail.effectiveDate}">
														<f:converter converterId="us.oh.state.epa.stars2.webcommon.converter.TwoDigitYearDateConverter" />
														<af:validateDateTimeRange 
															minimum="#{null != permitDetail.oldestApplicationReceivedDate 
																? permitDetail.oldestApplicationReceivedDate 
																: permitDetail.defaultMinimumDate}" 
															minimumMessageDetail="#{null != permitDetail.oldestApplicationReceivedDate
																 ? 'The date entered {1} is before the oldest application received date {2}'
																 : 'The date entered {1} is before the first valid date {2}'}" />
													</af:selectInputDate>
												</f:facet>
											</af:switcher>

											<af:selectInputDate label="Expiration Date :" id="expDate"
												rendered="#{permitDetail.expirationDateNeeded}"
												readOnly="#{! permitDetail.editMode}"
												value="#{permitDetail.permit.expirationDate}">
												<af:validateDateTimeRange
													minimum="#{permitDetail.finalIssueDate}"
													minimumMessageDetail="Expiration Date must fall on or after the Final Issuance date." />
											</af:selectInputDate>

											<af:selectInputDate label="Mod. Effective Date :"
												id="effDate" readOnly="#{! permitDetail.editMode}"
												rendered="false"
												value="#{permitDetail.permit.modEffectiveDate}">
												<af:validateDateTimeRange
													minimum="#{permitDetail.finalIssueDate}" />
											</af:selectInputDate>

											<af:selectInputDate label="Rescission Date :" id="recissionDate"
												rendered="#{permitDetail.finalIssueDate != null && permitDetail.permitIssuedFinal}"
												readOnly="#{! (permitDetail.editMode || (permitDetail.semiEditMode && permitDetail.NSRAdmin))}"
												value="#{permitDetail.permit.recissionDate}">
												<af:validateDateTimeRange minimum="1900-01-01" />
											</af:selectInputDate>

											<%-- this drop-down list is displayed only for permits that were created
											     prior to release 7.0 and have a value for invoice paid --%>
											<af:selectOneChoice id="invoicePaid" unselectedLabel=" "
												readOnly="true"
												rendered="#{permitDetail.permit.permitType == 'NSR'
															&& permitDetail.permit.invoicePaid != null}"
												label="Invoice Paid?"
												value="#{permitDetail.permit.invoicePaid}"
												tip="*legacy data">
												<f:selectItems value="#{permitReference.invoicePaidDefs}" />
											</af:selectOneChoice>

											<af:selectOneChoice id="commentTransmittalLettersSent"
												unselectedLabel=" " readOnly="#{!permitDetail.editMode}"
												rendered="#{permitDetail.permit.permitType == 'NSR' && permitDetail.permit.commentTransmittalRequired}"
												label="Comment Transmittal Letters Sent?"
												value="#{permitDetail.permit.commentTransmittalLettersSentFlag}">
												<f:selectItem itemLabel="Yes" itemValue="Y" />
												<f:selectItem itemLabel="No" itemValue="N" />
											</af:selectOneChoice>

										</af:panelForm>

									</af:panelGroup>
								</af:panelHeader>
								<%
									/* Final end */
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
									<af:panelButtonBar
										rendered="#{permitDetail.editMode && permitDetail.permit.finalIssuanceStatusCd != permitReference.issuanceStatusIssued}">

									</af:panelButtonBar>
									<af:panelButtonBar
										rendered="#{!permitDetail.editMode && permitDetail.editAllowed && permitDetail.permit.finalIssuanceStatusCd != permitReference.issuanceStatusIssued}">
										<af:commandButton text="Zip Documents"
											action="#{permitDetail.getPermitDocsZipFile}"
											disabled="#{permitDetail.readOnlyUser}" rendered="false" />

										<af:commandButton text="Add Permit to Issuance List"
											rendered="#{permitDetail.permit.finalIssuanceStatusCd == permitReference.issuanceStatusNotReady}"
											disabled="#{permitDetail.readOnlyUser}"
											action="#{permitDetail.markReadyIssuance}">
										</af:commandButton>
										<af:commandButton text="Deselect From Issuance List"
											rendered="#{permitDetail.permit.finalIssuanceStatusCd == permitReference.issuanceStatusReady}"
											action="#{permitDetail.unprepareIssuance}">
										</af:commandButton>
										<af:commandButton text="Skip Issuance" rendered="false"
											action="#{permitDetail.skipIssuance}">
										</af:commandButton>
										<af:commandButton text="Validate Issuance"
											rendered="#{permitDetail.permit.finalIssuance != null}"
											disabled="#{permitDetail.readOnlyUser}"
											action="#{permitDetail.validateIssuance}">
										</af:commandButton>
										<af:commandButton
											rendered="#{permitDetail.permit.finalIssuanceStatusCd == permitReference.issuanceStatusReady && false}"
											text="Finalize Issuance"
											returnListener="#{permitDetail.finalizeIssuance}"
											action="#{confirmWindow.confirm}" useWindow="true"
											windowWidth="#{confirmWindow.width}"
											windowHeight="#{confirmWindow.height}">
											<t:updateActionListener property="#{confirmWindow.type}"
												value="#{permitDetail.doSelectedConfirmType}" />
											<t:updateActionListener property="#{confirmWindow.message}"
												value="#{permitDetail.doSelectedConfirmMsg}" />
										</af:commandButton>
										<af:commandButton text="Reset Issuance" id="delIssue"
											rendered="#{permitDetail.permit.finalIssuanceStatusCd != permitReference.issuanceStatusReady}"
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
								</f:subview> --%>
								<afh:rowLayout halign="center" rendered="#{!permitDetail.readOnlyUser}">
								  <af:switcher defaultFacet="view"
								    facetName="#{(permitDetail.editMode || permitDetail.semiEditMode)? 'edit': 'view'}">
								    <f:facet name="view">
								      <af:panelButtonBar>
								        <af:commandButton text="Edit"
								          rendered="#{permitDetail.permit.permitType == 'TVPTO' ? permitDetail.editAllowed : permitDetail.semiEditAllowed}"
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
