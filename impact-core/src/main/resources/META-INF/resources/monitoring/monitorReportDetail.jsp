<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>

<f:view>
	<af:document title="Ambient Monitor Report">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:page var="foo" value="#{menuModel.model}"
				id="monitorReportDetail" title="Ambient Monitor Report">
				<%@ include file="header.jsp"%>
				<af:inputHidden value="#{monitorGroupDetail.popupRedirect}" />
				<afh:rowLayout halign="center">
					<h:panelGrid border="1" width="1000px">
						<f:subview id="monitorReportTop">
							<jsp:include page="monitorReportTop.jsp" />
						</f:subview>
						<af:panelGroup layout="vertical">
							<af:panelForm rows="2" maxColumns="2" labelWidth="220px" width="98%">
								<af:selectOneRadio id="legacyReport"
									label="Is this a legacy report?: "
									rendered="#{monitorReportDetail.internalApp}"
									readOnly="#{!monitorReportDetail.editable || !monitorReportDetail.editAllowed}"
									layout="horizontal" value="#{monitorReportDetail.monitorReport.legacyReport}">
									<f:selectItem itemLabel="Yes" itemValue="true" />
									<f:selectItem itemLabel="No" itemValue="false" />
								</af:selectOneRadio>
							</af:panelForm>
							
							<af:panelForm rows="3" maxColumns="1" labelWidth="220px" width="98%"
								partialTriggers="reportType">
								<af:selectOneChoice id="reportType"
									label="Report Type: "
									readOnly="#{!monitorReportDetail.editable || !monitorReportDetail.editAllowed}"
									unselectedLabel="" autoSubmit="true"
									value="#{monitorReportDetail.monitorReport.reportTypeCd}">
									<mu:selectItems value="#{monitorReportDetail.monitorReportTypeDef}" />
								</af:selectOneChoice>
								<af:selectOneChoice id="year"
									label="Reporting Year: "
									readOnly="#{!monitorReportDetail.editable || !monitorReportDetail.editAllowed}"
									unselectedLabel=""
									value="#{monitorReportDetail.monitorReport.year}">
									<f:selectItems value="#{infraDefs.years}" />
								</af:selectOneChoice>
								<af:selectOneChoice id="quarter"
									label="Quarter: "
									readOnly="#{!monitorReportDetail.editable || !monitorReportDetail.editAllowed}"
									rendered="#{!monitorReportDetail.monitorReport.annualReportType}"
									unselectedLabel=""
									value="#{monitorReportDetail.monitorReport.quarter}">
									<f:selectItem itemLabel="First" itemValue="1"/>
									<f:selectItem itemLabel="Second" itemValue="2"/>
									<f:selectItem itemLabel="Third" itemValue="3"/>
									<f:selectItem itemLabel="Fourth" itemValue="4"/>
								</af:selectOneChoice>
							</af:panelForm>
							
							<af:panelForm rows="2" maxColumns="2" labelWidth="220px" width="98%">
								<af:inputText id="description"
									label="Description: "
									readOnly="#{!monitorReportDetail.editable || !monitorReportDetail.editAllowed}"
									value="#{monitorReportDetail.monitorReport.description}"
									columns="100" rows= "5"
									maximumLength="500" />
							</af:panelForm>
							
							<af:panelForm rows="2" maxColumns="2" labelWidth="220px" width="98%">
								<af:showDetailHeader text="AQD Staff" disclosed="true"
									rendered="#{monitorReportDetail.internalApp && monitorReportDetail.monitorReport.submitted}">
									<af:panelForm rows="2" maxColumns="2" labelWidth="220px" width="98%">
										<af:selectInputDate id="submittedDate"
											label="Submitted On: "
											readOnly="true"
											value="#{monitorReportDetail.monitorReport.submittedDate}">
										</af:selectInputDate>
									</af:panelForm>
									
									<af:panelForm rows="3" maxColumns="2" labelWidth="220px" width="98%">
										<af:selectOneChoice id="complianceReviewerId"
											label="Compliance Reviewer: "
											readOnly="#{!monitorReportDetail.editable || !monitorReportDetail.editAllowedbyAqd}"
											unselectedLabel=""
											value="#{monitorReportDetail.monitorReport.complianceReviewerId}">
											<f:selectItems
												value="#{infraDefs.basicUsersDef.items[(
															empty monitorReportDetail.monitorReport.complianceReviewerId ? 0
															: monitorReportDetail.monitorReport.complianceReviewerId)]}"/>
										</af:selectOneChoice>
										<af:selectInputDate id="complianceReviewDate"
											label="Compliance Review Date: "
											readOnly="#{!monitorReportDetail.editable || !monitorReportDetail.editAllowedbyAqd}"
											value="#{monitorReportDetail.monitorReport.complianceReviewDate}">
											<af:validateDateTimeRange
												minimum="#{monitorReportDetail.minDate}"
												maximum="#{monitorReportDetail.maxDate}"/>
										</af:selectInputDate>
										<af:selectOneChoice id="complianceStatusCd"
											label="Compliance Status: "
											readOnly="#{!monitorReportDetail.editable || !monitorReportDetail.editAllowedbyAqd}"
											unselectedLabel=""
											value="#{monitorReportDetail.monitorReport.complianceStatusCd}">
											<f:selectItems
													value="#{monitorReportDetail.complianceStatusDef.items[(empty '')]}" />
										</af:selectOneChoice>
										
										<af:selectOneChoice id="monitoringReviewerId"
											label="Monitoring Reviewer: "
											readOnly="#{!monitorReportDetail.editable || !monitorReportDetail.editAllowedbyAqd}"
											unselectedLabel=""
											value="#{monitorReportDetail.monitorReport.monitoringReviewerId}">
											<f:selectItems
												value="#{infraDefs.basicUsersDef.items[(
															empty monitorReportDetail.monitorReport.monitoringReviewerId ? 0
															: monitorReportDetail.monitorReport.monitoringReviewerId)]}"/>
										</af:selectOneChoice>	
										<af:selectInputDate id="monitoringReviewDate"
											label="Monitoring Review Date: "
											readOnly="#{!monitorReportDetail.editable || !monitorReportDetail.editAllowedbyAqd}"
											value="#{monitorReportDetail.monitorReport.monitoringReviewDate}">
											<af:validateDateTimeRange
												minimum="#{monitorReportDetail.minDate}" 
												maximum="#{monitorReportDetail.maxDate}"/>
										</af:selectInputDate>
									</af:panelForm>
									
									<af:panelForm rows="2" maxColumns="2" labelWidth="220px" width="98%">
										<af:inputText id="permitNumber"
											label="Permit Number: "
											readOnly="#{!monitorReportDetail.editable || !monitorReportDetail.editAllowedbyAqd}"
											value="#{monitorReportDetail.monitorReport.permitNumber}"
											columns="15" maximumLength="32" />
										<af:selectOneChoice id="reportAccepted"
											label="Report Accepted: "
											readOnly="#{!monitorReportDetail.editable || !monitorReportDetail.editAllowedbyAqd}"
											value="#{monitorReportDetail.monitorReport.reportAcceptedCd}">
											<mu:selectItems value="#{monitorReportDetail.reportAcceptedStatusDef}" />
										</af:selectOneChoice>					
									</af:panelForm>
									
									<af:panelForm rows="2" maxColumns="2" labelWidth="220px" width="98%">
										<af:inputText id="aqdComments"
											label="AQD Comments: "
											readOnly="#{!monitorReportDetail.editable || !monitorReportDetail.editAllowedbyAqd}"
											value="#{monitorReportDetail.monitorReport.aqdComments}"
											columns="100" rows= "5"
											maximumLength="4000" />
									</af:panelForm>
								</af:showDetailHeader>
							</af:panelForm>
							
							<af:showDetailHeader text="Report Attachments" disclosed="true" 
								id="reportAttachments" rendered="true">
								<jsp:include flush="true" page="reportAttachments.jsp" />
							</af:showDetailHeader>
							
							<af:objectSpacer height="10"/>

							<afh:rowLayout halign="center">
								<af:panelButtonBar>
									<af:commandButton text="Edit"
										rendered="#{monitorReportDetail.displayEditBtn}"
										action="#{monitorReportDetail.startEditReport}" />
									<af:commandButton text="Validate"
										rendered="#{monitorReportDetail.validateAllowed}"
										action="#{monitorReportDetail.validateReport}" />
									<af:commandButton id="deleteReportBtn" text="Delete"
										shortDesc="#{monitorReportDetail.activeWorkFlowProcessExist
													 ? 'Delete is disabled because there is an active workflow associated with this report'
													 : 'Delete'}"
										disabled="#{monitorReportDetail.activeWorkFlowProcessExist}"
										rendered="#{monitorReportDetail.deleteAllowed}"
										action="dialog:deleteMonitorReport" useWindow="true"
										windowWidth="600" windowHeight="300" />
									<af:commandButton text="Save"
										rendered="#{monitorReportDetail.editable}"
										action="#{monitorReportDetail.saveReport}" />			
									<af:commandButton text="Cancel"
										rendered="#{monitorReportDetail.editable}"
										action="#{monitorReportDetail.cancel}" />
									<af:commandButton text="Submit"
										rendered="#{monitorReportDetail.submitAllowed && monitorReportDetail.internalApp}"
										action="#{monitorReportDetail.submitReport2}" />
									<af:commandButton text="Workflow Task"
										rendered="#{monitorReportDetail.fromTODOList && !monitorReportDetail.editable}"
										action="#{monitorReportDetail.goToCurrentWorkflow}" />	
								</af:panelButtonBar>
							</afh:rowLayout>
							<afh:rowLayout halign="center">
								<af:panelButtonBar>
									<af:commandButton text="Show Associated Monitor Group"
										action="#{monitorReportDetail.showAssociatedGroup}"
										rendered="#{! monitorReportDetail.editable}" />
								</af:panelButtonBar>
							</afh:rowLayout>
							
						</af:panelGroup>
					</h:panelGrid>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>

