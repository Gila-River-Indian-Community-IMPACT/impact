<%@ page session="true" contentType="text/html;charset=windows-1252"%> 
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="Compliance Report Detail">
		<af:inputHidden value="#{submitTask.logUserOff}"
			rendered="#{facilityProfile.portalApp}" />
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}"
				title="Compliance Report Detail">
				<af:inputHidden value="#{complianceReport.popupRedirect}"
					rendered="#{complianceReport.internalApp}" />
				<af:inputHidden value="#{submitTask.popupRedirect}"
					rendered="#{complianceReport.portalApp}" />
				<jsp:include flush="true" page="header.jsp" />
	
				<afh:rowLayout halign="center" width="1000" rendered="#{!complianceReport.blankOutPage}">
				<h:panelGrid border="1">
					<af:panelBorder>

						<f:facet name="top">
							<f:subview id="facilityHeader">
								<jsp:include page="/compliance/complianceHeader.jsp" />
							</f:subview>
						</f:facet>

						<f:facet name="right">
							<h:panelGrid columns="1" border="0" width="100%">

								<af:panelGroup layout="vertical" partialTriggers="legacyComplianceReport receivedDate1">
									<af:showDetailHeader text="AQD Staff" disclosed="true" rendered="#{complianceReport.internalApp}">
										<af:objectSpacer height="5" />
										
										<afh:rowLayout halign="left">
											<af:panelForm rows="1" maxColumns="1" width = "100%">
												<af:selectOneRadio id="legacyComplianceReport"
														autoSubmit="true"
														label="Is this a legacy compliance report? :"
														readOnly="#{!complianceReport.editMode || complianceReport.submittedFromPortal || (!complianceReport.admin && complianceReport.complianceReport.reportStatus == 'sbmt') || complianceReport.complianceReport.legacyFlag}"
														rendered="#{complianceReport.internalApp}"
														layout="horizontal"
														value="#{complianceReport.complianceReport.legacyFlag}"
														valueChangeListener="#{complianceReport.complianceReport.updateDefaults}">
														<f:selectItem itemLabel="Yes" itemValue="true" />
														<f:selectItem itemLabel="No" itemValue="false" />
												</af:selectOneRadio>
											</af:panelForm>
										</afh:rowLayout>

										<af:panelForm rows="2" maxColumns="3" width="100%" fieldWidth="90%">
												<af:selectInputDate readOnly="#{!complianceReport.editMode || (!complianceReport.admin && complianceReport.complianceReport.reportStatus == 'sbmt')}"
													label="Received On:"
													rendered="#{complianceReport.complianceReport.legacyFlag}"
													id="receivedDate1"
													autoSubmit="true"
													value="#{complianceReport.complianceReport.receivedDate}"
													valueChangeListener="#{complianceReport.complianceReport.updateReviewedDate}">
													<af:validateDateTimeRange
														minimum="1900-01-01"
														maximum="#{complianceReport.maxDate}" />
												</af:selectInputDate>
											
												<af:selectInputDate readOnly="#{!complianceReport.editMode || (!complianceReport.admin && complianceReport.complianceReport.reportStatus == 'sbmt')}"
													label="Received On:"
													rendered="#{!complianceReport.complianceReport.legacyFlag}"
													id="receivedDate2"
													value="#{complianceReport.complianceReport.receivedDate}">
													<af:validateDateTimeRange
														minimum="1900-01-01"
														maximum="#{complianceReport.maxDate}" />
												</af:selectInputDate>
											
												<af:selectInputDate
													readOnly="#{(complianceReport.complianceReport.reportStatus=='drft' && !complianceReport.complianceReport.legacyFlag) || !complianceReport.editMode || !complianceReport.complianceReport.receivedDateEntered}"
													label="Reviewed On:" id="dapcDateReviewed"
													value="#{complianceReport.complianceReport.dapcDateReviewed}">
													<af:validateDateTimeRange
														maximum="#{complianceReport.maxDate}" />
												</af:selectInputDate>
											
												<af:selectOneChoice label="Reviewed By:"
													id="dapcReviewer"
													value="#{complianceReport.complianceReport.dapcReviewer}"
													readOnly="#{complianceReport.complianceReport.dapcReviewer == 0 || !complianceReport.editMode}">
													<f:selectItems value="#{infraDefs.basicUsersDef.items[empty complianceReport.complianceReport.dapcReviewer?0:complianceReport.complianceReport.dapcReviewer]}" />
												</af:selectOneChoice>
											
												<af:selectOneChoice
													rendered="#{complianceReport.complianceReport.dapcAccepted != 'nyr' && !complianceReport.admin}"
													label="Report Accepted:"
													readOnly="#{complianceReport.complianceReport.reportStatus=='drft' || !complianceReport.editMode}"
													id="dapcAccepted1"
													value="#{complianceReport.complianceReport.dapcAccepted}">
													<f:selectItems
														value="#{complianceReport.complianceReportAcceptedReviewedDef.items[(empty '')]}" />
												</af:selectOneChoice>
											
												<af:selectOneChoice
													rendered="#{complianceReport.complianceReport.dapcAccepted == 'nyr' || complianceReport.admin}"
													label="Report Accepted:"
													readOnly="#{complianceReport.complianceReport.reportStatus=='drft' || !complianceReport.editMode}"
													id="dapcAccepted2"
													value="#{complianceReport.complianceReport.dapcAccepted}">
													<f:selectItems
														value="#{complianceReport.complianceReportAcceptedDef.items[(empty '')]}" />
												</af:selectOneChoice>
											
												<af:inputText label="Permit Number:" maximumLength="8"
													columns="8" readOnly="#{!complianceReport.editMode}"
													rows="1"
													id="permitNumber"
													value="#{complianceReport.complianceReport.permitNumber}" />

												<af:selectOneChoice label="Compliance Status:"
													id="complianceStatusCd"
													readOnly="#{!complianceReport.editMode}"
													unselectedLabel=""
													value="#{complianceReport.complianceReport.complianceStatusCd}">
													<f:selectItems
														value="#{complianceReport.complianceStatusDef.items[(empty '')]}" />
												</af:selectOneChoice>
										</af:panelForm>
										
										
										<afh:rowLayout halign="left">
										  <af:panelForm rows="1" maxColumns="1">
											<af:inputText label="Comments:" maximumLength="4000"
												columns="135" readOnly="#{!complianceReport.editMode}"
												rows="3"
												id="dapcReviewComments"
												value="#{complianceReport.complianceReport.dapcReviewComments}" />
										  </af:panelForm>
										</afh:rowLayout>

									</af:showDetailHeader>
									
									<af:panelGroup layout="vertical"
										partialTriggers="otherCategoryCd reportYear">

										<af:showDetailHeader text="Report Category" disclosed="true">
											<af:objectSpacer height="5" />
											
												<af:panelForm rows="1" maxColumns="3" width="100%" fieldWidth="95%">
													<af:selectOneChoice id="otherCategoryCd" label="Category: "
														showRequired="true" readOnly="true" unselectedLabel=""
														autoSubmit="true"
														value="#{complianceReport.complianceReport.otherCategoryCd}">
														<f:selectItems
															value="#{complianceReport.complianceReportTypeCategoryDefs.items[
																		(empty complianceReport.complianceReport.otherCategoryCd
																			? '' : complianceReport.complianceReport.otherCategoryCd)]}" />
													</af:selectOneChoice>

													<af:selectOneChoice id="reportYear" label="Report Year: "
														showRequired="true" autoSubmit="true"
														valueChangeListener="#{complianceReport.reportYearChanged}"
														rendered="#{complianceReport.complianceReport.cemsComsRataRpt}"
														value="#{complianceReport.complianceReport.reportYear}"
														readOnly="#{complianceReport.locked || !complianceReport.editMode}">
														<f:selectItems value="#{complianceReport.years}" />
													</af:selectOneChoice>
													<af:selectOneChoice id="reportQuarter"
														label="Report Quarter: "
														showRequired="#{not empty complianceReport.complianceReport.reportYear}"
														rendered="#{complianceReport.complianceReport.cemsComsRataRpt && not empty complianceReport.complianceReport.reportYear}"
														value="#{complianceReport.complianceReport.reportQuarter}"
														readOnly="#{complianceReport.locked || !complianceReport.editMode}">
														<f:selectItems value="#{complianceReport.yearQuarters}" />
													</af:selectOneChoice>
												</af:panelForm>
											
										</af:showDetailHeader>
									</af:panelGroup>
									
									<af:panelGroup partialTriggers="legacyComplianceReport" layout="vertical" rendered="#{complianceReport.complianceReport.secondGenerationQuarterlyCemComRpt && !complianceReport.complianceReport.legacyFlag}">
									<af:showDetailHeader text="CEM/COM/CMS Monitor Audits and Limits"
											disclosed="true">
											<af:objectSpacer height="5" />
											<f:subview id="continuous_monitors">
												<af:forEach
													items="#{complianceReport.complianceReport.compReportMonitorList}"
													var="complianceReportMonitor">
													<%@ include file="compReportMonitorList.jsp"%>
												</af:forEach>
											</f:subview>
									</af:showDetailHeader>
									</af:panelGroup>
									
									
									<af:panelGroup partialTriggers="legacyComplianceReport" layout="vertical" 
										rendered="#{complianceReport.complianceReport.secondGenerationAnnualRATARpt && !complianceReport.complianceReport.legacyFlag}">
									<af:showDetailHeader text="CEM/COM/CMS Monitor Certifications and Limits"
											disclosed="true">
											<af:objectSpacer height="5" />
											<f:subview id="continuous_monitors_rata">
												<af:forEach
													items="#{complianceReport.complianceReport.compReportMonitorList}"
													var="complianceReportMonitor">
													<%@ include file="compReportMonitorListRATA.jsp"%>
												</af:forEach>
											</f:subview>
									</af:showDetailHeader>
									</af:panelGroup>
									
									<af:panelGroup layout="vertical">
	
										<af:showDetailHeader
											text="Description, Reporting Period and/or Date(s)"
											id="commentsHdr" disclosed="true">
											<af:outputText inlineStyle="font-size:75%;color:#666"
												value="Enter the reporting period and due date if applicable. 
												Also summarize the contents of the attached compliance report, 
												including the test date, notification date, and any notable issues. 
												Attach the compliance report below.">
											</af:outputText>
											<af:inputText label="" maximumLength="4000" columns="150"
												rows="3"
												id="comments"
												readOnly="#{complianceReport.locked || !complianceReport.editMode}"
												value="#{complianceReport.complianceReport.comments}" />
										</af:showDetailHeader>
									</af:panelGroup>
	
									<f:subview id="doc_attachments">
										<jsp:include flush="true"
											page="doc_attachments.jsp" />
									</f:subview>
									
									<af:showDetailHeader text="Notes" disclosed="true" id="CRPTNotes" rendered="#{complianceReport.internalApp}">
										<jsp:include flush="true" page="notesComplianceReportTable.jsp" />
								    </af:showDetailHeader>
									
								</af:panelGroup>

								<af:objectSpacer height="15" />
								<afh:rowLayout halign="center">
									<af:panelButtonBar rendered="#{!complianceReport.readOnlyUser}">
										<af:commandButton text="New Stack Test for Results" useWindow="true"
												windowWidth="600" windowHeight="500"
												rendered="#{complianceReport.cetaUpdate && complianceReport.complianceReport.reportType=='test' &&
													complianceReport.complianceReport.otherCategoryCd == '36'}"
												id="newStackTestBtn"
												action="#{stackTests.newStackTest}">
												<t:updateActionListener
												property="#{stackTests.facility}" value="#{complianceReport.facility}" />
											</af:commandButton>
										<af:commandButton text="Edit"
											id="editBtn"
											action="#{complianceReport.edit}"
											rendered="#{!complianceReport.editMode && complianceReport.editable && !complianceReport.readOnlyUser }" />
											
										<af:commandButton text="Delete"
											id="deleteBtn"
											action="#{complianceReport.delete}"
											disabled="#{complianceReport.activeWorkflowProcess}"
											shortDesc="#{!complianceReport.activeWorkflowProcess ? 'Delete' : 
	                							'Delete is disabled because there is an active workflow for this Compliance Report. If you need to delete this Compliance Report, please first cancel the associated workflow.'}"
											rendered="#{(!complianceReport.admin && complianceReport.internalApp && complianceReport.complianceReport.reportStatus != 'sbmt'
              			&& complianceReport.editable && !complianceReport.editMode) || (complianceReport.admin && complianceReport.internalApp && !complianceReport.submittedFromPortal
              			&& complianceReport.editable && !complianceReport.editMode)}" 
              			useWindow="true"
										windowWidth="600" windowHeight="300"/>

										<af:commandButton text="Validate"
											id="validateBtn"
											action="#{complianceReport.validate}"
											rendered="#{((complianceReport.editable && complianceReport.portalApp && !complianceReport.editMode) 
                        || (complianceReport.internalApp && complianceReport.editable && !complianceReport.editMode && complianceReport.complianceReport.reportStatus != 'sbmt'))}"
											disabled="#{!complianceReport.editable}" >
											<t:updateActionListener property="#{facilityProfile.validTradeSecretNotifFrom}"
												value="complianceDetail" />
										</af:commandButton>

										<af:commandButton text="Save"
											id="saveBtn"
											action="#{complianceReport.save}"
											rendered="#{complianceReport.editMode && complianceReport.editable}"
											disabled="#{!complianceReport.editMode}" />
										<af:commandButton text="Cancel"
											action="#{complianceReport.cancelEdit}"
											rendered="#{complianceReport.editMode && complianceReport.editable}" immediate="true" />

									</af:panelButtonBar>
								</afh:rowLayout>


								
								<af:panelForm partialTriggers="templateYesNo">
								
									<af:objectSeparator rendered="#{complianceReport.passedValidation && 
												complianceReport.complianceReport.bulkEnabled eq true &&
												!complianceReport.editMode && 
												complianceReport.complianceReport.reportStatus=='drft'}" />
									
									<afh:rowLayout halign="center">
										<af:selectOneRadio  id="templateYesNo" autoSubmit="true"
											rendered="#{complianceReport.passedValidation && 
												complianceReport.complianceReport.bulkEnabled eq true &&
												!complianceReport.editMode && 
												complianceReport.complianceReport.reportStatus=='drft'}"
											label="Does this report pertain/affect additional facilities?"
											value="#{complianceReport.complianceReport.template}"
											valueChangeListener="#{complianceReport.templateValueChanged}">
										  <f:selectItem itemLabel="Yes" itemValue="true"/>
										  <f:selectItem itemLabel="No" itemValue="false"/>
										</af:selectOneRadio>
									</afh:rowLayout>
	
	  							    <af:objectSpacer height="15"  rendered="#{complianceReport.complianceReport.template eq true}" />
	


									<jsp:include flush="true" page="bulkFacilitySelectionTable.jsp" />


									<afh:rowLayout halign="center">
										<af:panelButtonBar rendered="#{!complianceReport.readOnlyUser}">
											<af:commandButton text="Submit" id="internalSubmit"
												rendered="#{complianceReport.complianceReport.reportStatus=='drft' && complianceReport.internalApp 
	              											&& !complianceReport.editMode && complianceReport.passedValidation}"
												disabled="#{!(complianceReport.editable) || (complianceReport.complianceReport.bulkEnabled eq true &&
													complianceReport.complianceReport.template eq null)}"
												action="#{complianceReport.submit}" />
	
												<af:commandButton text="Submit" id="submit"
													rendered="#{complianceReport.complianceReport.reportStatus=='drft' && complianceReport.portalApp 
										              			&& !complianceReport.editMode && complianceReport.passedValidation}"
													disabled="#{!complianceReport.editable || !myTasks.hasSubmit || 
														(complianceReport.complianceReport.bulkEnabled eq true &&
															complianceReport.complianceReport.template eq null)}"
													action="#{complianceReport.submit}"
													shortDesc="#{(myTasks.hasSubmit || !complianceReport.attestationRequired) ? '' : 'Submit'}"
													useWindow="true" windowWidth="#{submitTask.attestWidth}"
													windowHeight="#{submitTask.attestHeight}">
													<t:updateActionListener property="#{submitTask.type}"
														value="#{submitTask.yesNo}" />
													<t:updateActionListener property="#{submitTask.task}"
														value="#{complianceReport.task}" />
												</af:commandButton>
										</af:panelButtonBar>
									</afh:rowLayout>

								</af:panelForm>

								<af:objectSpacer height="5" />

								<afh:rowLayout halign="center">
									<af:panelButtonBar>

										<af:commandButton immediate="true"
											text="Show Current Facility Inventory"
											rendered="#{(complianceReport.internalApp && complianceReport.editable 
															&& !complianceReport.editMode && !complianceReport.complianceReport.secondGenerationCemComRataRpt)}"
											id="showCurrentFacilityBtn"
											action="#{complianceReport.initFacilityProfile}">
											<t:updateActionListener
												property="#{menuItem_facProfile.disabled}" value="false" />
										</af:commandButton>
										
										<af:commandButton immediate="true"
											text="Show Associated Facility Inventory"
											rendered="#{complianceReport.complianceReport.secondGenerationCemComRataRpt
															&& !complianceReport.editMode
															&& (complianceReport.internalApp || !complianceReport.editable)}"
											id="showAssociatedFacilityBtn"
											action="#{complianceReport.initFacilityProfile}">
											<t:updateActionListener
												property="#{menuItem_facProfile.disabled}" value="false" />
										</af:commandButton>
										
										<af:commandButton
								            text="Associate with Current Facility Inventory"
								            rendered="#{complianceReport.allowedToUpdateFacilityVersion}"
								        	useWindow="true"
											windowWidth="#{confirmWindow.width}"  windowHeight="#{confirmWindow.height}"
											id="associateWithCurrentBtn"
											action="#{confirmWindow.confirm}">
			        	    				<t:updateActionListener property="#{confirmWindow.type}"
			                					value="#{confirmWindow.yesNo}" />
			             					<t:updateActionListener property="#{confirmWindow.message}"
			                				value="This operation will associate this compliance report with 
              										the current version of the facility inventory. Click Yes to proceed 
              										or No to cancel."/>
              								<t:updateActionListener property="#{confirmWindow.method}"
			                					value="complianceReport.associateRptWithCurrentFacility" />		
								        </af:commandButton>
										
										<af:commandButton text="Download/Print" useWindow="true"
											windowWidth="500" windowHeight="300"
											rendered="#{!complianceReport.editMode}"
											id="downloadPrint"
											action="#{complianceReport.printComplianceReport}">
											<t:updateActionListener value="true"
												property="#{complianceReport.hideTradeSecret}" />
										</af:commandButton>

										<af:commandButton id="printTradeSecretBtn"
											text="Download/Print Trade Secret Version"
											rendered="#{!complianceReport.editMode && complianceReport.tradeSecretVisible && !applicationDetail.publicReadOnlyUser}"
											useWindow="true" windowWidth="500" windowHeight="300"
											action="#{complianceReport.printComplianceReport}">
											<t:updateActionListener value="false"
												property="#{complianceReport.hideTradeSecret}" />
										</af:commandButton>
										
										<%-- hide the download attestation button until it is not supported in IMPACT
										<af:goButton id="attestationDocButton"
											text="Download Attestation Document"
											rendered="#{complianceReport.complianceReport.reportStatus=='drft' && !complianceReport.internalApp 
              			&& !complianceReport.editMode}"
											targetFrame="_blank"
											destination="#{complianceReport.attestationDocURL}" />--%>

										<af:commandButton text="Workflow Task"
											rendered="#{complianceReport.internalApp && complianceReport.fromTODOList && !complianceReport.editMode}"
											id="workflowTaskBtn"
											action="#{complianceReport.goToCurrentWorkflow}" />
											
										<af:commandButton id="goToCompReports" text="Show Facility Compliance Reports"
											rendered="#{!complianceReport.editMode && complianceReport.internalApp}"
											action="#{complianceReport.goToSummaryPage}">
											<t:updateActionListener
												property="#{menuItem_facProfile.disabled}" value="false" />
										</af:commandButton>
									</af:panelButtonBar>
								</afh:rowLayout>
									
									
								
							</h:panelGrid>
						</f:facet>


					</af:panelBorder>
				</h:panelGrid>
				</afh:rowLayout>
			</af:page>

			<af:iterator value="#{complianceReport}" var="validationBean" id="v">
				<%@ include file="../util/validationComponents.jsp"%>
			</af:iterator>
			<%-- hidden controls for navigation to compliance report from popup --%>
			<%@ include file="../util/hiddenControls.jsp"%>
		</af:form>
	</af:document>
</f:view>



